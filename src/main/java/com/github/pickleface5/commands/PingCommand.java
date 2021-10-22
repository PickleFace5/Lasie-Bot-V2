package com.github.pickleface5.commands;

import com.github.pickleface5.Main;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PingCommand extends ListenerAdapter {
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (!event.getName().equals("ping")) return;
        event.reply("My ping is " + Main.JDA.getGatewayPing() + "ms!").queue();
    }
}
