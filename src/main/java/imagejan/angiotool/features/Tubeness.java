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
 * angiotool.nci.nih.gov
 *
 * Based on Stephan Preibisch
 * http://www.longair.net/edinburgh/imagej/tubeness/"
 */

package Tubeness.features;

import ij.*;


public class Tubeness  {

    public ImagePlus runTubeness (ImagePlus original, int threshold, double [] _sigma, boolean showResult){
        if (original == null)
            throw new UnsupportedOperationException("No image to compute tubeness.");

        TubenessProcessor tp = new TubenessProcessor(threshold, _sigma);

        ImagePlus result = tp.generateImage(original);
        result.setTitle("Tubeness of " + original.getTitle());

        if (showResult) result.show();

        return result;
    }
}
