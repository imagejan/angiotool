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

package Utils;

import Tubeness.features.Tubeness;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.gui.Wand;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import ij.process.Blitter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import Skeleton.Skeletonize3D;
import AngioTool.ThresholdToSelection;
import AngioTool.PolygonPlus;
import java.io.File;

public class Utils {

    public static final String NAME = "AngioTool";
    public static final String VERSION = "0.5a (May 25, 2011)";

    public final static String LOOKANDFEEL = "System";

    public final static String THEME = "Test";

    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String TIFF = "TIFF";
    public final static String tif = "tif";
    public final static String png = "png";
    public final static String bmp = "bmp";
    public final static String xls = "xls";
    public final static String xlsx = "xlsx";


    public static ImageProcessor skeletonize (ImageProcessor ip, String algorithm){
        if (ip.isBinary()){
            ImagePlus iplusThresholded = new ImagePlus ("iplusThresholded Kike", ip);

            if (algorithm.equals("ImageJ")){
                ByteProcessor ipThresholded2 = (ByteProcessor)iplusThresholded.getProcessor();
                ipThresholded2.invert();
                ipThresholded2.skeletonize();
            } else if (algorithm.equals("itk")) {
                Skeletonize3D s3d = new Skeletonize3D();
                s3d.setup("", iplusThresholded);
                ImageProcessor ipSkeleton = s3d.run(ip);
                return ipSkeleton;
            }
        }
        return null;
    }


    public static ArrayList <AnalyzeSkeleton.Point> computeActualJunctions (ArrayList <AnalyzeSkeleton.Point> jv){
        ArrayList <AnalyzeSkeleton.Point> removed = new ArrayList <AnalyzeSkeleton.Point>();
        for (int i = 0; i<jv.size(); i++){
            AnalyzeSkeleton.Point jv1 = jv.get(i);
            for (int ii = 0; ii<jv.size(); ii++){
                AnalyzeSkeleton.Point jv2 = jv.get(ii);
                if (isNeighbor(jv1, jv2)){
                    removed.add (jv2);
                    jv.remove(ii);

                }
            }
        }
        return removed;
    }

    private static boolean isNeighbor (AnalyzeSkeleton.Point p1, AnalyzeSkeleton.Point p2){
        if (
            ((p1.x == p2.x-1) && (p1.y == p2.y-1)) ||
            ((p1.x == p2.x-1) && (p1.y == p2.y)) ||
            ((p1.x == p2.x-1) && (p1.y == p2.y+1)) ||
            ((p1.x == p2.x) && (p1.y == p2.y-1)) ||
            ((p1.x == p2.x) && (p1.y == p2.y+1)) ||
            ((p1.x == p2.x+1) && (p1.y == p2.y-1)) ||
            ((p1.x == p2.x+1) && (p1.y == p2.y)) ||
            ((p1.x == p2.x+1) && (p1.y == p2.y+1))
        ) {
            return true;
        }
        else
            return false;
    }


    public static boolean checkJavaVersion (int Major, int minor, int point) {
        String version = System.getProperty("java.version");
        int M = Integer.parseInt(version.substring(0,1));
        int m = Integer.parseInt(version.substring(2, 3));
        int p = Integer.parseInt(version.substring(4, 5));
        if(M < Major || m < minor || p < point) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "JRE " + Major + "." + minor + "." + point + " or higher is required to run AllantoisAnalysis. "
                  + "Download the last JRE version from http://dlc.sun.com.edgesuite.net/jdk7/binaries/index.html\n" +
                  "", "Error", JOptionPane.ERROR_MESSAGE);

