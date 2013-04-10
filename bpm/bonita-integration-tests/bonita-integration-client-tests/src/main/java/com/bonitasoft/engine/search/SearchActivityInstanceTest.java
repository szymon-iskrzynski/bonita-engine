/**
 * Copyright (C) 2013 BonitaSoft S.A.
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
package com.bonitasoft.engine.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.bonitasoft.engine.bpm.model.ActivityInstance;
import org.bonitasoft.engine.bpm.model.AutomaticTaskInstance;
import org.bonitasoft.engine.bpm.model.DesignProcessDefinition;
import org.bonitasoft.engine.bpm.model.FlowNodeType;
import org.bonitasoft.engine.bpm.model.HumanTaskInstance;
import org.bonitasoft.engine.bpm.model.ManualTaskInstance;
import org.bonitasoft.engine.bpm.model.ProcessDefinition;
import org.bonitasoft.engine.bpm.model.ProcessDefinitionBuilder;
import org.bonitasoft.engine.bpm.model.ProcessInstance;
import org.bonitasoft.engine.bpm.model.UserTaskInstance;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.search.ActivityInstanceSearchDescriptor;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.engine.test.APITestUtil;
import org.bonitasoft.engine.test.WaitUntil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.bonitasoft.engine.CommonAPISPTest;

/**
 * @author Baptiste Mesta
 */
public class SearchActivityInstanceTest extends CommonAPISPTest {

    @After
    public void afterTest() throws BonitaException {
        logout();
    }

    @Before
    public void beforeTest() throws BonitaException {
        login();
    }

