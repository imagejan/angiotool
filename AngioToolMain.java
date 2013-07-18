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

package AngioTool;

import javax.swing.JOptionPane;

public class AngioToolMain  {

    public static void main(String[] args) {     

        if (!ApplicationInstanceManager.registerInstance()) {

            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Another instance of this application is already running.  Exiting...", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);            
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AngioTool();
            }
        });
    }
}
