/**********************************************************************
 * $Source: /cvsroot/jameica/jameica/src/de/willuhn/jameica/gui/util/SWTUtil.java,v $
 * $Revision: 1.26 $
 * $Date: 2011/08/18 09:17:09 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.gui.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.Session;

/**
 * Diverse statische SWT-Hilfsfunktionen.
 */
public class SWTUtil {

	private static Session imagecache = new Session();

	/**
	 * Disposed alle Kinder des Composites rekursiv jedoch nicht das Composite selbst.
   * @param c Composite, dessen Kinder disposed werden sollen.
	 */
	public static void disposeChildren(Composite c)
	{
	  if (c == null || c.isDisposed())
	    return;
	  
		try {
			Control[] children = c.getChildren();
			if (children == null)
				return;
			for (int i=0;i<children.length;++i)
			{
				// schauen, ob es ein Composite ist
				if (children[i] instanceof Composite)
					disposeChildren((Composite)children[i]);
				if (children[i] != null && !children[i].isDisposed())
        {
          children[i].dispose();
        }
			}
		}
		catch (Throwable t)
		{
			Logger.error("error while disposing composite children",t);
		}
	}

	/**
	 * Liefert ein SWT-Image basierend auf dem uebergebenen Dateinamen zurueck.
	 * Wenn die Datei nicht existiert, wird stattdessen ein 1x1 Pixel grosses
	 * und transparentes Dummy-Bild zurueckgeliefert.
	 * @param filename Dateiname (muss sich im Verzeichnis "img" befinden.
	 * @return das erzeugte Bild.
	 */
	public static Image getImage(String filename)
	{
    return getImage(filename, Application.getClassLoader());
	}

  /**
   * Liefert ein SWT-Image basierend auf dem uebergebenen Dateinamen zurueck.
   * Wenn die Datei nicht existiert, wird stattdessen ein 1x1 Pixel grosses
   * und transparentes Dummy-Bild zurueckgeliefert.
   * @param filename Dateiname (muss sich im Verzeichnis "img" befinden.
   * @param cl der Classloader, ueber den die Ressource geladen werden soll.
   * @return das erzeugte Bild.
   */
  public static Image getImage(String filename, ClassLoader cl)
  {
    Image image = (Image) imagecache.get(filename);
    if (image != null && !image.isDisposed())
      return image;

    InputStream is = null;
    try
    {
      
      // Wir versuchen erstmal, das Bild via Resource-Loader zu laden
      try
      {
        is = cl.getResourceAsStream("img/" + filename);
      }
      catch (Exception e)
      {
        // tolerieren wir
      }

      // OK, dann via Filesystem
      if (is == null)
      {
        try
        {
          File file = new File(filename);
          if (file.isFile() && file.canRead())
            is = new BufferedInputStream(new FileInputStream(file));
        }
        catch (Exception e2)
        {
          Logger.error("unable to load image from " + filename,e2);
        }
      }
      image = getImage(is);

      if (image != null)
      {
        imagecache.put(filename, image);
      }
      return image;
    }
    finally
    {
      if (is != null)
      {
        try
        {
          is.close();
        }
        catch (Exception e)
        {
          // ignore
        }
      }
    }
  }

  /**
   * Liefert ein SWT-Image basierend auf dem uebergebenen Dateinamen zurueck.
   * Wenn die Datei nicht existiert, wird stattdessen ein 1x1 Pixel grosses
   * und transparentes Dummy-Bild zurueckgeliefert.
   * @param is InputStream
   * @return das erzeugte Bild.
   */
  public static Image getImage(InputStream is)
  {
    Image image = null;
    
    if (is != null)
    {
      try
      {
        ImageData data = new ImageData(is);
        ImageData data2 = null;
        if (data.transparentPixel > 0) {
          data2 = data.getTransparencyMask();
          image = new Image(GUI.getDisplay(), data, data2);
        }
        else {
          image = new Image(GUI.getDisplay(), data);
        }
        
        return image;
      }
      catch (Throwable t)
      {
        Logger.error("unable to load image",t);
      }
    }
    return new Image(GUI.getDisplay(), Application.getClassLoader().getResourceAsStream("img" + "/empty.gif"));
  }

