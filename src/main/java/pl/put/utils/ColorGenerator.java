package pl.put.utils;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Author: Krystian Świdurski
 */
public class ColorGenerator {

    public static final int NUMBER_OF_COLORS = 100;
    private static final Color DEFAULT_MIX_COLOR = Color.WHITE;

    private List<Color> colors = new ArrayList<>();
    private Color mix;


    public ColorGenerator() {
        this(DEFAULT_MIX_COLOR);
    }

    public ColorGenerator(Color mix) {
        this.mix = mix;
        // Initialize first 100 colors
        for (int i = 0; i < NUMBER_OF_COLORS; i++) {
            colors.add(generateRandomColor(mix));
        }
    }


    public Color getColor(int i) {
        if (i < colors.size()) {
            return colors.get(i);
        }
        Color color = generateRandomColor(this.mix);
        colors.add(color);
        return color;
    }

    public Background getBackground(int i) {
        return new Background(new BackgroundFill(getColor(i), null, null));
    }


    public Color generateRandomColor(Color mix) {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        // mix the color
        if (mix != null) {
            red = (int) ((red + 255 * mix.getRed()) / 2);
            green = (int) ((green + 255 * mix.getGreen()) / 2);
            blue = (int) ((blue + 255 * mix.getBlue()) / 2);
        }
        return Color.rgb(red, green, blue);
    }
}
