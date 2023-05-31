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

package com.autonomy.aci.client.util;

import com.autonomy.aci.client.transport.EncryptionCodecException;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

public class EncryptionCodecUtilsTest {

    private static String string;

    private static byte[] bytes;

    @BeforeClass
    public static void createArguments() throws UnsupportedEncodingException {
        string = "This is a test";
        bytes = string.getBytes("UTF-8");
    }

    @Test(expected = NullPointerException.class)
    public void testToStringNullBytes() throws EncryptionCodecException {
        EncryptionCodecUtils.getInstance().toString(null, "UTF-8");
        fail("Should have thrown an exception");
    }

    @Test(expected = NullPointerException.class)
    public void testToStringNullCharsetName() throws EncryptionCodecException, UnsupportedEncodingException {
        EncryptionCodecUtils.getInstance().toString(bytes, null);
        fail("Should have thrown an exception");
    }

    @Test(expected = EncryptionCodecException.class)
    public void testToStringBadCharsetName() throws EncryptionCodecException, UnsupportedEncodingException {
        EncryptionCodecUtils.getInstance().toString(bytes, "wibble");
        fail("Should have thrown an exception");
    }

    @Test
    public void testToString() throws EncryptionCodecException, UnsupportedEncodingException {
        final String result = EncryptionCodecUtils.getInstance().toString(bytes, "UTF-8");
        assertThat("String incorrect", string, is(equalTo(result)));
    }

    @Test(expected = NullPointerException.class)
    public void testToBytesNullString() throws EncryptionCodecException {
        EncryptionCodecUtils.getInstance().toBytes(null, "UTF-8");
        fail("Should have thrown an exception");
    }

    @Test(expected = NullPointerException.class)
    public void testToBytesNullCharsetName() throws EncryptionCodecException, UnsupportedEncodingException {
        EncryptionCodecUtils.getInstance().toBytes(string, null);
        fail("Should have thrown an exception");
    }

    @Test(expected = EncryptionCodecException.class)
    public void testToBytesBadCharsetName() throws EncryptionCodecException, UnsupportedEncodingException {
        EncryptionCodecUtils.getInstance().toBytes(string, "wibble");
        fail("Should have thrown an exception");
    }

    @Test
    public void testToBytes() throws EncryptionCodecException, UnsupportedEncodingException {
        final byte[] result = EncryptionCodecUtils.getInstance().toBytes(string, "UTF-8");
        assertThat("String incorrect", bytes, is(equalTo(result)));
    }

}
