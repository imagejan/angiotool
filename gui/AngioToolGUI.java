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

import AngioTool.MemoryMonitor;
import AngioTool.RGBStackSplitter;
import AngioTool.Results;
import AngioTool.SaveToExcel;
import AnalyzeSkeleton.Edge;
import AnalyzeSkeleton.Graph;
import Lacunarity.Lacunarity;
import Utils.ForkShapeRoiSplines;
import Utils.Utils;
import com.jidesoft.swing.RangeSlider;
import Tubeness.features.Tubeness;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.process.Blitter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import kz.swing.markSlider;
import AngioTool.PolygonPlus;
import AngioTool.SwingWorker;

public class AngioToolGUI extends javax.swing.JFrame {

    private Image imgIcon; 

    private SwingWorker worker;

    private File currentDir;

    private File imageFile;

    private SaveToExcel ste;

    private boolean doScaling = false; 
    private boolean fillHoles = false; 
    private boolean smallParticles = false;


    private double resizingFactor = 1; 

    private boolean sigmaIsChanged = false; 
    private boolean fillHolesIsChanged = false; 
    private boolean smallParticlesIsChanged = false; 
    private boolean hideOverlay = false; 

    private ImagePlus imageOriginal; 
    private ImagePlus imageResult; 
    private ImagePlus imageThresholded; 
    private ImagePlus imageTubeness;

    private ImageProcessor ipOriginal; 
    private ImageProcessor ipThresholded; 
    private ImageProcessor tubenessIp;

    private int minSigma, maxSigma;
    private double[] firstSigma = {5.0};
    private ArrayList <Double> allSigmas;
    private ArrayList <Double> currentSigmas; 

    private ArrayList <sigmaImages> sI;
    private ArrayList <AnalyzeSkeleton.Point> al2; 
    private ArrayList <AnalyzeSkeleton.Point> removedJunctions; 
    private ArrayList <AnalyzeSkeleton.Point> endPoints;

    private PolygonPlus convexHull; 
    private double convexHullArea;

    private Overlay allantoisOverlay; 
    private Roi outlineRoi; 
    private PolygonRoi convexHullRoi; 
    private ArrayList <Roi> skeletonRoi; 
    private ArrayList <Roi> junctionsRoi; 
    private long thresholdedPixelArea = 0;

    private Graph [] graph; 

    private Color initialOutlineColor = Color.yellow;
    private Color initialSkeletonColor = Color.red;
    private Color initialBranchingPointColor = new Color (0,153,255);
    private Color initialConvexHullColor = new Color (204,255,255);

    private int initialOutlineSize = 1; 
    private int initialSkeletonSize = 1;
    private int initialBranchingPointsSize = 1;
    private int initialConvexHullSize = 1;

    private String imageResultFormat = "jpg";

    private AngioToolAboutBox aboutBox;

    private Icon lockedIcon, unlockedIcon;

    Date startDate, stopDate;

    ImagePlus imageThickness;

    AnalyzeSkeleton.SkeletonResult skelResult;

    private boolean computeLacunarity = true;
    private double ElSlope;
    private double medialELacunarity;
    private int [] lacunarityBoxes; 
    private ArrayList <Double> Elamdas = new ArrayList <Double>();
    private double meanEl;

    private double LinearScalingFactor = 0.0;
    private double AreaScalingFactor = 0.0;

    private String outputString; 
    private Results results;


    private class sigmaImages {
        double sigma;
        ImageProcessor tubenessImage;
        public sigmaImages (double sigma, ImageProcessor tubenessImage) {
            this.sigma = sigma;
            this.tubenessImage = tubenessImage;
        }
        public void showImage (){
            ImagePlus iplus = new ImagePlus ("sigma " + sigma, tubenessImage);
            iplus.show();
        }

        @Override
        public String toString (){
            return "sigma " + sigma + " tubenessIamge= " + tubenessImage;
        }
    }

