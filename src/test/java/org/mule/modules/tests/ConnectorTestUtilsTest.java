/**
 * (c) 2003-2013 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.tests;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mule.transport.NullPayload;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Mulesoft, Inc
 */
public class ConnectorTestUtilsTest {

    private static final NullPayload NULL_PAYLOAD = NullPayload.getInstance();

    @Rule
    public ExpectedException thrownException = ExpectedException.none();

    @Test
    public void testAssertNullPayload() {
        assertTrue(ConnectorTestUtils.assertNullPayload(NULL_PAYLOAD));
        assertTrue(ConnectorTestUtils.assertNullPayload("{NullPayload}"));
        assertFalse(ConnectorTestUtils.assertNullPayload("NullPayload"));

        thrownException.expect(NullPointerException.class);
        assertFalse(ConnectorTestUtils.assertNullPayload(null));
    }
}
