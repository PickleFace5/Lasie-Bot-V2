package com.github.pickleface5.commands.tictactoe;

import com.github.pickleface5.util.ListenerRegistry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.Objects;

public class TicTacToeCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("tictactoe")) return;

        User opponent = Objects.requireNonNull(event.getOption("opponent")).getAsUser();
        if (event.getUser().equals(opponent)) {
            event.reply("You can't play against yourself!").setEphemeral(true).queue();
            return;
        }

        boolean player1First = true;
        if (event.getOption("playeronefirst") != null)
            if (!Objects.requireNonNull(event.getOption("playeronefirst")).getAsBoolean()) {
                player1First = false;
            }

        ListenerRegistry.listener(new TicTacToe(event.getHook(), event.getUser(), opponent, player1First));

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