  /**
	 * Erzeugt ein Canvas mit dem dem angegebenen Hintergrundbild.
	 * @param parent Composite, in dem das Canvas gemalt werden soll.
	 * Hinweis: Das Composite muss ein GridLayout haben.
	 * @param image anzuzeigendes Hintergrundbild.
	 * @param align logische Kombinationen aus SWT.TOP, SWT.BOTTOM, SWT.LEFT, SWT.RIGHT.
	 * Wenn sowohl SWT.TOP als auch SWT.BOTTOM angegeben sind, wird das Bild vertikal gestreckt.
	 * @return das erzeugte Canvas.
	 */
	public static Canvas getCanvas(final Composite parent, final Image image, final int align)
	{
		final Rectangle i = image.getBounds();

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = i.height;
		Canvas canvas = new Canvas(parent,SWT.NONE);
		canvas.setLayoutData(gd);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Rectangle r = parent.getBounds();
				int x = 0;
				int y = 0;
				
				if ((align & SWT.BOTTOM) != 0) y = r.height - i.height;
				if ((align & SWT.RIGHT) != 0) x = r.width - i.width;
				
				if ((align & SWT.TOP) != 0 && (align & SWT.BOTTOM) != 0) // BUGZILLA 286 stretch vertically
				  e.gc.drawImage(image,0,0, r.width, i.height, 0, 0, r.width, r.height);
				else
          e.gc.drawImage(image,x,y);
			}
		});
		return canvas;
	}
  
  /**
   * Erzeugt ein rahmenloses GridLayout mit der angegebenen Anzahl von Spalten.
   * @param numColumns Anzahl der Spalten.
   * @param makeEqualsWidth legt fest, ob die Spalten gleich gross ein sollen, falls es mehrere sind.
   * @return das GridLayout.
   */
  public static GridLayout createGrid(int numColumns, boolean makeEqualsWidth)
  {
    final GridLayout l = new GridLayout(numColumns, makeEqualsWidth);
    l.marginWidth = 0;
    l.marginHeight = 0;
    l.horizontalSpacing = 0;
    l.verticalSpacing = 0;
    return l;
  }
  
  /**
   * Rechnet eine Angabe von pt (Point) entsprechend der DPI-Anzahl des Displays in Pixel um.
   * @param pt Points.
   * @return Anzahl der Pixel oder -1 wenn es zu einem Fehler kam.
   */
  public final static int pt2px(int pt)
  {
    try
    {
      Point dpi = GUI.getDisplay().getDPI();
      if (dpi == null)
        return -1;
      
      // Das sind die Pixel pro Inch.
      int pixel = dpi.y;

      // Ein Punkt ist 1/72 inch.
      // Also rechnen wir aus, wieviele Pixel auf 1/72 inch passen.
      // Und das sind genau die, die auf ein pt passen.
      double i = (double) pixel / 72d;
      
      // Also multiplizieren wir noch mit den pt und haben die Pixel
      return (int)((double) pt * i);
    }
    catch (Throwable t)
    {
      return -1;
    }
  }
  
  /**
   * Rechnet eine Angabe von mm (Millimeter) entsprechend der DPI-Anzahl des Displays in Pixel um.
   * @param mm die Millimeter.
   * @return Anzahl der Pixel oder -1 wenn es zu einem Fehler kam.
   */
  public final static int mm2px(int mm)
  {
    try
    {
      Point dpi = GUI.getDisplay().getDPI();
      if (dpi == null)
        return -1;
      
      // Das sind die Pixel pro Inch.
      int pixel = dpi.y;
      
      // Anzahl der Millimeter pro Inch
      double millis = 25.4d;
      
      // Anzahl der Inches ermitteln
      double inches = mm / millis;
      
      // Anzahl der Pixel auf dieser Laenge
      return (int) (pixel * inches);
    }
    catch (Throwable t)
    {
      return -1;
    }
  }
  
  /**
   * Ersetzt Zeichen aus einem Text, die SWT-intern als Steuerzeichen gelten.
   * @param text Originaler Text.
   * @return ersetzter Text.
   * BNUGZILLA 604 https://www.willuhn.de/bugzilla/show_bug.cgi?id=604
   */
  public final static String escapeLabel(String text)
  {
    if (text == null || text.length() == 0)
      return text;
    
    text = text.replaceAll("&","&&"); // "&" wird mit "&&" escaped.
    
    // Hier ggf. noch weitere Escapings vornehmen.
    return text;
  }


}


