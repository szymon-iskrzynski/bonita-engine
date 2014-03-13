/**
 * Copyright (C) 2014 BonitaSoft S.A.
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
 */
package org.bonitasoft.engine.api.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import org.bonitasoft.engine.identity.CustomUserInfoDefinition;
import org.bonitasoft.engine.identity.CustomUserInfoDefinitionCreator;
import org.bonitasoft.engine.identity.IdentityService;
import org.bonitasoft.engine.identity.model.SCustomUserInfoDefinition;
import org.bonitasoft.engine.identity.model.builder.SCustomUserInfoDefinitionBuilderFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Vincent Elcrin
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomUserInfoDefinitionAPITest {

    @Mock
    private IdentityService service;

    @Mock(answer = Answers.RETURNS_MOCKS)
    private SCustomUserInfoDefinitionBuilderFactory factory;

    private CustomUserInfoDefinitionAPI api;

    @Before
    public void setUp() throws Exception {
        api = new CustomUserInfoDefinitionAPI(service);
    }

    @Test
    public void create_should_call_service_to_retrieve_the_item_and_return_result_as_a_CustomUserDefinition() throws Exception {
        given(service.createCustomUserInfoDefinition(any(SCustomUserInfoDefinition.class)))
                .willReturn(new DummySCustomUserInfoDefinition(1L));

        CustomUserInfoDefinition definition = api.create(factory, new CustomUserInfoDefinitionCreator("skill"));

        assertThat(definition.getId()).isEqualTo(1L);
    }

    @Test
    public void list_call_service_to_retrieve_items_and_return_result_as_a_list_of_CustomUserDefinition() throws Exception {
        given(service.getCustomUserInfoDefinitions(0, 3)).willReturn(
                Arrays.<SCustomUserInfoDefinition> asList(
                        new DummySCustomUserInfoDefinition(1L),
                        new DummySCustomUserInfoDefinition(2L),
                        new DummySCustomUserInfoDefinition(3L)));

        List<CustomUserInfoDefinition> definitions = api.list(0, 3);

        assertThat(definitions.get(0).getId()).isEqualTo(1L);
        assertThat(definitions.get(1).getId()).isEqualTo(2L);
        assertThat(definitions.get(2).getId()).isEqualTo(3L);
    }

    @Test
    public void delete_should_call_server_to_delete_the_item() throws Exception {
        SCustomUserInfoDefinition definition = new DummySCustomUserInfoDefinition(1L);
        given(service.getCustomUserInfoDefinition(1L))
                .willReturn(definition);

        api.delete(1);

        verify(service, atLeastOnce()).deleteCustomUserInfoDefinition(definition);
    }

    public void delete_should_return_deleted_item_with_an_invalid_id() throws Exception {
        given(service.getCustomUserInfoDefinition(1L))
                .willReturn(new DummySCustomUserInfoDefinition(1L, "name", "", ""));

        CustomUserInfoDefinition definition = api.delete(1);

        assertThat(definition.getId()).isEqualTo(-1L);
        assertThat(definition.getName()).isEqualTo("name");
    }
}
