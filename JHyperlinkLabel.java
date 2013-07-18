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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

public class JHyperlinkLabel extends JLabel  {
    private Color underlineColor = null;

    public JHyperlinkLabel(String label) {
        super(label);

        setForeground(Color.BLUE.darker());
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(underlineColor == null ? getForeground() : underlineColor);

        Insets insets = getInsets();

        int left = insets.left;
        if (getIcon() != null)
        left += getIcon().getIconWidth() + getIconTextGap();

        g.drawLine(left, getHeight() - 1 - insets.bottom, (int) getPreferredSize().getWidth()
        - insets.right, getHeight() - 1 - insets.bottom);
    }

    public class HyperlinkLabelMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            System.out.println(getText());
            try {
                open(new URI("http://java.sun.com"));
            } catch (URISyntaxException ex) {
                Logger.getLogger(JHyperlinkLabel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void open(URI uri) throws URISyntaxException {
        if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                try {
                        desktop.browse(uri);
                } catch (IOException e) {
                }
        } else {
        }
    }

    public Color getUnderlineColor() {
        return underlineColor;
    }

    public void setUnderlineColor(Color underlineColor) {
        this.underlineColor = underlineColor;
    }
}
