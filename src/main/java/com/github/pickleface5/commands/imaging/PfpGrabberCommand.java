package com.github.pickleface5.commands.imaging;

import com.github.pickleface5.Main;
import com.github.pickleface5.util.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public class PfpGrabberCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) { //TODO: Optimize/Revamp + higher quality(?)
        if (!(event.getName().equals("pfpgrabber"))) return;
        User user;
        try {
            user = Objects.requireNonNull(event.getOption("user")).getAsUser();
        } catch (NullPointerException e) {
            user = event.getUser();
        }
        if (!(event.getOption("user") == null)) {
            user = Objects.requireNonNull(event.getOption("user")).getAsUser();
        }
        event.deferReply().queue();
        try {
            URL url = new URL(Objects.requireNonNull(user.getAvatarUrl()));
            URLConnection openConnection = url.openConnection();

            try {
                openConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                openConnection.connect();
            } catch (Exception e) {
                event.getHook().sendMessage("There was an issue while loading your profile photo.").queue();
                e.printStackTrace();
                return;
            }
            BufferedImage img;
            try {
                InputStream in = new BufferedInputStream(openConnection.getInputStream());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n;
                while (-1 != (n = in.read(buf))) {
                    out.write(buf, 0, n);
                }
                out.close();
                in.close();
                byte[] response = out.toByteArray();
                img = ImageIO.read(new ByteArrayInputStream(response));
            } catch (Exception e) {
                event.getHook().sendMessage("I couldn't read an image from this link. Please tell the developer :pray: ").queue();
                e.printStackTrace();
                return;
            }
            File authorPfp = new File(Main.TEMP_DIRECTORY.getAbsolutePath() + "/" + user.getId() + ".png");
            try {
                final ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
                writer.setOutput(new FileImageOutputStream(authorPfp));
                assert img != null;
                writer.write(null, new IIOImage(img, null, null), null);
            } catch (IOException e) {
                event.getHook().sendMessage("Couldn't create/send the output image.").queue();
                e.printStackTrace();
                return;
            }
        } catch (IOException | NullPointerException e) {
            event.getHook().sendMessage("There was an error while downloading your profile photo! " +
                    "Make sure you have a custom profile photo, not an discord profile photo. If it *still* doesn't " +
                    "work for you, it's an issue with us. Please contact the developer.").queue();
            e.printStackTrace();
            return;
        }
        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                        .setTitle(user.getName() + "'s Profile Icon")
                        .setColor(EmbedUtils.EMBED_COLOR)
                        .setImage("attachment://" + user.getId() + ".png")
                .build()
        ).addFiles(FileUpload.fromData(new File(Main.TEMP_DIRECTORY.getAbsolutePath() + "/" + user.getId() + ".png"))).queue();
    }
}
