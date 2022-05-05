package com.github.pickleface5.commands;

import com.github.pickleface5.Main;
import com.github.pickleface5.util.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class InfoCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("info")) return;
        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Lasie Bot")
                .setDescription("Made in Java using JDA.")
                .setFooter("Made by Pickle_Face5")
                .setColor(EmbedUtils.EMBED_COLOR)
                .build();
        event.replyEmbeds(embed).queue();
    }
}
