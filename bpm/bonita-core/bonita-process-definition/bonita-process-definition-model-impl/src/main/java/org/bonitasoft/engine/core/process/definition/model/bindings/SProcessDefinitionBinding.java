/**
 * Copyright (C) 2012 BonitaSoft S.A.
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
package org.bonitasoft.engine.core.process.definition.model.bindings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bonitasoft.engine.core.process.definition.model.SActorDefinition;
import org.bonitasoft.engine.core.process.definition.model.SParameterDefinition;
import org.bonitasoft.engine.core.process.definition.model.impl.SFlowElementContainerDefinitionImpl;
import org.bonitasoft.engine.core.process.definition.model.impl.SProcessDefinitionImpl;
import org.bonitasoft.engine.xml.SXMLParseException;

/**
 * @author Baptiste Mesta
 * @author Matthieu Chaffotte
 */
public class SProcessDefinitionBinding extends SNamedElementBinding {

    private String version;

    private final List<SActorDefinition> actors = new ArrayList<SActorDefinition>();

    private SActorDefinition actorInitiator;

    private final Set<SParameterDefinition> parameters = new HashSet<SParameterDefinition>();

    private SFlowElementContainerDefinitionImpl processContainer;

    private String stringIndexLabel1;

    private String stringIndexLabel2;

    private String stringIndexLabel3;

    private String stringIndexLabel4;

    private String stringIndexLabel5;

    @Override
    public void setAttributes(final Map<String, String> attributes) throws SXMLParseException {
        super.setAttributes(attributes);
        version = attributes.get(XMLSProcessDefinition.VERSION);
        stringIndexLabel1 = attributes.get(XMLSProcessDefinition.STRING_INDEX_LABEL + 1);
        stringIndexLabel2 = attributes.get(XMLSProcessDefinition.STRING_INDEX_LABEL + 2);
        stringIndexLabel3 = attributes.get(XMLSProcessDefinition.STRING_INDEX_LABEL + 3);
        stringIndexLabel4 = attributes.get(XMLSProcessDefinition.STRING_INDEX_LABEL + 4);
        stringIndexLabel5 = attributes.get(XMLSProcessDefinition.STRING_INDEX_LABEL + 5);
    }

    @Override
    public void setChildElement(final String name, final String value, final Map<String, String> attributes) throws SXMLParseException {
    }

    @Override
    public void setChildObject(final String name, final Object value) throws SXMLParseException {
        if (XMLSProcessDefinition.ACTOR_NODE.equals(name)) {
            actors.add((SActorDefinition) value);
        } else if (XMLSProcessDefinition.INITIATOR_NODE.equals(name)) {
            actorInitiator = (SActorDefinition) value;
        } else if (XMLSProcessDefinition.PARAMETER_NODE.equals(name)) {
            parameters.add((SParameterDefinition) value);
        } else if (XMLSProcessDefinition.FLOW_ELEMENTS_NODE.equals(name)) {
            processContainer = (SFlowElementContainerDefinitionImpl) value;
        }
    }

    @Override
    public SProcessDefinitionImpl getObject() {
        final SProcessDefinitionImpl processDefinitionImpl = new SProcessDefinitionImpl(name, version);
        processDefinitionImpl.setId(Long.valueOf(id));
        processDefinitionImpl.setDescription(description);
        processDefinitionImpl.setStringIndexLabel(1, stringIndexLabel1);
        processDefinitionImpl.setStringIndexLabel(2, stringIndexLabel2);
        processDefinitionImpl.setStringIndexLabel(3, stringIndexLabel3);
        processDefinitionImpl.setStringIndexLabel(4, stringIndexLabel4);
        processDefinitionImpl.setStringIndexLabel(5, stringIndexLabel5);

        for (final SActorDefinition actor : actors) {
            processDefinitionImpl.addActor(actor);
        }
        if (actorInitiator != null) {
            processDefinitionImpl.setActorInitiator(actorInitiator);
        }
        for (final SParameterDefinition parameter : parameters) {
            processDefinitionImpl.addParameter(parameter);
        }
        if (processContainer != null) {
            processDefinitionImpl.setProcessContainer(processContainer);
            processContainer.setElementContainer(processDefinitionImpl);
        }
        return processDefinitionImpl;
    }

    @Override
    public String getElementTag() {
        return XMLSProcessDefinition.PROCESS_NODE;
    }

}
