/**
 * Copyright (C) 2019 Bonitasoft S.A.
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
package org.bonitasoft.engine.api.impl.application.installer.detector;

import java.io.*;

import org.bonitasoft.engine.api.impl.application.installer.ApplicationArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnSingleCandidate(ArtifactTypeDetector.class)
public class ArtifactTypeDetector {

    private static final Logger logger = LoggerFactory.getLogger(ArtifactTypeDetector.class);

    private static final String REST_API_EXTENSION_CONTENT_TYPE = "apiExtension";

    private final BdmDetector bdmDetector;

    private final LivingApplicationDetector livingApplicationDetector;
    private final OrganizationDetector organizationDetector;
    private final CustomPageDetector customPageDetector;
    private final ProcessDetector processDetector;
    private final ThemeDetector themeDetector;
    private final PageAndFormDetector pageAndFormDetector;
    private final LayoutDetector layoutDetector;

    public ArtifactTypeDetector(BdmDetector bdmDetector, LivingApplicationDetector livingApplicationDetector,
            OrganizationDetector organizationDetector, CustomPageDetector customPageDetector,
            ProcessDetector processDetector, ThemeDetector themeDetector, PageAndFormDetector pageAndFormDetector,
            LayoutDetector layoutDetector) {
        this.bdmDetector = bdmDetector;
        this.livingApplicationDetector = livingApplicationDetector;
        this.organizationDetector = organizationDetector;
        this.customPageDetector = customPageDetector;
        this.processDetector = processDetector;
        this.themeDetector = themeDetector;
        this.pageAndFormDetector = pageAndFormDetector;
        this.layoutDetector = layoutDetector;
    }

    public boolean isApplication(File file) throws IOException {
        return livingApplicationDetector.isCompliant(file);
    }

    public boolean isOrganization(File file) throws IOException {
        return organizationDetector.isCompliant(file);
    }

    public boolean isRestApiExtension(File file) throws IOException {
        return customPageDetector.isCompliant(file, REST_API_EXTENSION_CONTENT_TYPE);
    }

    public boolean isPage(File file) throws IOException {
        return pageAndFormDetector.isCompliant(file);
    }

    public boolean isLayout(File file) throws IOException {
        return layoutDetector.isCompliant(file);
    }

    public boolean isTheme(File file) throws IOException {
        return themeDetector.isCompliant(file);
    }

    public boolean isBdm(File file) {
        return bdmDetector.isCompliant(file);
    }

    public boolean isProcess(File file) throws IOException {
        return processDetector.isCompliant(file);
    }

    public void checkFileAndAddToArchive(File file, ApplicationArchive applicationArchive) throws IOException {
        if (isApplication(file)) {
            logger.info("Found application file: '{}'. ", file.getName());
            applicationArchive.addApplication(file);
        } else if (isProcess(file)) {
            logger.info("Found process file: '{}'. ", file.getName());
            applicationArchive.addProcess(file);
        } else if (isOrganization(file)) {
            logger.info("Found organization file: '{}'. ", file.getName());
            applicationArchive.setOrganization(file);
        } else if (isPage(file)) {
            logger.info("Found page file: '{}'. ", file.getName());
            applicationArchive.addPage(file);
        } else if (isLayout(file)) {
            logger.info("Found layout file: '{}'. ", file.getName());
            applicationArchive.addLayout(file);
        } else if (isTheme(file)) {
            logger.info("Found theme file: '{}'. ", file.getName());
            applicationArchive.addTheme(file);
        } else if (isRestApiExtension(file)) {
            logger.info("Found rest api extension file: '{}'. ", file.getName());
            applicationArchive.addRestAPIExtension(file);
        } else if (isBdm(file)) {
            logger.info("Found business data model file: '{}'. ", file.getName());
            applicationArchive.setBdm(file);
        } else {
            logger.warn("Ignoring file '{}'.", file.getName());
        }
    }

}
