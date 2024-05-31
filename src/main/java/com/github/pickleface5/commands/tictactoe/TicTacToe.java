package com.github.pickleface5.commands.tictactoe;

import com.github.pickleface5.Main;
import com.github.pickleface5.util.ListenerRegistry;

import ch.qos.logback.classic.Logger;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Timer;

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
    private static final Logger logger = (Logger) LoggerFactory.getLogger(TicTacToe.class);

    InteractionHook hook;
    User player1;
    User player2;
    boolean player1Turn = true;
    boolean gameOver = false;
    private final int[][] WIN_CONDITIONS = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};
    static final ArrayList<String> STARTING_TABLE = new ArrayList<>(Collections.nCopies(9, Tiles.EMPTY.getEmoji()));
    ArrayList<String> table;
    Timer timer;
    Tiles botTile;
    boolean player1first;

    @SuppressWarnings("unchecked")
    public TicTacToe(InteractionHook hook, User player1, User player2, boolean player1first) {
        this.hook = hook;
        this.player1 = player1;
        this.player2 = player2;
        this.table = (ArrayList<String>) STARTING_TABLE.clone();
        this.timer = new Timer();
        this.player1first = player1first;

        this.updateTable();
        if (!player1first) {
            this.botTile = Tiles.PLAYER_ONE;
            this.lasieTurn(botTile);
        } else {
            this.botTile = Tiles.PLAYER_TWO;
        }
    }

    // If we already have a game with the same players going, delete this instance.
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("tictactoe")) return;
        if (event.getUser().equals(player1) && Objects.requireNonNull(event.getOption("opponent")).getAsUser().equals(player2)) deleteGame();
    }

    void updateTable() {
        this.hook.editOriginal(this.getMessageTable()).queue();

        if (this.gameOver) return;
        User playerWin = checkForWin();
        if (playerWin != null) {
            if (playerWin.getId().equals(Main.BOT_USER_ID)) endGame("**I win!!!**");
            else endGame("**" + playerWin.getName() + " wins!!!**");
        } else if (isDraw()) {
            endGame("It's a draw!");
        }
    }

    void deleteGame() {
        this.hook.editOriginal("Game deleted: New game started between player 1 and 2").queue();
        this.gameOver = true;
        try {
            ListenerRegistry.removeListener(this);
        } catch (IllegalArgumentException ignored) {
            logger.debug("EventListener for \"{} ||| {}\" is already deleted, ignoring...", this.player1.getId(), this.player2.getId());
        }
    }

    User checkForWin() {
        for (int[] list : WIN_CONDITIONS) {
            int trues = 0;
            for (int j : list) {
                if (this.table.get(j).equals(Tiles.PLAYER_ONE.getEmoji())) trues++;
                if (trues >= 3) break;
            }
            if (trues >= 3) return (this.player1first) ? this.player1 : this.player2;
        }
        for (int[] list : WIN_CONDITIONS) {
            int trues = 0;
            for (int j : list) {
                if (this.table.get(j).equals(Tiles.PLAYER_TWO.getEmoji())) trues++;
                if (trues >= 3) break;
            }
            if (trues >= 3) return (this.player1first) ? this.player2 : this.player1;
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
                this.table.set(num, (this.player1first) ? Tiles.PLAYER_ONE.getEmoji() : Tiles.PLAYER_TWO.getEmoji());
                this.player1Turn = !this.player1Turn;
                if (this.player2.isBot() || this.player2.isSystem()) {
                    this.lasieTurn(this.botTile);
                    this.player1Turn = true;
                }
            } else if (event.getUser().equals(this.player2) && !this.player1Turn) {
                int num = Integer.parseInt(event.getValues().get(0)) - 1;
                if (!(Objects.equals(this.table.get(num), Tiles.EMPTY.getEmoji()))) {
                    event.reply("You can't go there!").setEphemeral(true).queue();
                    return;
                }
                this.table.set(num, (this.player1first) ? Tiles.PLAYER_TWO.getEmoji() : Tiles.PLAYER_ONE.getEmoji());
                this.player1Turn = !this.player1Turn;
            } else {
                event.reply("It's not your turn!").setEphemeral(true).queue();
                return;
            }
            event.deferEdit().queue();
            this.updateTable();
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
            event.reply("To take your turn, select a number between 1 and 9. The order goes from top to bottom, left to right (1 is top left, 2 is top center, 9 is bottom right, etc).").setEphemeral(true).queue();
        }
    }

    void endGame(String msg) {
        this.gameOver = true;
        this.hook.sendMessage(msg).queue();
        ListenerRegistry.removeListener(this);
    }

    // lil secret (plus no one wants to help debug a discord bot lol)
    void lasieTurn(Tiles botTile) {
        if (!(checkForWin() == null)) return;
        int index = TicTacToeAutoPlay.takeTurn(table, botTile);
        if (index != -1) this.table.set(index, botTile.getEmoji());
        this.updateTable();
    }

    private boolean isInGame(User user) {
        return user.equals(this.player1) || user.equals(this.player2);
    }
}
