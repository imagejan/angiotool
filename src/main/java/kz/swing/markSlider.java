package kz.swing;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;


/**
 * @author Enrique Zudaire
 * @version 012711
 * @since 012711
 */
public class markSlider extends JSlider implements MouseListener, MouseMotionListener{

    CustomSliderUI sliderUI;
    ArrayList <Integer> marks;
    int selectedMark;
    Rectangle track;
    private int markWidth = 8;
    private int markHeight = 25;
    private int markArcWidth = 5;
    private int markArcHeight = 5;

    public markSlider (){
        super();

        sliderUI = new CustomSliderUI(this);
        this.setUI(sliderUI);

        //retrieves the track rectangle
        track = sliderUI.getTrackRect();

        this.setMinimum(0);
        this.setMaximum(100);
        this.setMajorTickSpacing(10);
        this.setMinorTickSpacing(1);
        this.setPaintTicks(true);
        this.setPaintLabels(true);
        
        marks = new ArrayList <Integer>();

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void addMark(int m){
        if (!marks.contains(m)){
            marks.add(m);
            this.repaint();
        }
    }


    public ArrayList<Integer> getMarks (){
        return marks;
    }

    public void resetAll (){
        marks.clear();
        this.repaint();
    }



    private void drawMark (Graphics g, int x, int y){
        g.fillRoundRect(x, y, markWidth, markHeight, markArcWidth, markArcHeight);
    }


    private void drawMarks (Graphics g){
        for (int i = 0; i<marks.size(); i++){
            if (selectedMark == marks.get(i)){
                g.setColor(Color.green);
            }
            int x = valueForXPosition (marks.get(i))-(markWidth/2);
            int y = track.y-(markHeight/2);
            g.fillRoundRect(x, y, markWidth, markHeight, markArcWidth, markArcHeight);

            g.setColor(Color.red);
        }
    }

    @Override
    public void paintComponent (Graphics g){
        super.paintComponent(g);

        g.setColor(Color.red);

        drawMarks(g);
    }

    private int valueForXPosition (int xPos){

        int value;

        final int minValue = getMinimum();
        final int maxValue = getMaximum();
        final int trackLength = track.width;
        final int trackLeft = track.x;
        final int trackRight = track.y;

        double tempValue = ((double)(xPos-minValue) / (double)(maxValue-minValue)) * (double)trackLength;
        tempValue+=track.x;

        return (int)tempValue;
    }

    public void mouseClicked(MouseEvent e) {
        int value = sliderUI.valueForXPosition(e.getX());
        if (!marks.contains(value)) {
            marks.add(value);
        }else{
            marks.remove(new Integer(value));
        }
    }

    public void mouseMoved(MouseEvent e) {
        int mouseX = sliderUI.valueForXPosition(e.getX());
        //Point p = e.getPoint();
        for (int i = 0; i<marks.size(); i++){
            int x = marks.get(i);
            if (mouseX == x ) {
                selectedMark = x;
                repaint ();
                break;
            }
            else {
                selectedMark = 0;
                repaint();
            }
        }
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {
                    selectedMark = 0;
                repaint();
    }
    public void mouseDragged(MouseEvent e) {}


    class CustomSliderUI extends BasicSliderUI{

        public CustomSliderUI(JSlider b){
            super(b);
        }

        @Override
        public void paintThumb (Graphics g){

        }

        public Rectangle getTrackRect (){
            return this.trackRect;
        }
    }
}