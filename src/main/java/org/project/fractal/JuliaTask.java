package org.project.fractal;

import java.awt.*;
import java.awt.image.BufferedImage;

public class JuliaTask implements Runnable {

        private int[] colors;
        private final double xPos;
        private final double yPos;
        private final double zoom;
        private final int chunkX;
        private final int chunkY;
        private final int chunkSize;
        private final BufferedImage image;
        private final int height;
        private final int width;
        private final int MAX_ITERATIONS;
        private static final double CX = -0.7;
        private static final double CY = 0.27015;

        public JuliaTask(double xPos, double yPos, double zoom, int chunkX, int chunkY, BufferedImage image, int height, int width, int MAX_ITERATIONS, int chunkSize) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.zoom = zoom;
            this.chunkX = chunkX;
            this.chunkY = chunkY;
            this.chunkSize = chunkSize;
            this.image = image;
            this.height = height;
            this.width = width;
            this.MAX_ITERATIONS = MAX_ITERATIONS;
        }

        @Override
        public void run() {

            double demiWidth = width / 2;
            double demiHeight = height / 2;
            int maximumX = (chunkX + chunkSize);
            int maximumY = (chunkY + chunkSize);

            for (int row = chunkX; row < maximumX && row < width; row++) {
                for (int col = chunkY; col < maximumY && col < height; col++) {

                    double zx = 1.5 * (row - width / 2) / (0.5 * zoom * width) + xPos;
                    double zy = (col - height / 2) / (0.5 * zoom * height) + yPos;
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
        }

}
