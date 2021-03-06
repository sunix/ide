package com.codenvy.ide.ext.git.server.provider.rest;

import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.rest.HTTPStatus;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Mapping all Provider exceptions into one response exception stream.
 */
@Provider
public class ProviderExceptionMapper implements ExceptionMapper<ProviderException> {
    /** {@inheritDoc} */
    @Override
    public Response toResponse(ProviderException e) {
        return Response.status(e.getResponseStatus())
                       .header(HTTPHeader.JAXRS_BODY_PROVIDED,
                               e.getResponseStatus() == HTTPStatus.UNAUTHORIZED ? "Authentication-required" : "Error-Message")
                       .entity(e.getMessage())
                       .type(e.getContentType())
                       .build();
    }
}
