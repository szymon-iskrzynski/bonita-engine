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
package org.bonitasoft.console.common.server.page;

import static java.lang.String.format;

import java.io.Serializable;

import org.assertj.core.api.AbstractAssert;
import org.bonitasoft.web.extension.rest.RestApiResponse;

/**
 * {@link RestApiResponse} specific assertions - Generated by CustomAssertionGenerator.
 */
public class RestApiResponseAssert
        extends AbstractAssert<RestApiResponseAssert, org.bonitasoft.web.extension.rest.RestApiResponse> {

    /**
     * Creates a new </code>{@link RestApiResponseAssert}</code> to make assertions on actual RestApiResponse.
     *
     * @param actual the RestApiResponse we want to make assertions on.
     */
    public RestApiResponseAssert(org.bonitasoft.web.extension.rest.RestApiResponse actual) {
        super(actual, RestApiResponseAssert.class);
    }

    /**
     * An entry point for RestApiResponseAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
     * With a static import, one's can write directly : <code>assertThat(myRestApiResponse)</code> and get specific
     * assertion with code completion.
     *
     * @param actual the RestApiResponse we want to make assertions on.
     * @return a new </code>{@link RestApiResponseAssert}</code>
     */
    public static RestApiResponseAssert assertThat(org.bonitasoft.web.extension.rest.RestApiResponse actual) {
        return new RestApiResponseAssert(actual);
    }

    /**
     * Verifies that the actual RestApiResponse has no additionalCookies.
     *
     * @return this assertion object.
     * @throws AssertionError if the actual RestApiResponse's additionalCookies is not empty.
     */
    public RestApiResponseAssert hasNoAdditionalCookies() {
        // check that actual RestApiResponse we want to make assertions on is not null.
        isNotNull();

        // we overrides the default error message with a more explicit one
        String errorMessage = format("\nExpected :\n  <%s>\nnot to have additionalCookies but had :\n  <%s>", actual,
                actual.getAdditionalCookies());

        // check
        if (!actual.getAdditionalCookies().isEmpty())
            throw new AssertionError(errorMessage);

        // return the current assertion for method chaining
        return this;
    }

    /**
     * Verifies that the actual RestApiResponse's httpStatus is equal to the given one.
     *
     * @param httpStatus the given httpStatus to compare the actual RestApiResponse's httpStatus to.
     * @return this assertion object.
     * @throws AssertionError - if the actual RestApiResponse's httpStatus is not equal to the given one.
     */
    public RestApiResponseAssert hasHttpStatus(int httpStatus) {
        // check that actual RestApiResponse we want to make assertions on is not null.
        isNotNull();

        // we overrides the default error message with a more explicit one
        String errorMessage = format("\nExpected <%s> httpStatus to be:\n  <%s>\n but was:\n  <%s>", actual, httpStatus,
                actual.getHttpStatus());

        // check
        if (actual.getHttpStatus() != httpStatus) {
            throw new AssertionError(errorMessage);
        }

        // return the current assertion for method chaining
        return this;
    }

    /**
     * Verifies that the actual RestApiResponse's response is equal to the given one.
     *
     * @param response the given response to compare the actual RestApiResponse's response to.
     * @return this assertion object.
     * @throws AssertionError - if the actual RestApiResponse's response is not equal to the given one.
     */
    public RestApiResponseAssert hasResponse(Serializable response) {
        // check that actual RestApiResponse we want to make assertions on is not null.
        isNotNull();

        // we overrides the default error message with a more explicit one
        String errorMessage = format("\nExpected <%s> response to be:\n  <%s>\n but was:\n  <%s>", actual, response,
                actual.getResponse());

        // check
        if (!actual.getResponse().equals(response)) {
            throw new AssertionError(errorMessage);
        }

        // return the current assertion for method chaining
        return this;
    }

}
