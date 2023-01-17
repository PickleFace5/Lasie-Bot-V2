package com.github.pickleface5.user;


import com.github.pickleface5.Main;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MINUTES;

public class BotStatus { //TODO: Automatically get list(?) (see roadmap)
    private static final Random GENERATOR = new Random();
    static final String[] GAME_LIST = {"Spooky's Jumpscare Mansion: HD Renovation", "Hollow Knight", "A Hat In Time", "KARLSON",
            "Hollow Knight: Silksong", "Celeste", "Factorio", "Satisfactory", "Halo: Infinite", "Apex Legends",
            "Fortnite", "Terraria", "Papers, Please", "Valheim", "Overwatch", "ASTRONEER", "No Mans Sky",
            "Friday Night Funkin", "Among Us", "Cuphead", "Fall Guys", "Oxygen Not Included", "People Playground",
            "Satisfactory", "Spore", "Stick Fight: The Game", "UNDERTALE", "Stardew Valley",
            "Ori and the Will of the Wisps", "RUST", "Ori and the Blind Forest", "Titanfall", "Tic Tac Toe",
            "Subnautica", "Subnautica: Below Zero", "Pikuniku", "Cyberpunk 2077", "osu!", "Universe Sandbox",
            "PowerWash Simulator", "Muck", "Bloons TD 6", "Cookie Clicker", "Omori", "Don't Starve",
            "Don't Starve Together", "OneShot", "DELTARUNE", "Redmatch 2", "A Dance of Fire and Ice",
            "Sid Meier's Civilization VI", "Elden Ring", "ROUNDS", "LEGO® Star Wars™: The Skywalker Saga",
            "Forza Horizon 5", "Kerbal Space Program", "Kerbal Space Program 2", "It Takes Two", "EVE Online",
            "God of War", "Just Cause 3", "Halo Infinite", "Splitgate", "Tom Clancy's Rainbow Six® Siege",
            "Gorilla Tag", "Geometry Dash", "Among Us VR", "Who's Your Daddy?!", "Portal", "Portal 2", "Overwatch 2",
            "Kerbal Space Program", "Stray", "Enter the Gungeon", "Bloons TD 5", "ARK: Survival Evolved", "Bloons TD 4",
            "Bloons TD 3", "Bloons TD 2", "Bloons Tower Defense", "Clash of Clans", "Clash Royale",
            "Hatsune Miku: Project Diva X", "Risk of Rain", "Risk of Rain 2",
            "Freddy Fazbear's Pizzeria Simulator", "Splatoon", "Splatoon 2", "Splatoon 3", "Mario Kart Wii",
            "Mario Kart 8", "Wii Sports", "Wii Sports Resort", "Red Dead Redemption 2", "Mario Kart 8 Deluxe",
            "Call of Duty: Black Ops II", "Call of Duty: Modern Warfare II", "Halo Reach", "Goat Simulator",
            "Halo 3: ODST", "LittleBigPlanet", "LittleBigPlanet 2", "LittleBigPlanet 3", "Sackboy: A Big Adventure",
            "ModNation Racers", "Mario + Rabbids Sparks of Hope", "The Legend of Zelda: Breath of the Wild",
            "Super Smash Bros. Ultimate", "Just Shapes and Beats", "PAYDAY 2", "PAYDAY 3", "Roblox", "Minecraft"};

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
                return ActivityType.PLAYING;
            }

            @Nullable
            @Override
            public Timestamps getTimestamps() {
                return null;
            }

            @Nullable
            @Override
            public EmojiUnion getEmoji() {
                return null;
            }
        });
    }
}
