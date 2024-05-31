package com.github.pickleface5;

import com.github.pickleface5.user.BotStatus;
import com.github.pickleface5.util.CommandRegistry;
import com.github.pickleface5.util.MusicUtils;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;

import ch.qos.logback.classic.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.io.File;
import java.util.HashMap;

import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(Main.class);
    public static JDA JDA;
    public static final File TEMP_DIRECTORY = new File(new File(System.getProperty("java.io.tmpdir")), "files");
    public static String BOT_USER_ID = null;

    public static void main(String[] args) {

        if (args.length > 0) {
            logger.info(args[0]);
            try {
                String token = args[0];
                JDA = JDABuilder.createDefault(token)
                        .enableCache(CacheFlag.VOICE_STATE)
                        .build()
                        .awaitReady();
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                System.exit(-1);
            }
        } else {
            try {
                JDA = JDABuilder.createDefault(System.getenv("BOT_TOKEN"))
                        .enableCache(CacheFlag.VOICE_STATE)
                        .build()
                        .awaitReady();
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                System.exit(-1);
            }
        }
        BOT_USER_ID = JDA.getSelfUser().getId();

        if (TEMP_DIRECTORY.exists()) {
            logger.info("Temp Directory exists");
        } else {
            if (TEMP_DIRECTORY.mkdirs()) // Returns a boolean, but this has a very low chance of returning false, sooo...
                logger.info("Temp Directory created");
            else {
                logger.error("TEMP DIRECTORY ERROR, NOT CREATED");
                System.exit(-1);
            }
        }
        logger.info("Current temp path: {}", Main.TEMP_DIRECTORY.getAbsolutePath());

        new MusicUtils(new DefaultAudioPlayerManager(), new HashMap<>());

        new CommandRegistry();

        BotStatus.activateBotActivityRoutine();
    }
}
