package de.quantumrange.imageUtils;

import de.quantumrange.colorUtil.impl.HSLColor;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

    public static void main(String[] args) throws IOException {
        AWTEventListener listener = event -> {
            try {
                KeyEvent evt = (KeyEvent) event;

                if (evt.getID() == KeyEvent.KEY_PRESSED) {
                    System.out.println("KeyChar: " + evt.getKeyChar());
                }
            }  catch (Exception e) {
                e.printStackTrace();
            }
        };

        Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.KEY_EVENT_MASK);

        Image img = new Image(new File("/Users/quantumrange/Desktop/test.png"));

        AtomicReference<Float> rotation = new AtomicReference<>(new Random().nextFloat() * 360f);

        img = img.overridePixels(rgb -> {
            HSLColor color = new HSLColor(new Color(rgb));

            if (new Random().nextInt(8000) == 0)  {
                rotation.set(new Random().nextFloat() * 360f);
            }

            color.setHue(color.getHue() + rotation.get());

            return color.toColor().getRGB();
        }).completion();

        img.writeToFile("test.png").completion();

        System.out.println("Done!");
    }

}