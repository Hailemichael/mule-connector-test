/**
 * (c) 2003-2013 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.tests;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.InputStream;
import java.util.GregorianCalendar;

/**
 * Util class for connector tests.
 * @see org.apache.commons.lang.RandomStringUtils
 * @see org.apache.commons.lang.math.RandomUtils
 *
 * @author Mulesoft, Inc
 */
public class AutomationTestUtils {

    protected static DatatypeFactory datatypeFactory;

    private AutomationTestUtils() {
        // Prevent instantiation since all methods are static
    }

    // Utility method to avoid throwing Exception everywhere
    protected static DatatypeFactory getDataTypeFactory() {
        if (datatypeFactory == null) {
            try {
                datatypeFactory = DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException e) {
            }
        }
        return datatypeFactory;
    }

    /**
     * @return A random email address matching the following regex:
     * {@code qaTest[0-9a-zA-Z]{10}@example.com}
     */
    public static String randomEmailAddress() {
        return String.format("qaTest%s@example.com", RandomStringUtils.randomAlphanumeric(10));
    }

    /**
     * @param n the amount of characters to include in the InputStream
     * @return An InputStream containing a random string of arbitrary length.
     * @see org.apache.commons.lang.RandomStringUtils#randomAlphanumeric
     */
    public static InputStream randomInputStream(int n) {
        return IOUtils.toInputStream(RandomStringUtils.randomAlphanumeric(n));
    }

    /**
     * Returns an XMLGregorianCalendar for a datetime relative to today.
     *
     * @param unit   the unit (DATE, HOUR, etc.) to add to today's date. Values must
     *               be taken from GregorianCalendar's constants:
     * @param amount how many units to add (or subtract, if negative) to today's date.
     * @return An XMLGregorianCalendar instance for today plus {@code unit * amount} (signed).
     * @see java.util.GregorianCalendar#SECOND
     * @see java.util.GregorianCalendar#MINUTE
     * @see java.util.GregorianCalendar#HOUR
     * @see java.util.GregorianCalendar#DATE
     * @see java.util.GregorianCalendar#MONTH
     * @see java.util.GregorianCalendar#YEAR
     */
    public static XMLGregorianCalendar xmlGregorianCalendarPlus(int unit, int amount) {
        GregorianCalendar date = new GregorianCalendar();
        date.add(unit, amount);
        return getDataTypeFactory().newXMLGregorianCalendar(date);
    }

    /**
     * @param days number of days to add (signed)
     * @return An XMLGregorianCalendar instance today's datetime, plus a signed number of days.
     */
    public static XMLGregorianCalendar xmlGregorianCalendarPlusDays(int days) {
        return xmlGregorianCalendarPlus(GregorianCalendar.DATE, days);
    }

    /**
     * @param hours number of hours to add (signed)
     * @return An XMLGregorianCalendar instance today's datetime, plus a signed number of hours.
     */
    public static XMLGregorianCalendar xmlGregorianCalendarPlusHours(int hours) {
        return xmlGregorianCalendarPlus(GregorianCalendar.HOUR, hours);
    }

    /**
     * @param minutes number of minutes to add (signed)
     * @return An XMLGregorianCalendar instance today's datetime, plus a signed number of minutes.
     */
    public static XMLGregorianCalendar xmlGregorianCalendarPlusMinutes(int minutes) {
        return xmlGregorianCalendarPlus(GregorianCalendar.MINUTE, minutes);
    }

}
