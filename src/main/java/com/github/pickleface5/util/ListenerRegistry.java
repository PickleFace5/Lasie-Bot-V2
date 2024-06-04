package com.github.pickleface5.util;

import com.github.pickleface5.Main;
import com.github.pickleface5.commands.*;
import com.github.pickleface5.commands.imaging.AbstractCommand;
import com.github.pickleface5.commands.imaging.ChadCommand;
import com.github.pickleface5.commands.imaging.PfpGrabberCommand;
import com.github.pickleface5.commands.music.*;
import com.github.pickleface5.commands.robot.CreditCommand;
import com.github.pickleface5.commands.robot.ServerCommand;
import com.github.pickleface5.commands.robot.PingCommand;
import com.github.pickleface5.commands.tictactoe.TicTacToeCommand;
import com.github.pickleface5.logging.ServerChecker;
import com.github.pickleface5.music.VoiceChannelDisconnectIfAlone;

import ch.qos.logback.classic.Logger;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ListenerRegistry {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(ListenerRegistry.class);

    static {

        // Ping
        slash("ping", "Gets the time taken for Discord to respond to a request.", new PingCommand());
        
        // Credits

        // Server Stats (member count, age, owner, icon, etc)
        slash(Commands.slash("server", "Returns information about the server."), new ServerCommand());

        // Account Stats (age, icon, etc)


        listener(new VoiceChannelDisconnectIfAlone());

        listener(new ServerChecker());

        
        slash("credits", "Credits for Lasie Bot.", new CreditCommand());
        

        slash(Commands.slash("apex", "Returns the stats of an Apex Legends player.")
                .addOption(OptionType.STRING, "subcommand", "Username of the Apex Legends player you want to look up.", true, true)
                .addOption(OptionType.STRING, "username", "Enter username here. (For player statistics only, leave blank otherwise)")
                .addOption(OptionType.STRING, "uid", "Enter player UID here. (REQUIRED for switch players for player stats.)"), new ApexCommand());

        slash("join", "Joins your current voice channel.", new JoinCommand());
        slash("leave", "Leaves the voice channel the bot is connected to.", new LeaveCommand());
        slash(Commands.slash("play", "Plays audio in your voice channel.")
                .addOption(OptionType.STRING, "search", "The URL for your desired audio or the search result.", true), new PlayCommand());
        slash(Commands.slash("volume", "Sets the volume for the music player, default is 100.")
                .addOption(OptionType.INTEGER, "volume", "Volume to set the music player to.", true), new VolumeCommand());
        slash("forceskip", "Immediately skips the current track.", new ForceSkipCommand());
        slash("queue", "Shows the current music queue", new QueueCommand());
        slash("purgequeue", "Purges the entire queue.", new PurgeQueueCommand());
        slash("pause", "Pauses the current song.", new PauseCommand());
        slash("resume", "Resumes the current song.", new ResumeCommand());
        slash("loop", "Toggles looping the current track.", new LoopCommand());
        slash("skip", "Allows you to skip the song when your alone in voice.", new SkipCommand());
        slash(Commands.slash("forward", "Moves the current position of the song forward by the specifed amount.")
                .addOption(OptionType.NUMBER, "duration", "Specified duration to move foward by, in seconds.", true), new ForwardCommand());

        slash(Commands.slash("chad", "Turns you into a gigachad.")
                .addOption(OptionType.USER, "user", "The user to want to make a **king**", false), new ChadCommand());
        slash("abstract", "Creates randomly generated abstract art.", new AbstractCommand());
        slash(Commands.slash("pfpgrabber", "Grabs a users profile photo.")
                .addOption(OptionType.USER, "user", "The user profile photo you want to grab."), new PfpGrabberCommand());

        slash(Commands.slash("minecraft", "Shows a minecraft servers info, such as current players, version, and time.")
                .addOption(OptionType.STRING, "address", "The Minecraft server IP address.", true)
                .addOption(OptionType.BOOLEAN, "bedrock", "If the server is on Minecraft: Bedrock, select this as true."), new MinecraftCommand());

        //registerSlashCommand(Commands.slash("ben", "Asks Ben the Talking Dog a question of your choice.")
                //.addOption(OptionType.STRING, "question", "Your question for the almighty Ben.", true), new BenCommand());

        slash(Commands.slash("tictactoe", "Play Tic Tac Toe against someone.")
                .addOption(OptionType.USER, "opponent", "Opponent you want to play.", true)
                .addOption(OptionType.BOOLEAN, "playeronefirst", "Set this to false if you want the other player to go first."), new TicTacToeCommand());

        slash(Commands.slash("frc", "Grabs FRC team info from The Blue Alliance.")
                .addOption(OptionType.INTEGER, "team", "The Team Number (e.g. 6343, 2471, etc)", true), new FRCCommand());
    }

    public static void slash(String name, String description, ListenerAdapter listener) {
        if (!Main.JDA.getSelfUser().getName().equals("Lasie Bot")) {
            try {
                Objects.requireNonNull(Main.JDA.getGuildById("798332906614423563")).upsertCommand(name, description).queue();
            } catch (NullPointerException | ErrorResponseException exception) {
                logger.error("Test server not found");
            }
        } else {
            Main.JDA.upsertCommand(name, description).queue();
        }
        Main.JDA.addEventListener(listener);
        logger.debug("Added Slash Command {}", name);
    }

    public static void slash(CommandData commandData, ListenerAdapter listener) {
        if (!Main.JDA.getSelfUser().getName().equals("Lasie Bot")) {
            try {
                Objects.requireNonNull(Main.JDA.getGuildById("798332906614423563")).upsertCommand(commandData).queue();
            } catch (ErrorResponseException | NullPointerException exception) {
                logger.error("Test server not found");
            }
        } else {
            Main.JDA.upsertCommand(commandData).queue();
        }
        Main.JDA.addEventListener(listener);
        logger.debug("Added Slash Command {}", commandData.getName());
    }

    public static void listener(ListenerAdapter listener) {
        Main.JDA.addEventListener(listener);
    }

    public static void removeListener(ListenerAdapter listener) {
        Main.JDA.removeEventListener(listener);
    }
}
