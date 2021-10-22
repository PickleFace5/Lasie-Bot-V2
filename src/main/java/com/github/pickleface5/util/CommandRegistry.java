package com.github.pickleface5.util;

import com.github.pickleface5.Main;
import com.github.pickleface5.commands.*;
import com.github.pickleface5.commands.music.*;
import com.github.pickleface5.music.VoiceChannelDisconnectIfAlone;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class CommandRegistry {
    private static final Logger LOGGER = LogManager.getLogger(CommandRegistry.class);

    static {
        registerEventListener(new VoiceChannelDisconnectIfAlone());
        registerSlashCommand("ping", "Returns the ping for the bot.", new PingCommand());
        registerSlashCommand("info", "Credits for Lasie Bot.", new InfoCommand());
        registerSlashCommand("members", "Returns a count of members in the server.", new MembersCommand());
        registerSlashCommand(new CommandData("apex", "Returns the stats of an Apex Legends player.")
                .addOption(OptionType.STRING, "username", "Username of the Apex Legends player you want to look up."), new ApexCommand());
        registerSlashCommand("join", "Joins your current voice channel.", new JoinCommand());
        registerSlashCommand("leave", "Leaves the voice channel the bot is connected to.", new LeaveCommand());
        registerSlashCommand(new CommandData("play", "Plays audio in your voice channel.")
                .addOption(OptionType.STRING, "query", "The URL or query for your desired audio."), new PlayCommand());
        registerSlashCommand("forceskip", "Immediately skips the current track.", new ForceSkipCommand());
        registerSlashCommand("purgequeue", "Purges the entire queue.", new PurgeQueueCommand());
        registerSlashCommand("pause", "Pauses the current song.", new PauseCommand());
        registerSlashCommand("resume", "Resumes the current song.", new ResumeCommand());
    }

    // It's a bad idea to use the bots name, but it's okay since it will never get renamed. Ever.
    private static void registerSlashCommand(String name, String description, ListenerAdapter listener) {
        if (!Main.JDA.getSelfUser().getName().equals("Lasie Bot")) {
            Objects.requireNonNull(Main.JDA.getGuildById("798332906614423563")).upsertCommand(name, description).queue();
        } else {
            Main.JDA.upsertCommand(name, description).queue();
        }
        Main.JDA.addEventListener(listener);
        LOGGER.debug("Added Slash Command {} to {}", name, Main.JDA.getSelfUser().getName());
    }

    private static void registerSlashCommand(CommandData commandData, ListenerAdapter listener) {
        if (!Main.JDA.getSelfUser().getName().equals("Lasie Bot")) {
            Objects.requireNonNull(Main.JDA.getGuildById("798332906614423563")).upsertCommand(commandData).queue();
        } else {
            Main.JDA.upsertCommand(commandData).queue();
        }
        Main.JDA.addEventListener(listener);
        LOGGER.debug("Added Slash Command {} to {}", commandData.getName(), Main.JDA.getSelfUser().getName());
    }

    private static void registerEventListener(ListenerAdapter listener) {
        Main.JDA.addEventListener(listener);
    }
}
