package com.github.pickleface5.commands;

import com.github.pickleface5.util.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

@Deprecated
public class BenCommand extends ListenerAdapter { // Leaving this here in case I want to re-add it later for some reason
    private static final Logger logger = LogManager.getLogger(BenCommand.class);
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("ben")) return;
        Random random = new Random(System.currentTimeMillis());
        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(EmbedUtils.EMBED_COLOR);
        int type = random.nextInt(4);
        if (type == 0) {
            embedBuilder.setTitle("Yyyeeeessss...");
            embedBuilder.setImage("https://c.tenor.com/6St4vNHkyrcAAAAd/yes.gif");
            embedBuilder.setFooter("Ben says YES to: " + Objects.requireNonNull(event.getOption("question")).getAsString(), event.getUser().getAvatarUrl());
        } else if (type == 1) {
            embedBuilder.setTitle("No.");
            embedBuilder.setImage("https://c.tenor.com/x2u_MyapWvcAAAAd/no.gif");
            embedBuilder.setFooter("Ben says NO to: " + Objects.requireNonNull(event.getOption("question")).getAsString(), event.getUser().getAvatarUrl());
        } else if (type == 2) {
            embedBuilder.setTitle("Ho ho ho!");
            embedBuilder.setImage("https://c.tenor.com/agrQMQjQTzgAAAAd/talking-ben-laugh.gif");
            embedBuilder.setFooter("Ben LAUGHS at: " + Objects.requireNonNull(event.getOption("question")).getAsString(), event.getUser().getAvatarUrl());
        } else {
            embedBuilder.setTitle("Ughgh...");
            embedBuilder.setImage("https://c.tenor.com/aomZLSiXCQ8AAAAC/ugh.gif");
            embedBuilder.setFooter("Ben is DISGUSTED at: " + Objects.requireNonNull(event.getOption("question")).getAsString(), event.getUser().getAvatarUrl());
        }
        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
