package com.github.pickleface5.user;


import com.github.pickleface5.Main;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.RichPresence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MINUTES;

public class BotStatus {
    private static final Random GENERATOR = new Random();
    static final String[] GAME_LIST = {"Spooky's Jumpscare Mansion: HD Renovation", "Hollow Knight", "A Hat In Time", "KARLSON",
            "Hollow Knight: Silksong", "Celeste", "Factorio", "Satisfactory", "Halo: Infinite", "Apex Legends",
            "Fortnite", "Terraria", "Papers, Please", "Valheim", "Overwatch", "ASTRONEER", "No Mans Sky",
            "Friday Night Funkin", "Among Us", "Cuphead", "Fall Guys", "Oxygen Not Included", "People Playground",
            "Satisfactory", "Spore", "Stick Fight: The Game", "UNDERTALE", "Stardew Valley",
            "Ori and the Will of the Wisps", "RUST", "Ori and the Blind Forest", "Titanfall", "Tic Tac Toe",
            "Subnautica", "Subnautica: Below Zero", "Pikuniku", "Cyberpunk 2077", "osu!", "Universe Sandbox",
            "PowerWash Simulator", "Muck", "Bloons Tower Defense 6", "Cookie Clicker"};

    private static final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public static void activateBotActivityRoutine() {
        final Runnable method = BotStatus::setBotActivity;
        scheduler.scheduleAtFixedRate(method, 0, 10, MINUTES);
    }

    public static void setBotActivity() {
        Main.JDA.getPresence().setActivity(new Activity() {
            @Override
            public boolean isRich() {
                return false;
            }

            @Nullable
            @Override
            public RichPresence asRichPresence() {
                return null;
            }

            @NotNull
            @Override
            public String getName() {
                return GAME_LIST[GENERATOR.nextInt(GAME_LIST.length)];
            }

            @Nullable
            @Override
            public String getUrl() {
                return null;
            }

            @NotNull
            @Override
            public ActivityType getType() {
                return ActivityType.DEFAULT;
            }

            @Nullable
            @Override
            public Timestamps getTimestamps() {
                return null;
            }

            @Nullable
            @Override
            public Emoji getEmoji() {
                return null;
            }
        });
    }
}
