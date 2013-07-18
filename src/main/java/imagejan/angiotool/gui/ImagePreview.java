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

import ij.*;
import javax.swing.*;
import java.beans.*;
import java.awt.*;
import java.io.File;
import javax.swing.border.TitledBorder;
import javax.swing.border.Border;

public class ImagePreview extends JComponent implements PropertyChangeListener {
    ImageIcon thumbnail = null;
    File file = null;
    int thumbnailSize = 450;
    private JComponent parent; 
    Dimension parentSize; 

    public ImagePreview(JFileChooser fc) {
        setPreferredSize(new Dimension(500, 700));

        Border blackline;
        blackline = BorderFactory.createLineBorder(Color.black);
        TitledBorder title;
        title = BorderFactory.createTitledBorder(blackline, "Image Preview");
        title.setTitleJustification(TitledBorder.CENTER);
        setBorder (title);	
        parent = fc;
        fc.addPropertyChangeListener(this);
    }

    public void loadImage() {
        if (file == null) {
            thumbnail = null;
            return;
        }

        ImagePlus ip = IJ.openImage (file.getPath());

        ImageIcon tmpIcon = new ImageIcon (ip.getImage());
        if (tmpIcon != null) {
            int tmpIconWidth = tmpIcon.getIconWidth();
            int tmpIconHeight = tmpIcon.getIconHeight(); 
            if (tmpIconWidth > tmpIconHeight) {
                thumbnail = new ImageIcon(tmpIcon.getImage().
                                getScaledInstance(thumbnailSize , (tmpIconHeight * thumbnailSize ) / tmpIconWidth ,
                                Image.SCALE_DEFAULT));
            } 
            if (tmpIconHeight > tmpIconWidth) {
                thumbnail = new ImageIcon(tmpIcon.getImage().
                                getScaledInstance((tmpIconWidth * thumbnailSize ) /tmpIconHeight, thumbnailSize ,
                                Image.SCALE_DEFAULT));
            } else {
                thumbnail = new ImageIcon(tmpIcon.getImage().
                                getScaledInstance(thumbnailSize , thumbnailSize ,
                                Image.SCALE_DEFAULT));
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        boolean update = false;
        String prop = e.getPropertyName();

        if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
            file = null;
            update = true;

        } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
            file = (File) e.getNewValue();
            if (file == null){
                update = false;
            } else {  

                if (file.isFile()){
                update = true;
                } else { 
                    update = false;
                }
            }
        }

        if (update) {
            thumbnail = null;
            if (isShowing()) {
                loadImage();
                repaint();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (thumbnail == null) {
            loadImage();
        }
        if (thumbnail != null) {
            int x = getWidth()/2 - thumbnail.getIconWidth()/2;
            int y = getHeight()/2 - thumbnail.getIconHeight()/2;
            if (y < 0){
                y = 2;
            }
            if (x < 5){
                x = 5;
            }
            thumbnail.paintIcon(this, g, x, y);
        }
    }
}
