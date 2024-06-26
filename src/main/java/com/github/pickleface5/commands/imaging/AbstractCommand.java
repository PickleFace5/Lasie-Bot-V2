package com.github.pickleface5.commands.imaging;

import com.github.pickleface5.Main;
import com.github.pickleface5.util.EmbedUtils;

import ch.qos.logback.classic.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;

import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import static java.lang.Math.sqrt;

public class AbstractCommand extends ListenerAdapter { //TODO: Completely redo (see roadmap)
    private static final Logger logger = (Logger) LoggerFactory.getLogger(AbstractCommand.class);

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("abstract")) return;
        event.deferReply().queue();

        createImage(event.getUser());

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setTitle("Tada! How'd I do?")
                .setImage("attachment://abstract.png")
                .setColor(EmbedUtils.EMBED_COLOR)
                .build())
                .addFiles(FileUpload.fromData(new File(Main.TEMP_DIRECTORY.getAbsolutePath() + "/" + event.getUser().getId() + "_abstract.png"), "abstract.png"))
                .addActionRow(Button.primary("AbstractRegen", "Regenerate"))
                .queue();


    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getComponentId().equals("AbstractRegen")) return;
        createImage(event.getUser());
        event.deferEdit().queue();

        event.getHook().editOriginalEmbeds(new EmbedBuilder()
                        .setTitle("Tada! How'd I do?")
                        .setImage("attachment://abstract.png")
                        .setColor(EmbedUtils.EMBED_COLOR)
                        .build())
                .setFiles(FileUpload.fromData(new File(Main.TEMP_DIRECTORY.getAbsolutePath() + "/" + event.getUser().getId() + "_abstract.png"), "abstract.png"))
                .setActionRow(Button.primary("AbstractRegen", "Regenerate"))
                .queue();
    }

    private void createImage(User user) {
        Random random = new Random();

        Color backgroundColor = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));


        // Create image with random background
        int width = 1920;
        int height = 1080;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D imgGraphics = img.createGraphics();
        imgGraphics.setBackground(backgroundColor);
        int totalLines = random.nextInt(62) + 3;

        // DRAW SHAPES (100% chance, sides randomized (3-25), ranges from 1-5 shapes
        for (int i = 0; i < random.nextInt(5) + 1; i++) {
            Color shapeColor = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
            int vertices = random.nextInt(7) + 3;
            int[] xPoly = new int[vertices];
            int[] yPoly = new int[vertices];

            for (int j = 0; j < vertices; j++) {
                xPoly[j] = random.nextInt(width);
                yPoly[j] = random.nextInt(height);
            }
            imgGraphics.setColor(shapeColor);
            logger.trace("drawing shape {}", i);
            imgGraphics.drawPolygon(xPoly, yPoly, xPoly.length);
        }

        // DRAW STRAIGHT LINES (95% chance with the line either being round, straight, or jagged, 3 minimum, 50 maximum)
        for (int i = 0; i < totalLines; i++) {
            if (random.nextInt(100) <= 95) {
                Color lineColor = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
                if (ColorsAreClose(lineColor, backgroundColor)) {
                    logger.trace("lineColor too close to background, skipping... (line {} out of {}", i, totalLines);
                    continue;
                }
                imgGraphics.setColor(lineColor);
                imgGraphics.setStroke(new BasicStroke(random.nextInt(24), BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
                if (random.nextInt(100) <= 33) {
                    QuadCurve2D quadcurve = new QuadCurve2D.Float(random.nextInt(width), random.nextInt(height), random.nextInt(width), random.nextInt(height), random.nextInt(width), random.nextInt(height));
                    imgGraphics.draw(quadcurve);
                } else if (random.nextInt(100) <= 33) {
                    drawSpring(random.nextInt(width), random.nextInt(height), random.nextInt(width), random.nextInt(height), random.nextInt(1080), random.nextInt(35), imgGraphics);
                } else {
                    imgGraphics.drawLine(random.nextInt(width), random.nextInt(height), random.nextInt(width), random.nextInt(height));
                }
            }
        }

        //Build, Save, then send image
        try {
            ImageIO.write(img, "png", new File(Main.TEMP_DIRECTORY.getAbsolutePath() + "/" + user.getId() + "_abstract.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean ColorsAreClose(Color a, Color z)
    {
        int r = a.getRed() - z.getRed(),
                g = a.getGreen() - z.getGreen(),
                b = a.getBlue() - z.getBlue();
        return (r*r + g*g + b*b) <= 50*50;
    }

    void drawSpring(double x1, double y1, double x2, double y2, double w, int N, Graphics g)
    {
        // vector increment
        double inv = 0.25 / (double)N;
        double dx = (x2 - x1) * inv,
                dy = (y2 - y1) * inv;

        // perpendicular direction
        double inv2 = w / sqrt(dx * dx + dy * dy);
        double px =  dy * inv2,
                py = -dx * inv2;

        // loop
        double x = x1, y = y1;
        for (int i = 0; i < N; i++)
        {
            g.drawLine((int)x, (int)y, (int)x + (int)dx + (int)px, (int)y + (int)dy + (int)py);
            g.drawLine((int)x + (int)dx + (int)px, (int)y + (int)dy + (int)py, (int)x + 3 * (int)dx - (int)px, (int)y + 3 * (int)dy - (int)py);
            g.drawLine((int)x + 3 * (int)dx - (int)px, (int)y + 3 * (int)dy - (int)py, (int)x + 4 * (int)dx, (int)y + 4 * (int)dy);
            x += 4.0 * dx;
            y += 4.0 * dy;
        }
    }

}
