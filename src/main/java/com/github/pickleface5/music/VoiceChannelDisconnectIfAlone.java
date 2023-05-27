package com.github.pickleface5.music;

import com.github.pickleface5.Main;
import com.github.pickleface5.util.MusicUtils;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class VoiceChannelDisconnectIfAlone extends ListenerAdapter {

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        if (event.getChannelLeft() != null) {
            if (event.getChannelLeft().getType().equals(ChannelType.VOICE)) {
                leaveIfAlone(event.getChannelLeft().asVoiceChannel());
            }
        }
    }


    private void leaveIfAlone(AudioChannel voiceChannel) {
        if (voiceChannel.getMembers().isEmpty()) return;
        if (voiceChannel.getMembers().size() <= 1 && voiceChannel.getMembers().get(0).getUser().getId().equals(Main.BOT_USER_ID)) {
            MusicUtils.getGuildAudioPlayer(voiceChannel.getGuild()).close();
            voiceChannel.getGuild().getAudioManager().closeAudioConnection();
        }
    }
}
