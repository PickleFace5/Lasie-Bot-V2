package com.github.pickleface5.music;

import com.github.pickleface5.Main;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class VoiceChannelDisconnectIfAlone extends ListenerAdapter {
    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        leaveIfAlone(event.getChannelLeft());
    }

    @Override
    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event) {
        leaveIfAlone(event.getChannelLeft());
    }

    private void leaveIfAlone(VoiceChannel voiceChannel) {
        if (voiceChannel.getMembers().isEmpty()) return;
        if (voiceChannel.getMembers().size() <= 1 && voiceChannel.getMembers().get(0).getUser().getId().equals(Main.BOT_USER_ID)) {
            voiceChannel.getGuild().getAudioManager().closeAudioConnection();
        }
    }
}
