/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.texteditor.api.reconciler;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;

/**
 * Queue used by {@link ReconcilerImpl} to manage
 * dirty regions. When a dirty region is inserted into the queue, the queue tries
 * to fold it into the neighboring dirty region.
 *
 */
class DirtyRegionQueue
{

   /** The list of dirty regions. */
   private JsonArray<DirtyRegion> fDirtyRegions = JsonCollections.createArray();

   /**
    * Creates a new empty dirty region.
    */
   public DirtyRegionQueue()
   {
      super();
   }

   /**
    * Adds a dirty region to the end of the dirty-region queue.
    *
    * @param dr the dirty region to add
    */
   public void addDirtyRegion(DirtyRegion dr)
   {
      // If the dirty region being added is directly after the last dirty
      // region on the queue then merge the two dirty regions together.
      DirtyRegion lastDR = getLastDirtyRegion();
      boolean wasMerged = false;
      if (lastDR != null)
         if (lastDR.getType() == dr.getType())
            if (lastDR.getType() == DirtyRegion.INSERT)
            {
               if (lastDR.getOffset() + lastDR.getLength() == dr.getOffset())
               {
                  lastDR.mergeWith(dr);
                  wasMerged = true;
               }
            }
            else if (lastDR.getType() == DirtyRegion.REMOVE)
            {
               if (dr.getOffset() + dr.getLength() == lastDR.getOffset())
               {
                  lastDR.mergeWith(dr);
                  wasMerged = true;
               }
            }

      if (!wasMerged)
         // Don't merge- just add the new one onto the queue.
         fDirtyRegions.add(dr);
   }

   /**
    * Returns the last dirty region that was added to the queue.
    *
    * @return the last DirtyRegion on the queue
    */
   private DirtyRegion getLastDirtyRegion()
   {
      int size = fDirtyRegions.size();
      return (size == 0 ? null : (DirtyRegion)fDirtyRegions.get(size - 1));
   }

   /**
    * Returns the number of regions in the queue.
    *
    * @return the dirty-region queue-size
    */
   public int getSize()
   {
      return fDirtyRegions.size();
   }

   /**
    * Throws away all entries in the queue.
    */
   public void purgeQueue()
   {
      fDirtyRegions.clear();
   }

   /**
    * Removes and returns the first dirty region in the queue
    *
    * @return the next dirty region on the queue
    */
   public DirtyRegion removeNextDirtyRegion()
   {
      if (fDirtyRegions.size() == 0)
         return null;
      DirtyRegion dr = (DirtyRegion)fDirtyRegions.get(0);
      fDirtyRegions.remove(0);
      return dr;
   }
}