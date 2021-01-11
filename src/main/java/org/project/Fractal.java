package org.project;

import java.util.ArrayList;
import java.util.List;

public class Fractal {

    private int iterations = 1000;
    
    private int width = 900;
    private int height = 600;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private int xmin = -2;
    private int xmax = 1;
    private int ymin = -1;
    private int ymax = 1;
    private int zoom = 1;

    public int getXmin() {
        return xmin;
    }

    public void setXmin(int xmin) {
        this.xmin = xmin;
    }

    public int getXmax() {
        return xmax;
    }

    public void setXmax(int xmax) {
        this.xmax = xmax;
    }

    public int getYmin() {
        return ymin;
    }

    public void setYmin(int ymin) {
        this.ymin = ymin;
    }

    public int getYmax() {
        return ymax;
    }

    public void setYmax(int ymax) {
        this.ymax = ymax;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public void setMinMax(int xmin, int xmax, int ymin, int ymax) {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
    }

    public List<Integer> getFractal() {
        List<Integer> fractalValues = new ArrayList<>();

        for (var ix = 0; ix < width; ++ix) {
            for (var iy = 0; iy < height; ++iy) {
                var x = xmin + (xmax - xmin) * ix / (width - 1) * zoom;
                var y = ymin + (ymax - ymin) * iy / (height - 1) * zoom;
                //console.log("ymin : " + ymin + " | ymax : "+ ymax + " | iy : " + iy + " | width : " + height + " | y : " + y);
                var i = mandelIter(x, y, iterations);
                var ppos = 4 * (width * iy + ix);

                if (i > iterations) {
                    fractalValues.set(ppos, 0);
                    fractalValues.set(ppos + 1, 0);
                    fractalValues.set(ppos + 2, 0);
                } else {
                    double c = 3 * Math.log(i) / Math.log(iterations - 1.0);
                    if (c < 1) {
                        fractalValues.set(ppos, (int)Math.round(255 * c));
                        fractalValues.set(ppos + 1, 0);
                        fractalValues.set(ppos + 2, 0);
                    }
                    else if (c < 2) {
                        fractalValues.set(ppos, 255);
                        fractalValues.set(ppos + 1, (int)Math.round(255 * (c - 1)));
                        fractalValues.set(ppos + 2, 0);
                    } else {
                        fractalValues.set(ppos, 255);
                        fractalValues.set(ppos + 1, 255);
                        fractalValues.set(ppos + 2, (int)Math.round(255 * (c - 2)));
                    }
                }
                fractalValues.set(ppos + 3, 255);
            }
        }

        return fractalValues;
    }

    private int mandelIter(int cx, int cy, int maxIteration) {
        double x = 0.0;
        double y = 0.0;
        double xx = 0;
        double yy = 0;
        double xy = 0;

        int  i = maxIteration;
        while (i == 0 && xx + yy <= 4) {
            xy = x * y;
            xx = x * x;
            yy = y * y;
            x = xx - yy + cx;
            y = xy + xy + cy;
        }
        return maxIteration - i;
    }
}