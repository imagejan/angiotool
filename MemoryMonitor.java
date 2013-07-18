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

import GUI.TransparentTextField;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.Timer;


public class MemoryMonitor extends Timer implements ActionListener {

    private TransparentTextField textField;
    private Color OK, NO_OK;

    public MemoryMonitor(int interval, TransparentTextField textField) {
        super(interval, null);
        this.textField = textField;
        this.textField.setOpaque(true);
        this.textField.setAlpha(1.0f);
        OK = new Color (0.0f, 1.0f, 0.0f);
        NO_OK = new Color (1.0f, 0.0f, 0.0f);
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        double m = (double)Runtime.getRuntime().maxMemory()/(1024*1024);
        double f = (double)Runtime.getRuntime().freeMemory()/(1024*1024);
        double t = (double)Runtime.getRuntime().totalMemory()/(1024*1024);
        double u = t - f;

        NumberFormat formatter = new DecimalFormat("#.##");
        String free = formatter.format(f);
        String total = formatter.format (t);
        String maximum = formatter.format (m);
        String used = formatter.format(u);

        textField.setText("");
        if ((u*100)/m <= 90){
            textField.setBackground(OK);
        }
        else {
            textField.setBackground(NO_OK);
        }
        textField.setText(used + "/" + maximum);
    }
}
