package com.github.pickleface5.commands.music;

import com.github.pickleface5.Main;
import com.github.pickleface5.music.GuildMusicManager;
import com.github.pickleface5.util.MusicUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class ForwardCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("forward")) return;
        if (event.getGuild() == null) {
            event.reply("You need to use this in a server!").queue();
            return;
        }
        GuildVoiceState memberVoiceState = Objects.requireNonNull(event.getMember()).getVoiceState();
        assert memberVoiceState != null;
        if (!memberVoiceState.inAudioChannel()) {
            event.reply("You need to be in a voice channel!").queue();
            return;
        }
        if (Objects.requireNonNull(Objects.requireNonNull(event.getGuild().getMember(Main.JDA.getSelfUser())).getVoiceState()).getChannel() == null) {
            event.reply("I'm not in a voice channel!").queue();
            return;
        }
        GuildMusicManager musicInst = MusicUtils.getGuildAudioPlayer(event.getGuild());
        AudioTrack currentTrack = musicInst.player.getPlayingTrack();
        if (currentTrack == null) {
            event.reply("I'm not playing anything!").queue();
            return;
        }

        if (event.getOption("duration").getAsLong() <= 0) {
            event.reply("The duration has to be greater than 0!").queue();
            return;
        }
        currentTrack.setPosition(currentTrack.getPosition() + (event.getOption("duration").getAsLong() * 1000));
        event.reply("Skipped to `" + MusicUtils.getDurationString(currentTrack.getPosition()) + "`!").queue();
    }
}
