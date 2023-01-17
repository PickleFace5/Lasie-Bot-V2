package com.github.pickleface5.commands.music;

import com.github.pickleface5.Main;
import com.github.pickleface5.util.MusicUtils;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PlayCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) { //TODO: Completely Redo/Revamp Music Commands (see roadmap)
        if (!event.getName().equals("play")) return;
        event.deferReply().queue();
        if (event.getGuild() == null) {
            event.getHook().sendMessage("You need to use this in a server!").queue();
            return;
        }
        if (event.getOption("search") == null) {
            event.getHook().sendMessage("You need to enter a URL!").queue();
            return;
        }
        Guild guild = event.getGuild();
        GuildVoiceState memberVoiceState = Objects.requireNonNull(event.getMember()).getVoiceState();
        assert memberVoiceState != null;
        if (!memberVoiceState.inAudioChannel()) {
            event.getHook().sendMessage("You need to be in a voice channel!").queue();
            return;
        }
        if (Objects.requireNonNull(Objects.requireNonNull(event.getGuild().getMember(Main.JDA.getSelfUser())).getVoiceState()).getChannel() == null) {
            assert memberVoiceState.getChannel() != null;
            AudioChannel voiceChannel = Objects.requireNonNull(event.getMember().getVoiceState()).getChannel();
            try {
                MusicUtils.connectToVoice(guild, voiceChannel);
            } catch (InsufficientPermissionException exception) {
                event.getHook().sendMessage("I don't have the permissions to join your voice channel! Make sure I can have the permission ``Connect``, and if the user limit is filled, ``Move Members``.").queue();
                return;
            }
        }
        new MusicUtils(MusicUtils.playerManager, MusicUtils.musicManagers).loadAndPlay(event.getTextChannel(), Objects.requireNonNull(event.getOption("search")).getAsString(), event.getUser());
        event.getHook().sendMessage(":white_check_mark:").queue();
    }
}
