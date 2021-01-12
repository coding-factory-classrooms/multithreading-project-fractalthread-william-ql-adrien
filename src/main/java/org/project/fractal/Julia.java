package org.project.fractal;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class Julia {

    private static final int MAX_ITERATIONS = 300;
    private static final double CX = -0.7;
    private static final double CY = 0.27015;


    public BufferedImage generateFractal(int height, int width, double x, double y, double zoom, ExecutorService executorService,boolean parallel) {


        long start = System.currentTimeMillis();
        BufferedImage bufferedImage;
        if (parallel) {
            bufferedImage = fractalMultiThread(height, width, x, y, zoom, executorService);
        } else {
            bufferedImage = fractalSingleThread(height, width, x, y, zoom);
        }
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Time to generate : " + elapsed + " ms");
        return bufferedImage;


    }

    private BufferedImage fractalSingleThread(int height, int width, double x, double y, double zoom) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        double demiWidth = width / 2;
        double demiHeight = height / 2;

        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                double zx = 1.5 * (row - demiWidth) / (0.5 * zoom * width) + x;
                double zy = (col - demiHeight) / (0.5 * zoom * height) + y;
                float i = MAX_ITERATIONS;
                while (zx * zx + zy * zy < 4 && i > 0) {
                    double tmp = zx * zx - zy * zy + CX;
                    zy = 2.0 * zx * zy + CY;
                    zx = tmp;
                    i--;
                }
                int c = Color.HSBtoRGB((MAX_ITERATIONS / i) % 1, 1, i > 0 ? 1 : 0);
                image.setRGB(row, col, c);
            }
        }
        return image;
    }

    private BufferedImage fractalMultiThread(int height, int width, double x, double y, double zoom, ExecutorService executorService) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Cores :" + cores);
        int chunkSize = height / (cores * 4);

        for (int chunkX = 0; chunkX < width; chunkX += chunkSize) {
            for (int chunkY = 0; chunkY < height; chunkY += chunkSize) {
                JuliaTask juliaTask = new JuliaTask(x, y, zoom, chunkX, chunkY, image, height, width, MAX_ITERATIONS, chunkSize);
                executorService.execute(juliaTask);
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
}