    @Test
    public void searchActivityTaskInstances() throws Exception {
        final User user = createUser(USERNAME, PASSWORD);

        // define first process definition containing one userTask.
        final DesignProcessDefinition designProcessDef1 = APITestUtil.createProcessDefinitionWithHumanAndAutomaticSteps(Arrays.asList("userTask"),
                Arrays.asList(true));
        final ProcessDefinition processDef1 = deployAndEnableWithActor(designProcessDef1, ACTOR_NAME, user);
        // start twice and get 2 processInstances for processDef1
        getProcessAPI().startProcess(processDef1.getId());
        final ProcessInstance pi12 = getProcessAPI().startProcess(processDef1.getId());

        // define second process definition containing userTask, manuelTask and automaticTask one each
        final ProcessDefinitionBuilder definitionBuilder = new ProcessDefinitionBuilder().createNewInstance(PROCESS_NAME + "1", PROCESS_VERSION + "1");
        definitionBuilder.addStartEvent("start");
        definitionBuilder.addActor(ACTOR_NAME);
        definitionBuilder.addUserTask("userTask", ACTOR_NAME);
        definitionBuilder.addManualTask("manualTask", ACTOR_NAME);
        definitionBuilder.addAutomaticTask("automaticTask");
        definitionBuilder.addEndEvent("end");
        definitionBuilder.addTransition("start", "manualTask");
        definitionBuilder.addTransition("start", "automaticTask");
        definitionBuilder.addTransition("start", "userTask");
        definitionBuilder.addTransition("userTask", "end");
        final DesignProcessDefinition designProcessDef2 = definitionBuilder.done();

        final ProcessDefinition processDef2 = deployAndEnableWithActor(designProcessDef2, ACTOR_NAME, user);
        final long breakpointId = getProcessAPI().addBreakpoint(processDef2.getId(), "automaticTask", 2, 45);
        // start three times and get 3 processInstance for processDef2
        final ProcessInstance pi21 = getProcessAPI().startProcess(processDef2.getId());
        final ProcessInstance pi22 = getProcessAPI().startProcess(processDef2.getId());
        final ProcessInstance pi23 = getProcessAPI().startProcess(processDef2.getId());
        checkNbOfOpenTasks(pi12, "Expected 1 OPEN activities for process instance 1-2", 1);
        checkNbOfOpenTasks(pi21, "Expected 2 OPEN activities for process instance 2-1", 2);
        checkNbOfOpenTasks(pi22, "Expected 2 OPEN activities for process instance 2-2", 2);
        checkNbOfOpenTasks(pi23, "Expected 2 OPEN activities for process instance 2-3", 2);

        assertTrue("Expecting 9 activities for for process def " + processDef2.getId(), new WaitUntil(50, 2000) {

            @Override
            protected boolean check() throws Exception {
                final SearchOptionsBuilder searchOptionsBuilder = new SearchOptionsBuilder(0, 45);
                searchOptionsBuilder.filter(ActivityInstanceSearchDescriptor.PROCESS_DEFINITION_ID, processDef2.getId());
                final SearchResult<ActivityInstance> activityInstancesSearch = getProcessAPI().searchActivities(searchOptionsBuilder.done());
                return 9 == activityInstancesSearch.getCount();
            }
        }.waitUntil());

        // test activity type
        SearchOptionsBuilder searchOptionsBuilder = new SearchOptionsBuilder(0, 45);
        searchOptionsBuilder.filter(ActivityInstanceSearchDescriptor.PROCESS_DEFINITION_ID, processDef2.getId());
        searchOptionsBuilder.filter(ActivityInstanceSearchDescriptor.ACTIVITY_TYPE, FlowNodeType.USER_TASK);
        SearchResult<ActivityInstance> activityInstancesSearch = getProcessAPI().searchActivities(searchOptionsBuilder.done());
        assertEquals(3, activityInstancesSearch.getCount());
        for (final ActivityInstance activity : activityInstancesSearch.getResult()) {
            assertTrue(activity instanceof UserTaskInstance);
        }

        searchOptionsBuilder = new SearchOptionsBuilder(0, 45);
        searchOptionsBuilder.filter(ActivityInstanceSearchDescriptor.PROCESS_DEFINITION_ID, processDef2.getId());
        searchOptionsBuilder.filter(ActivityInstanceSearchDescriptor.ACTIVITY_TYPE, FlowNodeType.MANUAL_TASK);
        activityInstancesSearch = getProcessAPI().searchActivities(searchOptionsBuilder.done());
        assertEquals(3, activityInstancesSearch.getCount());
        for (final ActivityInstance activity : activityInstancesSearch.getResult()) {
            assertTrue(activity instanceof ManualTaskInstance);
        }

        searchOptionsBuilder = new SearchOptionsBuilder(0, 45);
        searchOptionsBuilder.filter(ActivityInstanceSearchDescriptor.PROCESS_DEFINITION_ID, processDef2.getId());
        searchOptionsBuilder.filter(ActivityInstanceSearchDescriptor.ACTIVITY_TYPE, FlowNodeType.HUMAN_TASK);
        activityInstancesSearch = getProcessAPI().searchActivities(searchOptionsBuilder.done());
        assertEquals(6, activityInstancesSearch.getCount());
        for (final ActivityInstance activity : activityInstancesSearch.getResult()) {
            assertTrue(activity instanceof HumanTaskInstance);
            assertTrue(activity instanceof ManualTaskInstance || activity instanceof UserTaskInstance);
        }

        searchOptionsBuilder = new SearchOptionsBuilder(0, 45);
        searchOptionsBuilder.filter(ActivityInstanceSearchDescriptor.PROCESS_DEFINITION_ID, processDef2.getId());
        searchOptionsBuilder.filter(ActivityInstanceSearchDescriptor.ACTIVITY_TYPE, FlowNodeType.AUTOMATIC_TASK);
        activityInstancesSearch = getProcessAPI().searchActivities(searchOptionsBuilder.done());
        assertEquals(3, activityInstancesSearch.getCount());
        assertTrue(activityInstancesSearch.getResult().get(0) instanceof AutomaticTaskInstance);
        for (final ActivityInstance activity : activityInstancesSearch.getResult()) {
            assertTrue(activity instanceof AutomaticTaskInstance);
        }

        searchOptionsBuilder = new SearchOptionsBuilder(0, 45);
        searchOptionsBuilder.filter(ActivityInstanceSearchDescriptor.PROCESS_DEFINITION_ID, processDef1.getId());
        activityInstancesSearch = getProcessAPI().searchActivities(searchOptionsBuilder.done());
        assertEquals(2, activityInstancesSearch.getCount());
        for (final ActivityInstance activity : activityInstancesSearch.getResult()) {
            assertTrue(activity instanceof HumanTaskInstance || activity instanceof AutomaticTaskInstance);
        }

        searchOptionsBuilder = new SearchOptionsBuilder(0, 45);
        searchOptionsBuilder.filter(ActivityInstanceSearchDescriptor.PROCESS_INSTANCE_ID, pi12.getId());
        activityInstancesSearch = getProcessAPI().searchActivities(searchOptionsBuilder.done());
        assertEquals(1, activityInstancesSearch.getCount());

        searchOptionsBuilder = new SearchOptionsBuilder(0, 45);
        searchOptionsBuilder.filter(ActivityInstanceSearchDescriptor.PROCESS_DEFINITION_ID, processDef2.getId());
        activityInstancesSearch = getProcessAPI().searchActivities(searchOptionsBuilder.done());
        assertEquals(9, activityInstancesSearch.getCount());

        searchOptionsBuilder = new SearchOptionsBuilder(0, 45);
        searchOptionsBuilder.filter(ActivityInstanceSearchDescriptor.PROCESS_DEFINITION_ID, processDef2.getId());
        searchOptionsBuilder.filter(ActivityInstanceSearchDescriptor.ACTIVITY_TYPE, FlowNodeType.HUMAN_TASK);
        activityInstancesSearch = getProcessAPI().searchActivities(searchOptionsBuilder.done());
        assertEquals(6, activityInstancesSearch.getCount());

        searchOptionsBuilder = new SearchOptionsBuilder(0, 45);
        searchOptionsBuilder.searchTerm("manualTask");
        activityInstancesSearch = getProcessAPI().searchActivities(searchOptionsBuilder.done());
        assertEquals(3, activityInstancesSearch.getCount());
        for (final ActivityInstance activity : activityInstancesSearch.getResult()) {
            assertEquals("manualTask", activity.getName());
        }

        searchOptionsBuilder = new SearchOptionsBuilder(0, 45);
        searchOptionsBuilder.searchTerm("userTask");
        activityInstancesSearch = getProcessAPI().searchActivities(searchOptionsBuilder.done());
        assertEquals(5, activityInstancesSearch.getCount());
        for (final ActivityInstance activity : activityInstancesSearch.getResult()) {
            assertTrue("keyword search sould return only tasks with name containing 'userTask'", activity.getName().contains("userTask"));
        }

        deleteUser(user.getId());
        getProcessAPI().removeBreakpoint(breakpointId);
        disableAndDelete(processDef1);
        disableAndDelete(processDef2);
    }

}