package org.juitar.infra.netty.client;

/**
 * @author sha1n
 * Date: 11/27/13
 */
public interface HttpResponse {

    int getStatusCode();

    String getStatusLine();
}
