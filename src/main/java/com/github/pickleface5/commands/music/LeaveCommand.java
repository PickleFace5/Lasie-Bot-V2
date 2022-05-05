package com.github.pickleface5.commands.music;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LeaveCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("leave") || event.getUser().isBot()) return;
        else if (event.getGuild() == null) {
            event.reply("You need to use this in a server!").queue();
            return;
        }
        Guild guild = event.getGuild();
        GuildVoiceState memberVoiceState = Objects.requireNonNull(event.getMember()).getVoiceState();
        assert memberVoiceState != null;
        if (!memberVoiceState.inAudioChannel() || memberVoiceState.getChannel() != guild.getAudioManager().getConnectedChannel()) {
            event.reply("You need to be in a voice channel!").queue();
            return;
        }
        if (!guild.getAudioManager().isConnected()) {
            event.reply("I'm not connected to a voice channel!").queue();
            return;
        }
        if (!event.getMember().hasPermission(Permission.VOICE_MOVE_OTHERS) || !(Objects.requireNonNull(guild.getAudioManager().getConnectedChannel()).getMembers().size() <= 2)) {
            event.reply("Only people who are alone in a voice channel or have permissions to move members can use this command!").queue();
            return;
        }
        guild.getAudioManager().closeAudioConnection();
        event.reply(":white_check_mark:").queue();
    }
}
