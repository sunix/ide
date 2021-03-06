/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.git.shared;

import com.codenvy.dto.shared.DTO;

/**
 * Request to commit current state of index in new commit.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CommitRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
@DTO
public interface CommitRequest extends GitRequest {
    /** @return commit message */
    String getMessage();
    
    void setMessage(String message);
    
    CommitRequest withMessage(String message);

    /** @return <code>true</code> if need automatically stage files that have been modified and deleted */
    boolean isAll();
    
    void setAll(boolean isAll);
    
    CommitRequest withAll(boolean all);

    /** @return <code>true</code> in case when commit is amending a previous commit. */
    boolean isAmend();
    
    void setAmend(boolean isAmend);
    
    CommitRequest withAmend(boolean amend);
}