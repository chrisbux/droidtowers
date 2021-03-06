/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apach3.http.impl.nio;

import java.io.IOException;

import org.apach3.http.HttpException;
import org.apach3.http.nio.ContentDecoder;
import org.apach3.http.nio.ContentEncoder;
import org.apach3.http.nio.NHttpClientConnection;
import org.apach3.http.nio.NHttpClientHandler;
import org.apach3.http.nio.NHttpClientEventHandler;

/**
 * @deprecated (4.2)
 */
@Deprecated
class NHttpClientEventHandlerAdaptor implements NHttpClientEventHandler {

    private final NHttpClientHandler handler;

    public NHttpClientEventHandlerAdaptor(final NHttpClientHandler handler) {
        super();
        this.handler = handler;
    }

    public void connected(final NHttpClientConnection conn, final Object attachment) {
        this.handler.connected(conn, attachment);
    }

    public void requestReady(
            final NHttpClientConnection conn) throws IOException, HttpException {
        this.handler.requestReady(conn);
    }

    public void responseReceived(
            final NHttpClientConnection conn) throws IOException, HttpException {
        this.handler.responseReceived(conn);
    }

    public void inputReady(
            final NHttpClientConnection conn,
            final ContentDecoder decoder) throws IOException, HttpException {
        this.handler.inputReady(conn, decoder);
    }

    public void outputReady(
            final NHttpClientConnection conn,
            final ContentEncoder encoder) throws IOException, HttpException {
        this.handler.outputReady(conn, encoder);
    }

    public void exception(final NHttpClientConnection conn, final Exception ex) {
        if (ex instanceof HttpException) {
            this.handler.exception(conn, (HttpException) ex);
        } else if (ex instanceof IOException) {
            this.handler.exception(conn, (IOException) ex);
        } else {
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            } else {
                throw new Error("Unexpected exception: ", ex);
            }
        }
    }

    public void endOfInput(final NHttpClientConnection conn) throws IOException {
        conn.close();
    }

    public void timeout(final NHttpClientConnection conn) {
        this.handler.timeout(conn);
    }

    public void closed(final NHttpClientConnection conn) {
        this.handler.closed(conn);
    }

}