/**********************************************************************
 * $Log: SWTUtil.java,v $
 * Revision 1.26  2011/08/18 09:17:09  willuhn
 * @N BUGZILLA 286 - Testcode
 *
 * Revision 1.25  2011-05-30 10:17:11  willuhn
 * @N Funktion zum Umrechnen von mm in px
 *
 * Revision 1.24  2011-04-06 16:13:16  willuhn
 * @N BUGZILLA 631
 *
 * Revision 1.23  2010-07-27 11:54:43  willuhn
 * @N Fehlertoleranteres Laden von Bildern
 * @N Bilder koennen nun auch direkt im Filesystem liegen und koennen ueber den Pfad im Filesystem angegeben werden
 *
 * Revision 1.22  2009/11/03 01:19:33  willuhn
 * *** empty log message ***
 *
 * Revision 1.21  2008/11/13 18:43:21  willuhn
 * @B Children muessen nicht disposed werden, wenn das Parent schon disposed wurde
 *
 * Revision 1.20  2008/06/27 11:16:19  willuhn
 * @B Bug 604
 *
 * Revision 1.19  2007/11/13 00:45:18  willuhn
 * @N Classloader (privat/global) vom Plugin beeinflussbar (via "shared=true/false" in plugin.xml)
 *
 * Revision 1.18  2007/05/14 11:18:09  willuhn
 * @N Hoehe der Statusleiste abhaengig von DPI-Zahl und Schriftgroesse
 * @N Default-Schrift konfigurierbar und Beruecksichtigung dieser an mehr Stellen
 *
 * Revision 1.17  2006/04/20 08:49:41  web0
 * @C s/Childs/Children/
 *
 * Revision 1.16  2006/04/20 08:44:03  web0
 * @C s/Childs/Children/
 *
 * Revision 1.15  2005/08/15 13:15:32  web0
 * @C fillLayout removed
 *
 * Revision 1.14  2005/07/08 17:41:45  web0
 * *** empty log message ***
 *
 * Revision 1.13  2005/03/05 19:11:03  web0
 * *** empty log message ***
 *
 * Revision 1.12  2004/11/17 19:02:24  willuhn
 * *** empty log message ***
 *
 * Revision 1.11  2004/11/12 18:23:59  willuhn
 * *** empty log message ***
 *
 * Revision 1.10  2004/08/29 19:31:13  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2004/08/18 23:14:19  willuhn
 * @D Javadoc
 *
 * Revision 1.8  2004/07/21 23:54:54  willuhn
 * @C massive Refactoring ;)
 *
 * Revision 1.7  2004/07/09 00:12:47  willuhn
 * @C Redesign
 *
 * Revision 1.6  2004/06/30 20:58:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2004/06/10 20:56:53  willuhn
 * @D javadoc comments fixed
 *
 * Revision 1.4  2004/05/27 23:38:25  willuhn
 * @B deadlock in swt event queue while startGUITimeout
 *
 * Revision 1.3  2004/05/26 23:23:23  willuhn
 * @N Timeout fuer Messages in Statusbars
 *
 * Revision 1.2  2004/05/23 15:30:52  willuhn
 * @N new color/font management
 * @N new styleFactory
 *
 * Revision 1.1  2004/04/29 23:05:54  willuhn
 * @N new snapin feature
 *
 **********************************************************************/