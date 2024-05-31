package com.github.pickleface5.commands.robot;

import com.github.pickleface5.util.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MembersCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("members")) return;
        if (event.isFromGuild()) {
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("Members in " + Objects.requireNonNull(event.getGuild()).getName())
                    .addField("Total Members", String.valueOf(event.getGuild().getMemberCount()), false)
                    .setColor(EmbedUtils.EMBED_COLOR)
                    .build();
            event.replyEmbeds(embed).queue();
        } else {
            event.reply("You need to run this in a server!").queue();
        }
    }
}
