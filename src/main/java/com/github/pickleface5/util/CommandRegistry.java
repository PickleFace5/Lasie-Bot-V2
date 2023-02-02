package com.github.pickleface5.util;

import com.github.pickleface5.Main;
import com.github.pickleface5.commands.*;
import com.github.pickleface5.commands.imaging.AbstractCommand;
import com.github.pickleface5.commands.imaging.ChadPfpCommand;
import com.github.pickleface5.commands.imaging.PfpGrabberCommand;
import com.github.pickleface5.commands.music.*;
import com.github.pickleface5.logging.ServerChecker;
import com.github.pickleface5.music.VoiceChannelDisconnectIfAlone;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Objects;

public class CommandRegistry {
    private static final Logger LOGGER = LogManager.getLogger(CommandRegistry.class);

    static {
        registerEventListener(new VoiceChannelDisconnectIfAlone());
        registerEventListener(new ServerChecker());
        registerSlashCommand("ping", "Returns the ping for the bot.", new PingCommand());
        registerSlashCommand("info", "Credits for Lasie Bot.", new InfoCommand());
        registerSlashCommand("members", "Returns a count of members in the server.", new MembersCommand());
        registerSlashCommand(Commands.slash("apex", "Returns the stats of an Apex Legends player.")
                .addOption(OptionType.STRING, "subcommand", "Username of the Apex Legends player you want to look up.", true, true)
                .addOption(OptionType.STRING, "username", "Enter username here. (For player statistics only, leave blank otherwise)")
                .addOption(OptionType.STRING, "uid", "Enter player UID here. (REQUIRED for switch players for player stats.)"), new ApexCommand());
        registerSlashCommand("join", "Joins your current voice channel.", new JoinCommand());
        registerSlashCommand("leave", "Leaves the voice channel the bot is connected to.", new LeaveCommand());
        registerSlashCommand(Commands.slash("play", "Plays audio in your voice channel.")
                .addOption(OptionType.STRING, "search", "The URL for your desired audio or the search result.", true), new PlayCommand());
        registerSlashCommand("forceskip", "Immediately skips the current track.", new ForceSkipCommand());
        registerSlashCommand("queue", "Shows the current music queue", new QueueCommand());
        registerSlashCommand("purgequeue", "Purges the entire queue.", new PurgeQueueCommand());
        registerSlashCommand("pause", "Pauses the current song.", new PauseCommand());
        registerSlashCommand("resume", "Resumes the current song.", new ResumeCommand());
        registerSlashCommand("loop", "Toggles looping the current track.", new LoopCommand());
        registerSlashCommand("skip", "Allows you to skip the song when your alone in voice.", new SkipCommand());
        try {
            registerSlashCommand(Commands.slash("chad", "Turns you into a gigachad.")
                    .addOption(OptionType.USER, "user", "The user to want to make a **king**", false), new ChadPfpCommand());
        } catch (IOException e) {
            LOGGER.error("chad could not be registered: IOException returned.");
        }
        registerSlashCommand("abstract", "Creates randomly generated abstract art.", new AbstractCommand());
        registerSlashCommand(Commands.slash("pfpgrabber", "Grabs a users profile photo.")
                .addOption(OptionType.USER, "user", "The user profile photo you want to grab."), new PfpGrabberCommand());
        registerSlashCommand(Commands.slash("minecraft", "Shows a minecraft servers info, such as current players, version, and time.")
                .addOption(OptionType.STRING, "address", "The Minecraft server IP address.", true)
                .addOption(OptionType.BOOLEAN, "bedrock", "If the server is on Minecraft: Bedrock, select this as true."), new MinecraftCommand());
        registerSlashCommand(Commands.slash("ben", "Asks Ben the Talking Dog a question of your choice.")
                .addOption(OptionType.STRING, "question", "Your question for the almighty Ben.", true), new BenCommand());
    }

    // It's a bad idea to use the bots name, but it's okay since it will never get renamed. Ever.
    // getGuildById("798332906614423563") sends the commands only to the debug guild, so I don't have to wait an hour.
    private static void registerSlashCommand(String name, String description, ListenerAdapter listener) {
        if (!Main.JDA.getSelfUser().getName().equals("Lasie Bot")) {
            try {
                Objects.requireNonNull(Main.JDA.getGuildById("798332906614423563")).upsertCommand(name, description).queue();
            } catch (NullPointerException | ErrorResponseException exception) {
                LOGGER.error("Test server not found");
            }
        } else {
            Main.JDA.upsertCommand(name, description).queue();
        }
        Main.JDA.addEventListener(listener);
        LOGGER.info("Added Slash Command {} to {}", name, Main.JDA.getSelfUser().getName());
    }

    private static void registerSlashCommand(CommandData commandData, ListenerAdapter listener) {
        if (!Main.JDA.getSelfUser().getName().equals("Lasie Bot")) {
            try {
                Objects.requireNonNull(Main.JDA.getGuildById("798332906614423563")).upsertCommand(commandData).queue();
            } catch (ErrorResponseException | NullPointerException exception) {
                LOGGER.error("Test server not found");
            }
        } else {
            Main.JDA.upsertCommand(commandData).queue();
        }
        Main.JDA.addEventListener(listener);
        LOGGER.info("Added Slash Command {} to {}", commandData.getName(), Main.JDA.getSelfUser().getName());
    }

    private static void registerEventListener(ListenerAdapter listener) {
        Main.JDA.addEventListener(listener);
    }
}
