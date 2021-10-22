package com.github.pickleface5;

import com.github.pickleface5.music.VoiceChannelDisconnectIfAlone;
import com.github.pickleface5.user.BotStatus;
import com.github.pickleface5.util.CommandRegistry;
import com.github.pickleface5.util.MusicUtils;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.util.HashMap;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static JDA JDA = null;
    static {
        try {
            JDA = JDABuilder.createDefault(System.getenv("BOT_TOKEN"))
                    .enableCache(CacheFlag.VOICE_STATE)
                    .addEventListeners(new VoiceChannelDisconnectIfAlone())
                    .build()
                    .awaitReady();
        } catch (InterruptedException | LoginException e) {
            e.printStackTrace();
        }
    }
    public static final String BOT_USER_ID = JDA.getSelfUser().getId();

    public static void main(String[] args) {
        logger.traceEntry();

        new MusicUtils(new DefaultAudioPlayerManager(), new HashMap<>());

        new CommandRegistry();

        BotStatus.activateBotActivityRoutine();
        logger.info("Finished loading {} on shard {}!", JDA.getSelfUser().getName(), JDA.getShardInfo().getShardId() + 1);
    }
}
