package com.github.pickleface5.commands.tictactoe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;

public class TicTacToeAutoPlay {
    private static final Logger LOGGER = LogManager.getLogger(TicTacToeAutoPlay.class);
    private static final int[][] WIN_CONDITIONS = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};

    public static int takeTurn(ArrayList<String> table) {
        ArrayList<Integer> intTable = convertTableToInt(table);

        // 1. Win
        // 3. Block opponent from winning
        // 4. Create a fork to let us win next turn
        // 5. Place anywhere (Will be a draw)

        if (placeWin(intTable) != -1) return placeWin(intTable);
        else if (blockWin(intTable) != -1) return blockWin(intTable);
        else if (placeFork(intTable) != -1) return placeFork(intTable);
        else return priorityPlace(intTable);
    }

    static ArrayList<Integer> convertTableToInt(ArrayList<String> table) {
        ArrayList<Integer> intTable = new ArrayList<>(9);
        for (String s: table) {
            if (s.equals(Tiles.EMPTY.getEmoji())) intTable.add(Tiles.EMPTY.getId());
            else if (s.equals(Tiles.PLAYER_ONE.getEmoji())) intTable.add(Tiles.PLAYER_ONE.getId());
            else intTable.add(Tiles.PLAYER_TWO.getId());
        }
        return intTable;
    }

    private static int placeWin(ArrayList<Integer> table) {
        for (int[] ints: WIN_CONDITIONS) {
            int computerSpots = 0;
            int emptySpots = 0;
            int lastEmptySpot = 0;
            for (int spot: ints) {
                if (table.get(spot) == Tiles.PLAYER_TWO.getId()) {
                    computerSpots++;
                }
                if (table.get(spot) == Tiles.EMPTY.getId()) {
                    emptySpots++;
                    lastEmptySpot = spot;
                }
            }

            if (computerSpots == 2 && emptySpots == 1) {
                return lastEmptySpot;
            }
        }

        return -1;
    }

    private static int blockWin(ArrayList<Integer> table) {
        for (int[] ints: WIN_CONDITIONS) {
            int computerSpots = 0;
            int emptySpots = 0;
            int lastEmptySpot = 0;
            for (int spot: ints) {
                if (table.get(spot) == Tiles.PLAYER_ONE.getId()) {
                    computerSpots++;
                } else if (table.get(spot) == Tiles.EMPTY.getId()) {
                    emptySpots++;
                    lastEmptySpot = spot;
                }
            }

            if (computerSpots == 2 && emptySpots == 1) {
                return lastEmptySpot;
            }
        }

        return -1;
    }

    private static int placeFork(ArrayList<Integer> table) {
        // Create list of indexes that are currently empty
        // For each index, test if there's 2 wins on the next move on that board, then return the spot to go to create the fork
        ArrayList<Integer> emptyIndex = new ArrayList<>(9);
        int index = 0;
        for (int i : table) {
            if (i == Tiles.EMPTY.getId()) {
                emptyIndex.add(index);
            }
            index++;
        }
        LOGGER.trace("Found {} possible placements: {}", emptyIndex.size(), emptyIndex.toString());

        for (int i : emptyIndex) {
             LOGGER.trace("--- TESTING INDEX #{} ---", i);
             ArrayList<Integer> testTable;
             testTable = table;
             testTable.set(i, Tiles.PLAYER_TWO.getId());
             LOGGER.trace("TEST TABLE: {}", testTable.toString());

             int possibleWins = 0;
             for (int[] condition : WIN_CONDITIONS) {
                 int computerSpots = 0;
                 int emptySpots = 0;
                 int playerSpots = 0;
                 for (int spot : condition) {
                     if (testTable.get(spot) == Tiles.PLAYER_TWO.getId()) computerSpots++;
                     else if (testTable.get(spot) == Tiles.EMPTY.getId()) emptySpots++;
                     else playerSpots++;
                 }

                 if (computerSpots == 2 && emptySpots == 1 && playerSpots == 0) {
                     LOGGER.trace("Comp. spots: {}, Empty spots: {}, Player spots: {} on win condition {}", computerSpots, emptySpots, playerSpots, Arrays.toString(condition));
                     possibleWins++;
                 } else {
                     LOGGER.trace("Win condition {} doesn't give win", Arrays.toString(condition));
                 }
             }
             LOGGER.trace("Possible wins found by placing at index #{}: {}", i, possibleWins);
             if (possibleWins >= 2) return i;
             testTable.set(i, Tiles.EMPTY.getId());
        }
        return -1;
    }

    private static int priorityPlace(ArrayList<Integer> table) {
        // Prioritizes middle, then corners, then anywhere else
        int[] priority = {4, 0, 2, 6, 8, 1, 3, 5, 7};

        for (int i : priority) {
            if (table.get(i).equals(Tiles.EMPTY.getId())) {
                return i;
            }
        }
        return -1;
    }
}
