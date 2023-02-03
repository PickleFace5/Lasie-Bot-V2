package com.github.pickleface5.commands;

import com.github.pickleface5.util.EmbedUtils;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONException;
import kong.unirest.json.JSONObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

enum Platforms {
    PC,
    PS4,
    X1,
    SWITCH
}

public class ApexCommand extends ListenerAdapter {

    private final String APEX_TOKEN = System.getenv("APEX_TOKEN");
    private static final Logger logger = LogManager.getLogger(ApexCommand.class);
    private final String[] OPTIONS = new String[]{"Player Statistics", "Map Rotation", "Predator Requirements"};

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("apex") && event.getFocusedOption().getName().equals("subcommand")) { // (Yes, this is taken from the jda docs :p)
            List<Command.Choice> options = Stream.of(OPTIONS)
                    .filter(word -> word.startsWith(event.getFocusedOption().getValue())) // only display words that start with the user's current input
                    .map(word -> new Command.Choice(word, word)) // map the words to choices
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("apex")) return;
        if (Objects.requireNonNull(event.getOption("subcommand")).getAsString().equals("Player Statistics")) {
            String playerUsername;
            String playerUID = null;
            try {
                playerUsername = Objects.requireNonNull(event.getOption("username")).getAsString();
            } catch (NullPointerException exception) {
                playerUsername = null;
                try {
                    playerUID = Objects.requireNonNull(event.getOption("uid")).getAsString();
                } catch (NullPointerException exception1) {
                    event.reply("You need to specify a username or UID!").setEphemeral(true).queue();
                    return;
                }
            }
            event.deferReply(false).queue();
            HttpResponse<JsonNode> jsonResponse = null;
            String playerPlatform = "";
            String jsonPlayerStatsPrefix;
            boolean isError = false;
            if (playerUsername != null) {
                jsonPlayerStatsPrefix = "https://api.mozambiquehe.re/bridge?auth=" + APEX_TOKEN + "&player=" + playerUsername + "&platform=";
                for (int i = 0; i < Platforms.values().length - 1; i++) {
                    playerPlatform = Platforms.values()[i].toString();
                    logger.debug("Searching player {} on {}...", playerUsername, playerPlatform);
                    jsonResponse = Unirest.get(jsonPlayerStatsPrefix + playerPlatform).asJson();
                    if (isError(jsonResponse)) {
                        if (i == Platforms.values().length) isError = true;
                    } else {
                        break;
                    }
                }
            } else {
                jsonPlayerStatsPrefix = "https://api.mozambiquehe.re/bridge?auth=" + APEX_TOKEN + "&uid=" + playerUID + "&platform=";
                for (int i = 0; i < Platforms.values().length; i++) {
                    playerPlatform = Platforms.values()[i].toString();
                    logger.debug("Searching player {} on {}...", playerUID, playerPlatform);
                    jsonResponse = Unirest.get(jsonPlayerStatsPrefix + playerPlatform).asJson();
                    if (isError(jsonResponse)) {
                        if (i == Platforms.values().length) isError = true;
                    } else {
                        break;
                    }
                }
            }
            assert jsonResponse != null;
            if (isError) {
                sendErrorMessage(jsonResponse, event);
                return;
            } else {
                logger.debug("Player stats found on {}", playerPlatform);
                logger.trace("({})", jsonPlayerStatsPrefix + playerPlatform);
            }

            assert jsonResponse != null;
            JSONObject playerGlobalStats = jsonResponse.getBody().getObject().getJSONObject("global");
            JSONObject playerRealtimeStats = jsonResponse.getBody().getObject().getJSONObject("realtime");
            JSONArray playerLegendData = jsonResponse.getBody().getObject().getJSONObject("legends").getJSONObject("selected").getJSONArray("data");
            playerUsername = playerGlobalStats.getString("name");
            String playerLevel = playerGlobalStats.getString("level");
            String playerNextLevel = playerGlobalStats.getString("toNextLevelPercent");
            String playerEmbedUID = playerGlobalStats.getString("uid");
            String playerSelectedLegend = playerRealtimeStats.getString("selectedLegend");
            String currentState = playerRealtimeStats.getString("currentStateAsText");
            JSONObject battleRoyaleStats = playerGlobalStats.getJSONObject("rank");
            String normalRankName = battleRoyaleStats.getString("rankName");
            String normalRankDiv = battleRoyaleStats.getString("rankDiv");
            String normalRankScore = battleRoyaleStats.getString("rankScore");
            JSONObject arenaStats = playerGlobalStats.getJSONObject("arena");
            String arenaName = arenaStats.getString("rankName");
            String arenaDiv = arenaStats.getString("rankDiv");
            String arenaScore = arenaStats.getString("rankScore");
            JSONObject battlePassStats = playerGlobalStats.getJSONObject("battlepass");
            String battlePassLevel = battlePassStats.getString("level");
            String rankImage = Integer.parseInt(normalRankScore) >= Integer.parseInt(arenaScore) ? battleRoyaleStats.getString("rankImg") : arenaStats.getString("rankImg");

            if (normalRankDiv.equals("0")) normalRankDiv = "";
            if (arenaDiv.equals("0")) arenaDiv = "";
            EmbedBuilder statsEmbed = new EmbedBuilder().setColor(EmbedUtils.EMBED_COLOR);
            statsEmbed.setTitle(playerUsername + " (" + playerPlatform + ")");
            statsEmbed.setThumbnail(rankImage);
            statsEmbed.addField("General", "**Status:** " + currentState + "\n**Level:** " + playerLevel + " *(" + playerNextLevel + "%)* \n **Selected Legend:** " + playerSelectedLegend + "\n **BP Level:** " + battlePassLevel, true);
            statsEmbed.addField("BR", "**" + normalRankName + " " + normalRankDiv + "**\n **Score:** " + normalRankScore + " RP", true);
            statsEmbed.addField("Arenas", "**" + arenaName + " " + arenaDiv + "**\n **Score:** " + arenaScore + " AP", true);
            try {
                for (int i = 0; i < 3; i++) {
                    statsEmbed.addField(EmbedUtils.toTitleCase(playerLegendData.getJSONObject(i).get("name").toString()), playerLegendData.getJSONObject(i).get("value").toString(), true);
                }
            } catch (JSONException ignored) {
            } // There isn't always 3 stats (can have none), so we just continue if it can't find 3.
            statsEmbed.setFooter("Player UID: " + playerEmbedUID + "\nPowered by https://apexlegendsapi.com");

            event.getHook().sendMessageEmbeds(statsEmbed.build()).queue();
        } else if (Objects.requireNonNull(event.getOption("subcommand")).getAsString().equals("Map Rotation")) {
            event.deferReply().queue();
            String mapRotationLink = "https://api.mozambiquehe.re/maprotation?auth=" + APEX_TOKEN + "&version=2";
            HttpResponse<JsonNode> jsonNode = Unirest.get(mapRotationLink).asJson();
            JSONObject jsonResponse = jsonNode.getBody().getObject();
            if (isError(jsonNode)) {
                sendErrorMessage(jsonNode, event);
                return;
            }

            JSONObject battleRoyaleMap = jsonResponse.getJSONObject("battle_royale");
            JSONObject brCurrent = battleRoyaleMap.getJSONObject("current");
            String brMap = brCurrent.getString("map");
            String brTimer = brCurrent.getString("remainingTimer");
            JSONObject brNext = battleRoyaleMap.getJSONObject("next");
            String brNextMap = brNext.getString("map");
            String brNextDuration = brNext.getString("DurationInMinutes");
            JSONObject arenasMapMain = jsonResponse.getJSONObject("arenas");
            JSONObject arenasCurrent = arenasMapMain.getJSONObject("current");
            String arenasMap = arenasCurrent.getString("map");
            String arenasTimer = arenasCurrent.getString("remainingTimer");
            JSONObject arenasNext = arenasMapMain.getJSONObject("next");
            String arenasNextMap = arenasNext.getString("map");
            String arenasNextDuration = arenasNext.getString("DurationInMinutes");
            JSONObject rankedBrMapMain = jsonResponse.getJSONObject("ranked");
            JSONObject rankedBrCurrent = rankedBrMapMain.getJSONObject("current");
            String rankedBrMap = rankedBrCurrent.getString("map");
            String rankedBrTimer = rankedBrCurrent.getString("remainingTimer");
            JSONObject rankedArenasMapMain = jsonResponse.getJSONObject("arenasRanked");
            JSONObject rankedArenasCurrent = rankedArenasMapMain.getJSONObject("current");
            String rankedArenasMap = rankedArenasCurrent.getString("map");
            String rankedArenasTimer = rankedArenasCurrent.getString("remainingTimer");
            JSONObject rankedArenasNext = rankedArenasMapMain.getJSONObject("next");
            String rankedArenasNextMap = rankedArenasNext.getString("map");
            String rankedArenasNextDuration = rankedArenasNext.getString("DurationInMinutes");

            EmbedBuilder mapEmbed = new EmbedBuilder().setColor(EmbedUtils.EMBED_COLOR);
            mapEmbed.setTitle("Current Map Rotations");
            mapEmbed.addField("Battle Royale", "**Current:** " + brMap + " (Change in " + brTimer + ")\n **Next:** " + brNextMap + " (" + brNextDuration + " minutes)", true);
            mapEmbed.addField("Arenas", "**Current:** " + arenasMap + " (Change in " + arenasTimer + ")\n **Next:** " + arenasNextMap + " (" + arenasNextDuration + " minutes)", true);
            mapEmbed.addBlankField(false);
            mapEmbed.addField("Ranked BR", "**Current:** " + rankedBrMap + " (Change in " + rankedBrTimer + ")", true);
            mapEmbed.addField("Ranked Arenas", "**Current:** " + rankedArenasMap + " (Change in " + rankedArenasTimer + ")\n **Next:** " + rankedArenasNextMap + " (" + rankedArenasNextDuration + " minutes)", true);
            mapEmbed.setFooter("Powered by https://apexlegendsapi.com");

            event.getHook().sendMessageEmbeds(mapEmbed.build()).queue();
        } else if (Objects.requireNonNull(event.getOption("subcommand")).getAsString().equals("Predator Requirements")) {
            event.deferReply().queue();

            String predatorLink = "https://api.mozambiquehe.re/predator?auth=" + APEX_TOKEN;
            HttpResponse<JsonNode> jsonNode = Unirest.get(predatorLink).asJson();
            if (isError(jsonNode)) {
                sendErrorMessage(jsonNode, event);
                return;
            }

            JSONObject jsonResponse = jsonNode.getBody().getObject();
            JSONObject predatorBr = jsonResponse.getJSONObject("RP");
            JSONObject BrPC = predatorBr.getJSONObject("PC");
            String PCValue = BrPC.getString("val");
            String PCTotal = BrPC.getString("totalMastersAndPreds");
            JSONObject BrPS4 = predatorBr.getJSONObject("PS4");
            String PS4Value = BrPS4.getString("val");
            String PS4Total = BrPS4.getString("totalMastersAndPreds");
            JSONObject BrX1 = predatorBr.getJSONObject("X1");
            String X1Value = BrX1.getString("val");
            String X1Total = BrX1.getString("totalMastersAndPreds");
            JSONObject BrSwitch = predatorBr.getJSONObject("SWITCH");
            String switchValue = BrSwitch.getString("val");
            String switchTotal = BrSwitch.getString("totalMastersAndPreds");
            JSONObject predatorArenas = jsonResponse.getJSONObject("AP");
            JSONObject ArenasPC = predatorArenas.getJSONObject("PC");
            String PCArenasValue = ArenasPC.getString("val");
            String PCArenasTotal = ArenasPC.getString("totalMastersAndPreds");
            JSONObject ArenasPS4 = predatorArenas.getJSONObject("PS4");
            String PS4ArenasValue = ArenasPS4.getString("val");
            String PS4ArenasTotal = ArenasPS4.getString("totalMastersAndPreds");
            JSONObject ArenasX1 = predatorArenas.getJSONObject("X1");
            String X1ArenasValue = ArenasX1.getString("val");
            String X1ArenasTotal = ArenasX1.getString("totalMastersAndPreds");
            JSONObject ArenasSwitch = predatorArenas.getJSONObject("SWITCH");
            String switchArenasValue = ArenasSwitch.getString("val");
            String switchArenasTotal = ArenasSwitch.getString("totalMastersAndPreds");

            EmbedBuilder predatorEmbed = new EmbedBuilder().setColor(EmbedUtils.EMBED_COLOR);
            predatorEmbed.setTitle("Predator Requirements");
            predatorEmbed.addField("Battle Royale", "**PC:** " + PCValue + " RP (" + PCTotal + " Predators) \n **PS4:** " + PS4Value + " RP (" + PS4Total + " Predators) \n **Xbox:** " + X1Value + " RP (" + X1Total + " Predators) \n **Switch:** " + switchValue + " RP (" + switchTotal + " Predators)", true);
            predatorEmbed.addBlankField(true);
            predatorEmbed.addField("Arenas", "**PC:** " + PCArenasValue + " RP (" + PCArenasTotal + " Predators) \n **PS4:** " + PS4ArenasValue + " RP (" + PS4ArenasTotal + " Predators) \n **Xbox:** " + X1ArenasValue + " RP (" + X1ArenasTotal + " Predators) \n **Switch:** " + switchArenasValue + " RP (" + switchArenasTotal + " Predators)", true);
            predatorEmbed.setFooter("Powered by https://apexlegendsapi.com");

            event.getHook().sendMessageEmbeds(predatorEmbed.build()).queue();
        } else {
            event.reply("That's not an option!").setEphemeral(true).queue();
        }
    }

    private boolean isError(HttpResponse<JsonNode> jsonNode) {
        try {
            jsonNode.getBody().getObject().get("Error");
            return true;
        } catch (JSONException exception) {
            return false;
        }
    }

    private void sendErrorMessage(HttpResponse<JsonNode> jsonNode, SlashCommandInteractionEvent event) {
        String message = jsonNode.getBody().getObject().getString("Error");
        EmbedBuilder errorEmbed = new EmbedBuilder().setColor(EmbedUtils.EMBED_COLOR);
        errorEmbed.setTitle("Error");
        errorEmbed.setDescription(message);
        event.getHook().sendMessageEmbeds(errorEmbed.build()).queue();
    }
}
