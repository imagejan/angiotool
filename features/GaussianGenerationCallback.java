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

public interface GaussianGenerationCallback {

    /* Any proportion >= 1.0 indicates completion.  A proportion less
     * than zero indicates that the generation of the Gaussian has
     * been cancelled. */

    public void proportionDone( double proportion );

}
