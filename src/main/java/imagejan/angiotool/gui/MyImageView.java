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
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.io.*;
import java.net.*;
import java.util.Dictionary;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.event.*;
import javax.imageio.ImageIO;

public class MyImageView extends View implements ImageObserver, MouseListener, MouseMotionListener {

    Class <?> c;
    
    public static final String
    	TOP = "top",
    	TEXTTOP = "texttop",
    	MIDDLE = "middle",
    	ABSMIDDLE = "absmiddle",
    	CENTER = "center",
    	BOTTOM = "bottom";

    public MyImageView(Element elem, Class <?> c) {
    	super(elem);
        this.c = c;

    	initialize(elem);
	StyleSheet sheet = getStyleSheet();
	attr = sheet.getViewAttributes(this);
    }
    
    
    private void initialize( Element elem ) {
	synchronized(this) {
	    loading = true;
	    fWidth = fHeight = 0;
	}
	int width = 0;
	int height = 0;
	boolean customWidth = false;
	boolean customHeight = false;
	try {
	    fElement = elem;
        
	    AttributeSet attr = elem.getAttributes();
            if (isURL()) {
	      URL src = getSourceURL();
	      if( src != null ) {
		  Dictionary cache = (Dictionary) getDocument().getProperty(IMAGE_CACHE_PROPERTY);
		  if( cache != null )
		      fImage = (Image) cache.get(src);
		  else
		      fImage = Toolkit.getDefaultToolkit().getImage(src);
	      }
            }
            else {
              String src =
                (String) fElement.getAttributes().getAttribute
                   (HTML.Attribute.SRC);
              src = src.replace("%20", " ");
              src = src.replace ("..", "");

              InputStream ip = c.getResourceAsStream(src);
                try {
                    fImage = ImageIO.read(ip);
                } catch (IOException ex) {
                    Logger.getLogger(MyImageView.class.getName()).log(Level.SEVERE, null, ex);
                }

              try { waitForImage(); }
              catch (InterruptedException e) { fImage = null; }

            }
	
	    height = getIntAttr(HTML.Attribute.HEIGHT,-1);
	    customHeight = (height>0);
	    if( !customHeight && fImage != null )
		height = fImage.getHeight(this);
	    if( height <= 0 )
		height = DEFAULT_HEIGHT;
		
	    width = getIntAttr(HTML.Attribute.WIDTH,-1);
	    customWidth = (width>0);
	    if( !customWidth && fImage != null )
		width = fImage.getWidth(this);
	    if( width <= 0 )
		width = DEFAULT_WIDTH;

	    if( fImage != null )
		if( customWidth && customHeight )
		    Toolkit.getDefaultToolkit().prepareImage(fImage,height,
							     width,this);
		else
		    Toolkit.getDefaultToolkit().prepareImage(fImage,-1,-1,
							     this);
	} finally {
	    synchronized(this) {
		loading = false;
		if (customWidth || fWidth == 0) {
		    fWidth = width;
		}
		if (customHeight || fHeight == 0) {
		    fHeight = height;
		}
	    }
	}
    }

    private boolean isURL() {
        String src =
          (String) fElement.getAttributes().getAttribute(HTML.Attribute.SRC);
        return src.toLowerCase().startsWith("file") ||
               src.toLowerCase().startsWith("http");
    }    

    private String processSrcPath(String src) {
      String val = src; 

      File imageFile = new File(src);
      if (imageFile.isAbsolute()) return src;

        String imagePath = System.getProperty("system.image.path.key");
        if (imagePath != null) {
          val = (new File(imagePath, imageFile.getPath())).toString();
        }
      return val;
    }

    private void waitForImage() throws InterruptedException { 
      int w = fImage.getWidth(this);
      int h = fImage.getHeight(this);

      while (true) {
        int flags = Toolkit.getDefaultToolkit().checkImage(fImage, w, h, this);

        if ( ((flags & ERROR) != 0) || ((flags & ABORT) != 0 ) )
          throw new InterruptedException();
        else if ((flags & (ALLBITS | FRAMEBITS)) != 0) 
          return;
        Thread.sleep(10);
      }
    }

    
    public AttributeSet getAttributes() {
	return attr;
    }

    boolean isLink( ) {
	AttributeSet anchorAttr = (AttributeSet)
	    fElement.getAttributes().getAttribute(HTML.Tag.A);
	if (anchorAttr != null) {
	    return anchorAttr.isDefined(HTML.Attribute.HREF);
	}
	return false;
    }
    
    int getBorder( ) {
        return getIntAttr(HTML.Attribute.BORDER, isLink() ?DEFAULT_BORDER :0);
    }
    
