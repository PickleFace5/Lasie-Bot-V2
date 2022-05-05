package com.github.pickleface5.commands.music;

import com.github.pickleface5.Main;
import com.github.pickleface5.music.GuildMusicManager;
import com.github.pickleface5.util.MusicUtils;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PauseCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("pause")) return;
        if (event.getGuild() == null) {
            event.getHook().sendMessage("You need to use this in a server!").queue();
            return;
        }
        GuildVoiceState memberVoiceState = Objects.requireNonNull(event.getMember()).getVoiceState();
        assert memberVoiceState != null;
        if (!memberVoiceState.inAudioChannel()) {
            event.getHook().sendMessage("You need to be in a voice channel!").queue();
            return;
        }
        if (Objects.requireNonNull(Objects.requireNonNull(event.getGuild().getMember(Main.JDA.getSelfUser())).getVoiceState()).getChannel() == null) {
            event.reply("I'm not in a voice channel!").queue();
            return;
        }
        GuildMusicManager guildMusicManager = MusicUtils.getGuildAudioPlayer(event.getGuild());
        if (guildMusicManager.player.isPaused()) {
            event.reply("I'm already paused!").queue();
            return;
        }
        guildMusicManager.player.setPaused(true);
        event.reply("Paused :play_pause:").queue();
    }
}
