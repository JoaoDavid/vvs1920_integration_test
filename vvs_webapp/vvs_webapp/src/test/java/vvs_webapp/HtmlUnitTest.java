package vvs_webapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class HtmlUnitTest {

	private static final String APPLICATION_URL = "http://localhost:8080/VVS_webappdemo/";

	private WebClient webClient;
	private TestUtil util;

	@Before
	public void setUpClass() throws Exception {
		webClient = new WebClient();
		util = new TestUtil(webClient);
	}

	@After
	public void tearDownClass() throws Exception {
		webClient.close();
	}

	//@Test
	public void myTest() throws Exception {
		final String vat = "244377090";
		util.addCustomer(vat, "batata assada", "918576089");
		util.addAddress(vat, "rua da batata", "9", "4242-225", "lisboa");
		HtmlTable tableAfter = util.getCustomerAddresses(vat);
		for (final HtmlTableRow row : tableAfter.getRows()) {
			System.out.println(row.asText());
		}
		util.addSale(vat);
		//util.removeCustomer(vat);
	}

	@Test
	public void narrativeA() throws IOException {
		//Set Up
		final String vat = "244377090";
		final String desig = "José";
		final String phone = "910576931";
		util.addCustomer(vat, desig, phone);
		//State of the table before adding the 2 new addresses
		final HtmlTable tableBefore = util.getCustomerAddresses(vat);
		final int numRowsBefore = tableBefore==null ? 1 : tableBefore.getRowCount();
		//Adding the 2 addresses
		final String address1 = "Rua Augusta";
		final String door1 = "9";
		final String postalCode1 = "1100-048";
		final String locality1 = "Lisboa";
		util.addAddress(vat, address1, door1, postalCode1, locality1);
		final String address2 = "Rua de São João";
		final String door2 = "1";
		final String postalCode2 = "4150-385";
		final String locality2 = "Porto";
		util.addAddress(vat, address2, door2, postalCode2, locality2);
		//State of the table after adding the 2 new addresses
		final HtmlTable tableAfter = util.getCustomerAddresses(vat);
		final int numRowsAfter = tableAfter.getRowCount();
		//Verify row count increased by 2
		assertEquals(2, numRowsAfter-numRowsBefore);
		//Verify that the table of addresses includes the new ones
		final HtmlTableRow fstAddedRow = tableAfter.getRow(numRowsAfter-2);
		assertEquals(address1, fstAddedRow.getCell(0).asText());
		assertEquals(door1, fstAddedRow.getCell(1).asText());
		assertEquals(postalCode1, fstAddedRow.getCell(2).asText());
		assertEquals(locality1, fstAddedRow.getCell(3).asText());
		final HtmlTableRow sndAddedRow = tableAfter.getRow(numRowsAfter-1);
		assertEquals(address2, sndAddedRow.getCell(0).asText());
		assertEquals(door2, sndAddedRow.getCell(1).asText());
		assertEquals(postalCode2, sndAddedRow.getCell(2).asText());
		assertEquals(locality2, sndAddedRow.getCell(3).asText());
		// Tear down
		util.removeCustomer(vat);
	}

	@Test
	public void narrativeB() throws IOException {
		String vat1 = "244377090";
		String desig1 = "José";
		String phone1 = "910576931";
		String vat2 = "217485367";
		String desig2 = "Mário";
		String phone2 = "960872610";
		int confirmedInfoCount = 0;
		//-------------before---------
		final HtmlTable tableBefore = util.getCustomers();
		final int countBefore = tableBefore==null ? 1 : tableBefore.getRowCount();
		//------------add customers---------
		util.addCustomer(vat1, desig1, phone1);
		util.addCustomer(vat2, desig2, phone2);
		//-------------after---------
		final HtmlTable tableAfter = util.getCustomers();
		int countAfter = tableAfter.getRowCount();
		for (final HtmlTableRow row : tableAfter.getRows()) {
			if (row.getCell(2).asText().equals(vat1)) {
				assertEquals(desig1, row.getCell(0).asText());
				assertEquals(phone1, row.getCell(1).asText());
				assertEquals(vat1, row.getCell(2).asText());
				confirmedInfoCount++;
			} else if (row.getCell(2).asText().equals(vat2)) {
				assertEquals(desig2, row.getCell(0).asText());
				assertEquals(phone2, row.getCell(1).asText());
				assertEquals(vat2, row.getCell(2).asText());
				confirmedInfoCount++;
			}
		}
		System.out.println(countBefore + ":" + countAfter);
		assertEquals(2, confirmedInfoCount);
		assertTrue((countAfter - countBefore) == confirmedInfoCount);
		// Tear down
		util.removeCustomer(vat1);
		util.removeCustomer(vat2);
	}

	@Test
	public void narrativeC() throws IOException {		
		//Set Up
		final String vat = "244377090";
		final String desig = "José";
		final String phone = "910576931";
		util.addCustomer(vat, desig, phone);
		//Add sale
		util.addSale(vat);
		final HtmlTable tableAfter = util.getCustomerSales(vat);
		int indexLatest = tableAfter.getRows().size() - 1;
		HtmlTableRow row = tableAfter.getRows().get(indexLatest);
		assertEquals("O", row.getCell(3).asText());
		// Tear down
		util.removeCustomer(vat);
	}



}





