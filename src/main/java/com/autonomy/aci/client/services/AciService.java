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

package com.autonomy.aci.client.services;

import com.autonomy.aci.client.transport.AciServerDetails;
import com.autonomy.aci.client.transport.ActionParameter;

import java.util.Set;

/**
 * Defines methods for executing ACI actions on any ACI server and processing of the resulting response.
 */
public interface AciService {

    /**
     * Executes an ACI action and processes the response with the supplied <tt>Processor</tt>.
     * @param <T> Return type.
     * @param parameters The parameters to use with the ACI command. This <strong>should</strong> include an {@code
     *                   Action=<command>} parameter
     * @param processor  The <tt>Processor</tt> to use for converting the response stream into an object
     * @return The ACI response encoded as an object of type <tt>T</tt>
     * @throws AciServiceException If an error occurred during the communication with the ACI Server, processing the
     *                             response or if the response contained an error
     */
    <T> T executeAction(Set<? extends ActionParameter<?>> parameters, Processor<T> processor);

    /**
     * Executes an ACI action and processes the response with the supplied <tt>Processor</tt>.
     * @param <T> Return type.
     * @param serverDetails The connection details of the ACI Server to execute the action on
     * @param parameters    The parameters to use with the ACI command. This <strong>should</strong> include an {@code
     *                      Action=<command>} parameter
     * @param processor     The <tt>Processor</tt> to use for converting the response stream into an object
     * @return The ACI response encoded as an object of type <tt>T</tt>
     * @throws AciServiceException If an error occurred during the communication with
     *                             the ACI Server, processing the response or if the response contained an error
     */
    <T> T executeAction(AciServerDetails serverDetails, Set<? extends ActionParameter<?>> parameters, Processor<T> processor);

}
