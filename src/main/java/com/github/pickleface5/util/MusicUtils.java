package com.github.pickleface5.util;

import com.github.pickleface5.music.AudioPlayerSendHandler;
import com.github.pickleface5.music.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import ch.qos.logback.classic.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MusicUtils {
    public static Map<Long, GuildMusicManager> musicManagers;
    public static AudioPlayerManager playerManager;
    final String youTubeUrlRegEx = "^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/";
    final String[] videoIdRegex = { "\\?vi?=([^&]*)","watch\\?.*v=([^&]*)", "(?:embed|vi?)/([^/?]*)", "^([A-Za-z0-9\\-]*)"};
    private static final Logger logger = (Logger) LoggerFactory.getLogger(MusicUtils.class);

    public MusicUtils(AudioPlayerManager playerManager, Map<Long, GuildMusicManager> musicManagers) {
        MusicUtils.musicManagers = musicManagers;
        MusicUtils.playerManager = playerManager;
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public static void connectToVoice(Guild guild, AudioChannel voiceChannel) {
        getGuildAudioPlayer(guild).getTrackScheduler().clearQueue();
        getGuildAudioPlayer(guild).player.destroy();
        getGuildAudioPlayer(guild).player.setVolume(50);
        getGuildAudioPlayer(guild).getTrackScheduler().isLooping = false;
        AudioManager audioManager = guild.getAudioManager();
        audioManager.openAudioConnection(voiceChannel);
        audioManager.setSendingHandler(new AudioPlayerSendHandler(getGuildAudioPlayer(guild).player));
    }

    public static GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public void loadAndPlay(final TextChannel channel, String trackUrl, User user) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        if (!trackUrl.startsWith("http")) {
            trackUrl = ("ytsearch: " + trackUrl + "").toLowerCase();
        }
        final String finalTrackUrl = trackUrl;
        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                String title = track.getInfo().title;
                if (title.equals("Unknown title")) title = track.getInfo().uri;
                channel.sendMessageEmbeds(new EmbedBuilder()
                        .setColor(EmbedUtils.EMBED_COLOR)
                        .setTitle("Adding to queue")
                        .setDescription("[" + title + "](" + track.getInfo().uri + ") (" + getDurationString(track.getDuration()) + ")"
                        )
                        .setThumbnail("https://img.youtube.com/vi/" + extractVideoIdFromUrl(track.getInfo().uri) + "/mqdefault.jpg")
                        .setFooter("Added by " + user.getName(), user.getAvatarUrl())
                        .build()).queue();

                play(musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                channel.sendMessageEmbeds(new EmbedBuilder()
                        .setColor(EmbedUtils.EMBED_COLOR)
                        .setTitle("Adding to queue")
                        .setDescription("[" + firstTrack.getInfo().title + "](" + firstTrack.getInfo().uri + ")")
                        .setThumbnail("https://img.youtube.com/vi/" + extractVideoIdFromUrl(firstTrack.getInfo().uri) + "/mqdefault.jpg")
                        .setFooter("Added by " + user.getName(), user.getAvatarUrl())
                        .build()).queue();

                play(musicManager, firstTrack);
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nothing found by " + finalTrackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
                logger.error(exception.getMessage());
            }
        });
    }

    public void play(GuildMusicManager musicManager, AudioTrack track) {
        musicManager.scheduler.queue(track);
    }

    public String extractVideoIdFromUrl(String url) {
        String youTubeLinkWithoutProtocolAndDomain = youTubeLinkWithoutProtocolAndDomain(url);

        for(String regex : videoIdRegex) {
            Pattern compiledPattern = Pattern.compile(regex);
            Matcher matcher = compiledPattern.matcher(youTubeLinkWithoutProtocolAndDomain);

            if(matcher.find()){
                return matcher.group(1);
            }
        }

        return null;
    }

    private String youTubeLinkWithoutProtocolAndDomain(String url) {
        Pattern compiledPattern = Pattern.compile(youTubeUrlRegEx);
        Matcher matcher = compiledPattern.matcher(url);

        if(matcher.find()){
            return url.replace(matcher.group(), "");
        }
        return url;
    }

    public static String getDurationString(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes);
        if (seconds < 10) return String.format("%d:0%d", minutes, seconds);
        else return String.format("%d:%d", minutes, seconds);
    }

    public static long getStringToMillis(String duration) {
        return LocalTime.parse(duration).toSecondOfDay() * 1000L;
    }
}
