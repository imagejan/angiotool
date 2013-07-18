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

import javax.swing.text.html.*;
import javax.swing.text.*;

public class MyHTMLEditorKit extends HTMLEditorKit {

    private static Class <?> c;

    @Override
  public ViewFactory getViewFactory() {
    return new HTMLFactoryX();
  }

    public void setJar (Class <?> c){
        this.c = c;
    }


  public static class HTMLFactoryX extends HTMLFactory
    implements ViewFactory {
    
        @Override
    public View create(Element elem) {
      Object o = 
        elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
      if (o instanceof HTML.Tag) {
	HTML.Tag kind = (HTML.Tag) o;
        if (kind == HTML.Tag.IMG) {
            MyImageView miv = new MyImageView (elem, c);
          return miv;
        }
      }
      return super.create( elem );
    }
  }
}