package rasterizers;

import models.Line;
import rasters.Raster;

import java.awt.*;

public class LineRasterizer implements Rasterizer {

    private Color defaultColor;
    private Raster raster;
    private int windowWidth, windowHeight;
    private int allignPointsNum = 8;

    public LineRasterizer(Raster raster, Color defaultColor, int windowWidth, int windowHeight) {
        this.raster = raster;
        this.defaultColor = defaultColor;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    @Override
    public void setColor(Color color) {
        defaultColor = color;
    }

    @Override
    public void rasterize(Line line) {
        int color = line.getColor().getRGB();
        int x1 = line.getP1().getX();
        int y1 = line.getP1().getY();
        int x2 = line.getP2().getX();
        int y2 = line.getP2().getY();
        int stepPixel = 1;
        if (line.isDotted()) stepPixel = 3;

        if (line.isAlignMode()) {
            //System.out.println(k + "\n" + q + "\n\n");
            int relX = x2 - x1;
            int relY = y2 - y1;
            int angle = (int) Math.round(Math.toDegrees(Math.atan2(relY, relX)));
            if (angle < 0) angle += 360;
            //System.out.println(angle);
            int alignAngleSize = 360 / allignPointsNum;
            int alignAngle = (int) Math.round((float) angle / alignAngleSize) * alignAngleSize;
            //System.out.println(alignAngle);

            double length = Math.sqrt(relX * relX + relY * relY);
            x2 = x1 + (int) (Math.round(length * Math.cos(Math.toRadians(alignAngle))));
            y2 = y1 + (int) (Math.round(length * Math.sin(Math.toRadians(alignAngle))));
        }


        double k = (y2 - y1) / (double) (x2 - x1);
        double q = y1 - k * x1;


        if (x1 == x2) {
            if (y1 > y2) { int temp = y1; y1 = y2; y2 = temp; }
            for (int y = y1; y <= y2; y+=stepPixel) {
                if (y < 0 || y >= windowHeight || x1 < 0 || x1 >= windowWidth) continue;
                raster.setPixel(x1, y, color);
            }
            return;
        } else if (Math.abs(k) <= 1) {
            if (x1 > x2) { int temp = x1; x1 = x2; x2 = temp; }

            for (int x = x1; x <= x2; x+=stepPixel) {
                int y = (int) Math.round(k * x + q);
                if (y < 0 || y >= windowHeight || x < 0 || x >= windowWidth) continue;
                raster.setPixel(x, y, color);
            }
        } else {
            if (y1 > y2) { int temp = y1; y1 = y2; y2 = temp; }

            for (int y = y1; y <= y2; y+=stepPixel) {
                int x = (int) Math.round((y - q) / k);
                if (y < 0 || y >= windowHeight || x < 0 || x >= windowWidth) continue;
                raster.setPixel(x, y, color);
            }
        }

    }

}