    public AngioToolGUI() {
        
        initLookAndFeel();

        Utils.checkJavaVersion(1, 7, 0);
        Utils.checkImageJVersion(1, 44, "i");

        ClassLoader loader = getClass().getClassLoader();
        URL fileLocation=loader.getResource("images/ATIcon20.gif");
        imgIcon = Toolkit.getDefaultToolkit().getImage(fileLocation);
        setIconImage(imgIcon);

        currentDir = new File ("c:/");

        initComponents();

        MemoryMonitor mm = new MemoryMonitor (1000, memTransparentTextField);
        mm.start();

        ste = new SaveToExcel ();
        
        aboutBox = new AngioToolAboutBox (this, false);

        setFocusable(true);

        startDate = new Date();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fillHolesRangeSlider = new com.jidesoft.swing.RangeSlider();
        smallParticlesRangeSlider = new com.jidesoft.swing.RangeSlider();
        openImageButton = new javax.swing.JButton();
        AnalyzeButton = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();
        TabbedPane = new javax.swing.JTabbedPane();
        AnalysisTabPanel = new javax.swing.JPanel();
        thicknessIntensityPanel = new javax.swing.JPanel();
        sigmasMarkSlider = new kz.swing.markSlider();
        sigmasSpinner = new javax.swing.JSpinner();
        thresholdRangeSlider = new com.jidesoft.swing.RangeSlider();
        lowThresholdTextField = new javax.swing.JTextField();
        highThresholdTextField = new javax.swing.JTextField();
        backgroundParticlesPanel = new javax.swing.JPanel();
        smallParticlesCheckBox = new javax.swing.JCheckBox();
        fillHolesCheckBox = new javax.swing.JCheckBox();
        fillHolesSpinner = new javax.swing.JSpinner();
        removeSmallParticlesSpinner = new javax.swing.JSpinner();
        fillHolesRangeSlider2 = new javax.swing.JSlider();
        smallParticlesRangeSlider2 = new javax.swing.JSlider();
        savingPreferencesPanel = new javax.swing.JPanel();
        saveResultsToButton = new javax.swing.JButton();
        saveResultsToTextField = new javax.swing.JTextField();
        saveResultImageCheckBox = new javax.swing.JCheckBox();
        toggleOverlayToggleButton = new javax.swing.JToggleButton();
        settingsTabPanel = new javax.swing.JPanel();
        resizeImagePanel = new javax.swing.JPanel();
        resizeImageCheckBox = new javax.swing.JCheckBox();
        resizingFactorLabel = new javax.swing.JLabel();
        resizingFactorSpinner = new javax.swing.JSpinner();
        unlockButton = new javax.swing.JButton();
        setScalePanel = new javax.swing.JPanel();
        distanceInPixelsLabel = new javax.swing.JLabel();
        distanceInMMLabel = new javax.swing.JLabel();
        scaleLabel = new javax.swing.JLabel();
        scaleTextField = new javax.swing.JTextField();
        distanceInPixelsNumberTextField = new GUI.JNumberTextField();
        distanceInPixelsNumberTextField.setFormat(GUI.JNumberTextField.NUMERIC);
        distanceInMMNumberTextField = new GUI.JNumberTextField();
        clearCalibrationButton = new javax.swing.JButton();
        overlaySettingsPanel = new javax.swing.JPanel();
        showOutlineCheckBox = new javax.swing.JCheckBox();
        showBranchingPointsCheckBox = new javax.swing.JCheckBox();
        showConvexHullCheckBox = new javax.swing.JCheckBox();
        showSkeletonCheckBox = new javax.swing.JCheckBox();
        outlineSpinner = new javax.swing.JSpinner();
        skeletonSpinner = new javax.swing.JSpinner();
        branchingPointsSpinner = new javax.swing.JSpinner();
        convexHullSizeSpinner = new javax.swing.JSpinner();
        outlineColorButton = new javax.swing.JButton();
        skeletonColorButton = new javax.swing.JButton();
        branchinPointsColorButton = new javax.swing.JButton();
        convexHullColorButton = new javax.swing.JButton();
        branchingPointsLabel = new javax.swing.JLabel();
        convexHullLabel = new javax.swing.JLabel();
        skeletonLabel = new javax.swing.JLabel();
        outlineLabel = new javax.swing.JLabel();
        skeletonColorRoundedPanel = new GUI.RoundedPanel();
        branchingPointsRoundedPanel = new GUI.RoundedPanel();
        convexHullRoundedPanel = new GUI.RoundedPanel();
        outlineRoundedPanel = new GUI.RoundedPanel();
        showOverlayCheckBox = new javax.swing.JCheckBox();
        saveImageButton = new javax.swing.JButton();
        imageResulFormatComboBox = new javax.swing.JComboBox();
        ExitButton = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();
        memTransparentTextField = new GUI.TransparentTextField();

        fillHolesRangeSlider.setPaintLabels(true);
        fillHolesRangeSlider.setPaintTicks(true);
        fillHolesRangeSlider.setEnabled(false);
        fillHolesRangeSlider.setHighValue(100);
        fillHolesRangeSlider.setLowValue(0);
        fillHolesRangeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fillHolesRangeSliderStateChanged(evt);
            }
        });

        smallParticlesRangeSlider.setPaintLabels(true);
        smallParticlesRangeSlider.setPaintTicks(true);
        smallParticlesRangeSlider.setEnabled(false);
        smallParticlesRangeSlider.setHighValue(100);
        smallParticlesRangeSlider.setLowValue(0);
        smallParticlesRangeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                smallParticlesRangeSliderStateChanged(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Utils.NAME + " " + Utils.VERSION);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(540, 790));
        setName("mainFrame"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        openImageButton.setFont(new java.awt.Font("Tahoma", 1, 11));
        openImageButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/OpenImages642.png"))); // NOI18N
        openImageButton.setText("Open Image");
        openImageButton.setToolTipText("Open Image");
        openImageButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openImageButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openImageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openImageButtonActionPerformed(evt);
            }
        });
        getContentPane().add(openImageButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 105, 100));

        AnalyzeButton.setFont(new java.awt.Font("Tahoma", 1, 11));
        AnalyzeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/RunAnalysis64-2.png"))); // NOI18N
        AnalyzeButton.setText("Run analysis");
        AnalyzeButton.setToolTipText("Run Analysis");
        AnalyzeButton.setEnabled(false);
        AnalyzeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        AnalyzeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        AnalyzeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AnalyzeButtonActionPerformed(evt);
            }
        });
        getContentPane().add(AnalyzeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 10, 105, 100));

        progressBar.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        progressBar.setPreferredSize(new java.awt.Dimension(152, 20));
        getContentPane().add(progressBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 730, 400, 21));

        TabbedPane.setToolTipText("Settings");
        TabbedPane.setFont(new java.awt.Font("Tahoma", 0, 14));

        AnalysisTabPanel.setFont(new java.awt.Font("Tahoma", 1, 14));
        AnalysisTabPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        thicknessIntensityPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Vessel diameter and intensity", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N
        thicknessIntensityPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        sigmasMarkSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sigmasMarkSliderMouseClicked(evt);
            }
        });
        thicknessIntensityPanel.add(sigmasMarkSlider, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 400, -1));

        sigmasSpinner.setFont(new java.awt.Font("Tahoma", 0, 14));
        sigmasSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), null, null, Integer.valueOf(10)));
        sigmasSpinner.setToolTipText("Adjust Maximum Vessel Thickness");
        sigmasSpinner.setBorder(javax.swing.BorderFactory.createCompoundBorder(null, new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        sigmasSpinner.setEnabled(false);
        sigmasSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sigmasSpinnerStateChanged(evt);
            }
        });
        thicknessIntensityPanel.add(sigmasSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 20, 56, 49));

        thresholdRangeSlider.setMajorTickSpacing(20);
        thresholdRangeSlider.setMaximum(255);
        thresholdRangeSlider.setPaintLabels(true);
        thresholdRangeSlider.setPaintTicks(true);
        thresholdRangeSlider.setEnabled(false);
        thresholdRangeSlider.setHighValue(255);
        thresholdRangeSlider.setLowValue(0);
        thresholdRangeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                thresholdRangeSliderStateChanged(evt);
            }
        });
        thicknessIntensityPanel.add(thresholdRangeSlider, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 90, 380, 70));

        lowThresholdTextField.setEditable(false);
        lowThresholdTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        lowThresholdTextField.setText("0");
        thicknessIntensityPanel.add(lowThresholdTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 105, 25, -1));

        highThresholdTextField.setEditable(false);
        highThresholdTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        highThresholdTextField.setText("0");
        thicknessIntensityPanel.add(highThresholdTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 105, 29, -1));

        AnalysisTabPanel.add(thicknessIntensityPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 490, 170));

        backgroundParticlesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Eliminate foreground and background small particles", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N
        backgroundParticlesPanel.setToolTipText("Eliminate Background and Foreground small particles");
        backgroundParticlesPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        smallParticlesCheckBox.setFont(new java.awt.Font("Tahoma", 0, 15));
        smallParticlesCheckBox.setText("Remove small particles");
        smallParticlesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smallParticlesCheckBoxActionPerformed(evt);
            }
        });
        backgroundParticlesPanel.add(smallParticlesCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 28, -1, -1));

        fillHolesCheckBox.setFont(new java.awt.Font("Tahoma", 0, 15));
        fillHolesCheckBox.setText("Fill holes");
        fillHolesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fillHolesCheckBoxActionPerformed(evt);
            }
        });
        backgroundParticlesPanel.add(fillHolesCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 95, -1, -1));

        fillHolesSpinner.setFont(new java.awt.Font("Tahoma", 0, 14));
        fillHolesSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), null, null, Integer.valueOf(10)));
        fillHolesSpinner.setToolTipText("Adjust Maximum Vessel Thickness");
        fillHolesSpinner.setBorder(javax.swing.BorderFactory.createCompoundBorder(null, new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        fillHolesSpinner.setEnabled(false);
        fillHolesSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fillHolesSpinnerStateChanged(evt);
            }
        });
        backgroundParticlesPanel.add(fillHolesSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 120, 75, 35));

        removeSmallParticlesSpinner.setFont(new java.awt.Font("Tahoma", 0, 14));
        removeSmallParticlesSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), null, null, Integer.valueOf(10)));
        removeSmallParticlesSpinner.setToolTipText("Adjust Maximum Vessel Thickness");
        removeSmallParticlesSpinner.setBorder(javax.swing.BorderFactory.createCompoundBorder(null, new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        removeSmallParticlesSpinner.setEnabled(false);
        removeSmallParticlesSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                removeSmallParticlesSpinnerStateChanged(evt);
            }
        });
        backgroundParticlesPanel.add(removeSmallParticlesSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 55, 75, 35));

        fillHolesRangeSlider2.setPaintLabels(true);
        fillHolesRangeSlider2.setPaintTicks(true);
        fillHolesRangeSlider2.setValue(0);
        fillHolesRangeSlider2.setEnabled(false);
        fillHolesRangeSlider2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fillHolesRangeSlider2StateChanged(evt);
            }
        });
        backgroundParticlesPanel.add(fillHolesRangeSlider2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 117, 340, 50));

        smallParticlesRangeSlider2.setPaintLabels(true);
        smallParticlesRangeSlider2.setPaintTicks(true);
        smallParticlesRangeSlider2.setValue(0);
        smallParticlesRangeSlider2.setEnabled(false);
        smallParticlesRangeSlider2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                smallParticlesRangeSlider2StateChanged(evt);
            }
        });
        backgroundParticlesPanel.add(smallParticlesRangeSlider2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, 340, 50));

        AnalysisTabPanel.add(backgroundParticlesPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 490, 170));

        savingPreferencesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Saving Preferences", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N
        savingPreferencesPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        saveResultsToButton.setFont(new java.awt.Font("Tahoma", 1, 11));
        saveResultsToButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Excel64.png"))); // NOI18N
        saveResultsToButton.setText("Save To");
        saveResultsToButton.setToolTipText("Save To");
        saveResultsToButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveResultsToButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveResultsToButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveResultsToButtonActionPerformed(evt);
            }
        });
        savingPreferencesPanel.add(saveResultsToButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, 100));

        saveResultsToTextField.setFont(new java.awt.Font("Tahoma", 0, 14));
        savingPreferencesPanel.add(saveResultsToTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 50, 360, 28));

        saveResultImageCheckBox.setFont(new java.awt.Font("Tahoma", 0, 14));
        saveResultImageCheckBox.setSelected(true);
        saveResultImageCheckBox.setText("Save result image");
        saveResultImageCheckBox.setToolTipText("Sve the result Image");
        savingPreferencesPanel.add(saveResultImageCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 90, -1, 37));

        AnalysisTabPanel.add(savingPreferencesPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 410, 490, 140));

        toggleOverlayToggleButton.setText("Hide Overlay");
        toggleOverlayToggleButton.setEnabled(false);
        toggleOverlayToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleOverlayToggleButtonActionPerformed(evt);
            }
        });
        AnalysisTabPanel.add(toggleOverlayToggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 10, -1, -1));

        TabbedPane.addTab("Analysis", AnalysisTabPanel);

        settingsTabPanel.setFont(new java.awt.Font("Tahoma", 1, 14));
        settingsTabPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        resizeImagePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Resize Image", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N
        resizeImagePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        resizeImageCheckBox.setFont(new java.awt.Font("Tahoma", 0, 14));
        resizeImageCheckBox.setText("Resize image");
        resizeImageCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resizeImageCheckBoxActionPerformed(evt);
            }
        });
        resizeImagePanel.add(resizeImageCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, 131, -1));

        resizingFactorLabel.setFont(new java.awt.Font("Tahoma", 0, 14));
        resizingFactorLabel.setText("Resizing factor");
        resizingFactorLabel.setEnabled(false);
        resizeImagePanel.add(resizingFactorLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 45, -1, -1));

        resizingFactorSpinner.setModel(new javax.swing.SpinnerNumberModel(1.0d, 1.0d, 10.0d, 0.5d));
        resizingFactorSpinner.setToolTipText("Set resizing factor");
        resizingFactorSpinner.setEnabled(false);
        resizingFactorSpinner.setValue(1);
        resizingFactorSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                resizingFactorSpinnerStateChanged(evt);
            }
        });
        resizeImagePanel.add(resizingFactorSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 40, 52, 30));

        unlockButton.setFont(new java.awt.Font("Tahoma", 1, 11));
        unlockButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/unlocked.png"))); // NOI18N
        unlockButton.setText("Lock");
        unlockButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        unlockButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/unlocked.png"))); // NOI18N
        unlockButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        unlockButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unlockButtonActionPerformed(evt);
            }
        });
        resizeImagePanel.add(unlockButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(395, 25, 70, -1));

        settingsTabPanel.add(resizeImagePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, 490, 100));

        setScalePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Calibration", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N
        setScalePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        distanceInPixelsLabel.setFont(new java.awt.Font("Tahoma", 0, 14));
        distanceInPixelsLabel.setText("Distance in pixels");
        setScalePanel.add(distanceInPixelsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 30, -1, -1));

        distanceInMMLabel.setFont(new java.awt.Font("Tahoma", 0, 14));
        distanceInMMLabel.setText("Distance in mm");
        setScalePanel.add(distanceInMMLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 30, -1, -1));

        scaleLabel.setFont(new java.awt.Font("Tahoma", 0, 14));
        scaleLabel.setText("Scale");
        setScalePanel.add(scaleLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 70, -1, -1));

        scaleTextField.setEditable(false);
        scaleTextField.setFont(new java.awt.Font("Tahoma", 1, 12));
        scaleTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        scaleTextField.setToolTipText("Scale");
        scaleTextField.setBorder(null);
        setScalePanel.add(scaleTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 73, 180, -1));

        distanceInPixelsNumberTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        distanceInPixelsNumberTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                distanceInPixelsNumberTextFieldKeyReleased(evt);
            }
        });
        setScalePanel.add(distanceInPixelsNumberTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 30, 120, -1));

        distanceInMMNumberTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        distanceInMMNumberTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                distanceInMMNumberTextFieldKeyReleased(evt);
            }
        });
        setScalePanel.add(distanceInMMNumberTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 30, 60, -1));

        clearCalibrationButton.setText("Clear Calibration");
        clearCalibrationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearCalibrationButtonActionPerformed(evt);
            }
        });
        setScalePanel.add(clearCalibrationButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 70, -1, -1));

        settingsTabPanel.add(setScalePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 490, 107));

        overlaySettingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Overlay Settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N
        overlaySettingsPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        showOutlineCheckBox.setFont(new java.awt.Font("Tahoma", 0, 14));
        showOutlineCheckBox.setSelected(true);
        showOutlineCheckBox.setText("Show outline");
        showOutlineCheckBox.setToolTipText("Show outline");
        showOutlineCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showOutlineCheckBoxActionPerformed(evt);
            }
        });
        overlaySettingsPanel.add(showOutlineCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 60, -1, -1));

        showBranchingPointsCheckBox.setFont(new java.awt.Font("Tahoma", 0, 14));
        showBranchingPointsCheckBox.setSelected(true);
        showBranchingPointsCheckBox.setText("Show branching points");
        showBranchingPointsCheckBox.setToolTipText("Show branching points");
        showBranchingPointsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showBranchingPointsCheckBoxActionPerformed(evt);
            }
        });
        overlaySettingsPanel.add(showBranchingPointsCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 120, -1, -1));

        showConvexHullCheckBox.setFont(new java.awt.Font("Tahoma", 0, 14));
        showConvexHullCheckBox.setSelected(true);
        showConvexHullCheckBox.setText("Show boundary");
        showConvexHullCheckBox.setToolTipText("Show boundary");
        showConvexHullCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showConvexHullCheckBoxActionPerformed(evt);
            }
        });
        overlaySettingsPanel.add(showConvexHullCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 150, -1, -1));

        showSkeletonCheckBox.setFont(new java.awt.Font("Tahoma", 0, 14));
        showSkeletonCheckBox.setSelected(true);
        showSkeletonCheckBox.setText("Show skeleton");
        showSkeletonCheckBox.setToolTipText("Show skeleton");
        showSkeletonCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showSkeletonCheckBoxActionPerformed(evt);
            }
        });
        overlaySettingsPanel.add(showSkeletonCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, -1, -1));

        outlineSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));
        outlineSpinner.setValue(this.initialOutlineSize);
        outlineSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                outlineSpinnerStateChanged(evt);
            }
        });
        overlaySettingsPanel.add(outlineSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 60, 50, 23));

        skeletonSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));
        skeletonSpinner.setValue(this.initialSkeletonSize);
        skeletonSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                skeletonSpinnerStateChanged(evt);
            }
        });
        overlaySettingsPanel.add(skeletonSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 90, 50, 23));

        branchingPointsSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));
        branchingPointsSpinner.setValue(this.initialBranchingPointsSize);
        branchingPointsSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                branchingPointsSpinnerStateChanged(evt);
            }
        });
        overlaySettingsPanel.add(branchingPointsSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 120, 50, 23));

        convexHullSizeSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));
        convexHullSizeSpinner.setValue(this.initialConvexHullSize);
        convexHullSizeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                convexHullSizeSpinnerStateChanged(evt);
            }
        });
        overlaySettingsPanel.add(convexHullSizeSpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 150, 50, 23));

        outlineColorButton.setContentAreaFilled(false);
        outlineColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outlineColorButtonActionPerformed(evt);
            }
        });
        overlaySettingsPanel.add(outlineColorButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 60, 30, 23));

        skeletonColorButton.setToolTipText("Skeleton Color");
        skeletonColorButton.setContentAreaFilled(false);
        skeletonColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                skeletonColorButtonActionPerformed(evt);
            }
        });
        overlaySettingsPanel.add(skeletonColorButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 90, 30, 23));

        branchinPointsColorButton.setContentAreaFilled(false);
        branchinPointsColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                branchinPointsColorButtonActionPerformed(evt);
            }
        });
        overlaySettingsPanel.add(branchinPointsColorButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 120, 30, 23));

        convexHullColorButton.setContentAreaFilled(false);
        convexHullColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                convexHullColorButtonActionPerformed(evt);
            }
        });
        overlaySettingsPanel.add(convexHullColorButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 150, 30, 23));

        branchingPointsLabel.setFont(new java.awt.Font("Tahoma", 0, 14));
        branchingPointsLabel.setText("Width");
        overlaySettingsPanel.add(branchingPointsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 120, -1, -1));

        convexHullLabel.setFont(new java.awt.Font("Tahoma", 0, 14));
        convexHullLabel.setText("Width");
        overlaySettingsPanel.add(convexHullLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 150, -1, -1));

        skeletonLabel.setFont(new java.awt.Font("Tahoma", 0, 14));
        skeletonLabel.setText("Width");
        overlaySettingsPanel.add(skeletonLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 90, -1, -1));

        outlineLabel.setFont(new java.awt.Font("Tahoma", 0, 14));
        outlineLabel.setText("Width");
        overlaySettingsPanel.add(outlineLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 60, -1, -1));

        skeletonColorRoundedPanel.setBackground(this.initialSkeletonColor);
        skeletonColorRoundedPanel.setCornerRadius(7);

        javax.swing.GroupLayout skeletonColorRoundedPanelLayout = new javax.swing.GroupLayout(skeletonColorRoundedPanel);
        skeletonColorRoundedPanel.setLayout(skeletonColorRoundedPanelLayout);
        skeletonColorRoundedPanelLayout.setHorizontalGroup(
            skeletonColorRoundedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        skeletonColorRoundedPanelLayout.setVerticalGroup(
            skeletonColorRoundedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 23, Short.MAX_VALUE)
        );

        overlaySettingsPanel.add(skeletonColorRoundedPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 90, 30, 23));

        branchingPointsRoundedPanel.setBackground(this.initialBranchingPointColor);
        branchingPointsRoundedPanel.setCornerRadius(7);

        javax.swing.GroupLayout branchingPointsRoundedPanelLayout = new javax.swing.GroupLayout(branchingPointsRoundedPanel);
        branchingPointsRoundedPanel.setLayout(branchingPointsRoundedPanelLayout);
        branchingPointsRoundedPanelLayout.setHorizontalGroup(
            branchingPointsRoundedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        branchingPointsRoundedPanelLayout.setVerticalGroup(
            branchingPointsRoundedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 23, Short.MAX_VALUE)
        );

        overlaySettingsPanel.add(branchingPointsRoundedPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 120, 30, 23));

        convexHullRoundedPanel.setBackground(this.initialConvexHullColor);
        convexHullRoundedPanel.setCornerRadius(7);

        javax.swing.GroupLayout convexHullRoundedPanelLayout = new javax.swing.GroupLayout(convexHullRoundedPanel);
        convexHullRoundedPanel.setLayout(convexHullRoundedPanelLayout);
        convexHullRoundedPanelLayout.setHorizontalGroup(
            convexHullRoundedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        convexHullRoundedPanelLayout.setVerticalGroup(
            convexHullRoundedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 23, Short.MAX_VALUE)
        );

        overlaySettingsPanel.add(convexHullRoundedPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 150, 30, 23));

        outlineRoundedPanel.setBackground(this.initialOutlineColor);
        outlineRoundedPanel.setCornerRadius(7);

        javax.swing.GroupLayout outlineRoundedPanelLayout = new javax.swing.GroupLayout(outlineRoundedPanel);
        outlineRoundedPanel.setLayout(outlineRoundedPanelLayout);
        outlineRoundedPanelLayout.setHorizontalGroup(
            outlineRoundedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        outlineRoundedPanelLayout.setVerticalGroup(
            outlineRoundedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 23, Short.MAX_VALUE)
        );

        overlaySettingsPanel.add(outlineRoundedPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 60, 30, 23));

        showOverlayCheckBox.setFont(new java.awt.Font("Tahoma", 0, 14));
        showOverlayCheckBox.setSelected(true);
        showOverlayCheckBox.setText("Show overlay");
        showOverlayCheckBox.setToolTipText("Show overlay");
        showOverlayCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showOverlayCheckBoxActionPerformed(evt);
            }
        });
        overlaySettingsPanel.add(showOverlayCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        saveImageButton.setFont(new java.awt.Font("Tahoma", 1, 14));
        saveImageButton.setText("Save Image");
        saveImageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveImageButtonActionPerformed(evt);
            }
        });
        overlaySettingsPanel.add(saveImageButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 210, 125, 25));

        imageResulFormatComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "jpg", "tiff", "png", "bmp" }));
        imageResulFormatComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imageResulFormatComboBoxActionPerformed(evt);
            }
        });
        overlaySettingsPanel.add(imageResulFormatComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 210, 50, 25));

        settingsTabPanel.add(overlaySettingsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 490, 250));

        TabbedPane.addTab("Settings", settingsTabPanel);

        getContentPane().add(TabbedPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 129, 515, 590));

        ExitButton.setFont(new java.awt.Font("Tahoma", 1, 11));
        ExitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Close64.png"))); // NOI18N
        ExitButton.setText("Exit");
        ExitButton.setToolTipText("Exit");
        ExitButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ExitButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ExitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitButtonActionPerformed(evt);
            }
        });
        getContentPane().add(ExitButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, 105, 100));

        helpButton.setFont(new java.awt.Font("Tahoma", 1, 11));
        helpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/help64.png"))); // NOI18N
        helpButton.setText("Help");
        helpButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        helpButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });
        getContentPane().add(helpButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 10, 105, 100));

        memTransparentTextField.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        memTransparentTextField.setEditable(false);
        memTransparentTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        memTransparentTextField.setFont(new java.awt.Font("Tahoma", 1, 11));
        getContentPane().add(memTransparentTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 730, 100, 20));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void openImageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openImageButtonActionPerformed

        JFileChooser fc = new JFileChooser();
        fc.setPreferredSize(new Dimension(1000, 600));
        fc.setFileView(new ImageFileView());
        fc.setAccessory(new ImagePreview(fc));
        fc.setMultiSelectionEnabled(false);
        fc.setCurrentDirectory(currentDir);
        fc.setFileFilter(new ImageFilter());

        int returnVal = fc.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            initVariables();
            imageFile = fc.getSelectedFile();
            currentDir = fc.getCurrentDirectory();
            if (imageFile!=null){

                results.image = imageFile; //add the image file to the results class
                imageOriginal = IJ.openImage(imageFile.getAbsolutePath());
                if (imageOriginal!=null){
                    if (imageOriginal.getType() == ImagePlus.COLOR_RGB){
                        imageOriginal = RGBStackSplitter.split(imageOriginal, "green");
                    }

                    if (resizeImageCheckBox.isSelected()){
                        ImageProcessor resized = imageOriginal.getProcessor().resize((int)(imageOriginal.getWidth()/resizingFactor));
                        imageOriginal.setProcessor(resized);
                    }

                    resizeImageCheckBox.setEnabled(false);
                    resizingFactorSpinner.setEnabled(false);
                    resizingFactorLabel.setEnabled(false);
                    unlockButton.setText(resizeImageCheckBox.isEnabled()? "Lock" : "Unlock");
                    unlockButton.setSelected(true);

                    this.AnalyzeButton.setEnabled(true);

                    imageOriginal.show();
                    ipOriginal = imageOriginal.getProcessor().convertToByte(doScaling);
                    imageResult = imageOriginal;
                    imageResult.getWindow().setLocation(this.getX()+this.getWidth(), this.getY());
                    imageResult.getWindow().setIconImage(imgIcon);

                    initControls();
       
                    computeFirstOutline (firstSigma, thresholdRangeSlider.getLowValue(), thresholdRangeSlider.getHighValue());
                }
            }
        }
    }//GEN-LAST:event_openImageButtonActionPerformed

    private void thresholdRangeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_thresholdRangeSliderStateChanged
        RangeSlider source = (RangeSlider)evt.getSource();
        if (! source.getValueIsAdjusting()) {
            int lowValue = source.getLowValue();
            int highValue = source.getHighValue();
            lowThresholdTextField.setText(""+lowValue);
            highThresholdTextField.setText(""+highValue);

            updateOutline();
        }
    }//GEN-LAST:event_thresholdRangeSliderStateChanged

    private void fillHolesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fillHolesCheckBoxActionPerformed
        fillHoles = (fillHoles ) ? false : true;
        fillHolesRangeSlider2.setEnabled(fillHoles);
        fillHolesIsChanged = fillHoles;
        fillHolesSpinner.setEnabled(fillHoles);
        updateOutline();
    }//GEN-LAST:event_fillHolesCheckBoxActionPerformed

    private void fillHolesRangeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fillHolesRangeSliderStateChanged
        if (fillHoles){
            RangeSlider source = (RangeSlider)evt.getSource();
            if (! source.getValueIsAdjusting()) {
                fillHolesIsChanged = true;
                updateOutline();
            }
        }
    }//GEN-LAST:event_fillHolesRangeSliderStateChanged

    private void sigmasMarkSliderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sigmasMarkSliderMouseClicked
        markSlider ms = (markSlider) evt.getSource();
        final ArrayList <Integer> s = ms.getMarks();
        try {
            updateSigmas(s);
        }catch (java.lang.OutOfMemoryError e){
            if (JOptionPane.showConfirmDialog(null, "Your system has run out of memory" +
                    "\n Reinitializing all variables...",
                "Allantois Analysis",
                JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null) == 0){
                    initVariables();
                }
        }
        sigmaIsChanged = true;
        updateOutline();


    }//GEN-LAST:event_sigmasMarkSliderMouseClicked

    private void smallParticlesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smallParticlesCheckBoxActionPerformed
        smallParticles = (smallParticles ) ? false : true;
        smallParticlesRangeSlider2.setEnabled(smallParticles);
        smallParticlesIsChanged = smallParticles;
        removeSmallParticlesSpinner.setEnabled(smallParticles);
        updateOutline();
    }//GEN-LAST:event_smallParticlesCheckBoxActionPerformed

    private void smallParticlesRangeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_smallParticlesRangeSliderStateChanged
        if (smallParticles){
            RangeSlider source = (RangeSlider)evt.getSource();
            if (! source.getValueIsAdjusting()) {
                smallParticlesIsChanged = true;
                updateOutline();
            }
        }
    }//GEN-LAST:event_smallParticlesRangeSliderStateChanged

    private void AnalyzeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnalyzeButtonActionPerformed

        worker = new SwingWorker() {
            @Override
            public Object construct() {
                return doAnalysis();
            }
        @Override
            public void finished() {
                populateResults();
            }
        };
        worker.start();
    }//GEN-LAST:event_AnalyzeButtonActionPerformed

    private void resizeImageCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resizeImageCheckBoxActionPerformed
        resizingFactorSpinner.setEnabled(resizeImageCheckBox.isSelected());
        resizingFactorLabel.setEnabled(resizeImageCheckBox.isSelected());
    }//GEN-LAST:event_resizeImageCheckBoxActionPerformed

    private void toggleOverlayToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleOverlayToggleButtonActionPerformed
        imageResult.setHideOverlay(toggleOverlayToggleButton.isSelected());
        String title = toggleOverlayToggleButton.isSelected() ? "Show Overlay" : "Hide Overlay";
        toggleOverlayToggleButton.setText(title);

        showOverlayCheckBox.setSelected(!toggleOverlayToggleButton.isSelected());
    }//GEN-LAST:event_toggleOverlayToggleButtonActionPerformed

    private void sigmasSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sigmasSpinnerStateChanged
        if ((Integer)sigmasSpinner.getValue()<=0)
            sigmasSpinner.setValue((Integer)0);

        sigmasMarkSlider.setMaximum((Integer)(Integer)sigmasSpinner.getValue());
    }//GEN-LAST:event_sigmasSpinnerStateChanged

    private void saveResultsToButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveResultsToButtonActionPerformed
        JFileChooser fc;
        fc = new JFileChooser();
        fc.setDialogTitle("Save As");
        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        fc.setApproveButtonToolTipText("Set the Excel file name where the data will be saved");
        fc.setCurrentDirectory(currentDir);


        fc.setFileFilter(new ExcelFilter());
        int returnVal = fc.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File resultsFile = fc.getSelectedFile();

            String extension = null;
            extension = Utils.getExtension(resultsFile);
            if (extension != null){  
                if (extension.equals("xls") || extension.equals("xlsx")){  
                    saveResultsToTextField.setText(resultsFile.getPath());
                }else{ 
                    saveResultsToTextField.setText(resultsFile.getPath()+".xls");
                }
            } else { 
                saveResultsToTextField.setText(resultsFile.getPath()+".xls");
            }

            ste = new SaveToExcel(resultsFile.getPath(), true);
        }
    }//GEN-LAST:event_saveResultsToButtonActionPerformed

    private void resizingFactorSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_resizingFactorSpinnerStateChanged
        resizingFactor = (Double)resizingFactorSpinner.getValue();
    }//GEN-LAST:event_resizingFactorSpinnerStateChanged

    private void skeletonColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_skeletonColorButtonActionPerformed
        JButton button = (JButton)evt.getSource();
        Color background = JColorChooser.showDialog(null,
            button.getToolTipText(), initialSkeletonColor);
        if (background != null) {
            skeletonColorRoundedPanel.setBackground(background);
            updateOverlay();
        }
    }//GEN-LAST:event_skeletonColorButtonActionPerformed

    private void branchinPointsColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_branchinPointsColorButtonActionPerformed
        JButton button = (JButton)evt.getSource();
        Color background = JColorChooser.showDialog(null,
            button.getToolTipText(), initialBranchingPointColor);
        if (background != null) {
            branchingPointsRoundedPanel.setBackground(background);
            updateOverlay();
        }
    }//GEN-LAST:event_branchinPointsColorButtonActionPerformed

    private void convexHullColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_convexHullColorButtonActionPerformed
        JButton button = (JButton)evt.getSource();
        Color background = JColorChooser.showDialog(null,
            button.getToolTipText(), initialConvexHullColor);
        if (background != null) {
            convexHullRoundedPanel.setBackground(background);
            updateOverlay();
        }
    }//GEN-LAST:event_convexHullColorButtonActionPerformed

    private void outlineColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outlineColorButtonActionPerformed
        JButton button = (JButton)evt.getSource();
        Color background = JColorChooser.showDialog(null,
            button.getToolTipText(), initialOutlineColor);
        if (background != null) {
            outlineRoundedPanel.setBackground(background);
            updateOverlay();
        }
    }//GEN-LAST:event_outlineColorButtonActionPerformed

    private void distanceInPixelsNumberTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_distanceInPixelsNumberTextFieldKeyReleased
        if (!distanceInMMNumberTextField.getText().equals("") && !distanceInPixelsNumberTextField.getText().equals(""))
            scaleTextField.setText("" + (distanceInPixelsNumberTextField.getInt()/distanceInMMNumberTextField.getDouble())+ " pixels/mm");
    }//GEN-LAST:event_distanceInPixelsNumberTextFieldKeyReleased

    private void distanceInMMNumberTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_distanceInMMNumberTextFieldKeyReleased
        if (!distanceInMMNumberTextField.getText().equals("") && !distanceInPixelsNumberTextField.getText().equals(""))
            scaleTextField.setText("" + (distanceInPixelsNumberTextField.getInt()/distanceInMMNumberTextField.getDouble())+ " pixels/mm");
    }//GEN-LAST:event_distanceInMMNumberTextFieldKeyReleased

    private void fillHolesSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fillHolesSpinnerStateChanged
        if ((Integer)fillHolesSpinner.getValue()<=0)
            fillHolesSpinner.setValue((Integer)0);

        fillHolesRangeSlider2.setMaximum((Integer)(Integer)fillHolesSpinner.getValue());
        fillHolesRangeSlider2.setMajorTickSpacing((int)(fillHolesRangeSlider2.getMaximum() / 10));
        Hashtable h = fillHolesRangeSlider2.createStandardLabels((int)(fillHolesRangeSlider2.getMaximum() / 10));
        fillHolesRangeSlider2.setLabelTable(h);

    }//GEN-LAST:event_fillHolesSpinnerStateChanged

    private void removeSmallParticlesSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_removeSmallParticlesSpinnerStateChanged
        if ((Integer)removeSmallParticlesSpinner.getValue()<=0)
            removeSmallParticlesSpinner.setValue((Integer)0);

        smallParticlesRangeSlider2.setMaximum((Integer)(Integer)removeSmallParticlesSpinner.getValue());
        smallParticlesRangeSlider2.setMajorTickSpacing((int)(smallParticlesRangeSlider2.getMaximum() / 10));
        Hashtable h = smallParticlesRangeSlider2.createStandardLabels((int)(smallParticlesRangeSlider2.getMaximum() / 10));
        smallParticlesRangeSlider2.setLabelTable(h);
    }//GEN-LAST:event_removeSmallParticlesSpinnerStateChanged

    private void ExitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitButtonActionPerformed
        if (JOptionPane.showConfirmDialog(null, "Do you really want to exit Allantois Analysis?",
                "Allantois Analysis",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null) == 0){

            if (imageResult!=null)
                this.imageResult.close();
            this.setVisible(false);

            exit();
            System.exit(0);
        }
    }//GEN-LAST:event_ExitButtonActionPerformed

    private void showConvexHullCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showConvexHullCheckBoxActionPerformed
        updateOverlay();
    }//GEN-LAST:event_showConvexHullCheckBoxActionPerformed

    private void showSkeletonCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showSkeletonCheckBoxActionPerformed
        updateOverlay();
    }//GEN-LAST:event_showSkeletonCheckBoxActionPerformed

    private void skeletonSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_skeletonSpinnerStateChanged
        updateOverlay();
    }//GEN-LAST:event_skeletonSpinnerStateChanged

    private void outlineSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_outlineSpinnerStateChanged
        updateOverlay();
    }//GEN-LAST:event_outlineSpinnerStateChanged

    private void branchingPointsSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_branchingPointsSpinnerStateChanged
        updateOverlay();
    }//GEN-LAST:event_branchingPointsSpinnerStateChanged

    private void convexHullSizeSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_convexHullSizeSpinnerStateChanged
        updateOverlay();
    }//GEN-LAST:event_convexHullSizeSpinnerStateChanged

    private void showOutlineCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showOutlineCheckBoxActionPerformed
        updateOverlay();
    }//GEN-LAST:event_showOutlineCheckBoxActionPerformed

    private void showBranchingPointsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showBranchingPointsCheckBoxActionPerformed
        updateOverlay();
    }//GEN-LAST:event_showBranchingPointsCheckBoxActionPerformed

    private void showOverlayCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showOverlayCheckBoxActionPerformed
        imageResult.setHideOverlay(!showOverlayCheckBox.isSelected());
        String title = !showOverlayCheckBox.isSelected() ? "Show Overlay" : "Hide Overlay";
        toggleOverlayToggleButton.setText(title);
        toggleOverlayToggleButton.setSelected(!showOverlayCheckBox.isSelected());
    }//GEN-LAST:event_showOverlayCheckBoxActionPerformed

    private void saveImageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveImageButtonActionPerformed
        if (imageResult!=null){
            ImagePlus imageResultFlattenen = imageResult.flatten();
            IJ.saveAs(imageResultFlattenen, imageResultFormat, imageFile.getAbsolutePath()+ " result." + imageResultFormat);
        } else
            if (JOptionPane.showConfirmDialog(null, "No image to save",
                    "Allantois Analysis",
                    JOptionPane.CLOSED_OPTION, JOptionPane.WARNING_MESSAGE, null) == 0){
                System.gc();
            }
    }//GEN-LAST:event_saveImageButtonActionPerformed

    private void unlockButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unlockButtonActionPerformed
        resizeImageCheckBox.setEnabled(!resizeImageCheckBox.isEnabled());
        resizingFactorLabel.setEnabled(resizeImageCheckBox.isSelected() && resizeImageCheckBox.isEnabled());
        resizingFactorSpinner.setEnabled(resizeImageCheckBox.isSelected() && resizeImageCheckBox.isEnabled());
        unlockButton.setText(resizeImageCheckBox.isEnabled()? "Lock" : "Unlock");
        unlockButton.setSelected(resizeImageCheckBox.isEnabled()? false : true);
    }//GEN-LAST:event_unlockButtonActionPerformed

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenDimension = toolkit.getScreenSize();
        int x = (screenDimension.width/2) - (430/2);
        int y = (screenDimension.height/2) - (180/2);
        aboutBox.setLocation(x,y);
        aboutBox.setVisible(true);
    }//GEN-LAST:event_helpButtonActionPerformed

    private void fillHolesRangeSlider2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fillHolesRangeSlider2StateChanged
        if (fillHoles){
            JSlider source = (JSlider)evt.getSource();
            if (! source.getValueIsAdjusting()) {
                fillHolesIsChanged = true;
                updateOutline();
            }
        }
    }//GEN-LAST:event_fillHolesRangeSlider2StateChanged

    private void smallParticlesRangeSlider2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_smallParticlesRangeSlider2StateChanged
        if (smallParticles){
            JSlider source = (JSlider)evt.getSource();
            if (! source.getValueIsAdjusting()) {
                smallParticlesIsChanged = true;
                updateOutline();
            }
        }
    }//GEN-LAST:event_smallParticlesRangeSlider2StateChanged

    private void imageResulFormatComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imageResulFormatComboBoxActionPerformed
        imageResultFormat = (String)imageResulFormatComboBox.getSelectedItem();
    }//GEN-LAST:event_imageResulFormatComboBoxActionPerformed

    private void clearCalibrationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearCalibrationButtonActionPerformed
        distanceInMMNumberTextField.setText("");
        distanceInPixelsNumberTextField.setText("");
        scaleTextField.setText("");
        LinearScalingFactor = 1.0;
        AreaScalingFactor = 1.0;
    }//GEN-LAST:event_clearCalibrationButtonActionPerformed

    private Object doAnalysis (){
        int progress = 0;

        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setStringPainted(true);
        updateStatus (progress, "");

        updateOutline();

        for (int i = 0; i<3; i++){
            this.smoothROIs(5);
            progress+=11;
            updateStatus (progress, "Computing vessel outline");
        }

        results.vesselPixelArea = Utils.thresholdedPixelArea(imageThresholded.getProcessor());

        if (computeLacunarity){                                                                
            updateStatus (progress, "Computing lacunarity...");
            ImageProcessor ipTemp = imageThresholded.getProcessor().duplicate();
            ImagePlus iplusTemp  = new ImagePlus ("iplusTemp", ipTemp);
            this.computeLacunarity(iplusTemp, 10, 10, 5);
            results.meanEl = this.meanEl;
        }
        
        convexHull = Utils.computeConvexHull(imageThresholded.getProcessor());
        convexHullArea = convexHull.area();
        convexHullRoi = new PolygonRoi (convexHull.polygon(), Roi.POLYGON);

        progress+=5;
        updateStatus (progress, "Analyzing skeleton... ");

        updateStatus (progress, "Analyzing skeleton...");
        ImageProcessor ip = imageThresholded.getProcessor();
        ip = Utils.skeletonize(ip, "itk");
        ipThresholded = ip;

        progress+=33;
        updateStatus (progress, "Computing convex hull... ");


        AnalyzeSkeleton.AnalyzeSkeleton as2 = new AnalyzeSkeleton.AnalyzeSkeleton();
        ImageProcessor ipSkeleton = ipThresholded.duplicate();
        ImagePlus iplusSkeleton = new ImagePlus ("iplusSkeleton", ipSkeleton);

        as2 = new AnalyzeSkeleton.AnalyzeSkeleton();
        as2.setup("", iplusSkeleton);
        skelResult = as2.run(AnalyzeSkeleton.AnalyzeSkeleton.NONE, false, false, iplusSkeleton, false, false);

        graph = as2.getGraphs();
        skeletonRoi = computeSkeletonRoi (graph, skeletonColorRoundedPanel.getBackground(), (Integer)skeletonSpinner.getValue());

        al2 = skelResult.getListOfJunctionVoxels();
        removedJunctions = Utils.computeActualJunctions(al2);

        junctionsRoi = computeJunctionsRoi(al2, branchingPointsRoundedPanel.getBackground(), (Integer)branchingPointsSpinner.getValue());

        updateOverlay();

        progress = 95;
        updateStatus (progress, "Saving result image... ");

        progress = 100;
        updateStatus (progress, "Done... ");

        return "Good";
    }

    private void populateResults (){

        if (distanceInMMNumberTextField.getText().equals("") || distanceInPixelsNumberTextField.getText().equals("")){
            LinearScalingFactor = 1.0;
            AreaScalingFactor = 1.0;
        }
        else{
            LinearScalingFactor = distanceInMMNumberTextField.getDouble()/distanceInPixelsNumberTextField.getInt();
            AreaScalingFactor = ((distanceInMMNumberTextField.getDouble()*distanceInMMNumberTextField.getDouble())/(distanceInPixelsNumberTextField.getInt()*distanceInPixelsNumberTextField.getInt()));
            AreaScalingFactor = LinearScalingFactor * LinearScalingFactor;
        }

        results.image = imageFile;
        results.thresholdLow = thresholdRangeSlider.getLowValue();
        results.thresholdHigh = thresholdRangeSlider.getHighValue();
        results.sigmas = sigmasMarkSlider.getMarks();
        results.removeSmallParticles = smallParticlesRangeSlider2.getValue();
        results.fillHoles = fillHolesRangeSlider2.getValue();

        results.LinearScalingFactor = LinearScalingFactor;
        results.AreaScalingFactor = AreaScalingFactor; 
        results.allantoisPixelsArea = convexHullArea;
        results.allantoisMMArea = convexHullArea * AreaScalingFactor;
        results.totalNJunctions = al2.size();
        results.JunctionsPerArea = al2.size() / convexHullArea;
        results.JunctionsPerScaledArea = al2.size() / results.allantoisMMArea;
        //results.vesselPixelArea = Utils.thresholdedPixelArea(imageThresholded.getProcessor());
        results.vesselMMArea = results.vesselPixelArea * AreaScalingFactor;
        results.vesselPercentageArea = (results.vesselMMArea * 100)/results.allantoisMMArea; 

        //compute total length and average length
        double [] branchLengths = skelResult.getAverageBranchLength();
        int [] branchNumbers = skelResult.getBranches();

        double totalLength = 0;
        double averageLength = 0;
        for (int i = 0; i < branchNumbers.length; i++) {
                totalLength += branchNumbers[i] * branchLengths[i];
        }
        results.totalLength = totalLength * LinearScalingFactor;
        results.averageBranchLength = (totalLength/branchNumbers.length) * LinearScalingFactor;

        //total end points
        results.totalNEndPoints = skelResult.getListOfEndPoints().size();

        //Set up Excel file and write resuls
        String name = imageFile.getName();
        int fileExtensionLength = Utils.getExtension(imageFile).length() + 1;
        ste.setFileName(name.substring(0, name.length()- fileExtensionLength));
        ste.writeResultsToExcel(results);

        //save the final flattened image
        if (saveResultImageCheckBox.isSelected()) {
            ImagePlus imageResultFlattenen = imageResult.flatten();
            IJ.saveAs(imageResultFlattenen, "jpg", imageFile.getAbsolutePath()+ " result.jpg");
        }
    }

    public static void updateStatus(final int i, final String s) {
        Runnable doSetProgressBarValue = new Runnable() {
            @Override
            public void run() {
                progressBar.setValue(i);
                progressBar.setString(s + " " + i + "%");
            }
        };
        SwingUtilities.invokeLater(doSetProgressBarValue);
    }


    public void updateOverlay (){
        allantoisOverlay.clear();

        if (showOverlayCheckBox.isSelected()) {
            //add the outline
            if (outlineRoi!= null && showOutlineCheckBox.isSelected()){
                outlineRoi.setStrokeColor(outlineRoundedPanel.getBackground());
                outlineRoi.setStrokeWidth((Integer)outlineSpinner.getValue());
                allantoisOverlay.add (outlineRoi);
            }

            //add the skeleton. Recalculates the skeleton based on user defined size and color
            if (graph!=null) skeletonRoi = computeSkeletonRoi (graph, skeletonColorRoundedPanel.getBackground(), (Integer)skeletonSpinner.getValue());
            if (skeletonRoi != null && this.showSkeletonCheckBox.isSelected()) {
                Color color = skeletonColorRoundedPanel.getBackground();
                int strokeWith = (Integer)skeletonSpinner.getValue();
                for (int i = 0; i<skeletonRoi.size(); i++){
                    Roi r = skeletonRoi.get(i);
                    r.setStrokeWidth((float)strokeWith);
                    r.setStrokeColor(color);
                    allantoisOverlay.add (r);
                }

                for (int i = 0; i<removedJunctions.size(); i++){
                    AnalyzeSkeleton.Point p = removedJunctions.get(i);
                    OvalRoi r = new OvalRoi (p.x, p.y, 1, 1);
                    r.setStrokeWidth((float)strokeWith);
                    r.setStrokeColor(color);
                    allantoisOverlay.add(r);
                }
            }

            if (al2!=null) junctionsRoi = computeJunctionsRoi (al2, this.branchingPointsRoundedPanel.getBackground(), (Integer)this.branchingPointsSpinner.getValue());
            if (junctionsRoi != null && showBranchingPointsCheckBox.isSelected()){
                Color color = branchingPointsRoundedPanel.getBackground();
                int strokeWith = (Integer)this.branchingPointsSpinner.getValue();
                for (int i = 0; i<junctionsRoi.size(); i++){
                    Roi r = junctionsRoi.get(i);
                    r.setStrokeWidth((float)strokeWith);
                    r.setStrokeColor(color);
                    allantoisOverlay.add (r);
                }
            }

            if (convexHullRoi != null && showConvexHullCheckBox.isSelected()) {
                convexHullRoi.setStrokeColor(convexHullRoundedPanel.getBackground());
                convexHullRoi.setStrokeWidth((Integer)convexHullSizeSpinner.getValue());
                allantoisOverlay.add(convexHullRoi);
            }

        }
            imageResult.setOverlay(allantoisOverlay);
    }

    private void updateOutline (){
        if (sigmaIsChanged){
            ImageProcessor ip = new ByteProcessor(tubenessIp.getWidth(), tubenessIp.getHeight());
            for (int i = 0; i<currentSigmas.size(); i++){
                double s = currentSigmas.get(i);
                for (int si = 0; si<sI.size(); si++){
                    sigmaImages siTemp = sI.get(si);
                    if (siTemp.sigma == s){
                        ImageProcessor tempIp = siTemp.tubenessImage.duplicate();
                        tempIp.copyBits(ip, 0, 0, Blitter.MAX);
                        ip = tempIp;
                        break;
                    }
                }
            }
            tubenessIp = ip.duplicate();
        }


        ImageProcessor temp = tubenessIp.duplicate();
        temp = temp.convertToByte(true);

        Utils.threshold(temp, thresholdRangeSlider.getLowValue(), thresholdRangeSlider.getHighValue());
        imageThresholded.setProcessor(temp);
        temp.setThreshold(255.0, 255.0, ImageProcessor.NO_LUT_UPDATE);

        int iterations = 2;
        for (int i = 0; i<iterations; i++)
            imageThresholded.getProcessor().erode();
        for (int i = 0; i<iterations; i++)
            imageThresholded.getProcessor().dilate();

        if (smallParticlesCheckBox.isSelected()){
            Utils.fillHoles(imageThresholded, 0, smallParticlesRangeSlider2.getValue(), 0.0, 1.0, 0);  //eliminate white foreground small particles
        }
        if (fillHolesCheckBox.isSelected()){
            imageThresholded.killRoi();
            ImageProcessor temp1 = imageThresholded.getProcessor();
            temp1.invert();
            Utils.fillHoles(imageThresholded, 0, fillHolesRangeSlider2.getValue(), 0.0, 1.0, 0);  
            temp1.invert();
        }

        ImagePlus iplus = new ImagePlus ("tubenessIp", imageThresholded.getProcessor());
        outlineRoi = Utils.thresholdToSelection(iplus);
        outlineRoi.setStrokeWidth((Integer)outlineSpinner.getValue());
        allantoisOverlay.clear();
        allantoisOverlay.add(outlineRoi);
        allantoisOverlay.setStrokeColor(outlineRoundedPanel.getBackground());
        imageResult.setOverlay(allantoisOverlay);

        sigmaIsChanged = false;
        fillHolesIsChanged = false;
    }

    private void initVariables(){

        if (imageFile != null){
            imageFile = null;
            imageFile = new File("");
        }

        sigmasMarkSlider.resetAll();
        sigmasMarkSlider.setMaximum(100);
        sigmasMarkSlider.setEnabled(false);
        sigmasSpinner.setValue(100);
        sigmaIsChanged = false;
        minSigma = Integer.MAX_VALUE;
        maxSigma = Integer.MIN_VALUE;
        allSigmas = new ArrayList <Double>();
        currentSigmas = new ArrayList <Double>();

        thresholdRangeSlider.setEnabled(false);
        thresholdRangeSlider.setLowValue(15);
        thresholdRangeSlider.setHighValue(255);

        doScaling = false;

        fillHoles = false;
        fillHolesCheckBox.setSelected(fillHoles);
        fillHolesCheckBox.setEnabled(false);
        fillHolesIsChanged = false;

        smallParticles = false;
        smallParticlesCheckBox.setSelected(false);
        smallParticlesCheckBox.setEnabled(smallParticles);
        smallParticlesIsChanged = false;

        if (imageOriginal!=null){
            imageOriginal.close();
            imageOriginal.flush();
        }

        if (imageResult!=null){
            imageResult.close();
            imageResult.flush();
        }
        imageResult = new ImagePlus ();
        imageResult.setTitle("Result");

        if (imageThresholded!=null){
            imageThresholded.close();
            imageThresholded.flush();
        }
        imageThresholded = new ImagePlus();
        imageThresholded.setTitle("Thresholded");

        if (imageTubeness!=null){
            imageTubeness.close();
            imageTubeness.flush();
        }
        imageTubeness = new ImagePlus();
        imageTubeness.setTitle("Tubeness");

        sI = new ArrayList <sigmaImages>();
        
        allantoisOverlay = new Overlay();

        results = new Results();
        results.computeLacunarity = computeLacunarity;
    }

    private void smoothROIs (int fraction) {
        ShapeRoi sr = (ShapeRoi)Utils.thresholdToSelection(imageThresholded);

        ForkShapeRoiSplines fs = new ForkShapeRoiSplines ();
        ShapeRoi tempSr = fs.computeSplines(sr, 5);

        imageThresholded.getProcessor().setColor(Color.black);
        imageThresholded.getProcessor().fill();
        Utils.selectionToThreshold (tempSr, imageThresholded);
        outlineRoi = Utils.thresholdToSelection(imageThresholded);
        allantoisOverlay.clear();
        allantoisOverlay.add(tempSr);
        allantoisOverlay.setStrokeColor(Color.yellow);
        imageThresholded.setOverlay(allantoisOverlay);
        imageResult.setOverlay(allantoisOverlay);
    }

     public void computeLacunarity(ImagePlus iplus, int numBoxes, int minBoxSize, int slideXY){
         Lacunarity l = new Lacunarity (iplus,
                 numBoxes,
                 minBoxSize,
                 slideXY, 
                 true);
         this.ElSlope = l.getEl3Slope();
         this.lacunarityBoxes = l.getBoxes();
         Elamdas = l.getEoneplusl3();
         medialELacunarity = l.getMedialELacunarity();
         meanEl = l.getMeanEl();
     }


    private ArrayList <Roi> computeSkeletonRoi (AnalyzeSkeleton.Graph [] graph, Color color, int size){
        ArrayList <Roi> r  = new ArrayList <Roi>();

        for (int g = 0; g<graph.length; g++){
            ArrayList<Edge> edges = graph[g].getEdges();
            for (int e = 0; e<edges.size(); e++){
                Edge edge = edges.get(e);
                ArrayList <AnalyzeSkeleton.Point> points = edge.getSlabs();
                for (int p1 = 0; p1<points.size(); p1++){
                    OvalRoi or = new OvalRoi (points.get(p1).x-size/2, points.get(p1).y-size/2, size, size);
                    r.add (or);
                }
            }
        }
        return r;
    }

    private ArrayList <Roi> computeJunctionsRoi (ArrayList <AnalyzeSkeleton.Point> al, Color color, int size){
        ArrayList <Roi> r  = new ArrayList <Roi>();

        for (int i = 0; i<al.size(); i++){
            AnalyzeSkeleton.Point p = al.get(i);
            OvalRoi or = new OvalRoi (p.x-size/2, p.y-size/2, size, size);
            r.add(or);
        }
        return r;
    }


    private Object updateSigmas (ArrayList<Integer> s){
        upateAllSigmas (s);
        updateCurrentSimgas (s);
        return "";
    }

    private void upateAllSigmas (ArrayList<Integer> s){
        for (int i = 0; i<s.size(); i++){
            double sigma = (double) s.get(i);
            if (!allSigmas.contains(sigma)) {
                allSigmas.add(sigma);

                Tubeness t = new Tubeness ();
                double sigmaDouble [] = {sigma};
                imageTubeness = t.runTubeness(new ImagePlus ("", ipOriginal), 100, sigmaDouble, false);
                sI.add(new sigmaImages (sigma, imageTubeness.getProcessor()));
            }
            updateStatus((i/s.size())*100, "computing outline... ");
        }
    }

    private void updateCurrentSimgas(ArrayList<Integer> s){
        currentSigmas.clear();
        for (int i = 0; i<s.size(); i++){
            currentSigmas.add((double)s.get(i));
        }
    }

    private void updateSigmas (int low, int high){
        upateAllSigmas (low, high);
        updateCurrentSimgas (low, high);
    }

    private void upateAllSigmas (int low, int high){
        if (!allSigmas.contains((double)low)) {
            allSigmas.add((double)low);

            Tubeness t = new Tubeness ();
            double s [] = {low};
            imageTubeness = t.runTubeness(new ImagePlus ("", ipOriginal), 100, s, false);
            sI.add(new sigmaImages (low, imageTubeness.getProcessor()));
        }
        if (!allSigmas.contains((double)high)) {
            allSigmas.add((double)high);

            Tubeness t = new Tubeness ();
            double s [] = {high};
            imageTubeness = t.runTubeness(new ImagePlus ("", ipOriginal), 100, s, false);
            sI.add(new sigmaImages (high, imageTubeness.getProcessor()));
        }

        Collections.sort (allSigmas);
    }

    private void updateCurrentSimgas(int low, int high){
        currentSigmas.clear();
        for (int i = 0; i<allSigmas.size(); i++){
            double s = allSigmas.get(i);
            if (s>=low && s<=high && !currentSigmas.contains(s))
                currentSigmas.add(s);
        }
    }


    private void preCompute (){

        initControls();

        computeFirstOutline (firstSigma, thresholdRangeSlider.getLowValue(), thresholdRangeSlider.getHighValue());
    }

    private void initControls (){
        int width = ipOriginal.getWidth();
        int height = ipOriginal.getHeight();

        minSigma = 1;
        maxSigma = (int)Math.sqrt(width*width + height*height)/70;
        maxSigma = Utils.roundIntegerToNearestUpperTenth(maxSigma);
        sigmasMarkSlider.setEnabled(true);
        sigmasMarkSlider.resetAll();
        sigmasMarkSlider.setMaximum((int)minSigma);
        sigmasMarkSlider.setMaximum((int)maxSigma);

        sigmasSpinner.setEnabled(true);
        sigmasSpinner.setValue((Integer)maxSigma);

        thresholdRangeSlider.setEnabled(true);
        thresholdRangeSlider.setValue(15);
        lowThresholdTextField.setText(""+thresholdRangeSlider.getLowValue());
        highThresholdTextField.setText(""+thresholdRangeSlider.getHighValue());

        fillHolesCheckBox.setEnabled(true);
        fillHolesCheckBox.setSelected(false);
        fillHolesRangeSlider2.setEnabled(fillHolesCheckBox.isSelected());
        fillHolesRangeSlider2.setMinimum((int)minSigma-1);
        fillHolesRangeSlider2.setMaximum((int)(30*maxSigma));
        fillHolesRangeSlider2.setMajorTickSpacing((int)((30*maxSigma) / 10));
        fillHolesSpinner.setEnabled(fillHolesCheckBox.isSelected());
        fillHolesSpinner.setValue((int)(30*maxSigma));

        smallParticlesCheckBox.setEnabled(true);
        smallParticlesCheckBox.setSelected(false);
        smallParticlesRangeSlider2.setEnabled(smallParticlesCheckBox.isSelected());
        smallParticlesRangeSlider2.setMinimum((int)minSigma-1);
        smallParticlesRangeSlider2.setMaximum((int)(40*maxSigma));
        smallParticlesRangeSlider2.setMajorTickSpacing((int)((40*maxSigma) / 10));
        removeSmallParticlesSpinner.setEnabled(smallParticlesCheckBox.isSelected());
        removeSmallParticlesSpinner.setValue((int)((40*maxSigma) / 10));

        firstSigma [0] = Math.round(maxSigma/4);

        toggleOverlayToggleButton.setEnabled(true);

        outlineSpinner.setValue(1);
        skeletonSpinner.setValue((Integer)maxSigma/8);
        branchingPointsSpinner.setValue((Integer)maxSigma/5);
        convexHullSizeSpinner.setValue(1);
    }

    private void computeFirstOutline(double [] sigmas, int minThreshold, int maxThreshold){

        Tubeness t = new Tubeness ();
        imageTubeness = t.runTubeness(new ImagePlus ("", ipOriginal), 100, sigmas, false);

        tubenessIp = imageTubeness.getProcessor();

        sI.add (new sigmaImages (sigmas[0], tubenessIp));

        sigmasMarkSlider.addMark((int)sigmas[0]);
        allSigmas.add (sigmas[0]);
        currentSigmas.add(sigmas[0]);

        ipThresholded = tubenessIp.duplicate();
        ipThresholded = ipThresholded.convertToByte(doScaling);
        imageThresholded.setProcessor(ipThresholded);
        Utils.threshold(ipThresholded, thresholdRangeSlider.getLowValue(), thresholdRangeSlider.getHighValue());
        ipThresholded.setThreshold(255.0, 255.0, ImageProcessor.NO_LUT_UPDATE);

        //set the flags to false so simgas, etc dont get recomputed in updateoutline();
        sigmaIsChanged = false;
        fillHolesIsChanged = false;
        smallParticlesIsChanged = false;

        updateOutline();
    }

    private void initLookAndFeel() {
        String lookAndFeel = null;

        if (Utils.LOOKANDFEEL != null) {
            if (Utils.LOOKANDFEEL.equals("Metal")) {
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            } else if (Utils.LOOKANDFEEL.equals("System")) {
                lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            } else if (Utils.LOOKANDFEEL.equals("Motif")) {
                lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
            } else if (Utils.LOOKANDFEEL.equals("GTK")) {
                lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
            } else if (Utils.LOOKANDFEEL.equals("Nimbus")) {
                lookAndFeel = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
            } else {
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            }

            try {
                UIManager.setLookAndFeel(lookAndFeel);

                if (Utils.LOOKANDFEEL.equals("Metal")) {
                  if (Utils.THEME.equals("DefaultMetal"))
                     MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
                  else if (Utils.THEME.equals("Ocean"))

                  UIManager.setLookAndFeel(new MetalLookAndFeel());
                }
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            catch (UnsupportedLookAndFeelException e) {
                  e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void exit (){
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AnalysisTabPanel;
    private javax.swing.JButton AnalyzeButton;
    private javax.swing.JButton ExitButton;
    private javax.swing.JTabbedPane TabbedPane;
    private javax.swing.JPanel backgroundParticlesPanel;
    private javax.swing.JButton branchinPointsColorButton;
    private javax.swing.JLabel branchingPointsLabel;
    private GUI.RoundedPanel branchingPointsRoundedPanel;
    private javax.swing.JSpinner branchingPointsSpinner;
    private javax.swing.JButton clearCalibrationButton;
    private javax.swing.JButton convexHullColorButton;
    private javax.swing.JLabel convexHullLabel;
    private GUI.RoundedPanel convexHullRoundedPanel;
    private javax.swing.JSpinner convexHullSizeSpinner;
    private javax.swing.JLabel distanceInMMLabel;
    private GUI.JNumberTextField distanceInMMNumberTextField;
    private javax.swing.JLabel distanceInPixelsLabel;
    private GUI.JNumberTextField distanceInPixelsNumberTextField;
    private javax.swing.JCheckBox fillHolesCheckBox;
    private com.jidesoft.swing.RangeSlider fillHolesRangeSlider;
    private javax.swing.JSlider fillHolesRangeSlider2;
    private javax.swing.JSpinner fillHolesSpinner;
    private javax.swing.JButton helpButton;
    private javax.swing.JTextField highThresholdTextField;
    private javax.swing.JComboBox imageResulFormatComboBox;
    private javax.swing.JTextField lowThresholdTextField;
    private GUI.TransparentTextField memTransparentTextField;
    private javax.swing.JButton openImageButton;
    private javax.swing.JButton outlineColorButton;
    private javax.swing.JLabel outlineLabel;
    private GUI.RoundedPanel outlineRoundedPanel;
    private javax.swing.JSpinner outlineSpinner;
    private javax.swing.JPanel overlaySettingsPanel;
    private static javax.swing.JProgressBar progressBar;
    private javax.swing.JSpinner removeSmallParticlesSpinner;
    private javax.swing.JCheckBox resizeImageCheckBox;
    private javax.swing.JPanel resizeImagePanel;
    private javax.swing.JLabel resizingFactorLabel;
    private javax.swing.JSpinner resizingFactorSpinner;
    private javax.swing.JButton saveImageButton;
    private javax.swing.JCheckBox saveResultImageCheckBox;
    private javax.swing.JButton saveResultsToButton;
    private javax.swing.JTextField saveResultsToTextField;
    private javax.swing.JPanel savingPreferencesPanel;
    private javax.swing.JLabel scaleLabel;
    private javax.swing.JTextField scaleTextField;
    private javax.swing.JPanel setScalePanel;
    private javax.swing.JPanel settingsTabPanel;
    private javax.swing.JCheckBox showBranchingPointsCheckBox;
    private javax.swing.JCheckBox showConvexHullCheckBox;
    private javax.swing.JCheckBox showOutlineCheckBox;
    private javax.swing.JCheckBox showOverlayCheckBox;
    private javax.swing.JCheckBox showSkeletonCheckBox;
    private kz.swing.markSlider sigmasMarkSlider;
    private javax.swing.JSpinner sigmasSpinner;
    private javax.swing.JButton skeletonColorButton;
    private GUI.RoundedPanel skeletonColorRoundedPanel;
    private javax.swing.JLabel skeletonLabel;
    private javax.swing.JSpinner skeletonSpinner;
    private javax.swing.JCheckBox smallParticlesCheckBox;
    private com.jidesoft.swing.RangeSlider smallParticlesRangeSlider;
    private javax.swing.JSlider smallParticlesRangeSlider2;
    private javax.swing.JPanel thicknessIntensityPanel;
    private com.jidesoft.swing.RangeSlider thresholdRangeSlider;
    private javax.swing.JToggleButton toggleOverlayToggleButton;
    private javax.swing.JButton unlockButton;
    // End of variables declaration//GEN-END:variables

}
