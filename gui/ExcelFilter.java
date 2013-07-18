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

public class ExcelFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
                return true;
        }

        String extension = Utils.Utils.getExtension(f);
        if (extension != null) {
            if (extension.equals(Utils.Utils.xls)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    
    @Override
    public String getDescription() {
            return "xls";
    }
}