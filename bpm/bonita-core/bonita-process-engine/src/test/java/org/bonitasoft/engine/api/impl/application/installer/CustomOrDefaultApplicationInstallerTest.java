/**
 * Copyright (C) 2023 Bonitasoft S.A.
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
package org.bonitasoft.engine.api.impl.application.installer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

import org.bonitasoft.engine.business.application.importer.DefaultLivingApplicationImporter;
import org.bonitasoft.engine.exception.ApplicationInstallationException;
import org.bonitasoft.engine.tenant.TenantServicesManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * @author Emmanuel Duchastenier
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomOrDefaultApplicationInstallerTest {

    @Captor
    ArgumentCaptor<Callable<Object>> callableCaptor;

    @Mock
    private ApplicationInstaller applicationInstaller;
    @Mock
    DefaultLivingApplicationImporter defaultLivingApplicationImporter;
    @Mock
    TenantServicesManager tenantServicesManager;
    @InjectMocks
    @Spy
    private CustomOrDefaultApplicationInstaller listener;

    @Before
    public void before() throws Exception {
        doAnswer(inv -> callableCaptor.getValue().call()).when(tenantServicesManager)
                .inTenantSessionTransaction(callableCaptor.capture());

    }

    @Test
    public void should_detect_one_custom_application() throws Exception {
        //given
        Resource resource1 = mockResource("resource1", true, true, 1L);

        doReturn(new Resource[] { resource1 })
                .when(listener)
                .getCustomAppResourcesFromClasspath();

        //when
        Resource result = listener.detectCustomApplication();

        //then
        assertThat(result).isNotNull();
        assertThat(result.getFilename()).isEqualTo("resource1");
    }

    @Test
    public void should_raise_exception_if_more_than_one_application() throws Exception {
        //given
        Resource resource1 = mockResource("resource1", true, true, 1L);
        Resource resource2 = mockResource("resource2", true, true, 1L);

        doReturn(new Resource[] { resource1, resource2 })
                .when(listener)
                .getCustomAppResourcesFromClasspath();

        //then
        assertThatExceptionOfType(ApplicationInstallationException.class)
                .isThrownBy(listener::detectCustomApplication)
                .withMessage("More than one resource of type application zip detected. Abort startup.");
    }

    @Test
    public void should_ignore_non_existing_zip_file() throws Exception {
        //given
        Resource resource1 = mockResource("resource1", false, true, 1L);

        doReturn(new Resource[] { resource1 })
                .when(listener)
                .getCustomAppResourcesFromClasspath();

        //when
        Resource result = listener.detectCustomApplication();

        //then
        assertThat(result).isNull();
    }

    @Test
    public void should_ignore_non_readable_zip_file() throws Exception {
        //given
        Resource resource1 = mockResource("resource1", true, false, 1L);

        doReturn(new Resource[] { resource1 })
                .when(listener)
                .getCustomAppResourcesFromClasspath();

        //when
        Resource result = listener.detectCustomApplication();

        //then
        assertThat(result).isNull();
    }

    @Test
    public void should_ignore_empty_zip_file() throws Exception {
        //given
        Resource resource1 = mockResource("resource1", true, true, 0L);

        doReturn(new Resource[] { resource1 })
                .when(listener)
                .getCustomAppResourcesFromClasspath();

        //when
        Resource result = listener.detectCustomApplication();

        //then
        assertThat(result).isNull();
    }

    @Test
    public void should_install_custom_application_if_detected_and_platform_first_init_and_install_provided_resources_is_false()
            throws Exception {
        //given
        Resource resource1 = mockResource("resource1", true, true, 0L);
        InputStream resourceStream1 = mock(InputStream.class);

        doReturn(resource1).when(listener).detectCustomApplication();
        doReturn(resourceStream1).when(resource1).getInputStream();
        final ApplicationArchive applicationArchive = mock(ApplicationArchive.class);
        doReturn(applicationArchive).when(listener).getApplicationArchive(resourceStream1);
        doReturn(true).when(listener).isPlatformFirstInitialization();
        doReturn(false).when(listener).isAddDefaultPages();
        //when
        listener.autoDeployDetectedCustomApplication(any());

        //then
        verify(defaultLivingApplicationImporter, never()).importDefaultPages();
        verify(applicationInstaller).install(eq(applicationArchive));

        verify(defaultLivingApplicationImporter, never()).execute();
    }

    @Test
    public void should_install_custom_application_and_provided_provide_page_if_detected_and_platform_first_init_and_install_provided_resources_is_true()
            throws Exception {
        //given
        Resource resource1 = mockResource("resource1", true, true, 0L);
        InputStream resourceStream1 = mock(InputStream.class);

        doReturn(resource1).when(listener).detectCustomApplication();
        doReturn(resourceStream1).when(resource1).getInputStream();
        final ApplicationArchive applicationArchive = mock(ApplicationArchive.class);
        doReturn(applicationArchive).when(listener).getApplicationArchive(resourceStream1);
        doReturn(true).when(listener).isPlatformFirstInitialization();
        doReturn(true).when(listener).isAddDefaultPages();
        //when
        listener.autoDeployDetectedCustomApplication(any());

        //then
        InOrder inOrder = inOrder(defaultLivingApplicationImporter, applicationInstaller);
        inOrder.verify(defaultLivingApplicationImporter).importDefaultPages();
        inOrder.verify(applicationInstaller).install(eq(applicationArchive));
        verify(defaultLivingApplicationImporter, never()).execute();
    }

    @Test
    public void should_install_default_applications_if_no_custom_app_detected()
            throws Exception {
        //given
        doReturn(null).when(listener).detectCustomApplication();

        //when
        listener.autoDeployDetectedCustomApplication(any());

        //then
        verify(applicationInstaller, never()).install(any());
        verify(defaultLivingApplicationImporter).execute();
        verify(defaultLivingApplicationImporter, never()).importDefaultPages();
    }

    private Resource mockResource(String filename, boolean exists, boolean isReadable, long contentLength)
            throws IOException {
        Resource resource = spy(new FileSystemResource(mock(File.class)));
        doReturn(exists).when(resource).exists();
        doReturn(isReadable).when(resource).isReadable();
        doReturn(contentLength).when(resource).contentLength();
        doReturn(filename).when(resource).getFilename();
        return resource;
    }
}
