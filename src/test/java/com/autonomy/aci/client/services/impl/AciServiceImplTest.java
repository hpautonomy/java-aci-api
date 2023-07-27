/*
 * Copyright 2006-2018 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.autonomy.aci.client.services.impl;

import com.autonomy.aci.client.services.AciConstants;
import com.autonomy.aci.client.services.AciServiceException;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.services.ProcessorException;
import com.autonomy.aci.client.transport.AciHttpClient;
import com.autonomy.aci.client.transport.AciHttpException;
import com.autonomy.aci.client.transport.AciParameter;
import com.autonomy.aci.client.transport.AciResponseInputStream;
import com.autonomy.aci.client.transport.AciServerDetails;
import com.autonomy.aci.client.util.ActionParameters;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedHashSet;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JUnit test class for the <tt>com.autonomy.aci.client.services.impl.AciServiceImpl</tt> class.
 */
public class AciServiceImplTest {

    /**
     * <tt>AciServerDetails</tt> instance to use...
     */
    private final AciServerDetails details = new AciServerDetails("localhost", 9000);

    @Test
    public void testDefaultConstructor() {
        // Create a new service implementation...
        final AciServiceImpl service = new AciServiceImpl();

        // Check...
        assertThat("AciHttpClient is not null.", service.getAciHttpClient(), is(nullValue()));
        assertThat("AciServerDetails is not null.", service.getAciServerDetails(), is(nullValue()));
    }

    @Test
    public void testSinglePropertyConstructor() {
        // Create a new service implementation...
        final AciHttpClient mockAciHttpClient = mock(AciHttpClient.class);
        final AciServiceImpl service = new AciServiceImpl(mockAciHttpClient);

        // Check...
        assertThat(service.getAciHttpClient(), is(sameInstance(mockAciHttpClient)));
        assertThat(service.getAciServerDetails(), is(nullValue()));
    }

    @Test
    public void testDoublePropertyConstructor() {
        // Create a new service implementation...
        final AciHttpClient mockAciHttpClient = mock(AciHttpClient.class);
        final AciServiceImpl service = new AciServiceImpl(mockAciHttpClient, details);

        // Check...
        assertThat(service.getAciHttpClient(), is(sameInstance(mockAciHttpClient)));
        assertThat(service.getAciServerDetails(), is(sameInstance(details)));
    }

    @Test(expected = NullPointerException.class)
    public void testExecuteActionNullAciHttpClient() throws AciServiceException {
        new AciServiceImpl().executeAction(new ActionParameters("test"), null);
        fail("Should have thrown an NullPointerException.");
    }

    @Test(expected = NullPointerException.class)
    public void testExecuteActionNullConnectionDetails() throws AciServiceException {
        new AciServiceImpl(mock(AciHttpClient.class)).executeAction(new ActionParameters("test"), null);
        fail("Should have thrown an NullPointerException.");
    }