    int getSpace( int axis ) {
    	return getIntAttr( axis==X_AXIS ?HTML.Attribute.HSPACE :HTML.Attribute.VSPACE,
    			   0 );
    }
    
    Color getBorderColor( ) {
    	StyledDocument doc = (StyledDocument) getDocument();
        return doc.getForeground(getAttributes());
    }
    
    float getVerticalAlignment( ) {
	String align = (String) fElement.getAttributes().getAttribute(HTML.Attribute.ALIGN);
	if( align != null ) {
	    align = align.toLowerCase();
	    if( align.equals(TOP) || align.equals(TEXTTOP) )
	        return 0.0f;
	    else if( align.equals(this.CENTER) || align.equals(MIDDLE)
					       || align.equals(ABSMIDDLE) )
	        return 0.5f;
	}
	return 1.0f;
    }
    
    boolean hasPixels( ImageObserver obs ) {
        return fImage != null && fImage.getHeight(obs)>0
			      && fImage.getWidth(obs)>0;
    }
    

    private URL getSourceURL( ) {
 	String src = (String) fElement.getAttributes().getAttribute(HTML.Attribute.SRC);
 	if( src==null ) return null;

	URL reference = ((HTMLDocument)getDocument()).getBase();
        try {
 	    URL u = new URL(reference,src);
	    return u;
        } catch (MalformedURLException e) {
	    return null;
        }
    }
    
    private int getIntAttr(HTML.Attribute name, int deflt ) {
    	AttributeSet attr = fElement.getAttributes();
    	if( attr.isDefined(name) ) {
    	    int i;
 	    String val = (String) attr.getAttribute(name);
 	    if( val == null )
 	    	i = deflt;
 	    else
 	    	try{
 	            i = Math.max(0, Integer.parseInt(val));
 	    	}catch( NumberFormatException x ) {
 	    	    i = deflt;
 	    	}
	    return i;
	} else
	    return deflt;
    }
    
    public void setParent(View parent) {
	super.setParent(parent);
	fContainer = parent!=null ?getContainer() :null;
	if( parent==null && fComponent!=null ) {
	    fComponent.getParent().remove(fComponent);
	    fComponent = null;
	}
    }

    public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {
    	super.changedUpdate(e,a,f);
    	float align = getVerticalAlignment();
    	
    	int height = fHeight;
    	int width  = fWidth;
    	
    	initialize(getElement());
    	
    	boolean hChanged = fHeight!=height;
    	boolean wChanged = fWidth!=width;
    	if( hChanged || wChanged || getVerticalAlignment()!=align ) {
    	    getParent().preferenceChanged(this,hChanged,wChanged);
    	}
    }

    public void paint(Graphics g, Shape a) {
	Color oldColor = g.getColor();
	fBounds = a.getBounds();
        int border = getBorder();
	int x = fBounds.x + border + getSpace(X_AXIS);
	int y = fBounds.y + border + getSpace(Y_AXIS);
	int width = fWidth;
	int height = fHeight;
	int sel = getSelectionState();

	if( ! hasPixels(this) ) {
	    g.setColor(Color.lightGray);
	    g.drawRect(x,y,width-1,height-1);
	    g.setColor(oldColor);
	    loadIcons();
	    Icon icon = fImage==null ?sMissingImageIcon :sPendingImageIcon;
	    if( icon != null )
	        icon.paintIcon(getContainer(), g, x, y);
	}
		    
	if( fImage != null ) {
	    g.drawImage(fImage,x, y,width,height,this);
	}
	
	Color bc = getBorderColor();
	if( sel == 2 ) {
	    int delta = 2-border;
	    if( delta > 0 ) {
	    	x += delta;
	    	y += delta;
	    	width -= delta<<1;
	    	height -= delta<<1;
	    	border = 2;
	    }
	    bc = null;
	    g.setColor(Color.black);
	    g.fillRect(x+width-5,y+height-5,5,5);
	}

	if( border > 0 ) {
	    if( bc != null ) g.setColor(bc);
	    for( int i=1; i<=border; i++ )
	        g.drawRect(x-i, y-i, width-1+i+i, height-1+i+i);
	    g.setColor(oldColor);
	}
    }

    protected void repaint( long delay ) {
    	if( fContainer != null && fBounds!=null ) {
	    fContainer.repaint(delay,
	   	      fBounds.x,fBounds.y,fBounds.width,fBounds.height);
    	}
    }
    
