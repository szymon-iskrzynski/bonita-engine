/**
 * Copyright (C) 2022 Bonitasoft S.A.
 * Bonitasoft, 32 rue Gustave Eiffel - 38000 Grenoble
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
package org.bonitasoft.web.rest.server.datastore.system;

import java.util.Map;

import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.web.rest.model.system.TenantAdminItem;
import org.bonitasoft.web.rest.server.engineclient.EngineAPIAccessor;
import org.bonitasoft.web.rest.server.engineclient.EngineClientFactory;
import org.bonitasoft.web.rest.server.engineclient.TenantManagementEngineClient;
import org.bonitasoft.web.rest.server.framework.api.Datastore;
import org.bonitasoft.web.rest.server.framework.api.DatastoreHasGet;
import org.bonitasoft.web.rest.server.framework.api.DatastoreHasUpdate;
import org.bonitasoft.web.toolkit.client.common.exception.api.APIException;
import org.bonitasoft.web.toolkit.client.data.APIID;

/**
 * @author Julien Mege
 */
public class TenantAdminDatastore extends Datastore
        implements DatastoreHasUpdate<TenantAdminItem>, DatastoreHasGet<TenantAdminItem> {

    protected final APISession apiSession;

    public TenantAdminDatastore(final APISession apiSession) {
        this.apiSession = apiSession;
    }

    @Override
    public TenantAdminItem update(final APIID unusedId, final Map<String, String> attributes) {
        final TenantAdminItem tenantAdminItem = new TenantAdminItem();
        try {
            final boolean doPause = Boolean.parseBoolean(attributes.get(TenantAdminItem.ATTRIBUTE_IS_PAUSED));
            if (!doPause) {
                getTenantManagementEngineClient().resumeTenant();
            } else {
                getTenantManagementEngineClient().pauseTenant();
            }
            tenantAdminItem.setIsPaused(doPause);
            return tenantAdminItem;
        } catch (final Exception e) {
            throw new APIException(e);
        }
    }

    @Override
    public TenantAdminItem get(final APIID id) {
        final TenantAdminItem tenantAdminItem = new TenantAdminItem();
        try {
            final boolean tenantPaused = getTenantManagementEngineClient().isTenantPaused();
            tenantAdminItem.setIsPaused(tenantPaused);
            return tenantAdminItem;
        } catch (final Exception e) {
            throw new APIException(e);
        }
    }

    protected TenantManagementEngineClient getTenantManagementEngineClient() {
        return new EngineClientFactory(new EngineAPIAccessor(apiSession)).createTenantManagementEngineClient();
    }
}
