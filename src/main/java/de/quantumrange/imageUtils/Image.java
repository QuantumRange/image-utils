package de.quantumrange.imageUtils;

import de.quantumrange.actionlib.Action;
import de.quantumrange.actionlib.ActionManager;
import de.quantumrange.actionlib.impl.actions.NormalAction;
import de.quantumrange.actionlib.impl.manager.MultiThreadManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.function.Function;

public class Image extends BufferedImage {

    public static ActionManager MANAGER = new MultiThreadManager(1f);
    
    public Image(int width, int height, int imageType) {
        super(width, height, imageType);
    }

    public Image(int width, int height, int imageType, IndexColorModel cm) {
        super(width, height, imageType, cm);
    }

    public Image(ColorModel cm, WritableRaster raster, boolean isRasterPremultiplied, Hashtable<?, ?> properties) {
        super(cm, raster, isRasterPremultiplied, properties);
    }

    public Image(BufferedImage image) {
        this(image.getWidth(), image.getHeight(), image.getType());
        setData(image.getData());
    }

    public Image(URL url) throws IOException {
        this(ImageIO.read(url));
    }

    public Image(File file) throws IOException {
        this(ImageIO.read(file));
    }

    public Action<Image> overridePixels(Function<Integer, Integer> pixelFunction) {
        return new NormalAction<>(MANAGER, throwable -> {
            Image img = new Image(getWidth(), getHeight(), getType());

            Graphics2D g = img.createGraphics();
            g.drawImage(this, 0, 0, getWidth(), getHeight(), null);
            g.dispose();

            for (int x = 0; x < getWidth(); x++) {
                for (int y = 0; y < getHeight(); y++) {
                    img.setRGB(x, y, pixelFunction.apply(img.getRGB(x, y)));
                }
            }

            return img;
        });
    }

    public Action<Boolean> writeToFile(String filename) {
        return writeToFile(new File(filename));
    }

    public Action<Boolean> writeToFile(File file) {
        return new NormalAction<>(MANAGER, throwable -> {
            String ending = "PNG";

            if (file.getName().contains(".")) {
                String[] args = file.getName().split("\\.");
                ending = args[args.length - 1];
            }

            try {
                ImageIO.write(this, ending, file);
                return true;
            } catch (IOException e) {
                throwable.accept(e);
            }
            return false;
        });
    }

}
