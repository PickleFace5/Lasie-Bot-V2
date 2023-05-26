package com.github.pickleface5.commands;

import com.github.pickleface5.util.CommandRegistry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class TicTacToeCommand extends ListenerAdapter {
    static final Logger LOGGER = LogManager.getLogger(TicTacToeCommand.class);

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("tictactoe")) return;

        User opponent = Objects.requireNonNull(event.getOption("opponent")).getAsUser();

        CommandRegistry.registerEventListener(new TicTacToe(event.getHook(), event.getUser(), opponent));

        event.reply("**Game Started**: *" + event.getUser().getName() + "* VS *" + opponent.getName() + "*!!!")
                .addActionRow(StringSelectMenu.create("TTTTurnDone")
                        .setPlaceholder("Pick where you want to go here on your turn...")
                        .addOption("1", "1")
                        .addOption("2", "2")
                        .addOption("3", "3")
                        .addOption("4", "4")
                        .addOption("5", "5")
                        .addOption("6", "6")
                        .addOption("7", "7")
                        .addOption("8", "8")
                        .addOption("9", "9").build())
                .addActionRow(Button.secondary("TTTHelp", "Help"), Button.danger("TTTResign", "Resign")).queue();
    }
}


