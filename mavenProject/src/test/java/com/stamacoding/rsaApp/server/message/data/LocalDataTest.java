package com.stamacoding.rsaApp.server.message.data;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.stamacoding.rsaApp.server.exceptions.InvalidValueException;
import com.stamacoding.rsaApp.server.exceptions.NullPointerException;

class LocalDataTest {

	@DisplayName("Test the local data's constructor")
	@Nested
	class ConstructorTest{
		
		@DisplayName("Test with valid arguments")
		@Test
		void testWithValidArguments() {
			assertDoesNotThrow(() -> {
				LocalData l = new LocalData(23, SendState.PENDING);
				assertEquals(SendState.PENDING, l.getSendState());
				assertEquals(23, l.getId());
			});
			assertDoesNotThrow(() -> {
				LocalData l = new LocalData(11, SendState.SENT);
				assertEquals(SendState.SENT, l.getSendState());
				assertEquals(11, l.getId());
			});
			assertDoesNotThrow(() -> {
				LocalData l = new LocalData(-1, SendState.PENDING);
				assertEquals(SendState.PENDING, l.getSendState());
				assertEquals(-1, l.getId());
			});
		}
		
		@DisplayName("Test with sendstate set to null")
		@Test
		void testNullSendState() {
			assertThrows(NullPointerException.class, () -> {new LocalData(23, null);});
		}
		
		@DisplayName("Test with an invalid id")
		@Test
		void testInvalidId() {
			assertThrows(InvalidValueException.class, () -> {new LocalData(-2, SendState.PENDING);});
			assertThrows(InvalidValueException.class, () -> {new LocalData(-12, SendState.PENDING);});
			assertThrows(InvalidValueException.class, () -> {new LocalData(-112, SendState.PENDING);});
		}
	}
	
	@DisplayName("Test sendState's setter")
	@Nested
	class SendStateSetterTest{
		
		@DisplayName("Test with valid arguments")
		@Test
		void testWithValidArguments() {
			LocalData d = new LocalData(2, SendState.PENDING);
			assertDoesNotThrow(() -> { 
				d.setSendState(SendState.PENDING); 
				assertEquals(SendState.PENDING, d.getSendState());
			});
			assertDoesNotThrow(() -> { 
				d.setSendState(SendState.SENT); 
				assertEquals(SendState.SENT, d.getSendState());
			});
		}
		
		@DisplayName("Test with invalid arguments")
		@Test
		void testWithInvalidArguments() {
			LocalData d = new LocalData(2, SendState.PENDING);
			assertThrows(NullPointerException.class, () -> { d.setSendState(null); });
		}
	}
	
	@DisplayName("Test id's setter")
	@Nested
	class IdSetterTest{
		
		@DisplayName("Test with valid arguments")
		@Test
		void testWithValidArguments() {
			LocalData d = new LocalData(2, SendState.PENDING);
			assertDoesNotThrow(() -> { 
				d.setId(-1); 
				assertEquals(-1, d.getId());
			});
			assertDoesNotThrow(() -> { 
				d.setId(23); 
				assertEquals(23, d.getId());
			});
			assertDoesNotThrow(() -> { 
				d.setId(222); 
				assertEquals(222, d.getId());
			});
		}
		
		@DisplayName("Test with invalid arguments")
		@Test
		void testWithInvalidArguments() {
			LocalData d = new LocalData(2, SendState.PENDING);
			assertThrows(InvalidValueException.class, () -> { d.setId(-2); });
			assertThrows(InvalidValueException.class, () -> { d.setId(-100); });
			assertThrows(InvalidValueException.class, () -> { d.setId(-28); });
		}
		
	}
	
	@DisplayName("Test isToStore()")
	@Nested
	class testIsToStore {
		
		@DisplayName("Test with an unstored message")
		@Test
		void testNotStored() {
			LocalData d = new LocalData(-1, SendState.PENDING);
			assertTrue(d.isToStore());
		}
		
		@DisplayName("Test with an stored message")
		@Test
		void testStored() {
			LocalData d = new LocalData(23, SendState.PENDING);
			assertFalse(d.isToStore());
			
			d.setId(111);
			assertFalse(d.isToStore());
			
			d.setId(99);
			assertFalse(d.isToStore());
		}
		
	}
	
	@DisplayName("Test isToUpdate()")
	@Nested
	class testIsToUpdate {
		
		@DisplayName("Test with a message that is up-to-date")
		@Test
		void testUpToDate() {
			LocalData d = new LocalData(23, SendState.PENDING);
			
			d.setUpdateRequested(true);
			assertTrue(d.isToUpdate());
		}
		
		@DisplayName("Test with a message that is not up-to-date")
		@Test
		void testNotUpToDate() {
			LocalData d = new LocalData(23, SendState.PENDING);
			
			d.setUpdateRequested(false);
			assertFalse(d.isToUpdate());
		}
		
		@DisplayName("Test with a default message")
		@Test
		void testDefault() {
			LocalData d = new LocalData(23, SendState.PENDING);
			assertFalse(d.isToUpdate());
		}
	}
	
	@DisplayName("Test testIsToSend()")
	@Nested
	class testIsToSend{
		
		@DisplayName("Test with a message that is to send")
		@Test
		void testPending() {
			LocalData d = new LocalData(23, SendState.PENDING);
			
			assertTrue(d.isToSend());
			
			d = new LocalData(23, SendState.SENT);
			d.setSendState(SendState.PENDING);
			
			assertTrue(d.isToSend());
		}
		
		@DisplayName("Test with a message that already has been sent")
		@Test
		void testSent() {
			LocalData d = new LocalData(23, SendState.SENT);
			
			assertFalse(d.isToSend());
			
			d = new LocalData(23, SendState.PENDING);
			d.setSendState(SendState.SENT);
			
			assertFalse(d.isToSend());
		}
		
	}
	
	@DisplayName("Test equals()")
	@Nested
	class EqualityTest{
		
		@DisplayName("Test equal objects")
		@Test
		void testEqual() {
			LocalData d1 = new LocalData(3, SendState.PENDING);
			LocalData d2 = new LocalData(3, SendState.PENDING);
			
			assertTrue(d2.equals(d1));
		}
		
		@DisplayName("Test not equal objects")
		@Test
		void testNotEqual() {
			LocalData d1 = new LocalData(3, SendState.PENDING);
			LocalData d2 = new LocalData(4, SendState.PENDING);
			assertFalse(d2.equals(d1));
			
			LocalData d3 = new LocalData(3, SendState.PENDING);
			LocalData d4 = new LocalData(3, SendState.SENT);
			assertFalse(d3.equals(d4));
			
			LocalData d5 = new LocalData(3, SendState.SENT);
			LocalData d6 = new LocalData(4, SendState.PENDING);
			assertFalse(d5.equals(d6));
		}
	}
}
