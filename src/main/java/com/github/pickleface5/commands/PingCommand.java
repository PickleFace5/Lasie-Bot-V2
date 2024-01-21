package com.github.pickleface5.commands;

import com.github.pickleface5.Main;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class PingCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("ping")) return;
        event.reply("My ping is `" + Main.JDA.getRestPing().complete() + "` ms!").queue();
    }
}