    protected int getSelectionState( ) {
    	int p0 = fElement.getStartOffset();
    	int p1 = fElement.getEndOffset();
	if (fContainer instanceof JTextComponent) {
	    JTextComponent textComp = (JTextComponent)fContainer;
	    int start = textComp.getSelectionStart();
	    int end = textComp.getSelectionEnd();
	    if( start<=p0 && end>=p1 ) {
		if( start==p0 && end==p1 && isEditable() )
		    return 2;
		else
		    return 1;
	    }
	}
    	return 0;
    }
    
    protected boolean isEditable( ) {
    	return fContainer instanceof JEditorPane
    	    && ((JEditorPane)fContainer).isEditable();
    }
    
    /** Returns the text editor's highlight color. */
    protected Color getHighlightColor( ) {
    	JTextComponent textComp = (JTextComponent)fContainer;
    	return textComp.getSelectionColor();
    }

    public boolean imageUpdate( Image img, int flags, int x, int y,
    				int width, int height ) {
    	if( fImage==null || fImage != img )
    	    return false;
    	    
        if( (flags & (ABORT|ERROR)) != 0 ) {
            fImage = null;
            repaint(0);
            return false;
        }
        
	short changed = 0;
        if( (flags & ImageObserver.HEIGHT) != 0 )
            if( ! getElement().getAttributes().isDefined(HTML.Attribute.HEIGHT) ) {
		changed |= 1;
            }
        if( (flags & ImageObserver.WIDTH) != 0 )
            if( ! getElement().getAttributes().isDefined(HTML.Attribute.WIDTH) ) {
		changed |= 2;
            }
	synchronized(this) {
	    if ((changed & 1) == 1) {
		fWidth = width;
	    }
	    if ((changed & 2) == 2) {
		fHeight = height;
	    }
	    if (loading) {
		return true;
	    }
	}
        if( changed != 0 ) {
            if( DEBUG ) System.out.println("ImageView: resized to "+fWidth+"x"+fHeight);
	    
	    Document doc = getDocument();
	    try {
	      if (doc instanceof AbstractDocument) {
		((AbstractDocument)doc).readLock();
	      }
	      preferenceChanged(this,true,true);
	    } finally {
	      if (doc instanceof AbstractDocument) {
		((AbstractDocument)doc).readUnlock();
	      }
	    }			
	      
	    return true;
        }
	
	if( (flags & (FRAMEBITS|ALLBITS)) != 0 )
	    repaint(0);
	else if( (flags & SOMEBITS) != 0 )
	    if( sIsInc )
	        repaint(sIncRate);
        
        return ((flags & ALLBITS) == 0);
    }

    private static boolean sIsInc = true;
    private static int sIncRate = 100;

    public float getPreferredSpan(int axis) {
        int extra = 2*(getBorder()+getSpace(axis));
	switch (axis) {
	case View.X_AXIS:
	    return fWidth+extra;
	case View.Y_AXIS:
	    return fHeight+extra;
	default:
	    throw new IllegalArgumentException("Invalid axis: " + axis);
	}
    }

    public float getAlignment(int axis) {
	switch (axis) {
	case View.Y_AXIS:
	    return getVerticalAlignment();
	default:
	    return super.getAlignment(axis);
	}
    }

