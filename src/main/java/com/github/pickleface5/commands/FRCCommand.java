package com.github.pickleface5.commands;

import com.github.pickleface5.util.EmbedUtils;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import kong.unirest.core.json.JSONObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FRCCommand extends ListenerAdapter {
    private final Logger LOGGER = LogManager.getLogger(this);
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("frc")) return;
        event.deferReply().queue();

        HttpResponse<JsonNode> receivedInfo;
        try {
            receivedInfo = Unirest.get("https://www.thebluealliance.com/api/v3/team/frc" + event.getOption("team").getAsInt()).header("X-TBA-Auth-Key", System.getenv("FRC_API_KEY")).asJson();
        } catch (ArithmeticException e) {
            event.getHook().sendMessage("Team " + event.getOption("team").getAsString() + " does not exist!").queue();
            return;
        }

        LOGGER.info(receivedInfo.getBody().toString());

        // Check if team exists
        JSONObject mainObj = receivedInfo.getBody().getObject();
        if (mainObj.has("Error")) {
            event.getHook().setEphemeral(true).sendMessage("Team " + event.getOption("team").getAsInt() + " does not exist!").queue();
            return;
        }

        EmbedBuilder replyEmbed = new EmbedBuilder().setColor(EmbedUtils.EMBED_COLOR).setFooter("Powered by https://www.thebluealliance.com");
        replyEmbed.setTitle("Team " + mainObj.getInt("team_number") + " - " + mainObj.getString("nickname"));
        if (event.getOption("team").getAsInt() == 6343) replyEmbed.setTitle(":star: **Team " + mainObj.getInt("team_number") + " - " + mainObj.getString("nickname") + "** :star:");
        //replyEmbed.setDescription(mainObj.getString("name"));
        if (!mainObj.isNull("state_prov")) {
            replyEmbed.setDescription("Located in " +
                    ((mainObj.isNull("city")) ? (""): (mainObj.getString("city"))) +
                    ((mainObj.isNull("state_prov")) ? (""): (", " + mainObj.getString("state_prov"))) +
                    ((mainObj.isNull("postal_code")) ? (""): (" " + mainObj.getString("postal_code")))
            );
        } else if (!mainObj.isNull("country")) {
            replyEmbed.setDescription("Located in " + mainObj.getString("country"));
        }
        if (!mainObj.isNull("website")) {
            String teamWebsite = mainObj.getString("website");
            replyEmbed.appendDescription("\n[Team Website](" + teamWebsite + ")");
        }

        if (!mainObj.isNull("rookie_year")) replyEmbed.addField("**Rookie Year**", "" + mainObj.getInt("rookie_year"), false);

        if (!mainObj.isNull("school_name")) replyEmbed.addField("**School Name**", mainObj.getString("school_name"), false);

        if (!mainObj.isNull("name")) replyEmbed.addField("Sponsors", mainObj.getString("name"), false);

        event.getHook().sendMessageEmbeds(replyEmbed.build()).queue();
    }
}
