package org.project.fractal;

import java.awt.image.BufferedImage;

class MandelTask implements Runnable {

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

    public MandelTask(double xPos, double yPos, double zoom, int chunkX, int chunkY, BufferedImage image, int height, int width, int MAX_ITERATIONS, int chunkSize, int[] colors) {
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
        this.colors = colors;
    }

    @Override
    public void run() {

        boolean fullBlack = true;
        double demi_width = width / 2;
        double demi_height = height / 2;

        for (int row = chunkX; row < (chunkX + chunkSize) && row < height; row++) {
            for (int col = chunkY; col < (chunkY + chunkSize) && col < width; col++) {

                double c_re = ((col - demi_width) * zoom / width) + xPos;
                double c_im = ((row - demi_height) * zoom / width) + yPos;
                double x = 0, y = 0;
                int iteration = 0;
                while (iteration < MAX_ITERATIONS && x * x + y * y < 4) {
                    double x_new = x * x - y * y + c_re;
                    y = 2 * x * y + c_im;
                    x = x_new;
                    iteration++;
                }
                if (iteration < MAX_ITERATIONS) {
                    image.setRGB(col, row, colors[iteration]);
                    fullBlack = false;
                } else {
                    image.setRGB(col, row, 0);
                }
            }
            if (fullBlack) {
                if (checkOtherBounds()) {
                    setAllBlack();
                    return;
                }
            }


            // image.getRaster().setPixels(0, 0, 1024, 1024, new int[]{0,0,0});
        }
    }

    private boolean checkOtherBounds() {

        boolean up = checkUp();
        if (!up) return false;

        boolean right = checkRight();
        if (!right) return false;

        boolean down = checkDown();
        if (!down) return false;

        return true;
    }

    private boolean checkUp() {
        int max = (chunkX + chunkSize);
        int col = chunkY;
        for (int row = chunkX; row < max && row < height; row++) {
            double c_re = ((col - width / 2) * zoom / width) + xPos;
            double c_im = ((row - height / 2) * zoom / width) + yPos;
            double x = 0, y = 0;
            int iteration = 0;
            while (iteration < MAX_ITERATIONS && x * x + y * y < 4) {
                double x_new = x * x - y * y + c_re;
                y = 2 * x * y + c_im;
                x = x_new;
                iteration++;
            }
            if (iteration < MAX_ITERATIONS) {
                return false;
            }
        }
        return true;
    }

    private boolean checkRight() {
        int max = (chunkX + chunkSize);
        int col = (chunkY + chunkSize) - 1;
        for (int row = chunkX; row < max && row < height; row++) {
            double c_re = ((col - width / 2) * zoom / width) + xPos;
            double c_im = ((row - height / 2) * zoom / width) + yPos;
            double x = 0, y = 0;
            int iteration = 0;
            while (iteration < MAX_ITERATIONS && x * x + y * y < 4) {
                double x_new = x * x - y * y + c_re;
                y = 2 * x * y + c_im;
                x = x_new;
                iteration++;
            }
            if (iteration < MAX_ITERATIONS) {
                return false;
            }
        }
        return true;
    }

    private boolean checkDown() {
        int max = (chunkY + chunkSize);
        int row = (chunkX + chunkSize) - 1;
        for (int col = chunkY; col < max && col < height; col++) {
            double c_re = ((col - width / 2) * zoom / width) + xPos;
            double c_im = ((row - height / 2) * zoom / width) + yPos;
            double x = 0, y = 0;
            int iteration = 0;
            while (iteration < MAX_ITERATIONS && x * x + y * y < 4) {
                double x_new = x * x - y * y + c_re;
                y = 2 * x * y + c_im;
                x = x_new;
                iteration++;
            }
            if (iteration < MAX_ITERATIONS) {
                return false;
            }
        }
        return true;
    }

    private void setAllBlack() {
        for (int row = chunkX; row < (chunkX + chunkSize) && row < height; row++) {
            for (int col = chunkY; col < (chunkY + chunkSize) && col < width; col++) {

                image.setRGB(col, row, 0);
            }
        }

    }
}

