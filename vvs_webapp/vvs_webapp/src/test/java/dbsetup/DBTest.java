package dbsetup;

import static dbsetup.DBSetupUtils.DB_PASSWORD;
import static dbsetup.DBSetupUtils.DB_URL;
import static dbsetup.DBSetupUtils.DB_USERNAME;
import static dbsetup.DBSetupUtils.DELETE_ALL;
import static dbsetup.DBSetupUtils.INSERT_CUSTOMER_ADDRESS_DATA;
import static dbsetup.DBSetupUtils.startApplicationDatabaseForTesting;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.Destination;
import com.ninja_squad.dbsetup.destination.DriverManagerDestination;
import com.ninja_squad.dbsetup.operation.Operation;

import webapp.services.ApplicationException;
import webapp.services.CustomerDTO;
import webapp.services.CustomerService;
import webapp.services.CustomersDTO;
import webapp.services.SaleDTO;
import webapp.services.SaleService;

public class DBTest {

	private static Destination dataSource;
	
    // the tracker is static because JUnit uses a separate Test instance for every test method.
    private static DbSetupTracker dbSetupTracker = new DbSetupTracker();
	
    @BeforeClass
    public static void setupClass() {
    	startApplicationDatabaseForTesting();
		dataSource = DriverManagerDestination.with(DB_URL, DB_USERNAME, DB_PASSWORD);
    }
    
	@Before
	public void setup() throws SQLException {

		Operation initDBOperations = Operations.sequenceOf(
			  DELETE_ALL
			, INSERT_CUSTOMER_ADDRESS_DATA
			);
		
		DbSetup dbSetup = new DbSetup(dataSource, initDBOperations);
		
        // Use the tracker to launch the DbSetup. This will speed-up tests 
		// that do not not change the BD. Otherwise, just use dbSetup.launch();
        dbSetupTracker.launchIfNecessary(dbSetup);
		
	}		
	
	@Test
	public void ruleTestA() throws ApplicationException {
		int vat = 503183504;
		CustomerService.INSTANCE.addCustomer(vat, "FCUL", 217500000);
		assertThrows(ApplicationException.class, () -> {
			CustomerService.INSTANCE.addCustomer(vat, "FCUL2", 217500000);
		});		
	}	
	
	/*// read-only test: unnecessary to re-launch setup after test has been run
	dbSetupTracker.skipNextLaunch();*/
	/*
int expected = CustomerService.INSTANCE.getAllCustomers().customers.size();
		int actual = CustomerService.INSTANCE.getAllCustomers().customers.size();
		assertEquals(expected, actual);
	 */
	
	@Test
	public void ruleTestB() throws ApplicationException {
		int vat = 503183504;
		int phone = 217500000;
		int newPhone = 934850281;
		assertFalse(hasClient(vat));
		CustomerService.INSTANCE.addCustomer(vat, "FCUL", phone);
		assertTrue(hasClient(vat));
		CustomerService.INSTANCE.updateCustomerPhone(vat, newPhone);
		CustomerDTO customer = CustomerService.INSTANCE.getCustomerByVat(vat);
		assertEquals(newPhone, customer.phoneNumber);	
	}
	
	@Test
	public void ruleTestC() throws ApplicationException {
		List<CustomerDTO> customers = CustomerService.INSTANCE.getAllCustomers().customers;
		for (CustomerDTO curr : customers) {
			CustomerService.INSTANCE.removeCustomer(curr.vat);
		}
		int newSize = CustomerService.INSTANCE.getAllCustomers().customers.size();
		assertEquals(0, newSize);
	}
	
	@Test
	public void ruleTestD() throws ApplicationException {
		int vat = 503183504;
		assertFalse(hasClient(vat));
		CustomerService.INSTANCE.addCustomer(vat, "FCUL", 217500000);
		assertTrue(hasClient(vat));
		CustomerService.INSTANCE.removeCustomer(vat);
		assertFalse(hasClient(vat));
		CustomerService.INSTANCE.addCustomer(vat, "FCUL", 217500000);
		assertTrue(hasClient(vat));
	}
	
	@Test
	public void ruleTestE() throws ApplicationException {
		int vat = 197672337;
		assertTrue(hasClient(vat));
		CustomerService.INSTANCE.removeCustomer(vat);
		assertFalse(hasClient(vat));
		List<SaleDTO> sales = SaleService.INSTANCE.getSaleByCustomerVat(vat).sales;
		assertEquals(0, sales.size());
	}
	
	@Test
	public void ruleTestF() throws ApplicationException {
		int vat = 197672337;
		int oldNumSales = SaleService.INSTANCE.getSaleByCustomerVat(vat).sales.size();
		SaleService.INSTANCE.addSale(vat);
		int newNumSales = SaleService.INSTANCE.getSaleByCustomerVat(vat).sales.size();
		assertEquals(newNumSales, oldNumSales+1);
	}
	
	/**
	 * It should not be possible to add a sale to an non existent customer
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void extraSaleBehaviour1() throws ApplicationException {
		int vat = 503183504;
		assertFalse(hasClient(vat));
		assertThrows(ApplicationException.class, () -> {
			SaleService.INSTANCE.addSale(vat);
		});	
	}	
	
	/**
	 * All the new sales created for a customer
	 * must have a total of 0.0
	 * status must be open
	 * must be assoaciated to the right vat
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void extraSaleBehaviour2() throws ApplicationException {
		int vat = 503183504;
		assertFalse(hasClient(vat));
		CustomerService.INSTANCE.addCustomer(vat, "FCUL", 217500000);
		assertTrue(hasClient(vat));
		SaleService.INSTANCE.addSale(vat);
		List<SaleDTO> sales = SaleService.INSTANCE.getSaleByCustomerVat(vat).sales;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		for (SaleDTO curr : sales) {
			assertEquals(dateFormat.format(new Date()), curr.data.toString());
			assertEquals(new Double(0.0), curr.total);
			assertEquals("O", curr.statusId);
			assertEquals(vat, curr.customerVat);
		}		
	}


	
	private boolean hasClient(int vat) throws ApplicationException {	
		CustomersDTO customersDTO = CustomerService.INSTANCE.getAllCustomers();
		
		for(CustomerDTO customer : customersDTO.customers)
			if (customer.vat == vat)
				return true;			
		return false;
	}	

		
}
