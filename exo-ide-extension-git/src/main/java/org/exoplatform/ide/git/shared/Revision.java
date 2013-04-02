/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.git.shared;

/**
 * Describe single commit.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: Revision.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class Revision
{
   private String branch;

   private Boolean fake;

   /** Id of commit. */
   private String id;

   /** Commit message. */
   private String message;

   /** Time of commit in long format. */
   private long commitTime;

   /** Committer. */
   private GitUser committer;

   /**
    * @param branch branch name
    * @param id commit id
    * @param message commit message
    * @param commitTime time of commit in long format
    * @param committer committer
    */
   public Revision(String branch, String id, String message, long commitTime, GitUser committer)
   {
      this.fake = false;
      this.branch = branch;
      this.id = id;
      this.message = message;
      this.commitTime = commitTime;
      this.committer = committer;
   }

   /**
    * @param id commit id
    * @param message commit message
    * @param commitTime time of commit in long format
    * @param committer committer
    */
   public Revision(String id, String message, long commitTime, GitUser committer)
   {
      this.fake = false;
      this.id = id;
      this.message = message;
      this.commitTime = commitTime;
      this.committer = committer;
   }

   public Revision()
   {
      this.fake = true;
   }

   /**
    * Parameter which shows that this revision is a fake revision (i.e. TO for Exception)
    * @return
    */
   public Boolean isFake()
   {
      return fake;
   }

   public void setFake(Boolean fake)
   {
      this.fake = fake;
   }

   /** @return branch name */
   public String getBranch()
   {
      return branch;
   }

   /** @param branch branch name */
   public void setBranch(String branch)
   {
      this.branch = branch;
   }

   /** @return commit id */
   public String getId()
   {
      return id;
   }

   /** @return commit message */
   public String getMessage()
   {
      return message;
   }

   /** @return time of commit */
   public long getCommitTime()
   {
      return commitTime;
   }

   /** @return committer */
   public GitUser getCommitter()
   {
      return committer;
   }

   /** @param id the id to set */
   public void setId(String id)
   {
      this.id = id;
   }

   /** @param message the message to set */
   public void setMessage(String message)
   {
      this.message = message;
   }

   /** @param commitTime the commitTime to set */
   public void setCommitTime(long commitTime)
   {
      this.commitTime = commitTime;
   }

   /** @param committer the committer to set */
   public void setCommitter(GitUser committer)
   {
      this.committer = committer;
   }

   @Override
   public String toString()
   {
      return "Revision [branch=" + branch + ", id=" + id + ", message=" + message + ", commitTime=" + commitTime
         + ", committer=" + committer + ']';
   }
}
