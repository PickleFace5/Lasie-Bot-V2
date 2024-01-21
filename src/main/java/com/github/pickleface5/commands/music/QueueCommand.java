package com.github.pickleface5.commands.music;

import com.github.pickleface5.music.GuildMusicManager;
import com.github.pickleface5.util.EmbedUtils;
import com.github.pickleface5.util.MusicUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.DelegatedAudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.BlockingQueue;

public class QueueCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("queue")) return;
        else if (event.getGuild() == null) {
            event.reply("You need to use this in a server!").queue();
            return;
        }
        Guild guild = event.getGuild();
        GuildMusicManager guildMusicManager = MusicUtils.getGuildAudioPlayer(guild);
        BlockingQueue<AudioTrack> guildQueue = guildMusicManager.scheduler.getQueue();
        if (guildQueue.size() == 0 && !guildMusicManager.getTrackScheduler().isLooping && guildMusicManager.player.getPlayingTrack() == null) {
            event.reply("There's nothing in the queue!").queue();
            return;
        }

        event.deferReply().queue();
        EmbedBuilder messageEmbed = new EmbedBuilder().setColor(EmbedUtils.EMBED_COLOR).setTitle("Music Queue");
        AudioTrack currentTrack = guildMusicManager.player.getPlayingTrack();
        String title = currentTrack.getInfo().title;
        String uri = currentTrack.getInfo().uri;
        long totalTime = currentTrack.getDuration() - currentTrack.getPosition();
        String durationLeft = MusicUtils.getDurationString(totalTime);
        messageEmbed.addField("Now Playing", "[" + title + "](" + uri + ") (" + durationLeft + " left)", false);
        for (int i = 0; i < guildQueue.size(); i++) {
            Object[] audioTrack = guildQueue.toArray();
            if (audioTrack[i] instanceof DelegatedAudioTrack) {
                long duration = ((DelegatedAudioTrack) audioTrack[i]).getDuration();
                messageEmbed.addField("#" + (i + 1), "[" + ((DelegatedAudioTrack) audioTrack[i]).getInfo().title + "](" + ((DelegatedAudioTrack) audioTrack[i]).getInfo().uri + ") (" + MusicUtils.getDurationString(duration) + ")", false);
                totalTime = totalTime + duration;
            }
            else messageEmbed.addField("Unknown Error", "Unknown Error", false);
        }
        messageEmbed.setFooter("Total Playtime: " + MusicUtils.getDurationString(totalTime));
        event.getHook().sendMessageEmbeds(messageEmbed.build()).queue();
    }
}
