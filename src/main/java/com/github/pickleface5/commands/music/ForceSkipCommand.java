package com.github.pickleface5.commands.music;

import com.github.pickleface5.Main;
import com.github.pickleface5.util.MusicUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ForceSkipCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("forceskip")) {
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
            if ((!event.getMember().hasPermission(Permission.VOICE_MUTE_OTHERS) || !event.getMember().hasPermission(Permission.ADMINISTRATOR)) && !event.getUser().getId().equals("535622235755380746")) {
                event.reply("Only people who can server mute can force skip songs!").queue();
            }
            MusicUtils.getGuildAudioPlayer(event.getGuild()).scheduler.nextTrack(null);
            if (MusicUtils.getGuildAudioPlayer(event.getGuild()).getTrackScheduler().getQueue().isEmpty()) {
                event.reply("There's nothing to skip to!").queue();
            } else {
                event.reply("Skipped to next track.").queue();
            }
        }
    }
}
