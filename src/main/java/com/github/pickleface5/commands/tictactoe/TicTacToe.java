package com.github.pickleface5.commands.tictactoe;

import com.github.pickleface5.util.CommandRegistry;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.NotNull;

import java.util.*;

enum Tiles {
    PLAYER_ONE(1, ":regional_indicator_x:"),
    PLAYER_TWO(2, ":o2:"),
    EMPTY(0, ":orange_square:");

    private final String emoji;
    private final int id;

    Tiles(int id, String emoji) {
        this.emoji = emoji;
        this.id = id;
    }

    public String getEmoji() {
        return this.emoji;
    }

    public int getId() {
        return this.id;
    }
}

public class TicTacToe extends ListenerAdapter {
    InteractionHook hook;
    User player1;
    User player2;
    boolean player1Turn = true;
    boolean gameOver = false;
    private final int[][] WIN_CONDITIONS = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};
    static final ArrayList<String> STARTING_TABLE = new ArrayList<>(Collections.nCopies(9, Tiles.EMPTY.getEmoji()));
    ArrayList<String> table;
    Timer timer;

    public TicTacToe(InteractionHook hook, User player1, User player2) {
        this.hook = hook;
        this.player1 = player1;
        this.player2 = player2;
        this.table = (ArrayList<String>) STARTING_TABLE.clone();
        this.timer = new Timer();

        this.updateTable();
    }

    // If we already have a game with the same players going, delete this instance.
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!(event.getName().equals("tictactoe") && event.getUser().equals(player1) && Objects.requireNonNull(event.getOption("opponent")).getAsUser().equals(player2)))
            return;
        CommandRegistry.removeEventListener(this);
    }

    void updateTable() {
        this.hook.editOriginal(this.getMessageTable()).queue();

        if (this.gameOver) return;
        User playerWin = checkForWin();
        if (playerWin != null) {
            endGame("**" + playerWin.getName() + " wins!!!**");
        } else if (isDraw()) {
            endGame("It's a draw!");
        }
    }

    User checkForWin() {
        for (int[] list : WIN_CONDITIONS) {
            int trues = 0;
            for (int j : list) {
                if (this.table.get(j).equals(Tiles.PLAYER_ONE.getEmoji())) trues++;
                if (trues >= 3) break;
            }
            if (trues >= 3) return this.player1;
        }
        for (int[] list : WIN_CONDITIONS) {
            int trues = 0;
            for (int j : list) {
                if (this.table.get(j).equals(Tiles.PLAYER_TWO.getEmoji())) trues++;
                if (trues >= 3) break;
            }
            if (trues >= 3) return this.player2;
        }
        return null;
    }

    boolean isDraw() {
        return !table.contains(Tiles.EMPTY.getEmoji());
    }

    String getMessageTable() {
        StringBuilder buffer = new StringBuilder();

        int runs = 1;
        for (String s : this.table) {
            buffer.append(s);
            if (runs % 3 == 0) buffer.append("\n");
            runs++;
        }
        return buffer.toString();
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("TTTTurnDone") && isInGame(event.getUser()) && !this.gameOver) {
            if (event.getUser().equals(this.player1) && this.player1Turn) {
                int num = Integer.parseInt(event.getValues().get(0)) - 1;
                if (!(Objects.equals(this.table.get(num), Tiles.EMPTY.getEmoji()))) {
                    event.reply("You can't go there!").setEphemeral(true).queue();
                    return;
                }
                this.table.set(num, Tiles.PLAYER_ONE.getEmoji());
                this.player1Turn = !this.player1Turn;
                if (this.player2.isBot() || this.player2.isSystem()) {
                    this.lasieTurn();
                    this.player1Turn = true;
                }
            } else if (event.getUser().equals(this.player2) && !this.player1Turn) {
                int num = Integer.parseInt(event.getValues().get(0)) - 1;
                if (!(Objects.equals(this.table.get(num), Tiles.EMPTY.getEmoji()))) {
                    event.reply("You can't go there!").setEphemeral(true).queue();
                    return;
                }
                this.table.set(num, Tiles.PLAYER_TWO.getEmoji());
                this.player1Turn = !this.player1Turn;
            } else {
                event.reply("It's not your turn!").setEphemeral(true).queue();
                return;
            }
            this.updateTable();
            event.reply("Nice move!").setEphemeral(true).queue();
        } else {
            event.reply("This isn't your game!").setEphemeral(true).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("TTTResign")) {
            if (event.getUser().equals(this.player2)) {
                endGame(this.player2.getName() + " has resigned, **" + this.player1.getName() + " wins!!!**");
            } else if (event.getUser().equals(this.player1)) {
                endGame(this.player1.getName() + " has resigned, **" + this.player2.getName() + " wins!!!**");
            } else {
                event.reply("This isn't your game!").setEphemeral(true).queue();
            }
        } else if (event.getComponentId().equals("TTTHelp")) {
            event.reply("To take your turn, select a number between 1 and 9. The order goes from top to bottom, left to right (1 is top left, 2 is top center, 9 is bottom right, etc). *The player who ran the /tictactoe command goes first.*").setEphemeral(true).queue();
        }
    }

    void endGame(String msg) {
        this.gameOver = true;
        this.hook.sendMessage(msg).queue();
        CommandRegistry.removeEventListener(this);
    }

    // lil secret (plus no one wants to help debug a discord bot lol)
    void lasieTurn() {
        if (!(checkForWin() == null)) return;
        int index = TicTacToeAutoPlay.takeTurn(table);
        if (index != -1) this.table.set(index, Tiles.PLAYER_TWO.getEmoji());
        this.updateTable();
    }

    private boolean isInGame(User user) {
        return user.equals(this.player1) || user.equals(this.player2);
    }

    @NotNull
    public Long[] getPlayers() {
        return new Long[]{Long.parseLong(this.player1.getId()), Long.parseLong(this.player2.getId())};
    }
}
