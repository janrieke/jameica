/**********************************************************************
 * $Source: /cvsroot/jameica/jameica/src/de/willuhn/jameica/gui/AbstractItemXml.java,v $
 * $Revision: 1.2 $
 * $Date: 2004/10/11 22:41:17 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.gui;

import java.rmi.RemoteException;
import java.util.ArrayList;

import net.n3.nanoxml.IXMLElement;
import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.GenericObject;
import de.willuhn.datasource.GenericObjectNode;
import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * @author willuhn
 */
public abstract class AbstractItemXml implements Item
{

  protected Item parent;
  protected IXMLElement path;
  protected I18N i18n = Application.getI18n();
  protected ArrayList childs = new ArrayList();

  /**
   * ct.
   * @param parent das Eltern-Element.
   * @param path Pfad in der XML-Datei.
   * @param i18n optionaler Uebersetzer, um die Menu/Navi-Eintraege in die
   * ausgewaehlte Sprache uebersetzen zu koennen.
   */
  AbstractItemXml(Item parent, IXMLElement path, I18N i18n)
  {
    this.parent = parent;
    this.path = path;
    if (i18n != null)
      this.i18n = i18n;
  }

  /**
   * @see de.willuhn.jameica.gui.Item#getName()
   */
  public String getName()
  {
    return i18n.tr(path.getAttribute("name","unknown"));
  }

  /**
   * @see de.willuhn.jameica.gui.Item#getAction()
   */
  public Action getAction()
  {
    return new Action()
    {
      public void handleAction(Object context) throws ApplicationException
      {
        String action = path.getAttribute("action",null);
        GUI.startView(action,null);
        GUI.getStatusBar().setStatusText(getName());
      }
    };
  }

  /**
   * @see de.willuhn.datasource.GenericObjectNode#getChilds()
   */
  public GenericIterator getChilds() throws RemoteException
  {
    return PseudoIterator.fromArray((Item[])childs.toArray(new Item[childs.size()]));
  }

  /**
   * @see de.willuhn.datasource.GenericObjectNode#hasChild(de.willuhn.datasource.GenericObjectNode)
   */
  public boolean hasChild(GenericObjectNode object) throws RemoteException
  {
    throw new UnsupportedOperationException("not implemented");
  }

  /**
   * @see de.willuhn.datasource.GenericObjectNode#getParent()
   */
  public GenericObjectNode getParent() throws RemoteException
  {
    return this.parent;
  }

  /**
   * @see de.willuhn.datasource.GenericObjectNode#getPossibleParents()
   */
  public GenericIterator getPossibleParents() throws RemoteException
  {
    throw new UnsupportedOperationException("not implemented");
  }

  /**
   * @see de.willuhn.datasource.GenericObjectNode#getPath()
   */
  public GenericIterator getPath() throws RemoteException
  {
    throw new UnsupportedOperationException("not implemented");
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getAttribute(java.lang.String)
   */
  public Object getAttribute(String name) throws RemoteException
  {
    if ("name".equals(name))
      return getName();
    return path.getAttribute(name,null);
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getID()
   */
  public String getID() throws RemoteException
  {
    return this.path.getFullName() + ":" + this.path.getLineNr();
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "name";
  }

  /**
   * @see de.willuhn.datasource.GenericObject#equals(de.willuhn.datasource.GenericObject)
   */
  public boolean equals(GenericObject other) throws RemoteException
  {
    if (other == null)
      return false;
    return getID().equals(other.getID());
  }

  /**
   * @see de.willuhn.jameica.gui.NavigationItem#addChild(de.willuhn.jameica.gui.NavigationItem)
   */
  public void addChild(NavigationItem item) throws RemoteException
  {
    if (item == null)
      return;
    childs.add(item);
  }
}


/*********************************************************************
 * $Log: AbstractItemXml.java,v $
 * Revision 1.2  2004/10/11 22:41:17  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2004/10/08 16:41:58  willuhn
 * *** empty log message ***
 *
 **********************************************************************/