            System.exit(0);
        }
        return true;
    }

    public static boolean checkImageJVersion (int Major, int minor, String character) {
        String version = IJ.getVersion();
        int M = Integer.parseInt(version.substring(0,1));
        int m = Integer.parseInt(version.substring(2,4));
        String p = version.substring(4,5);
        if(M < Major || m < minor) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "ImageJ " + Major + "." + minor + "" + character + " or higher is required to run AllantoisAnalysis. "
                  + "Download the last ImageJ versio from http://rsbweb.nih.gov/ij/index.html", "Error", JOptionPane.ERROR_MESSAGE);

            System.exit(0);
        }
        return true;
    }

    public static long thresholdedPixelArea (ImageProcessor ip){
        if (ip.isBinary()){
            long countArea = 0;
            for (int i =  0; i<ip.getWidth(); i++){
                for (int ii = 0; ii<ip.getHeight(); ii++){
                    if (ip.getPixel(i, ii) == 255)
                        countArea++;
                }
            }
            return countArea;
        }
        return Long.MIN_VALUE;
    }


    public int thresholdedPixelArea (ImageProcessor ip, int foregroundColor) {
        if (ip.isBinary()){
            int countArea = 0;
            for (int i =  0; i<ip.getWidth(); i++){
                for (int ii = 0; ii<ip.getHeight(); ii++){
                    if (ip.getPixel(ii, ii) == foregroundColor)
                        countArea++;
                }
            }
            return countArea;
            }
        return Integer.MIN_VALUE;
    }


    public static PolygonPlus computeConvexHull (ImageProcessor thresholded){
        ImageProcessor ipConvex = thresholded.duplicate();
        Convex_Hull_Plus chp  = new Convex_Hull_Plus ();
        chp.run(ipConvex);
        PolygonRoi convexHullPolygonRoi = chp.getConvexHullPolygonRoi();
        PolygonPlus vqpol = new PolygonPlus (convexHullPolygonRoi.getPolygon());
        return vqpol;
    }

    public static int roundIntegerToNearestUpperTenth(int a){
        int remainder = a%10;
        while (remainder!=0){
            a+=1;
            remainder = a%10;
        }
        return a;
    }

    public static ImageProcessor addSigma (double [] sigmas, ImageProcessor ipOriginal, ImageProcessor ip){
        Tubeness t = new Tubeness ();
        ImagePlus iplus = t.runTubeness(new ImagePlus ("", ipOriginal), 100, sigmas, false);
        ImageProcessor ip2 = iplus.getProcessor();
        ip2.copyBits(ip, 0, 0, Blitter.MAX);
        return ip2;
    }

    public static ImageProcessor addSigma (double [] sigmas, ImageProcessor ipOriginal, ImageProcessor ip, Tubeness t){
        ImagePlus iplus = t.runTubeness(new ImagePlus ("", ipOriginal), 100, sigmas, false);
        ImageProcessor ip2 = iplus.getProcessor();
        ip2.copyBits(ip, 0, 0, Blitter.MAX);
        return ip2;
    }


   public static int[] getFloatHistogram2 (ImagePlus iplus) {

       int nBins = 256;

       int[] histogram = new int[nBins];
        ImageProcessor ip = iplus.getProcessor();

        float histMin = Float.MAX_VALUE;
        float histMax = Float.MIN_VALUE;

        for (int y=0; y<iplus.getHeight(); y++) {
            //int i = y * width + roiX;
            for (int x=0; x<iplus.getWidth(); x++) {
                float v =  ip.getPixelValue(x, y);
                if (v>histMax) histMax = v;
                else if (v<histMin) histMin = v;
            }
        }

        double scale = nBins/(histMax-histMin);

        int index;
        for (int y=0; y<iplus.getHeight(); y++) {
            for (int x=0; x<iplus.getWidth(); x++) {
                float v =  ip.getPixelValue(x, y);
                index = (int)(scale*(v-histMin));
                if (index>=nBins)
                    index = nBins-1;

                histogram[index]++;
            }
        }
        return histogram;
    }

    public static ShapeRoi shapeRoiSplines2 (ShapeRoi sr, int fraction){
        Roi [] r = sr.getRois();

        ShapeRoi first = new ShapeRoi (new Roi (sr.getBounds()));
        ShapeRoi result = new ShapeRoi (first);
        for (int i = 0; i<r.length; i++){
            PolygonRoi pr = new PolygonRoi (r[i].getPolygon(), Roi.POLYGON);
            int coordinates = pr.getNCoordinates();
            double area = new PolygonPlus (pr.getPolygon()).area();
            pr.fitSpline(pr.getNCoordinates()/fraction);
            result.xor (new ShapeRoi (pr));
        }
        result.xor (first);
        return result;
    }
   

    public static int findHistogramMax (int [] histogram){
        double max = 0.0;
        int index=0;
        for (int i = 0; i<histogram.length; i++){
            if (max<histogram[i]){
                max = histogram[i];
                index = i;
            }
        }
        return index;
    }



    public static void fillHoles (ImagePlus iplus, int minSize, int maxSize, double minCircularity, double maxCircularity, int color){
        ImageProcessor result = iplus.getProcessor();
        if (!result.isBinary()){
        }

        PolygonRoi [] pr = findAndAnalyzeObjects (iplus, minSize, maxSize, minCircularity, maxCircularity, result);

        if (pr!=null){
            result.setColor(color);
            for (int i = 0; i< pr.length; i++) {
                result.fill(pr[i]);
            }
            iplus.setProcessor(result);
        }
    }

    public static Roi thresholdToSelection(ImagePlus iplus){
        if (iplus.getProcessor().getMaxThreshold() == ImageProcessor.NO_THRESHOLD){
            System.err.println ("In thresholdToSelection. ImagePlus " + iplus.toString() +
                    " does not have threshold levels defined");
            return null;
        }
        ThresholdToSelection tts = new ThresholdToSelection ();
        Roi r = tts.convert(iplus.getProcessor());
        iplus.setRoi(r);
        return r;
    }


    public static void threshold(ImageProcessor ip, int minLevel, int maxLevel) {
        if (ip instanceof ByteProcessor){
            byte [] pixels = (byte [])ip.getPixels();
            int width = ip.getWidth();
            int height = ip.getHeight();

            for (int i=0; i<width*height; i++) {
                if ((pixels[i] & 0xff) <= minLevel || (pixels[i] & 0xff) > maxLevel)
                    pixels[i] = 0;
                else
                    pixels[i] = (byte)255;
            }
        } else
            throw new IllegalArgumentException("ByteProcessor required");
    }

    public static void selectionToThreshold (Roi r, ImagePlus iplus){
        ImageProcessor ip = iplus.getProcessor();
        ip.setColor(255);
        ip.fill(r);
    }


    public static PolygonRoi [] findAndAnalyzeObjects (ImagePlus _iplus, int _minSize, int _maxSize, double _minCircularity, double _maxCircularity, ImageProcessor _ip){
        int options;
            options = (ParticleAnalyzer.RECORD_STARTS | ParticleAnalyzer.SHOW_PROGRESS);
        int measurements = ParticleAnalyzer.AREA;
        ResultsTable rt = new ResultsTable();
        ParticleAnalyzer pa = new ParticleAnalyzer(options, measurements, rt, _minSize, _maxSize, _minCircularity, _maxCircularity);
        pa.analyze (new ImagePlus("findAndAnalyzeObjects", _ip), _ip);

        int count = rt.getCounter();
        if (count <= 0){
            return null;
        }
        if (rt.getValueAsDouble(0,0) == _ip.getWidth()* _ip.getHeight()){
            return null;
        }

        float[] Xstart = rt.getColumn(rt.getColumnIndex("XStart"));
        float[] Ystart = rt.getColumn(rt.getColumnIndex("YStart"));

        int DataArrayLength = Xstart.length;

        PolygonRoi [] pr = new PolygonRoi [DataArrayLength];

        for (int i = 0; i < pr.length; i++){
            Wand w = new Wand (_ip);
            w.autoOutline ((int)Xstart [i], (int)Ystart[i], 254, 255);	//the black regions are 0, the white regions 255
            pr [i] = new PolygonRoi (w.xpoints, w.ypoints, w.npoints, Roi.POLYGON);
        }
        return pr;
    }

    public static PolygonRoi [] findAndAnalyzeObjects (ImagePlus _iplus, int _minSize, int _maxSize, ImageProcessor _ip){
        return findAndAnalyzeObjects (_iplus, _minSize, _maxSize, 0.0, 1.0, _ip);
    }

    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}