package com.github.pickleface5.commands.imageing;

import com.github.pickleface5.util.EmbedUtils;
import kong.unirest.Unirest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class ChadPfpCommand extends ListenerAdapter {
    private static final Logger logger = LogManager.getLogger(ChadPfpCommand.class);
    BufferedImage gigachad;

    public ChadPfpCommand() throws IOException {
        gigachad = ImageIO.read(new File("src/main/resources/imaging/chad/gigachad.png"));
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (!(event.getName().equals("chad"))) return;
        User user;
        try {
            user = Objects.requireNonNull(event.getOption("user")).getAsUser();
        } catch (NullPointerException e) {
            user = event.getUser();
        }
        logger.trace(user);
        if (!(event.getOption("user") == null)) {
            user = Objects.requireNonNull(event.getOption("user")).getAsUser();
            logger.trace(user);
        }
        event.deferReply().queue();
        Graphics2D newImg = gigachad.createGraphics();
        File authorPfpButDifferent = Unirest.get(user.getAvatarUrl()).asFile("src/main/resources/temp/" + user.getId() + ".png", StandardCopyOption.REPLACE_EXISTING).getBody();
        try {
            BufferedImage authorPfp = ImageIO.read(authorPfpButDifferent);
            logger.trace(new File("src/main/resources/temp/" + user.getId() + ".png").getAbsolutePath());
            newImg.drawImage(authorPfp, 170, 61, null);
            ImageIO.write(gigachad, "png", new File("src/main/resources/temp/" + user.getId() + "_final.png"));
        } catch (IOException | NullPointerException e) {
            event.getHook().sendMessage("There was an error while downloading your profile photo!").queue();
            e.printStackTrace();
            return;
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setImage("attachment://gigachad.png");
        event.getHook().sendMessageEmbeds(embed
                        .setTitle("Nice pic, king. :crown:")
                        .setColor(EmbedUtils.EMBED_COLOR)
                        .setFooter("'Absolute chad.'")
                        .build())
                .addFile(new File("src/main/resources/temp/" + user.getId() + "_final.png"), "gigachad.png")
                .queue();
    }
}
