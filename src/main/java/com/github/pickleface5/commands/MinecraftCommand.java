package com.github.pickleface5.commands;

import com.github.pickleface5.Main;
import com.github.pickleface5.util.EmbedUtils;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.Locale;
import java.util.Objects;

public class MinecraftCommand extends ListenerAdapter {

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (!event.getName().equals("minecraft")) return;
        assert event.getOption("address") != null;
        event.deferReply().queue();
        String MSSApi;
        if (event.getOption("bedrock") == null) {
            MSSApi = "https://api.mcsrvstat.us/2/" + Objects.requireNonNull(event.getOption("address")).getAsString().toLowerCase(Locale.ROOT);
        } else {
            if (Objects.requireNonNull(event.getOption("bedrock")).getAsBoolean()) {
                MSSApi = "https://api.mcsrvstat.us/bedrock/2/" + Objects.requireNonNull(event.getOption("address")).getAsString().toLowerCase(Locale.ROOT);
            } else {
                MSSApi = "https://api.mcsrvstat.us/2/" + Objects.requireNonNull(event.getOption("address")).getAsString().toLowerCase(Locale.ROOT);
            }
        }
        HttpResponse<JsonNode> jsonResponse = Unirest.get(MSSApi).asJson();

        if (!jsonResponse.getBody().getObject().getBoolean("online")) {
            MessageEmbed serverOfflineEmbed = new EmbedBuilder()
                    .setColor(EmbedUtils.EMBED_COLOR)
                    .setFooter("Powered by https://mcsrvstat.us")
                    .setTitle("**Server Offline**")
                    .setDescription("The selected server is currently offline. Make sure you typed the domain correctly.")
                    .build();
            event.getHook().sendMessageEmbeds(serverOfflineEmbed).queue();
            return;
        }
        String hostName;
        if (jsonResponse.getBody().getObject().getString("hostname") == null) {
            hostName = jsonResponse.getBody().getObject().getString("ip");
        } else {
            hostName = jsonResponse.getBody().getObject().getString("hostname");
        }

        String iconData = jsonResponse.getBody().getObject().getString("icon").split(",")[1];
        byte[] iconBytes = DatatypeConverter.parseBase64Binary(iconData);
        String path = Main.TEMP_DIRECTORY.getAbsolutePath() + "/" + event.getUser().getId() + ".png";
        File file = new File(path);
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            outputStream.write(iconBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }


        EmbedBuilder serverInfoEmbed = new EmbedBuilder()
                .setColor(EmbedUtils.EMBED_COLOR)
                .setFooter("Powered by https://mcsrvstat.us")
                .setImage("attachment://icon.png")
                .setDescription(jsonResponse.getBody().getObject().getJSONObject("motd").getJSONArray("clean").getString(0));

        if (event.getOption("bedrock") == null) {
            serverInfoEmbed.setTitle(hostName + " (Java)");
        } else if (Objects.requireNonNull(event.getOption("bedrock")).getAsBoolean()) {
            serverInfoEmbed.setTitle(hostName + " (Bedrock)");
        } else {
            serverInfoEmbed.setTitle(hostName + " (Java)");
        }

        if (jsonResponse.getBody().getObject().has("plugins")) {
            serverInfoEmbed.addField("", jsonResponse.getBody().getObject().getJSONObject("plugins").getJSONArray("raw").length() + " plugins active", false);
        } else if (jsonResponse.getBody().getObject().has("mods")) {
            serverInfoEmbed.addField("", jsonResponse.getBody().getObject().getJSONObject("mods").getJSONArray("raw").length() + " mods active", false);
        } else {
            serverInfoEmbed.addField("", "No plugins/mods detected", false);
        }
        serverInfoEmbed.addField("", "Version: " + jsonResponse.getBody().getObject().getString("version"), false);
                
        serverInfoEmbed.addField("Players", jsonResponse.getBody().getObject().getJSONObject("players").getInt("online") + "/" + jsonResponse.getBody().getObject().getJSONObject("players").getInt("max"), true);
        event.getHook().sendMessageEmbeds(serverInfoEmbed.build()).addFile(new File(path), "icon.png").queue();
    }
}

