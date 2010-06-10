/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ideall.client.outline;

import org.exoplatform.gwtframework.ui.client.smartgwt.component.TreeGrid;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.util.EnumTokenType;
import org.exoplatform.ideall.client.util.Token;

import java.util.List;

import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class OutlineTreeGrid <T extends Token> extends TreeGrid<T>
{
   
   private static final String ICON = "icon";
   
   private static final String VAR_ICON = Images.Outline.VAR_ITEM;
   
   private static final String FUNCTION_ICON = Images.Outline.FUNCTION_ITEM;
   
   private Tree tree;

   private TreeNode rootNode;
   
   public OutlineTreeGrid()
   {
      tree = new Tree();
      tree.setModelType(TreeModelType.CHILDREN);
      setData(tree);

      setSelectionType(SelectionStyle.SINGLE);

      setCanFocus(false);
      setShowConnectors(true);
      setCanSort(false);
      
      rootNode = new TreeNode("root");
      tree.setRoot(rootNode);

      setShowRoot(false);
   }


   @Override
   protected void doUpdateValue()
   {
      if (getValue().getTokens().size() > 0)
      {
         fillTreeItems(rootNode, getValue().getTokens());
      }
      else
      {
         return;
      }
      
   }
   
   public void fillTreeItems(TreeNode parentNode, List<Token> children)
   {
      TreeNode[] oldNodes = tree.getChildren(parentNode);
      tree.removeList(oldNodes);
      for (Token child : children)
      {
         TreeNode newNode = null;
         TreeNode[] nodes = tree.getChildren(parentNode);
         for (TreeNode node : nodes)
         {
            if (node.getAttributeAsObject(getValuePropertyName()) == child)
            {
               newNode = node;
               break;
            }
         }
         if (newNode == null)
         {
            newNode = new TreeNode(child.getName());
            newNode.setAttribute(getValuePropertyName(), child);
            if (child.getType().equals(EnumTokenType.FUNCTION))
            {
               newNode.setAttribute(ICON, FUNCTION_ICON);
            }
            else if (child.getType().equals(EnumTokenType.VARIABLE))
            {
               newNode.setAttribute(ICON, VAR_ICON);
            }
            tree.add(newNode, parentNode);
         }
         fillTreeItems(newNode, child.getTokens());
      }
   }

}
