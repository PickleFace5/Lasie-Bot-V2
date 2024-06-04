package com.github.pickleface5.commands.robot;

import com.github.pickleface5.Main;
import com.github.pickleface5.util.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.time.format.DateTimeFormatter;

import org.jetbrains.annotations.NotNull;

public class ServerCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("server")) return;
        else if (!event.isFromGuild()) {
            event.reply("You need to run this in a server!").queue();
            return;
        }

        Guild guild = event.getGuild();
        
        EmbedBuilder embed = new EmbedBuilder()
        .setColor(EmbedUtils.EMBED_COLOR)
        .setThumbnail(guild.getIconUrl());

        Member owner = guild.getOwner();
        if (owner != null) {
            embed.setFooter("Owner: " + guild.getOwner().getEffectiveName() + " (" + guild.getOwnerId() + ")", owner.getEffectiveAvatarUrl());
        }

        // Vanity URL to title if applicable
        try {
            if (guild.getVanityUrl() != null) embed.setTitle("[" + guild.getName() + "](" + guild.getVanityUrl() + ")");
            else embed.setTitle(guild.getName());
        } catch (IllegalArgumentException e) {
            embed.setTitle(guild.getName()); // Max length is 21 characters
        }

        embed.addField("Member Count", String.valueOf(guild.getMemberCount()), true);
        embed.addField("Max Presences", String.valueOf(guild.getMaxPresences()), true);
        embed.addField("Max Members", String.valueOf(guild.getMaxMembers()), true);

        embed.addField("Current Boosts", String.valueOf(guild.getBoostCount()), true);
        embed.addField("Boost Tier", EmbedUtils.toTitleCase(guild.getBoostTier().name()), true);
        if (guild.getBoostRole() != null) embed.addField("Booster Role", guild.getBoostRole().getName(), true);
        else embed.addBlankField(true);

        embed.addField("Total Channels", String.valueOf(guild.getChannels().size()), true);
        embed.addField("Total Emojis", String.valueOf(guild.getEmojis().size()), true);
        embed.addField("Total Roles", String.valueOf(guild.getRoles().size() - 1), true);

        embed.addField("Explicit Level", EmbedUtils.toTitleCase(guild.getExplicitContentLevel().name()), true);
        embed.addField("NSFW Level", EmbedUtils.toTitleCase(guild.getNSFWLevel().name()), true);
        embed.addField("Verification Level", EmbedUtils.toTitleCase(guild.getVerificationLevel().name()), true);

        embed.addField("Max File Size", String.valueOf(guild.getMaxFileSize() / 1048576) + "MB", true);
        embed.addField("Max Bitrate", String.valueOf(guild.getMaxBitrate() / 1000) + "kbps", true);
        embed.addField("Max Emojis", String.valueOf(guild.getMaxEmojis()), true);

        embed.addField("MFA Level", EmbedUtils.toTitleCase(guild.getRequiredMFALevel().name()), true);
        embed.addField("Total Stickers", String.valueOf(guild.getStickerCache().size()), true);

        embed.addField("Time Created", guild.getTimeCreated().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")), true);
        
        event.replyEmbeds(embed.build()).queue();
    }
}
