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
package com.codenvy.ide.ext.java.server;

import com.codenvy.ide.ext.java.shared.ShortTypeInfo;
import com.codenvy.ide.ext.java.shared.TypeInfo;

import org.apache.commons.io.IOUtils;
import org.everrest.core.impl.provider.json.*;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.ws.rs.core.MediaType;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Rest client for Codeassistant storage
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CodeAssistantStorageClient implements CodeAssistantStorage {
    private static final Log LOG = ExoLogger.getLogger(CodeAssistantStorageClient.class);

    private static final String STOGAGE_BASE = "/storage/get";

    public static final String STORAGE_BASE_URL = "exo.ide.codeassistan.storage-base-url";

    private final String baseURL;

    /**
     *
     */
    public CodeAssistantStorageClient(InitParams initParams) {
        this(readValueParam(initParams, "codeassistant-storage-base-url", System.getProperty(STORAGE_BASE_URL)));
    }

    /** @param baseURL */
    protected CodeAssistantStorageClient(String baseURL) {
        super();
        this.baseURL = baseURL;
    }

    private static String readValueParam(InitParams initParams, String paramName, String defaultValue) {
        if (initParams != null) {
            ValueParam vp = initParams.getValueParam(paramName);
            if (vp != null) {
                return vp.getValue();
            }
        }
        return defaultValue;
    }

    public String updateTypeIndex(String dependencyList, String zipUrl) throws IOException {
        return sendRequest(new URL(baseURL + "/storage/update/type"), dependencyList, zipUrl);
    }

    public String updateDockIndex(String dependencyList, String zipUrl) throws IOException {
        return sendRequest(new URL(baseURL + "/storage/update/dock"), dependencyList, zipUrl);
    }

    public String status(String statusUrl) throws IOException {

        HttpURLConnection http = null;
        String response = null;
        try {
            URL url = new URL(statusUrl);
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            int responseCode = http.getResponseCode();
            if (responseCode != 200) {
                LOG.error("Can't dowload dependency list from: " + statusUrl);
            }
            InputStream data = http.getInputStream();
            response = readBody(data, http.getContentLength());
        } catch (MalformedURLException e) {
            LOG.error("Invalid URL", e);
        } catch (IOException e) {
            LOG.error("Error", e);
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
        return response;

    }

    /**
     * @param dependencyList
     * @param zipUrl
     * @throws IOException
     */
    private String sendRequest(URL url, String dependencyList, String zipUrl) throws IOException {

        HttpURLConnection http = null;

        http = (HttpURLConnection)url.openConnection();
        http.setRequestMethod("POST");
        http.setRequestProperty("content-type", "application/json");
        http.setDoOutput(true);
        OutputStreamWriter w = new OutputStreamWriter(http.getOutputStream());
        w.write("{\"dependencies\":");
        w.write(dependencyList);
        w.write(",\"zipUrl\":");
        w.write("\"" + zipUrl + "\"");
        w.write("}");
        w.flush();
        w.close();
        return readBody(http.getInputStream(), http.getContentLength());
    }


    /** {@inheritDoc} */
    @Override
    public List<ShortTypeInfo> getAnnotations(String prefix, Set<String> dependencys) throws CodeAssistantException {
        return getShortTypeInfo("/annotations?prefix=" + prefix, dependencys);
    }


    private List<ShortTypeInfo> getShortTypeInfo(String urlPart, Set<String> dependencys) throws CodeAssistantException {
        HttpURLConnection in = null;
        try {
            URL url = new URL(baseURL + STOGAGE_BASE + urlPart);
            in = run(url, dependencys);
            if (in == null)
                return null;
            JsonParser parser = new JsonParser();
            parser.parse(in.getInputStream());
            ShortTypeInfo[] info =
                    (ShortTypeInfo[])ObjectBuilder.createArray(ShortTypeInfo[].class, parser.getJsonObject());
            return Arrays.asList(info);
        } catch (JsonException e) {
            if (LOG.isDebugEnabled())
                LOG.error("Can't parse JSON", e);
        } catch (IOException e) {
            if (LOG.isDebugEnabled())
                LOG.error("Invalid url", e);
        } finally {
            if (in != null)
                in.disconnect();
        }
        return null;

    }


    /** {@inheritDoc} */
    @Override
    public List<ShortTypeInfo> getClasses(String prefix, Set<String> dependencys) throws CodeAssistantException {
        return getShortTypeInfo("/classes?prefix=" + prefix, dependencys);
    }


    /** {@inheritDoc} */
    @Override
    public String getClassJavaDoc(String fqn, Set<String> dependencys) throws CodeAssistantException {
        return getJavadoc("/class-doc?fqn=" + fqn, dependencys);
    }


    /** {@inheritDoc} */
    @Override
    public List<ShortTypeInfo> getInterfaces(String prefix, Set<String> dependencys) throws CodeAssistantException {
        return getShortTypeInfo("/interfaces?prefix=" + prefix, dependencys);
    }


    /** {@inheritDoc} */
    @Override
    public String getMemberJavaDoc(String fqn, Set<String> dependencys) throws CodeAssistantException {
        try {
            return getJavadoc("/member-doc?fqn=" + URLEncoder.encode(fqn, "UTF-8"), dependencys);
        } catch (UnsupportedEncodingException e) {
            LOG.error("Can't encode fqn.", e);
            return null;
        }
    }


    private String getJavadoc(String urlPart, Set<String> dependencys) throws CodeAssistantException {
        HttpURLConnection in = null;
        try {
            URL url = new URL(baseURL + STOGAGE_BASE + urlPart);
            in = run(url, dependencys);
            if (in == null)
                return null;
            return IOUtils.toString(in.getInputStream());
        } catch (IOException e) {
            if (LOG.isDebugEnabled())
                LOG.error("Invalid url", e);
        } finally {
            if (in != null)
                in.disconnect();
        }
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public TypeInfo getTypeByFqn(String fqn, Set<String> dependencys) throws CodeAssistantException {
        HttpURLConnection in = null;
        try {
            URL url = new URL(baseURL + STOGAGE_BASE + "/type-by-fqn?fqn=" + fqn);
            JsonParser p = new JsonParser();
            in = run(url, dependencys);
            if (in == null)
                return null;
            p.parse(in.getInputStream());
            return ObjectBuilder.createObject(TypeInfo.class, p.getJsonObject());
        } catch (IOException e) {
            if (LOG.isDebugEnabled())
                LOG.error("Invalid url", e);
        } catch (JsonException e) {
            if (LOG.isDebugEnabled())
                LOG.error("Can't parse JSON", e);
        } finally {
            if (in != null)
                in.disconnect();
        }
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public List<ShortTypeInfo> getTypesByFqnPrefix(String fqnPrefix, Set<String> dependencys)
            throws CodeAssistantException {
        return getShortTypeInfo("/type-by-fqn-prefix?prefix=" + fqnPrefix, dependencys);
    }


    /** {@inheritDoc} */
    @Override
    public List<ShortTypeInfo> getTypesByNamePrefix(String namePrefix, Set<String> dependencys)
            throws CodeAssistantException {
        return getShortTypeInfo("/type-by-name-prefix?prefix=" + namePrefix, dependencys);
    }


    /** {@inheritDoc} */
    @Override
    public List<TypeInfo> getTypesInfoByNamePrefix(String namePrefix, Set<String> dependencys)
            throws CodeAssistantException {
        HttpURLConnection in = null;
        try {
            URL url = new URL(baseURL + STOGAGE_BASE + "/types-info-by-name-prefix?prefix=" + namePrefix);
            in = run(url, dependencys);
            if (in == null)
                return null;
            JsonParser p = new JsonParser();
            p.parse(in.getInputStream());
            TypeInfo[] info = (TypeInfo[])ObjectBuilder.createArray(TypeInfo[].class, p.getJsonObject());
            return Arrays.asList(info);
        } catch (IOException e) {
            if (LOG.isDebugEnabled())
                LOG.error("Invalid url", e);
        } catch (JsonException e) {
            if (LOG.isDebugEnabled())
                LOG.error("Can't parse JSON", e);
            return null;
        } finally {
            if (in != null)
                in.disconnect();
        }
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public List<String> getPackages(String packagePrefix, Set<String> dependencys) throws CodeAssistantException {
        HttpURLConnection in = null;
        try {
            URL url = new URL(baseURL + STOGAGE_BASE + "/find-packages?package=" + packagePrefix);
            in = run(url, dependencys);
            if (in == null)
                return null;
            JsonParser p = new JsonParser();
            p.parse(in.getInputStream());
            String[] pack = (String[])ObjectBuilder.createArray(String[].class, p.getJsonObject());
            return Arrays.asList(pack);
        } catch (IOException e) {
            if (LOG.isDebugEnabled())
                LOG.error("Invalid url", e);
        } catch (JsonException e) {
            if (LOG.isDebugEnabled())
                LOG.error("Can't parse JSON", e);
            return null;
        } finally {
            if (in != null)
                in.disconnect();
        }
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public List<String> getAllPackages(Set<String> dependencys) throws CodeAssistantException {
        HttpURLConnection in = null;
        try {
            URL url = new URL(baseURL + STOGAGE_BASE + "/get-packages");
            in = run(url, dependencys);
            if (in == null)
                return null;
            JsonParser p = new JsonParser();
            p.parse(in.getInputStream());
            String[] pack = (String[])ObjectBuilder.createArray(String[].class, p.getJsonObject());
            return Arrays.asList(pack);
        } catch (IOException e) {
            if (LOG.isDebugEnabled())
                LOG.error("Invalid url", e);
        } catch (JsonException e) {
            if (LOG.isDebugEnabled())
                LOG.error("Can't parse JSON", e);
            return null;
        } finally {
            if (in != null)
                in.disconnect();
        }
        return null;
    }

    private HttpURLConnection run(URL url, Set<String> dependencys) throws IOException, CodeAssistantException {
        HttpURLConnection http = null;
        http = (HttpURLConnection)url.openConnection();
        http.setRequestMethod("POST");
        http.setRequestProperty("content-type", MediaType.APPLICATION_JSON);
        http.setDoOutput(true);
        OutputStream out = null;
        try {
            JsonValue jsonArray = JsonGenerator.createJsonArray(dependencys);
            out = http.getOutputStream();
            JsonWriter writer = new JsonWriter(out);
            jsonArray.writeTo(writer);
            writer.flush();
        } catch (JsonException e) {
            String message = "Can't conwert dependencys to JSON";
            LOG.error(message, e);
            throw new CodeAssistantException(500, message);
        } finally {
            if (out != null) {
                out.close();
            }
        }

        int responseCode = http.getResponseCode();
        if (responseCode == 204) // no content
        {
            return null;
        }
        if (responseCode != 200) // 200 (Ok) response is expected.
        {
            fail(http);
        }

        return http;
    }

    private void fail(HttpURLConnection http) throws IOException, CodeAssistantException {
        InputStream errorStream = null;
        try {
            int responseCode = http.getResponseCode();
            int length = http.getContentLength();
            errorStream = http.getErrorStream();
            String body = null;
            if (errorStream != null) {
                body = readBody(errorStream, length);
            }
            throw new CodeAssistantException(responseCode, body);
        } finally {
            if (errorStream != null) {
                errorStream.close();
            }
        }
    }

    private String readBody(InputStream input, int contentLength) throws IOException {
        String body = null;
        if (contentLength > 0) {
            byte[] b = new byte[contentLength];
            int off = 0;
            int i;
            while ((i = input.read(b, off, contentLength - off)) > 0) {
                off += i;
            }
            body = new String(b);
        } else if (contentLength < 0) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int i;
            while ((i = input.read(buf)) != -1) {
                bout.write(buf, 0, i);
            }
            body = bout.toString();
        }
        return body;
    }

}