package com.github.pickleface5.commands.music;

import com.github.pickleface5.music.GuildMusicManager;
import com.github.pickleface5.util.EmbedUtils;
import com.github.pickleface5.util.MusicUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.DelegatedAudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.BlockingQueue;

public class QueueCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("queue")) return;
        else if (event.getGuild() == null) {
            event.reply("You need to use this in a server!").queue();
            return;
        }
        Guild guild = event.getGuild();
        GuildMusicManager guildMusicManager = MusicUtils.getGuildAudioPlayer(guild);
        BlockingQueue<AudioTrack> guildQueue = guildMusicManager.scheduler.getQueue();
        if (guildQueue.size() == 0) {
            event.reply("There's nothing in the queue!").queue();
            return;
        }
        event.deferReply().queue();
        EmbedBuilder messageEmbed = new EmbedBuilder().setColor(EmbedUtils.EMBED_COLOR)
                .setTitle("Music Queue");
        if (guildMusicManager.getTrackScheduler().isLooping()) {
            messageEmbed.addField("Now Playing", "[" + guildMusicManager.player.getPlayingTrack().getInfo().title + "](" + guildMusicManager.player.getPlayingTrack().getInfo().uri + ")", false);
            event.getHook().sendMessageEmbeds(messageEmbed.build()).queue();
            return;
        }
        for (int i = 0; i < guildQueue.size(); i++) {
            Object[] audioTrack = guildQueue.toArray();
            if (i == 0) messageEmbed.addField("Now Playing", "[" + guildMusicManager.player.getPlayingTrack().getInfo().title + "](" + guildMusicManager.player.getPlayingTrack().getInfo().uri + ")", false);
            else if (audioTrack[i] instanceof DelegatedAudioTrack) messageEmbed.addField("#" + (i + 1), "[" + ((DelegatedAudioTrack) audioTrack[i]).getInfo().title + "](" + ((DelegatedAudioTrack) audioTrack[i]).getInfo().uri + ")", false);
            else messageEmbed.addField("Unknown Error", "Unknown Error", false);
        }
        event.getHook().sendMessageEmbeds(messageEmbed.build()).queue();
    }
}
