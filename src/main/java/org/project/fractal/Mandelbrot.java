package org.project.fractal;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author ANDRIEU William
 */
public class Mandelbrot {


    // how long to test for orbit divergence
    private int MAX_ITERATIONS;


    private int height;
    private int width;

    private int black = 0;
    private int[] colors;

    /**
     * Construct a new Mandelbrot
     */
    public Mandelbrot(int maxIteration) {

        this.MAX_ITERATIONS = maxIteration;


        this.colors = new int[MAX_ITERATIONS];
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            colors[i] = Color.HSBtoRGB(i / 256f, 1, i / (i + 8f));
        }
    }


    public BufferedImage generateFractal(int height, int width, double x, double y, double zoom, ExecutorService executorService, boolean parallel) {
        // Directory path /x/y/zoom

        this.height = height;
        this.width = width;

        if (parallel) {
            long start = System.currentTimeMillis();
            BufferedImage bufferedImage = generateFractal(x, y, zoom, executorService);
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("Time to generate : " + elapsed + " ms");
            if (x == -0.7006458334216172 && y == -0.20206193410542064 && zoom == 0.5707581840228738){
                bufferedImage = addNameOnFractal(bufferedImage);
            }
            return bufferedImage;
        } else {
            long start = System.currentTimeMillis();
            BufferedImage bufferedImage = generateFractal(x, y, zoom);
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("Time to generate : " + elapsed + " ms");
            return bufferedImage;
        }
    }

    private BufferedImage addNameOnFractal(BufferedImage bufferedImage) {
        Image teamNameImage = new ImageIcon("./Name.PNG").getImage();
        BufferedImage bufferedImageTeamName = toBufferedImage(teamNameImage);
        for (int w = 0; w < bufferedImageTeamName.getWidth(); w++){
            for (int h = 0; h < bufferedImageTeamName.getHeight(); h++){
                bufferedImage.setRGB(w + 50,h + 50, bufferedImageTeamName.getRGB(w,h));
            }
        }
        return bufferedImage;
    }

    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }
        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        // Return the buffered image
        return bimage;
    }

    // col => Column in image
    // row => Rows in image
    // zoom  chiffre + grand = dezoom;
    // xPos position dans le fractal
    // yPos position dans le fractal
    private BufferedImage generateFractal(double xPos, double yPos, double zoom) {

        double demiWidth = width / 2;
        double demiHeight = height / 2;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {

                double c_re = ((col - demiWidth) * zoom / width) + xPos;
                double c_im = ((row - demiHeight) * zoom / width) + yPos;
                double x = 0, y = 0;
                int iteration = 0;
                while (x * x + y * y < 4 && iteration < MAX_ITERATIONS) {
                    double x_new = x * x - y * y + c_re;
                    y = 2 * x * y + c_im;
                    x = x_new;
                    iteration++;
                }
                if (iteration < MAX_ITERATIONS) {
                    bufferedImage.setRGB(col, row, getColor(iteration));
                } else {
                    bufferedImage.setRGB(col, row, black);
                }
            }
        }
        return bufferedImage;
    }

    // col => Column in image
    // row => Rows in image
    // zoom  chiffre + grand = dezoom;
    // xPos position dans le fractal
    // yPos position dans le fractal
    private BufferedImage generateFractal(double xPos, double yPos, double zoom, ExecutorService executorService) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int cores = Runtime.getRuntime().availableProcessors();

        int chunkSize = height / (cores * 4);

        for (int chunkX = 0; chunkX < height; chunkX += chunkSize) {
            for (int chunkY = 0; chunkY < width; chunkY += chunkSize) {
                MandelTask mandelTask = new MandelTask(xPos, yPos, zoom, chunkX, chunkY, image, height, width, MAX_ITERATIONS, chunkSize, colors);
                executorService.execute(mandelTask);
            }

        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return image;
    }

    private int getColor(int index) {
        return colors[index];
    }


}
