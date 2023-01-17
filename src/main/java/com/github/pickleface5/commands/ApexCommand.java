package com.github.pickleface5.commands;

import com.github.pickleface5.exceptions.PlayerNeverPlayedException;
import com.github.pickleface5.exceptions.PlayerNotFoundException;
import com.github.pickleface5.util.EmbedUtils;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONException;
import kong.unirest.json.JSONObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;


public class ApexCommand extends ListenerAdapter { // TODO: Completely redo (see roadmap)
    private final String apexToken = System.getenv("APEX_TOKEN");
    private static final Logger logger = LogManager.getLogger(ApexCommand.class);

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("apex")) return;
        if (event.getOption("username") == null) event.reply("You need to enter a username!").queue();
        event.deferReply().queue();
        String ALApi = "https://api.mozambiquehe.re/bridge?version=5&platform=*&player=" + Objects.requireNonNull(event.getOption("username")).getAsString() + "&auth=" + apexToken;
        HttpResponse<JsonNode> jsonResponse = Unirest.get(ALApi.replace("*", "PC")).asJson();
        try {
            try {
                checkForError(jsonResponse, Objects.requireNonNull(event.getOption("username")).getAsString());
            } catch (PlayerNotFoundException e) {
                logger.trace("Player not found: {}. Attempting search on PS4...", Objects.requireNonNull(event.getOption("username")).getAsString());
                jsonResponse = Unirest.get(ALApi.replace("*", "PS4")).asJson();
                try {
                    checkForError(jsonResponse, Objects.requireNonNull(event.getOption("username")).getAsString());
                } catch (PlayerNotFoundException e1) {
                    logger.trace("Player not found: {}. Attempting search on X1...", Objects.requireNonNull(event.getOption("username")).getAsString());
                    jsonResponse = Unirest.get(ALApi.replace("*", "X1")).asJson();
                    try {
                        checkForError(jsonResponse, Objects.requireNonNull(event.getOption("username")).getAsString());
                    } catch (PlayerNotFoundException e2) {
                        logger.trace("Player not found, sending error message...");
                        MessageEmbed errorEmbed = new EmbedBuilder()
                                .setColor(EmbedUtils.EMBED_COLOR)
                                .setFooter("Powered by https://apexlegendsapi.com")
                                .setTitle("Player Not Found")
                                .setDescription("The user doesn't exist or hasn't played Apex Legends. If you're sure the account exists, Steam accounts have to be linked to an Origins account to show account data. Nintendo Switch accounts can't be found.")
                                .build();
                        event.getHook().sendMessageEmbeds(errorEmbed).queue();
                        return;
                    }
                }
            }
        } catch (PlayerNeverPlayedException playerNeverPlayedException) {
            MessageEmbed errorEmbed = new EmbedBuilder()
                    .setColor(EmbedUtils.EMBED_COLOR)
                    .setFooter("Powered by https://apexlegendsapi.com")
                    .setTitle("Player Has Never Played Apex Legends")
                    .setDescription("The user has an account, but has never played Apex Legends. If you're sure that you've entered the right account, make sure you're using your Origin username associated to your Apex Legends account.")
                    .build();
            event.getHook().sendMessageEmbeds(errorEmbed).queue();
            return;
        } catch (NullPointerException nullPointerException) {
            MessageEmbed errorEmbed = new EmbedBuilder()
                    .setColor(EmbedUtils.EMBED_COLOR)
                    .setFooter("Powered by https://apexlegendsapi.com")
                    .setTitle("Multiple Usernames Detected")
                    .setDescription("Please only search for 1 account at a time.")
                    .build();
            event.getHook().sendMessageEmbeds(errorEmbed).queue();
            return;
        }
        event.getHook().sendMessageEmbeds(createEmbed(jsonResponse)).queue();
    }

    private void checkForError(HttpResponse<JsonNode> jsonResponse, String username) throws PlayerNotFoundException, PlayerNeverPlayedException, NullPointerException {
        String apiTest = jsonResponse.getBody().toString();
        if (apiTest.contains("from origin backup api") || apiTest.contains("Player not found. Try again?") || apiTest.contains("code 103 - skipping origin backup api")) {
            throw new PlayerNotFoundException("Player not found: " + username);
        } else if (apiTest.contains("Player exists but has never played Apex Legends")) {
            throw new PlayerNeverPlayedException(username + "Has never player Apex Legends.");
        } else if (username.contains(",")) {
            throw new NullPointerException();
        }
    }

    private MessageEmbed createEmbed(HttpResponse<JsonNode> jsonResponse) {
        JSONObject jsonData = jsonResponse.getBody().getObject();
        EmbedBuilder dataEmbed = new EmbedBuilder()
                .setColor(EmbedUtils.EMBED_COLOR)
                .setFooter("Powered by https://apexlegendsapi.com")
                .setTitle(jsonData.getJSONObject("global").get("name").toString())
                .setThumbnail(jsonData.getJSONObject("global").getJSONObject("rank").get("rankImg").toString())
                .addField("Level", jsonData.getJSONObject("global").get("level").toString(), true)
                .addField("Rank", jsonData.getJSONObject("global").getJSONObject("rank").get("rankName").toString() + " " + jsonData.getJSONObject("global").getJSONObject("rank").get("rankDiv").toString(), true)
                .addField("Selected Legend", jsonData.getJSONObject("realtime").get("selectedLegend").toString(), false);
        try {
            for (int i = 0; i < 3; i++) {
                dataEmbed.addField(EmbedUtils.toTitleCase(jsonData.getJSONObject("legends").getJSONObject("selected").getJSONArray("data").getJSONObject(i).get("name").toString()), jsonData.getJSONObject("legends").getJSONObject("selected").getJSONArray("data").getJSONObject(i).get("value").toString(), true);
            }
            dataEmbed.setImage(jsonData.getJSONObject("legends").getJSONObject("selected").getJSONObject("ImgAssets").get("banner").toString());
        } catch (JSONException jsonException) {
            dataEmbed.setImage(jsonData.getJSONObject("legends").getJSONObject("selected").getJSONObject("ImgAssets").get("banner").toString());
        }
        return dataEmbed.build();
    }
}
