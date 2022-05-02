package com.github.pickleface5.commands.music;

import com.github.pickleface5.Main;
import com.github.pickleface5.music.GuildMusicManager;
import com.github.pickleface5.util.MusicUtils;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SkipCommand extends ListenerAdapter {
    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (!event.getName().equals("skip") || event.getUser().isBot()) return;
        if (event.getGuild() == null) {
            event.reply("You need to use this in a server!").queue();
            return;
        }
        GuildVoiceState memberVoiceState = Objects.requireNonNull(event.getMember()).getVoiceState();
        assert memberVoiceState != null;
        if (!memberVoiceState.inVoiceChannel()) {
            event.reply("You need to be in a voice channel!").queue();
            return;
        }
        if (Objects.requireNonNull(Objects.requireNonNull(event.getGuild().getMember(Main.JDA.getSelfUser())).getVoiceState()).getChannel() == null) {
            event.reply("I'm not in a voice channel!").queue();
            return;
        }
        GuildMusicManager guildAudioPlayer = MusicUtils.getGuildAudioPlayer(event.getGuild());
        VoiceChannel voiceChannel = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(event.getGuild().getMember(Main.JDA.getSelfUser())).getVoiceState()).getChannel());
        if (guildAudioPlayer.getTrackScheduler().getQueue().isEmpty()) {
            event.reply("There's nothing to skip to!").queue();
            return;
        }
        if (voiceChannel.getMembers().size() <= 2) {
            guildAudioPlayer.scheduler.nextTrack(null);
            event.reply("Skipped to next track.").queue();
        } else {
            if (guildAudioPlayer.getTrackScheduler().skipVotes().contains(event.getMember())) {
                event.reply("You've already voted!").queue();
                return;
            }
            guildAudioPlayer.getTrackScheduler().addSkipVotes(event.getMember());
            if (guildAudioPlayer.getTrackScheduler().skipVotes().size() >= voiceChannel.getMembers().size() / 2) {
                guildAudioPlayer.getTrackScheduler().nextTrack(null);
                event.reply("Skipping track...").queue();
            } else {
                event.reply("Vote added! Current votes: " + guildAudioPlayer.getTrackScheduler().skipVotes() + " / " + (voiceChannel.getMembers().size() - 1)).queue();
            }
        }
    }
}
