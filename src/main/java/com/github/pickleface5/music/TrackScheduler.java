package com.github.pickleface5.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    public Boolean isLooping;
    public ArrayList<Member> skipVotes;
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;

    public TrackScheduler(AudioPlayer player) {
        this.isLooping = false;
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.skipVotes = new ArrayList<>();
    }

    public void queue(AudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.add(track);
        }
    }

    // Used for /purgequeue
    // Clears the LinkedBlockingQueue<> (yes I know, insane)
    public void clearQueue() {
        queue.clear();
    }

    public BlockingQueue<AudioTrack> getQueue() {
        return this.queue;
    }

    public void nextTrack(AudioTrack previousTrack) {
        if (this.isLooping && previousTrack != null) {
            player.startTrack(previousTrack.makeClone(), false);
        } else {
            player.startTrack(queue.poll(), false);
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack(track);
        }
    }

    public Boolean isLooping() {
        return isLooping;
    }

    public void setIsLooping(boolean bool) {
        isLooping = bool;
    }

    public ArrayList<Member> skipVotes() {
        return skipVotes;
    }

    public void addSkipVotes(Member memberVote) {
        skipVotes.add(memberVote);
    }

    public void clearSkipVotes() {
        skipVotes.clear();
    }

    public void close() {
        clearSkipVotes();
        setIsLooping(false);
        clearQueue();
    }
}
