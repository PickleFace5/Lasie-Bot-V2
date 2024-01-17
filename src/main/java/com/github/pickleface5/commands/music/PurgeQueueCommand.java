package com.github.pickleface5.commands.music;

import com.github.pickleface5.util.MusicUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PurgeQueueCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("purgequeue")) return;
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
        if (guild.getAudioManager().getConnectedChannel() == null) {
            event.reply("I need the be in a voice channel!").queue();
            return;
        }
        if ((!event.getMember().hasPermission(Permission.VOICE_MOVE_OTHERS) || !(guild.getAudioManager().getConnectedChannel().getMembers().size() <= 2)) && !event.getUser().getId().equals("535622235755380746")) {
            event.reply("Only people who are alone in a voice channel or have permissions to move members can use this command!").queue();
            return;
        }
        MusicUtils.getGuildAudioPlayer(guild).scheduler.clearQueue();
        event.reply("Purged queue :fire:").queue();
    }
}
