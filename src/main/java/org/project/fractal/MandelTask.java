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

        for (int row = chunkX; row < (chunkX + chunkSize) && row < height; row++) {
            for (int col = chunkY; col < (chunkY + chunkSize) && col < width; col++) {

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
                    image.setRGB(col, row, colors[iteration]);
                    fullBlack = false;
                } else {
                    //  imageData[col][row] = black;
                    image.setRGB(col, row, 0);
                }
            }


            // image.getRaster().setPixels(0, 0, 1024, 1024, new int[]{0,0,0});
        }
        if (fullBlack){
            checkOtherBounds();
        }


    }

    private void checkOtherBounds() {

        boolean up = checkUp();
        System.out.println("UP is black: "+ up);
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

}
