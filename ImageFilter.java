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

package GUI;

import java.io.File;
import javax.swing.filechooser.*;


public class ImageFilter extends FileFilter {

    public final static String bmp = "bmp";
    public final static String BMP = "BMP";
    public final static String dcm = "dcm";
    public final static String DCM = "DCM";
    public final static String gif = "gif";
    public final static String GIF = "GIF";
    public final static String jpeg = "jpeg";
    public final static String JPEG = "JPEG";
    public final static String jpg = "jpg";
    public final static String JPG = "JPG";
    public final static String png = "png";
    public final static String PNG = "PNG";
    public final static String tiff = "tiff";
    public final static String TIFF = "TIFF";
    public final static String tif = "tif";
    public final static String TIF = "TIF";


    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);
        if (extension != null) {
            if (extension.equals(dcm) ||
                extension.equals(DCM) ||
                extension.equals(tiff) ||
                extension.equals(TIFF) ||
                extension.equals(tif) ||
                extension.equals(TIF) ||
                extension.equals(bmp) ||
                extension.equals(BMP) ||
                extension.equals(gif) ||
                extension.equals(GIF) ||
                extension.equals(jpeg) ||
                extension.equals(JPEG) ||
                extension.equals(jpg) ||
                extension.equals(JPG) ||
                extension.equals(png) ||
                extension.equals(PNG)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "bmp, dcm, gif, jpg, jpeg, png, tif, tiff";
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
