package com.github.pickleface5.commands.music;

import com.github.pickleface5.util.MusicUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class JoinCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("join") || event.getUser().isBot()) return;
        else if (event.getGuild() == null) {
            event.reply("You need to use this in a server!").queue();
            return;
        }
        Guild guild = event.getGuild();
        GuildVoiceState memberVoiceState = Objects.requireNonNull(event.getMember()).getVoiceState();
        assert memberVoiceState != null;
        if (!memberVoiceState.inAudioChannel()) {
            event.reply("You need to be in a voice channel!").queue();
            return;
        }
        assert memberVoiceState.getChannel() != null;
        AudioChannel voiceChannel = Objects.requireNonNull(event.getMember().getVoiceState()).getChannel();
        try {
            MusicUtils.connectToVoice(guild, voiceChannel);
        } catch (InsufficientPermissionException exception) {
            event.reply("I don't have the permissions to join your voice channel! Make sure I can have the permission ``Connect``, and if the user limit is filled, ``Move Members``.").queue();
            return;
        }
        event.reply(":white_check_mark:").queue();
    }
}
