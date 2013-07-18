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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class AngioToolAboutBox extends javax.swing.JDialog implements MouseListener{

    Point location;
    MouseEvent pressed;

    String filename = "";
    InputStream is = this.getClass().getClassLoader().getResourceAsStream("doc/AngioTool.html");

    
    public AngioToolAboutBox(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        
        MyHTMLEditorKit kit = new MyHTMLEditorKit();
        kit.setJar(getClass());

        initComponents();

        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension AboutBoxDimension = this.getSize();
        this.setLocation((screenDimension.width/2)-(AboutBoxDimension.width/2), (screenDimension.height/2)-(AboutBoxDimension.height/2));

        helpEditorPane.setEditorKit(kit);
        helpEditorPane.addHyperlinkListener(new AboutBoxHyperlinkListener());

        addMouseListener (this);

        FileReader reader = null;

        try {
            helpEditorPane.read(is, filename);
        } catch (IOException ex) {
            Logger.getLogger(AngioToolAboutBox.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeAboutBox() {
        dispose();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        aboutBoxPanel = new javax.swing.JPanel();
        javax.swing.JLabel appTitleLabel = new javax.swing.JLabel();
        javax.swing.JLabel appDescLabel = new javax.swing.JLabel();
        javax.swing.JLabel versionLabel = new javax.swing.JLabel();
        javax.swing.JLabel homepageLabel = new javax.swing.JLabel();
        javax.swing.JLabel imageLabel = new javax.swing.JLabel();
        javax.swing.JLabel homepageLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel homepageLabel2 = new javax.swing.JLabel();
        javax.swing.JLabel homepageLabel3 = new javax.swing.JLabel();
        javax.swing.JLabel homepageLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        helpEditorPane = new javax.swing.JEditorPane();
        closeAboutBox = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(620, 180));
        setResizable(false);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        aboutBoxPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        aboutBoxPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                aboutBoxPanelMousePressed(evt);
            }
        });
        aboutBoxPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                aboutBoxPanelMouseDragged(evt);
            }
        });
        aboutBoxPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        appTitleLabel.setFont(appTitleLabel.getFont().deriveFont(appTitleLabel.getFont().getStyle() | java.awt.Font.BOLD, appTitleLabel.getFont().getSize()+19));
        appTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        appTitleLabel.setText(Utils.Utils.NAME);
        aboutBoxPanel.add(appTitleLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 20, -1, -1));
        aboutBoxPanel.add(appDescLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 40, 270, -1));

        versionLabel.setFont(versionLabel.getFont().deriveFont(versionLabel.getFont().getStyle() | java.awt.Font.BOLD, versionLabel.getFont().getSize()+3));
        versionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        versionLabel.setText("Version " +  Utils.Utils.VERSION);
        aboutBoxPanel.add(versionLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 70, 200, -1));

        homepageLabel.setFont(homepageLabel.getFont().deriveFont(homepageLabel.getFont().getStyle() | java.awt.Font.BOLD, homepageLabel.getFont().getSize()+3));
        homepageLabel.setText("Laure Gambardella");
        aboutBoxPanel.add(homepageLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 190, -1, -1));

        imageLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ATIcon20 128x128.gif"))); // NOI18N
        aboutBoxPanel.add(imageLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, 130));

        homepageLabel1.setFont(homepageLabel1.getFont().deriveFont(homepageLabel1.getFont().getStyle() | java.awt.Font.BOLD, homepageLabel1.getFont().getSize()+3));
        homepageLabel1.setText("Contributors:");
        aboutBoxPanel.add(homepageLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 110, -1, -1));

        homepageLabel2.setFont(homepageLabel2.getFont().deriveFont(homepageLabel2.getFont().getStyle() | java.awt.Font.BOLD, homepageLabel2.getFont().getSize()+3));
        homepageLabel2.setText("Enrique Zudaire");
        aboutBoxPanel.add(homepageLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 130, -1, -1));

        homepageLabel3.setFont(homepageLabel3.getFont().deriveFont(homepageLabel3.getFont().getStyle() | java.awt.Font.BOLD, homepageLabel3.getFont().getSize()+3));
        homepageLabel3.setText("Chris Kurcz");
        aboutBoxPanel.add(homepageLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 150, -1, -1));

        homepageLabel4.setFont(homepageLabel4.getFont().deriveFont(homepageLabel4.getFont().getStyle() | java.awt.Font.BOLD, homepageLabel4.getFont().getSize()+3));
        homepageLabel4.setText("Sonja Vermeren");
        aboutBoxPanel.add(homepageLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 170, -1, -1));

        jScrollPane1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        helpEditorPane.setEditable(false);
        helpEditorPane.setMargin(new java.awt.Insets(4, 4, 4, 4));
        jScrollPane1.setViewportView(helpEditorPane);

        aboutBoxPanel.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 760, 470));

        getContentPane().add(aboutBoxPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 785, 710));

        closeAboutBox.setFont(new java.awt.Font("Tahoma", 1, 18));
        closeAboutBox.setText("Close");
        closeAboutBox.setOpaque(false);
        closeAboutBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeAboutBoxActionPerformed(evt);
            }
        });
        getContentPane().add(closeAboutBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(3, 715, 780, 30));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void aboutBoxPanelMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_aboutBoxPanelMouseDragged
         location = getLocation(location);
         int x = location.x - pressed.getX() + evt.getX();
         int y = location.y - pressed.getY() + evt.getY();
         setLocation(x, y);
    }//GEN-LAST:event_aboutBoxPanelMouseDragged

    private void aboutBoxPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_aboutBoxPanelMousePressed
        pressed = evt;
    }//GEN-LAST:event_aboutBoxPanelMousePressed

    private void closeAboutBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeAboutBoxActionPerformed
        closeAboutBox();
    }//GEN-LAST:event_closeAboutBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel aboutBoxPanel;
    private javax.swing.JButton closeAboutBox;
    private javax.swing.JEditorPane helpEditorPane;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void mouseClicked(MouseEvent e) {
        closeAboutBox();
    }

    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

    class AboutBoxHyperlinkListener implements HyperlinkListener {
        public void hyperlinkUpdate(HyperlinkEvent evt) {
            if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    try {
                        java.awt.Desktop.getDesktop().browse(evt.getURL().toURI());
                    } catch (URISyntaxException ex) {
                        Logger.getLogger(AngioToolAboutBox.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (IOException e) {
                }
            }
        }
    }

}
