package org.project.fractal;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author ANDRIEU William
 */
public class Mandelbrot<T> {


    // how long to test for orbit divergence
    private static final int MAX_ITERATIONS = 5000;


    private int height;
    private int width;

    private int black = 0;
    private int[] colors;

    /**
     * Construct a new Mandelbrot
     *
     * @param width    the size of the fractal (width pixels).
     * @param height   the size of the fractal (height pixels).
     * @param parallel if true, render in parallel
     */
    public Mandelbrot(int width, int height, boolean parallel) {

        this.width = width;
        this.height = height;


        this.colors = new int[MAX_ITERATIONS];
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            colors[i] = Color.HSBtoRGB(i / 256f, 1, i / (i + 8f));
        }
    }


    public BufferedImage generateFractal(int height, int width, double x, double y, double zoom, ExecutorService executorService, boolean parallel) {
        // Directory path /x/y/zoom

        this.height = height;
        this.width = width;

        if(parallel) {
            long start = System.currentTimeMillis();
            BufferedImage bufferedImage = generateFractal(x, y, zoom, executorService);
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("Time to generate : " + elapsed + " ms");
            return bufferedImage;

        } else {
            long start = System.currentTimeMillis();
            BufferedImage bufferedImage = generateFractal(x, y, zoom);
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("Time to generate : " + elapsed + " ms");
            return bufferedImage;
        }


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