    public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
	int p0 = getStartOffset();
	int p1 = getEndOffset();
	if ((pos >= p0) && (pos <= p1)) {
	    Rectangle r = a.getBounds();
	    if (pos == p1) {
		r.x += r.width;
	    }
	    r.width = 0;
	    return r;
	}
	return null;
    }

    public int viewToModel(float x, float y, Shape a, Position.Bias[] bias) {
	Rectangle alloc = (Rectangle) a;
	if (x < alloc.x + alloc.width) {
	    bias[0] = Position.Bias.Forward;
	    return getStartOffset();
	}
	bias[0] = Position.Bias.Backward;
	return getEndOffset();
    }

    public void setSize(float width, float height) {
    }
    
    protected void resize( int width, int height ) {
    	if( width==fWidth && height==fHeight )
    	    return;
    	
    	fWidth = width;
    	fHeight= height;
    	
	MutableAttributeSet attr = new SimpleAttributeSet();
	attr.addAttribute(HTML.Attribute.WIDTH ,Integer.toString(width));
	attr.addAttribute(HTML.Attribute.HEIGHT,Integer.toString(height));
	((StyledDocument)getDocument()).setCharacterAttributes(
			fElement.getStartOffset(),
			fElement.getEndOffset(),
			attr, false);
    }
    
    public void mousePressed(MouseEvent e){
    	Dimension size = fComponent.getSize();
    	if( e.getX() >= size.width-7 && e.getY() >= size.height-7
    			&& getSelectionState()==2 ) {
    	    if(DEBUG)System.out.println("ImageView: grow!!! Size="+fWidth+"x"+fHeight);
    	    Point loc = fComponent.getLocationOnScreen();
    	    fGrowBase = new Point(loc.x+e.getX() - fWidth,
    	    			  loc.y+e.getY() - fHeight);
    	    fGrowProportionally = e.isShiftDown();
    	} else {
    	    fGrowBase = null;
    	    JTextComponent comp = (JTextComponent)fContainer;
    	    int start = fElement.getStartOffset();
    	    int end = fElement.getEndOffset();
    	    int mark = comp.getCaret().getMark();
    	    int dot  = comp.getCaret().getDot();
    	    if( e.isShiftDown() ) {
    	    	if( mark <= start )
    	    	    comp.moveCaretPosition(end);
    	    	else
    	    	    comp.moveCaretPosition(start);
    	    } else {
    	    	if( mark!=start )
    	            comp.setCaretPosition(start);
    	        if( dot!=end )
    	            comp.moveCaretPosition(end);
    	    }
    	}
    }
    
    public void mouseDragged(MouseEvent e ) {
    	if( fGrowBase != null ) {
    	    Point loc = fComponent.getLocationOnScreen();
    	    int width = Math.max(2, loc.x+e.getX() - fGrowBase.x);
    	    int height= Math.max(2, loc.y+e.getY() - fGrowBase.y);
    	    
    	    if( e.isShiftDown() && fImage!=null ) {
    	    	float imgWidth = fImage.getWidth(this);
    	    	float imgHeight = fImage.getHeight(this);
    	    	if( imgWidth>0 && imgHeight>0 ) {
    	    	    float prop = imgHeight / imgWidth;
    	    	    float pwidth = height / prop;
    	    	    float pheight= width * prop;
    	    	    if( pwidth > width )
    	    	        width = (int) pwidth;
    	    	    else
    	    	        height = (int) pheight;
    	    	}
    	    }
    	    
    	    resize(width,height);
    	}
    }

    public void mouseReleased(MouseEvent e){
    	fGrowBase = null;
    }

    public void mouseClicked(MouseEvent e){
    	if( e.getClickCount() == 2 ) {
    	}
    }

    public void mouseEntered(MouseEvent e){
    }
    public void mouseMoved(MouseEvent e ) {
    }
    public void mouseExited(MouseEvent e){
    }
    

    private Icon makeIcon(final String gifFile) throws IOException {
	InputStream resource = MyImageView.class.getResourceAsStream(gifFile);

        if (resource == null) {
            System.err.println(MyImageView.class.getName() + "/" + 
                               gifFile + " not found.");
            return null; 
        }
        BufferedInputStream in = 
            new BufferedInputStream(resource);
        ByteArrayOutputStream out = 
            new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int n;
        while ((n = in.read(buffer)) > 0) {
            out.write(buffer, 0, n);
        }
        in.close();
        out.flush();

        buffer = out.toByteArray();
        if (buffer.length == 0) {
            System.err.println("warning: " + gifFile + 
                               " is zero-length");
            return null;
        }
        return new ImageIcon(buffer);
    }

    private void loadIcons( ) {
        try{
            if( sPendingImageIcon == null )
            	sPendingImageIcon = makeIcon(PENDING_IMAGE_SRC);
            if( sMissingImageIcon == null )
            	sMissingImageIcon = makeIcon(MISSING_IMAGE_SRC);
	}catch( Exception x ) {
	    System.err.println("ImageView: Couldn't load image icons");
	}
    }
    
    protected StyleSheet getStyleSheet() {
	HTMLDocument doc = (HTMLDocument) getDocument();
	return doc.getStyleSheet();
    }


    private AttributeSet attr;
    private Element   fElement;
    private Image     fImage;
    private int       fHeight,fWidth;
    private Container fContainer;
    private Rectangle fBounds;
    private Component fComponent;
    private Point     fGrowBase;
    private boolean   fGrowProportionally;
    private boolean   loading;
    

    private static Icon sPendingImageIcon,
    			sMissingImageIcon;
    private static final String
        PENDING_IMAGE_SRC = "icons/image-delayed.gif", 
        MISSING_IMAGE_SRC = "icons/image-failed.gif";
    
    private static final boolean DEBUG = false;

    static final String IMAGE_CACHE_PROPERTY = "imageCache";

    private static final int
        DEFAULT_WIDTH = 32,
        DEFAULT_HEIGHT= 32,
        DEFAULT_BORDER=  2;

}
