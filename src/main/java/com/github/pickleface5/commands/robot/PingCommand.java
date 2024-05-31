package com.github.pickleface5.commands.robot;

import com.github.pickleface5.Main;
import com.github.pickleface5.util.EmbedUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import org.jetbrains.annotations.NotNull;

public class PingCommand extends ListenerAdapter {

    private MessageEmbed getPingEmbed() {
        EmbedBuilder pingEmbed = new EmbedBuilder();
        pingEmbed.setColor(EmbedUtils.EMBED_COLOR);


        var latency = Main.JDA.getRestPing().complete();
        var heartbeat = Long.valueOf(Main.JDA.getGatewayPing());

        pingEmbed.addField(":stopwatch: Latency", "`" + latency + " ms`", true);
        pingEmbed.addField(":heartbeat: Heartbeat", "`" + heartbeat + " ms`", true);

        return pingEmbed.build();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("ping")) return;

        event.replyEmbeds(getPingEmbed())
        .addActionRow(Button.secondary("ping", "Refresh"))
        .queue();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getButton().getId().equals("ping")) return;
        
        event.editMessageEmbeds(getPingEmbed()).queue();
    }
}
