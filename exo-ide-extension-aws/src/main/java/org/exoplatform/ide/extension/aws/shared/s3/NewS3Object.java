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
package org.exoplatform.ide.extension.aws.shared.s3;

/**
 * Information about newly created object in S3 bucket
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface NewS3Object {
    /**
     * Get name of S3 bucket where object stored
     *
     * @return name of S3 bucket
     */
    String getS3Bucket();

    /**
     * Set name of S3 bucket where bucket stored
     *
     * @param s3Bucket
     *         name of bucket
     */
    void setS3Bucket(String s3Bucket);

    /**
     * Get S3 key under which this object is stored
     *
     * @return name of S3 key under which object is stored
     */
    String getS3Key();

    /**
     * Set S3 key under which this object is stored
     *
     * @param s3Key
     *         name of key under which this object is stored
     */
    void setS3Key(String s3Key);

    /**
     * Get version ID of this uploaded object
     *
     * @return version ID of this object
     */
    String getVersionId();

    /**
     * Set version ID for this uploaded object
     *
     * @param versionId
     *         version ID for this uploaded object
     */
    void setVersionId(String versionId);
}