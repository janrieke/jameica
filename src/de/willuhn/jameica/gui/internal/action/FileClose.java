/**********************************************************************
 * $Source: /cvsroot/jameica/jameica/src/de/willuhn/jameica/gui/internal/action/FileClose.java,v $
 * $Revision: 1.3 $
 * $Date: 2004/11/04 23:59:04 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.gui.internal.action;

import de.willuhn.jameica.gui.Action;
import de.willuhn.util.ApplicationException;

/**
 * @author willuhn
 */
public class FileClose implements Action
{

  /**
   * @see de.willuhn.jameica.gui.Action#handleAction(java.lang.Object)
   */
  public void handleAction(Object context) throws ApplicationException
  {
    System.exit(0);
  }

}


/*********************************************************************
 * $Log: FileClose.java,v $
 * Revision 1.3  2004/11/04 23:59:04  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2004/10/08 16:41:58  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2004/10/08 13:38:19  willuhn
 * *** empty log message ***
 *
 **********************************************************************/