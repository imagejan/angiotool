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

import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import AngioTool.PolygonPlus;

public class ForkShapeRoiSplines extends RecursiveTask <ShapeRoi> {

    private int mLength;
    private int mStart;
    private int sThreshold = 60;
    private int fraction;

    private ShapeRoi originalShapeRoi;
    private Roi [] originalRoi;
    private ShapeRoi src;


    public ForkShapeRoiSplines () {

    }

    public ForkShapeRoiSplines (ShapeRoi sr, int fraction, int start, int length) {
        this.src = sr;
        this.originalShapeRoi = sr; 
        this.originalRoi = sr.getRois();
        this.mStart = start;
        this.mLength = length;
        this.fraction = fraction;
    }

    public ShapeRoi computeDirectly(){

        ShapeRoi first = new ShapeRoi (new Roi (src.getBounds()));
        ShapeRoi result = new ShapeRoi (first);
        for (int i = mStart; i < mStart + mLength; i++){
            PolygonRoi pr = new PolygonRoi (originalRoi[i].getPolygon(), Roi.POLYGON);
            int coordinates = pr.getNCoordinates();
            double area = new PolygonPlus (pr.getPolygon()).area();
            pr.fitSpline(pr.getNCoordinates()/fraction);
            result.xor (new ShapeRoi (pr));
        }
        result.xor (first);
        return result;
    }
    

    @Override
    protected ShapeRoi compute() {
        if (mLength < sThreshold) {
            return computeDirectly();

        } else {
            int split = mLength / 2;
            int remainder = mLength % 2;
            int secondHalf = split + remainder;
            ForkShapeRoiSplines left  = new ForkShapeRoiSplines (src, fraction, mStart,         split);
            ForkShapeRoiSplines right = new ForkShapeRoiSplines (src, fraction, mStart + split, secondHalf);

            right.fork();

            ShapeRoi a = (ShapeRoi) left.compute();

            a.xor((ShapeRoi)right.join());

            return a;
        }
    }

    public ShapeRoi computeSplines (ShapeRoi sr, int fraction){

        ShapeRoi ShapeRoiResult = new ShapeRoi (new Roi (0,0,0,0));
        originalShapeRoi = sr;

        ForkShapeRoiSplines fs = new ForkShapeRoiSplines(sr, fraction, 0, sr.getRois().length);

        ForkJoinPool pool = new ForkJoinPool();

        ShapeRoiResult = pool.invoke(fs);

        return ShapeRoiResult;
    }
}
