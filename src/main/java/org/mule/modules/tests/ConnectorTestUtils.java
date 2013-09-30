/**
 * (c) 2003-2013 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master
 * Subscription Agreement (or other Terms of Service) separately entered
 * into between you and MuleSoft. If such an agreement is not in
 * place, you may not use the software.
 */

package org.mule.modules.tests;

import java.util.GregorianCalendar;
import java.util.UUID;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.mule.transport.NullPayload;

/**
 * Util class for connector tests
 *
 * @author Mulesoft, Inc
 */
public class ConnectorTestUtils {

    public static Boolean assertNullPayload(Object actual) {
        return (actual instanceof NullPayload || actual.equals("{NullPayload}"));
    }
    
    public static String getStackTrace(Exception e) {
		return org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e);
	}
    
    public static String generateRandomEmailAddress() {
    	return String.format("%s@testaddress.com", UUID.randomUUID().toString().substring(0, 11));  	
    }
    
    public static String generateRandomShortString() {
    	return UUID.randomUUID().toString().substring(0, 11);  	
    }
    
    public static XMLGregorianCalendar generateXMLGregorianCalendarDateForYesterday() throws Exception {
    	GregorianCalendar date = new GregorianCalendar();
    	date.add(GregorianCalendar.DATE, -1);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(date);
    }
    
    public static XMLGregorianCalendar generateXMLGregorianCalendarDateForDaysBeforeCurrent(int days) throws Exception {
    	GregorianCalendar date = new GregorianCalendar();
    	date.add(GregorianCalendar.DATE, -1*days);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(date);
    }
    
    public static XMLGregorianCalendar generateXMLGregorianCalendarDateForHoursBeforeCurrent(int hours) throws Exception {
    	GregorianCalendar date = new GregorianCalendar();
    	date.add(GregorianCalendar.HOUR, -1*hours);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(date);
    }
    
    public static XMLGregorianCalendar generateXMLGregorianCalendarDateForMinutesBeforeCurrent(int minutes) throws Exception {
    	GregorianCalendar date = new GregorianCalendar();
    	date.add(GregorianCalendar.MINUTE, -1*minutes);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(date);
    }
    
    // algo que ayude con el nesting de objetos en el payload
    
    // que de List<Objeto> me extraiga una List<attributoDeObjeto>
}
