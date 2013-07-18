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
 */

package Lacunarity;

import ij.ImagePlus;
import ij.measure.CurveFitter;
import ij.process.ImageProcessor;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class Lacunarity {

    private int boxmov = 5; 
    private int numberOfBins = 10;
    private int minSize = 10; 
    private int imgWidth;
    private int imgHeight;
    private int smallDimension;
    private int rWidth;
    private int rHeight;

    private ArrayList <Integer> El3 = new ArrayList <Integer>();
    private ArrayList <Double> Elamda3 = new ArrayList <Double>();
    private ArrayList <Double> Eonepluslamda3 = new ArrayList <Double>();
    private ArrayList <Integer> epsilon3 = new ArrayList <Integer>();

    private ImagePlus imp;
    private ImageProcessor ip; 
    
    private Rectangle r; 

    private double averageEl;
    private double stdevEl;

    private boolean whiteForeground = true;

    private int [] boxS; 

    public Lacunarity (ImagePlus imp, int numberOfBins, int minSize, int boxmov, boolean whiteForeGround){

        this.imp = imp;
        this.ip = imp.getProcessor();

        this.numberOfBins = numberOfBins;
        this.minSize = minSize;
        this.boxmov = boxmov;
        this.whiteForeground = whiteForeGround;

        int [][] img = imp.getProcessor().getIntArray();
	ArrayList<Point> array = new ArrayList<Point>();
        for (int i = 0; i< img.length; i++){
            for (int ii = 0; ii<img[0].length; ii++){
                if (whiteForeground){
                    if (img [i][ii]==255) {
                        array.add(new Point (i, ii));
                    }
                } else {
                    if (img [i][ii]==0) {
                        array.add(new Point (i, ii));
                    }
                }
            }
        }

        Collections.sort(array, new PointCompareY());
        int minY = array.get(0).y;
        int maxY = array.get(array.size()-1).y;
        Collections.sort(array, new PointCompareX());
        int minX = array.get(0).x;
        int maxX = array.get(array.size()-1).x;

        Rectangle r2 = new Rectangle (minX, minY, (maxX-minX), (maxY-minY));

        imgWidth = ip.getWidth();
        imgHeight = ip.getHeight();
        rWidth = maxX-minX;
        rHeight = maxY-minY;

        boxS = computeBoxesBins2(rWidth, rHeight, minSize);

        for (int i = 0; i<boxS.length; i++){
            int boxsize = boxS[i];

            El3.clear();

            slidingBox5 (img, r2,boxsize, boxmov);

            Elamda3.add(lacunarity (El3));
            Eonepluslamda3.add(onePlusLacunarity (El3));
        }
    }

    private int [] computeBoxesBins2 (int width, int height, int minSize){

        int [] bins = new int [numberOfBins];

        this.smallDimension = width < height ? width : height;

        int factor = (int)Math.floor ( (smallDimension - minSize)/numberOfBins );

        bins[0] = minSize;

        for (int i = 1; i<numberOfBins-1; i++){
            bins[i] = bins[i-1] + factor;
        }

        bins[numberOfBins-1] = smallDimension;

        return bins;
    }

    public void slidingBox5 (int [][]imageArray, Rectangle r, int boxsize, int boxmov){
        int numXBox = computeNumXBox2 (r.width, boxmov, boxsize);
        int numYBox = computeNumYBox2 (r.height, boxmov, boxsize);
        
        Box [][] b = new Box [numXBox][numYBox];

        b[0][0] = firstBoxPixelMass (imageArray, r.x+(0*boxmov), r.y+(0*boxmov), boxsize);

        for (int i = 0; i<numXBox ; i++){

            if (i==0) {
                b[i][0] = firstBoxPixelMass (imageArray, r.x+(i*boxmov), r.y+(0*boxmov), boxsize);
            } else {
                b[i][0] = nextHorizontalBoxPixelMass (imageArray, b[i-1][0], r.x+((i)*boxmov), r.y+(0*boxmov), boxsize);
            }


            for (int ii = 0; ii<numYBox-1; ii++){
                b[i][ii+1] = nextBoxPixelMass (imageArray, b[i][ii], r.x+((i)*boxmov), r.y+((ii+1)*boxmov), boxsize);
            }
        }

        for (int i = 0; i<numXBox ; i++){
            for (int ii = 0; ii<numYBox; ii++){
                int count = b[i][ii].pixelCount;
                if (count>0){
                    El3.add(count);
                } else {
                    El3.add(count);
                }
            }
        }
    }

    public Box firstBoxPixelMass (int [][] imageArray, int xStart, int yStart, int boxsize){
        int count = 0;
        int firstColumnPixelCount = 0;
        int firstRawPixelCount = 0;

        int xBorder = (xStart+boxsize)>imgWidth ? imgWidth : (xStart+boxsize);
        int yBorder = (yStart+boxsize)>imgHeight ? imgHeight : (yStart+boxsize);

        Box b = new Box ();
        for (int x = xStart; x < xBorder; x++){
            for (int y = yStart; y< yBorder; y++){
                if (whiteForeground){
                    if (imageArray [x][y] == 255){
                        count++;
                    }
                }else {
                    if (imageArray [x][y] == 0){
                        count++;
                    }
                }

                if (whiteForeground){
                    if (x<boxmov && imageArray [x][y] == 255)
                        firstColumnPixelCount++;
                    if (y<boxmov && imageArray [x][y] == 255)
                        firstRawPixelCount++;
                } else {
                    if (x<boxmov && imageArray [x][y] == 0)
                        firstColumnPixelCount++;
                    if (y<boxmov && imageArray [x][y] == 0)
                        firstRawPixelCount++;
                }
            }
        }

        b.pixelCount = count;
        b.xStart = xStart;
        b.yStart = yStart;
        b.xBorder = xBorder;
        b.yBorder = yBorder;
        b.firstColumnPixelCount = firstColumnPixelCount;
        b.firstRawPixelCount = firstRawPixelCount;

        return b;
    }

    public Box nextHorizontalBoxPixelMass (int [][] imageArray, Box previousBox, int xStart, int yStart, int boxsize){
        int count = 0;
        int firstColumnPixelCount = 0;
        int firstRawPixelCount = 0;
        int lastColumnPixelCount = 0;

        int xBorder = (xStart+boxsize)>imgWidth ? imgWidth : (xStart+boxsize);
        int yBorder = (yStart+boxsize)>imgHeight ? imgHeight : (yStart+boxsize);

        Box b = new Box ();

        int rxStartF = xStart;
        int ryStartF = yStart;

        int rxWidthF = (rxStartF + boxsize)> imgWidth ? boxsize - (rxStartF+boxsize-imgWidth): boxsize;
        int ryWidthF = (ryStartF+boxmov)> imgHeight ? (boxmov-(ryStartF+boxmov-imgHeight)) : boxmov;
        firstRawPixelCount = pixelMassInRectangle (imageArray, new Rectangle (rxStartF, ryStartF, rxWidthF, ryWidthF));

        rxWidthF = (rxStartF+boxmov)> imgWidth ? (boxmov-(rxStartF+boxmov-imgWidth)) : boxmov;
        ryWidthF = (ryStartF + boxsize)> imgHeight ? boxsize - (ryStartF+boxsize-imgHeight): boxsize;
        firstColumnPixelCount = pixelMassInRectangle (imageArray, new Rectangle (rxStartF, ryStartF, rxWidthF, ryWidthF));

        int rxStartL = xStart+boxsize-boxmov;
        int rxWidthL = (rxStartL+boxmov)> imgWidth ? (boxmov-(rxStartL+boxmov-imgWidth)) : boxmov;
        lastColumnPixelCount = pixelMassInRectangle (imageArray, new Rectangle (rxStartL, ryStartF, rxWidthL, ryWidthF));

        count = previousBox.pixelCount - previousBox.firstColumnPixelCount + lastColumnPixelCount;

        b.pixelCount = count;
        b.xStart = xStart;
        b.yStart = yStart;
        b.xBorder = xBorder;
        b.yBorder = yBorder;
        b.firstColumnPixelCount = firstColumnPixelCount;
        b.firstRawPixelCount = firstRawPixelCount;

        return b;
    }

    public Box nextBoxPixelMass (int [][] imageArray, Box previousBox, int xStart, int yStart, int boxsize){
        int count = 0;
        int firstColumnPixelCount = 0;
        int firstRawPixelCount = 0;
        int lastRawPixelCount = 0;

        int xBorder = (xStart+boxsize)>imgWidth ? imgWidth : (xStart+boxsize);
        int yBorder = (yStart+boxsize)>imgHeight ? imgHeight : (yStart+boxsize);

        Box b = new Box ();

        int rxStartF = xStart;
        int rxWidthF = (rxStartF + boxsize)> imgWidth ? boxsize - (rxStartF+boxsize-imgWidth): boxsize;
        int ryStartF = yStart;
        int ryWidthF = (ryStartF+boxmov)> imgHeight ? (boxmov-(ryStartF+boxmov-imgHeight)) : boxmov;
        firstRawPixelCount = pixelMassInRectangle (imageArray, new Rectangle (rxStartF, ryStartF, rxWidthF, ryWidthF));

        int ryStartL = yStart+boxsize-boxmov;
        int ryWidthL = (ryStartL+boxmov)> imgHeight ? (boxmov-(ryStartL+boxmov-imgHeight)) : boxmov;
        lastRawPixelCount = pixelMassInRectangle (imageArray, new Rectangle (rxStartF, ryStartL, rxWidthF, ryWidthL));

        count = previousBox.pixelCount - previousBox.firstRawPixelCount + lastRawPixelCount;

        b.pixelCount = count;
        b.xStart = xStart;
        b.yStart = yStart;
        b.xBorder = xBorder;
        b.yBorder = yBorder;
        b.firstColumnPixelCount = firstColumnPixelCount;
        b.firstRawPixelCount = firstRawPixelCount;

        return b;
    }

    public int pixelMassInRectangle (int [][] imageArray, Rectangle r){
        int count = 0;

        for (int x = r.x; x < r.x+r.width; x++){
            for (int y = r.y; y < r.y+r.height; y++){
                if (whiteForeground){
                    if (imageArray [x][y] == 255){
                        count++;
                    }
                } else {
                    if (imageArray [x][y] == 0){
                        count++;
                    }
                }
            }
        }
        return count;
    }
    
    public static int computeNumXBox2 (int width, int boxmov, int boxsize){

        double numXBox =  (double)width/(double)boxmov;
        double factor = (double)boxsize/(double)boxmov;
        
        return (int)Math.ceil(numXBox-factor+1);
    }

    public static int computeNumYBox2 (int height, int boxmov, int boxsize){

        double numYBox = (double)height/(double)boxmov;
        double factor = (double)boxsize/(double)boxmov;

        return (int)Math.ceil(numYBox-factor+1);
    }


    public double average (ArrayList l){
        double average = 0.0;
        for (int i = 0; i<l.size(); i++){
            average += (Integer)l.get(i);
        }
        return average/l.size();
    }

    public double averageDouble (ArrayList l){
        double average = 0.0;
        for (int i = 0; i<l.size(); i++){
            average += (Double)l.get(i);
        }
        return average/l.size();
    }

    public double stdev2 (ArrayList l, double average){
        double stdev = 0.0;
        for (int i = 0; i<l.size(); i++){
            stdev += Math.pow(((Integer)l.get(i) - average), 2.0);
        }
        return Math.sqrt(1.0 / l.size()* stdev);
    }

    public double stdev (ArrayList l){
        double average = average (l);
        double stdev = 0.0;
        for (int i = 0; i<l.size(); i++){
            stdev += Math.pow(((Integer)l.get(i) - average), 2.0);
        }
        return Math.sqrt(1.0 / l.size()* stdev);
    }

    
    private double lacunarity (ArrayList l){
        return 0.0 + Math.pow((stdev(l)/average(l)), 2.0);
    }

    private double onePlusLacunarity (ArrayList l){
        return 1.0 + Math.pow((stdev(l)/average(l)), 2.0);
    }

    public ArrayList getEl3(){
        return Elamda3;
    }

    public ArrayList getEoneplusl3(){
        return Eonepluslamda3;
    }
    
    public int [] getBoxes (){
        return boxS;
    }

    public double getEl3Slope(){

        ArrayList alEl = getEoneplusl3();
        int [] _boxes = getBoxes();

        double [] el = new double [alEl.size()];
        double [] boxes = new double [_boxes.length];

        for (int i=0; i<el.length; i++){
            el [i] = Math.log ((Double) alEl.get(i));
            boxes [i] = Math.log((double) _boxes[i]);
        }

        CurveFitter cf = new CurveFitter(boxes, el);
        cf.doFit(CurveFitter.STRAIGHT_LINE);
        double[] p = cf.getParams();

        return p[1];
    }

    public double getMeanEl(){
        return averageDouble(getEl3());
    }

    public double getMedialELacunarity (){
        double halfSmallDimension = smallDimension/2;

        double min = Double.MAX_VALUE;
        int medialBox = 0;
        double diff = Double.MAX_VALUE;
        for (int i=0; i<boxS.length; i++){
            diff = Math.abs(boxS[i]-halfSmallDimension);
            if (diff < min){
                min = diff;
                medialBox = i;
            }
        }
        return (Double)getEl3().get(medialBox);
    }

    public class PointCompareY implements Comparator<Point> {

        @Override
        public int compare(final Point a, final Point b) {
            return (a.y < b.y) ? -1 : ((a.y == b.y) ? 0 : 1);
        }
    }

    public class PointCompareX implements Comparator<Point> {

        @Override
        public int compare(final Point a, final Point b) {
            return (a.x < b.x) ? -1 : ((a.x == b.x) ? 0 : 1);
        }
    }
}