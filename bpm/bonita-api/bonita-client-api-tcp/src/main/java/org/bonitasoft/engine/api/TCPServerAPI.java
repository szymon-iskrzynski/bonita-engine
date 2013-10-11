/**
 * Copyright (C) 2011 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.engine.api;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.api.internal.ServerAPI;
import org.bonitasoft.engine.api.internal.ServerWrappedException;
import org.bonitasoft.engine.api.tcp.MethodCall;
import org.bonitasoft.engine.exception.ServerAPIException;

/**
 * @author Matthieu Chaffotte
 */
public class TCPServerAPI implements ServerAPI {

    private static final long serialVersionUID = 1L;

    private final Socket remoteServerAPI;

    public TCPServerAPI(final Map<String, String> parameters) throws ServerAPIException {
        //build socket access to the server socket
        final String host = parameters.get("host");
        final int port = Integer.parseInt(parameters.get("port"));
        try {
            remoteServerAPI = new Socket(host, port);
        } catch (UnknownHostException e) {
            throw new ServerAPIException(e);
        } catch (IOException e) {
            throw new ServerAPIException(e);
        }
    }

    @Override
    public Object invokeMethod(final Map<String, Serializable> options, final String apiInterfaceName, final String methodName,
            final List<String> classNameParameters, final Object[] parametersValues) throws ServerWrappedException, RemoteException {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            oos = new ObjectOutputStream(this.remoteServerAPI.getOutputStream());
            oos.writeObject(new MethodCall(options, apiInterfaceName, methodName, classNameParameters, parametersValues));
            oos.flush();

            ois = new ObjectInputStream(remoteServerAPI.getInputStream());
            final Object callReturn = ois.readObject();

            return checkInvokeMethodReturn(callReturn);
        } catch (Throwable e) {
            throw new ServerWrappedException(e);
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Object checkInvokeMethodReturn(final Object callReturn) throws Throwable {
        if (callReturn != null && callReturn instanceof Throwable) {
            throw (Throwable) callReturn;
        }
        return callReturn;
    }

}