    @Test(expected = NullPointerException.class)
    public void testExecuteActionNullParameterSetNullProcessor() throws AciServiceException {
        new AciServiceImpl(mock(AciHttpClient.class), details).executeAction(null, null);
        fail("Should have thrown an NullPointerException.");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteActionEmptyParameterSetNullProcessor() throws AciServiceException {
        new AciServiceImpl(mock(AciHttpClient.class), details).executeAction(new LinkedHashSet<>(), null);
        fail("Should have thrown an IllegalArgumentException.");
    }

    @Test(expected = NullPointerException.class)
    public void testExecuteActionNullProcessor() throws AciServiceException {
        new AciServiceImpl(mock(AciHttpClient.class), details).executeAction(new ActionParameters("test"), null);
        fail("Should have thrown an NullPointerException.");
    }

    @Test(expected = NullPointerException.class)
    public void testExecuteActionWithDetailsNullServerDetails() throws AciServiceException {
        new AciServiceImpl(mock(AciHttpClient.class)).executeAction(null, null, null);
        fail("Should have thrown an NullPointerException.");
    }

    @Test(expected = NullPointerException.class)
    public void testExecuteActionWithDetailsNullParameterSetNullProcessor() throws AciServiceException {
        new AciServiceImpl(mock(AciHttpClient.class)).executeAction(details, null, null);
        fail("Should have thrown an NullPointerException.");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteActionWithDetailsEmptyParameterSetNullProcessor() throws AciServiceException {
        new AciServiceImpl(mock(AciHttpClient.class)).executeAction(details, new LinkedHashSet<>(), null);
        fail("Should have thrown an IllegalArgumentException.");
    }

    @Test(expected = NullPointerException.class)
    public void testExecuteActionWithDetailsBadParameterSetNullProcessor() throws AciServiceException {
        final ActionParameters parameters = new ActionParameters();
        parameters.add(AciConstants.PARAM_FORMAT, "wibble");
        parameters.add(AciConstants.PARAM_DATA, "wobble");
        parameters.add(AciConstants.PARAM_ACTION, AciConstants.ACTION_GET_LICENSE_INFO);

        new AciServiceImpl(mock(AciHttpClient.class)).executeAction(details, parameters, null);
        fail("Should have thrown an NullPointerException.");
    }

    @Test(expected = NullPointerException.class)
    public void testExecuteActionWithDetailsNullProcessor() throws AciServiceException {
        new AciServiceImpl(mock(AciHttpClient.class)).executeAction(details, new ActionParameters("test"), null);
        fail("Should have thrown an NullPointerException.");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteActionAciHttpException() throws AciServiceException, IOException, AciHttpException {
        final AciHttpClient mockAciHttpClient = mock(AciHttpClient.class);
        when(mockAciHttpClient.executeAction(any(AciServerDetails.class), anySetOf(AciParameter.class))).thenThrow(AciHttpException.class);

        try {
            new AciServiceImpl(mockAciHttpClient, details).executeAction(details, new ActionParameters("test"), mock(Processor.class));
            fail("Should have thrown an AciServiceException.");
        } catch (final AciServiceException ase) {
            assertThat("Exception cause is wrong", ase.getCause(), is(instanceOf(AciHttpException.class)));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteActionIOException() throws AciServiceException, IOException, AciHttpException {
        final AciHttpClient mockAciHttpClient = mock(AciHttpClient.class);
        when(mockAciHttpClient.executeAction(any(AciServerDetails.class), anySetOf(AciParameter.class))).thenThrow(IOException.class);

        try {
            new AciServiceImpl(mockAciHttpClient, details).executeAction(details, new ActionParameters("test"), mock(Processor.class));
            fail("Should have thrown an AciServiceException.");
        } catch (final AciServiceException ase) {
            assertThat("Exception cause is wrong", ase.getCause(), is(instanceOf(IOException.class)));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteActionProcessorException() throws AciServiceException, IOException, AciHttpException {
        final AciHttpClient mockAciHttpClient = mock(AciHttpClient.class);
        final AciResponseInputStream mockAciResponseInputStream = mock(AciResponseInputStream.class);
        when(mockAciHttpClient.executeAction(any(AciServerDetails.class), anySetOf(AciParameter.class))).thenReturn(mockAciResponseInputStream);

        final Processor<?> mockProcessor = mock(Processor.class);
        when(mockProcessor.process(any(AciResponseInputStream.class))).thenThrow(ProcessorException.class);

        try {
            new AciServiceImpl(mockAciHttpClient, details).executeAction(details, new ActionParameters("test"), mockProcessor);
            fail("Should have thrown an AciServiceException.");
        } catch (final AciServiceException ase) {
            assertThat("Exception cause is wrong", ase.getCause(), is(instanceOf(ProcessorException.class)));
            verify(mockAciResponseInputStream).close();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteAction() throws AciServiceException, IOException, AciHttpException {
        final AciHttpClient mockAciHttpClient = mock(AciHttpClient.class);
        final AciResponseInputStream mockAciResponseInputStream = mock(AciResponseInputStream.class);
        when(mockAciHttpClient.executeAction(any(AciServerDetails.class), anySetOf(AciParameter.class))).thenReturn(mockAciResponseInputStream);

        final Processor<String> mockProcessor = mock(Processor.class);
        when(mockProcessor.process(mockAciResponseInputStream)).thenReturn("Success!");

        final String result = new AciServiceImpl(mockAciHttpClient)
                .executeAction(details, new ActionParameters("test"), (Processor<String>) mockProcessor);

        assertThat(result, is(equalTo("Success!")));
        verify(mockAciResponseInputStream).close();
    }

    @Test
    public void testAciHttpClientProperty() {
        // Create a new service implementation...
        final AciServiceImpl service = new AciServiceImpl();
        assertThat(service.getAciHttpClient(), is(nullValue()));

        // Set and check...
        final AciHttpClient mockAciHttpClient = mock(AciHttpClient.class);
        service.setAciHttpClient(mockAciHttpClient);
        assertThat(service.getAciHttpClient(), is(sameInstance(mockAciHttpClient)));

        // Set to null and check...
        service.setAciHttpClient(null);
        assertThat(service.getAciHttpClient(), is(nullValue()));
    }

    @Test
    public void testAciServerDetailsProperty() {
        // Create a new service implementation...
        final AciServiceImpl service = new AciServiceImpl();
        assertThat(service.getAciServerDetails(), is(nullValue()));

        // Set and check...
        service.setAciServerDetails(details);
        assertThat(service.getAciServerDetails(), is(sameInstance(details)));

        // Set to null and check...
        service.setAciServerDetails(null);
        assertThat(service.getAciServerDetails(), is(nullValue()));
    }

}
