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
package org.exoplatform.ide.git.client;

import com.google.gwt.json.client.JSONObject;

import org.exoplatform.ide.git.client.marshaller.Constants;
import org.exoplatform.ide.git.client.marshaller.PullRequestMarshaller;
import org.exoplatform.ide.git.shared.PullRequest;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 28, 2011 9:55:30 AM anya $
 */
public class PullMarshallerGwtTest extends BaseGwtTest {
    /** Test pull from remote repository request marshaller. */
    public void testPullRequestMarshaller() {
        String remote = "origin";
        String refspec = "branchToPull";

        PullRequest pullRequest = new PullRequest(remote, refspec, 0);
        PullRequestMarshaller marshaller = new PullRequestMarshaller(pullRequest);
        String json = marshaller.marshal();

        assertNotNull(json);

        JSONObject jsonObject = new JSONObject(build(json));
        assertTrue(jsonObject.containsKey(Constants.REMOTE));
        assertEquals(remote, jsonObject.get(Constants.REMOTE).isString().stringValue());

        assertTrue(jsonObject.containsKey(Constants.REF_SPEC));
        assertEquals(refspec, jsonObject.get(Constants.REF_SPEC).isString().stringValue());
    }
}