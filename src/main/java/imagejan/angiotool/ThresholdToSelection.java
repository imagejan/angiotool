/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation
 * (http://www.gnu.org/licenses/gpl.txt ). This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * @author <a href="mailto:zudairee@mail.nih.gov">
 * Enrique Zudaire</a>, Radiation Oncology Branch, NCI, NIH
 * May, 2011
 * angiotool.nci.nih.gov
 *
 * Based on Wayne Rasband. ImageJ ThresholdToSelection
 * http://rsbweb.nih.gov/ij/
 */

package AngioTool;

import ij.IJ;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.process.*;

import java.awt.Polygon;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;


public class ThresholdToSelection {
    ImageProcessor ip;
    public float min, max;
    int w, h;

    public Roi convert(ImageProcessor ip) {
        this.ip = ip;
        min = (float)ip.getMinThreshold();
        max = (float)ip.getMaxThreshold();
        w = ip.getWidth();
        h = ip.getHeight();
        return getRoi();
    }

    final boolean selected(int x, int y) {
        float v = ip.getf(x,y);
        return v>=min && v<=max;
    }

    static class Outline {
        int[] x, y;
        int first, last, reserved;
        final int GROW = 10;

        public Outline() {
            reserved = GROW;
            x = new int[reserved];
            y = new int[reserved];
            first = last = GROW / 2;
        }

        private void needs(int newCount, int offset) {
            if (newCount > reserved || (offset > first)) {
                if (newCount < reserved + GROW + 1)
                    newCount = reserved + GROW + 1;
                int[] newX = new int[newCount];
                int[] newY = new int[newCount];
                System.arraycopy(x, 0, newX, offset, last);
                System.arraycopy(y, 0, newY, offset, last);
                x = newX;
                y = newY;
                first += offset;
                last += offset;
                reserved = newCount;
            }
        }

        public void push(int x, int y) {
            needs(last + 1, 0);
            this.x[last] = x;
            this.y[last] = y;
            last++;
        }

        public void shift(int x, int y) {
            needs(last + 1, GROW);
            first--;
            this.x[first] = x;
            this.y[first] = y;
        }

        public void push(Outline o) {
            int count = o.last - o.first;
            needs(last + count, 0);
            System.arraycopy(o.x, o.first, x, last, count);
            System.arraycopy(o.y, o.first, y, last, count);
            last += count;
        }

        public void shift(Outline o) {
            int count = o.last - o.first;
            needs(last + count + GROW, count + GROW);
            first -= count;
            System.arraycopy(o.x, o.first, x, first, count);
            System.arraycopy(o.y, o.first, y, first, count);
        }

        public Polygon getPolygon() {
            int i, j=first+1;
            for (i=first+1; i+1<last; j++) {
                int x1 = x[j] - x[j - 1];
                int y1 = y[j] - y[j - 1];
                int x2 = x[j + 1] - x[j];
                int y2 = y[j + 1] - y[j];
                if (x1 * y2 == x2 * y1) {
                    // merge i + 1 into i
                    last--;
                    continue;
                }
                if (i != j) {
                    x[i] = x[j];
                    y[i] = y[j];
                }
                i++;
            }

            int x1 = x[j] - x[j-1];
            int y1 = y[j] - y[j-1];
            int x2 = x[first] - x[j];
            int y2 = y[first] - y[j];
            if (x1*y2==x2*y1)
                last--;
            else {
                x[i] = x[j];
                y[i] = y[j];
            }
            int count = last - first;
            int[] xNew = new int[count];
            int[] yNew = new int[count];
            System.arraycopy(x, first, xNew, 0, count);
            System.arraycopy(y, first, yNew, 0, count);
            return new Polygon(xNew, yNew, count);
        }

        public String toString() {
            String res = "(first:" + first + ",last:" + last +
                ",reserved:" + reserved + ":";
            if (last > x.length) System.err.println("ERROR!");
            for (int i = first; i < last && i < x.length; i++)
                res += "(" + x[i] + "," + y[i] + ")";
            return res + ")";
        }
    }

    Roi getRoi() {
        boolean[] prevRow, thisRow;
        ArrayList polygons = new ArrayList();
        Outline[] outline;
        int progressInc = Math.max(h/50, 1);

        prevRow = new boolean[w + 2];
        thisRow = new boolean[w + 2];
        outline = new Outline[w + 1];

        for (int y = 0; y <= h; y++) {
            boolean[] b = prevRow; prevRow = thisRow; thisRow = b;
            for (int x = 0; x <= w; x++) {
                if (y < h && x < w)
                    thisRow[x + 1] = selected(x, y);
                else
                    thisRow[x + 1] = false;
                if (thisRow[x + 1]) {
                    if (!prevRow[x + 1]) {

                        if (outline[x] == null) {
                            if (outline[x + 1] == null) {
                                outline[x + 1] = outline[x] = new Outline();
                                outline[x].push(x + 1, y);
                                outline[x].push(x, y);
                            } else {
                                outline[x] = outline[x + 1];
                                outline[x + 1] = null;
                                outline[x].push(x, y);
                            }
                        } else {
                            if (outline[x + 1] == null) {
                                outline[x + 1] = outline[x];
                                outline[x] = null;
                                outline[x + 1].shift(x + 1, y);
                            } else if (outline[x + 1] == outline[x]) {
                                //System.err.println("subtract " + outline[x]);
                                polygons.add(outline[x].getPolygon()); // MINUS
                                outline[x] = outline[x + 1] = null;
                            } else {
                                outline[x].shift(outline[x + 1]);
                                for (int x1 = 0; x1 <= w; x1++)
                                    if (x1 != x + 1 && outline[x1] == outline[x + 1]) {
                                        outline[x1] = outline[x];
                                        outline[x] = outline[x + 1] = null;
                                        break;
                                    }
                                if (outline[x] != null)
                                    throw new RuntimeException("assertion failed");
                            }
                        }
                    }
                    if (!thisRow[x]) {
                        // left edge
                        if (outline[x] == null)
                            throw new RuntimeException("assertion failed!");
                        outline[x].push(x, y + 1);
                    }
                } else {
                    if (prevRow[x + 1]) {
  
                        if (outline[x] == null) {
                            if (outline[x + 1] == null) {
                                outline[x] = outline[x + 1] = new Outline();
                                outline[x].push(x, y);
                                outline[x].push(x + 1, y);
                            } else {
                                outline[x] = outline[x + 1];
                                outline[x + 1] = null;
                                outline[x].shift(x, y);
                            }
                        } else if (outline[x + 1] == null) {
                            outline[x + 1] = outline[x];
                            outline[x] = null;
                            outline[x + 1].push(x + 1, y);
                        } else if (outline[x + 1] == outline[x]) {
                            polygons.add(outline[x].getPolygon()); 
                            outline[x] = outline[x + 1] = null;
                        } else {
                            outline[x].push(outline[x + 1]);
                            for (int x1 = 0; x1 <= w; x1++)
                                if (x1 != x + 1 && outline[x1] == outline[x + 1]) {
                                    outline[x1] = outline[x];
                                    outline[x] = outline[x + 1] = null;
                                    break;
                                }
                            if (outline[x] != null)
                                throw new RuntimeException("assertion failed");
                        }
                    }
                    if (thisRow[x]) {
                        if (outline[x] == null)
                            throw new RuntimeException("assertion failed");
                        outline[x].shift(x, y + 1);
                    }
                }
            }
            if ((y&progressInc)==0) IJ.showProgress(y + 1, h + 1);
        }

        GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        for (int i = 0; i < polygons.size(); i++)
            path.append((Polygon)polygons.get(i), false);

        ShapeRoi shape = new ShapeRoi(path);
        Roi roi = shape!=null?shape.shapeToRoi():null;

        if (roi!=null){
            return roi;
            }
        else {
            return shape;
            }
    }
}
