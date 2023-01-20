package com.github.pickleface5;

import com.github.pickleface5.user.BotStatus;
import com.github.pickleface5.util.CommandRegistry;
import com.github.pickleface5.util.MusicUtils;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static JDA JDA = null;
    public static final File TEMP_DIRECTORY = new File(new File(System.getProperty("java.io.tmpdir")), "files");

    static {
        try {
            JDA = JDABuilder.createDefault(System.getenv("BOT_TOKEN"))
                    .enableCache(CacheFlag.VOICE_STATE)
                    .build()
                    .awaitReady();
        } catch (InterruptedException e) {
            logger.fatal(e.getMessage());
            System.exit(-1);
        }
    }
    public static final String BOT_USER_ID = JDA.getSelfUser().getId();

    public static void main(String[] args) {
        logger.traceEntry();

        if (TEMP_DIRECTORY.exists()) {
            logger.info("Temp Directory exists");
        } else {
            if (TEMP_DIRECTORY.mkdirs()) // Returns a boolean, but this has a very low chance of returning false, sooo...
                logger.info("Temp Directory created");
            else {
                logger.fatal("TEMP DIRECTORY ERROR, NOT CREATED");
            }

        }
        logger.info("Current temp path: {}", Main.TEMP_DIRECTORY.getAbsolutePath());

        new MusicUtils(new DefaultAudioPlayerManager(), new HashMap<>());

        new CommandRegistry();

        BotStatus.activateBotActivityRoutine();
        logger.info("Finished loading {} on shard {}!", JDA.getSelfUser().getName(), JDA.getShardInfo().getShardId() + 1);
        logger.info("Currently loaded in [{}] servers", JDA.getGuilds().size());
    }
}
