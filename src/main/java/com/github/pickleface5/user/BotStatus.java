package com.github.pickleface5.user;


import com.github.pickleface5.Main;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MINUTES;

public class BotStatus { //TODO: Automatically get list(?) (see roadmap)
    private static final Random GENERATOR = new Random();
    static final String[] GAME_LIST = {"5D Chess", "A Dance of Fire and Ice", "A Hat In Time",
            "ARK: Survival Evolved", "ASTRONEER", "Among Us", "Among Us VR", "Angry Birds", "Angry Birds 2",
            "Apex Legends", "Battle Cats", "Beat Saber", "Blender", "Bloons TD 2", "Bloons TD 3", "Bloons TD 4",
            "Bloons TD 5", "Bloons TD 6", "Bloons TD Battles", "Bloons Tower Defense", "Blush Blush",
            "Call of Duty: Black Ops II", "Call of Duty: Modern Warfare II", "Celeste", "Checkers", "Chess",
            "Clash Royale", "Clash of Clans", "Conan Exiles", "Cookie Clicker", "Cool Math Games", "Crush Crush",
            "Cuphead", "Cut The Rope", "Cyberpunk 2077", "DELTARUNE", "Dark Tide", "Doki Doki Literature Club",
            "Don't Starve", "Don't Starve Together", "EVE Online", "Elden Ring", "Enter the Gungeon", "FPS Chess",
            "Factorio", "Fall Guys", "Fallout 4", "Fallout Shelter", "Fallout: New Vegas", "Fortnite",
            "Forza Horizon 5", "Freddy Fazbear's Pizzeria Simulator", "Friday Night Funkin", "Garry's Mod",
            "Geometry Dash", "Goat Simulator", "Goat Simulator 3", "God of War", "Google Snake", "Gorilla Tag",
            "Halo 3: ODST", "Halo Infinite", "Halo Reach", "Halo: Infinite", "Hatsune Miku: Project Diva X",
            "Hollow Knight", "Hollow Knight: Silksong", "Hue", "It Takes Two", "Jetpack Joyride", "Jetpack Joyride 2",
            "Just Cause 3", "Just Shapes and Beats", "KARLSON", "Kerbal Space Program", "Kerbal Space Program 2",
            "LEGO Star Wars: The Skywalker Saga", "LittleBigPlanet", "LittleBigPlanet 2", "LittleBigPlanet 3",
            "Mario + Rabbids Sparks of Hope", "Mario Kart 8", "Mario Kart 8 Deluxe", "Mario Kart Wii", "Minecraft",
            "ModNation Racers", "Muck", "No Mans Sky", "OMORI", "Omori", "OneShot", "Ori and the Blind Forest",
            "Ori and the Will of the Wisps", "Overwatch", "Overwatch 2", "Oxygen Not Included", "PAC-MAN", "PAYDAY 2",
            "PAYDAY 3", "Papers, Please", "People Playground", "Pikuniku", "Portal", "Portal 2",
            "PowerWash Simulator", "ROUNDS", "RUST", "Red Dead Redemption 2", "Redmatch 2", "Risk of Rain",
            "Risk of Rain 2", "Roblox", "SCP: Secret Laboratory", "SIMS 4", "Sackboy: A Big Adventure", "Satisfactory",
            "Sea of Thieves", "Sex With Hitler", "Sex With Stalin", "Sid Meier's Civilization VI", "Smashy Road 2",
            "Smashy Road: Wanted", "Sons Of The Forest", "Splatoon", "Splatoon 2", "Splatoon 3", "Splitgate",
            "Spooky's Jumpscare Mansion: HD Renovation", "Spore", "Stardew Valley", "Starfield",
            "Stick Fight: The Game", "Stray", "Subnautica", "Subnautica: Below Zero", "Subway Surfers",
            "Super Smash Bros. Ultimate", "TABS", "TETRIS 99", "Terraria", "The Bible",
            "The Geneva Conventions of August 12th, 1949", "The Legend of Zelda: Breath of the Wild", "Tic Tac Toe",
            "Titanfall", "Tom Clancy's Rainbow Six Siege", "UNDERTALE", "Universe Sandbox", "Valheim", "Vermintide",
            "Vermintide 2", "Wallpaper Engine", "War Thunder", "Warhammer 40,000", "Who's Your Daddy?!", "Wii Sports",
            "Wii Sports Resort", "XCOM", "XCOM 2", "XCOM EW", "osu!", "Rain World"};

    private static final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public static void activateBotActivityRoutine() {
        final Runnable method = BotStatus::setBotActivity;
        scheduler.scheduleAtFixedRate(method, 0, 10, MINUTES);
    }

    private static void setBotActivity() {
        Main.JDA.getPresence().setActivity(new Activity() {
            @Override
            public boolean isRich() {
                return false;
            }

            @Override
            public RichPresence asRichPresence() {
                return null;
            }

            @Override
            public String getName() {
                return GAME_LIST[GENERATOR.nextInt(GAME_LIST.length)];
            }

            @Override
            public String getState() {
                return null;
            }

            @Override
            public String getUrl() {
                return null;
            }

            @Override
            public ActivityType getType() {
                return ActivityType.PLAYING;
            }

            @Override
            public Timestamps getTimestamps() {
                return null;
            }

            @Override
            public EmojiUnion getEmoji() {
                return null;
            }

            @Override
            public Activity withState(String state) {
                return null;
            }
        });
    }
}
