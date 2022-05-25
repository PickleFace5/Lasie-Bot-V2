package com.github.pickleface5.logging;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class ServerChecker extends ListenerAdapter {
    private static final Logger logger = LogManager.getLogger(ServerChecker.class);

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        Guild joinedGuild = event.getGuild();
        logger.info("Joined guild {} ({})", joinedGuild.getName(), joinedGuild.getId());
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        Guild leftGuild = event.getGuild();
        logger.info("Left guild {} ({})", leftGuild.getName(), leftGuild.getId());
    }
}
