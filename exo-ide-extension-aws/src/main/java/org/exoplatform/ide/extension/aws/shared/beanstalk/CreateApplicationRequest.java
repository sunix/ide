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
package org.exoplatform.ide.extension.aws.shared.beanstalk;

/**
 * Request to create new application.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface CreateApplicationRequest {
    /**
     * Get name of application. This name must be unique within AWS Beanstalk account. Length: 1 - 100 characters.
     *
     * @return application name
     */
    String getApplicationName();

    /**
     * Set name of application. This name must be unique within AWS Beanstalk account. Length: 1 - 100 characters.
     *
     * @param name
     *         application name
     * @see #getApplicationName()
     */
    void setApplicationName(String name);

    /**
     * Get application description. Length: 0 - 200 characters.
     *
     * @return application description
     */
    String getDescription();

    /**
     * Set application description. Length: 0 - 200 characters.
     *
     * @param description
     *         application description
     * @see #getDescription()
     */
    void setDescription(String description);

    /**
     * Name of S3 bucket where initial version of application uploaded before deploy to AWS Beanstalk. If this parameter
     * not specified random name generated and new S3 bucket created.
     *
     * @return S3 bucket name
     */
    String getS3Bucket();

    /**
     * Set S3 bucket name.
     *
     * @param s3Bucket
     *         S3 bucket name
     * @see #getS3Bucket()
     */
    void setS3Bucket(String s3Bucket);

    /**
     * Name of S3 key where initial version of  application uploaded before deploy to AWS Beanstalk. If this parameter
     * not specified random name generated and new S3 file created. If file with specified key already exists it content
     * will be overridden.
     *
     * @return S3 key
     */
    String getS3Key();

    /**
     * Set S3 key.
     *
     * @param s3Key
     *         S3 key
     * @see #getS3Key()
     */
    void setS3Key(String s3Key);

    /**
     * URL to pre-build war file. May be present for java applications ONLY.
     *
     * @return URL to pre-build war file
     */
    String getWar();

    /**
     * Set URL to pre-build war file.
     *
     * @param war
     *         URL to pre-build war file
     * @see #getWar()
     */
    void setWar(String war);
}