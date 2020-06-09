package htmlunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class HtmlUnitTest {

	private WebClient webClient;
	private TestUtils utils;
	
	private static final String VAT_1 = "244377090";
	private static final String DESIG_1 = "Customer 1";
	private static final String PHONE_1 = "910576931";
	
	private static final String VAT_2 = "217485367";
	private static final String DESIG_2 = "Customer 2";
	private static final String PHONE_2 = "960872610";
	
	private static final String ADDRESS_1 = "Rua Augusta";
	private static final String DOOR_1 = "9";
	private static final String POSTAL_CODE_1 = "1100-048";
	private static final String LOCALITY_1 = "Lisboa";
	
	private static final String ADDRESS_2 = "Rua de São João";
	private static final String DOOR_2 = "1";
	private static final String POSTAL_CODE_2 = "4150-385";
	private static final String LOCALITY_2 = "Porto";

	@Before
	public void setUpClass() throws Exception {
		webClient = new WebClient();
		webClient.setJavaScriptTimeout(15000);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
		utils = new TestUtils(webClient);
	}

	@After
	public void tearDownClass() throws Exception {
		webClient.close();
	}

	@Test
	public void narrativeA() throws IOException {
		//Set Up
		utils.addCustomer(VAT_1, DESIG_1, PHONE_1);
		//State of the table before adding the 2 new addresses
		final HtmlTable tableBefore = utils.getCustomerAddresses(VAT_1);
		final int numRowsBefore = tableBefore==null ? 1 : tableBefore.getRowCount();
		//Adding the 2 addresses
		utils.addAddress(VAT_1, ADDRESS_1, DOOR_1, POSTAL_CODE_1, LOCALITY_1);
		utils.addAddress(VAT_1, ADDRESS_2, DOOR_2, POSTAL_CODE_2, LOCALITY_2);
		//State of the table after adding the 2 new addresses
		final HtmlTable tableAfter = utils.getCustomerAddresses(VAT_1);
		final int numRowsAfter = tableAfter.getRowCount();
		//Verify row count increased by 2
		assertEquals(2, numRowsAfter-numRowsBefore);
		//Verify that the table of addresses includes the new ones
		final HtmlTableRow fstAddedRow = tableAfter.getRow(numRowsAfter-2);
		assertEquals(ADDRESS_1, fstAddedRow.getCell(1).asText());
		assertEquals(DOOR_1, fstAddedRow.getCell(2).asText());
		assertEquals(POSTAL_CODE_1, fstAddedRow.getCell(3).asText());
		assertEquals(LOCALITY_1, fstAddedRow.getCell(4).asText());
		final HtmlTableRow sndAddedRow = tableAfter.getRow(numRowsAfter-1);
		assertEquals(ADDRESS_2, sndAddedRow.getCell(1).asText());
		assertEquals(DOOR_2, sndAddedRow.getCell(2).asText());
		assertEquals(POSTAL_CODE_2, sndAddedRow.getCell(3).asText());
		assertEquals(LOCALITY_2, sndAddedRow.getCell(4).asText());
		// Tear down
		utils.removeCustomer(VAT_1);
	}

	@Test
	public void narrativeB() throws IOException {
		int confirmedInfoCount = 0;
		//-------------before---------
		final HtmlTable tableBefore = utils.getCustomers();
		final int countBefore = tableBefore==null ? 1 : tableBefore.getRowCount();
		//------------add customers---------
		utils.addCustomer(VAT_1, DESIG_1, PHONE_1);
		utils.addCustomer(VAT_2, DESIG_2, PHONE_2);
		//-------------after---------
		final HtmlTable tableAfter = utils.getCustomers();
		int countAfter = tableAfter.getRowCount();
		for (final HtmlTableRow row : tableAfter.getRows()) {
			if (row.getCell(2).asText().equals(VAT_1)) {
				assertEquals(DESIG_1, row.getCell(0).asText());
				assertEquals(PHONE_1, row.getCell(1).asText());
				assertEquals(VAT_1, row.getCell(2).asText());
				confirmedInfoCount++;
			} else if (row.getCell(2).asText().equals(VAT_2)) {
				assertEquals(DESIG_2, row.getCell(0).asText());
				assertEquals(PHONE_2, row.getCell(1).asText());
				assertEquals(VAT_2, row.getCell(2).asText());
				confirmedInfoCount++;
			}
		}
		assertEquals(2, confirmedInfoCount);
		assertTrue((countAfter - countBefore) == confirmedInfoCount);
		// Tear down
		utils.removeCustomer(VAT_1);
		utils.removeCustomer(VAT_2);
	}

	@Test
	public void narrativeC() throws IOException {		
		//Set Up
		utils.addCustomer(VAT_1, DESIG_1, PHONE_1);
		//Add sale
		utils.addSale(VAT_1);
		final HtmlTable tableAfter = utils.getCustomerSales(VAT_1);
		int indexLatest = tableAfter.getRows().size() - 1;
		HtmlTableRow row = tableAfter.getRow(indexLatest);
		assertEquals("O", row.getCell(3).asText());
		// Tear down
		utils.removeCustomer(VAT_1);
	}

	@Test
	public void narrativeD() throws IOException {		
		//Set Up
		utils.addCustomer(VAT_1, DESIG_1, PHONE_1);
		utils.addSale(VAT_1);
		//Close the sale		
		final HtmlTable table = utils.getCustomerSales(VAT_1);
		int indexLatest = table.getRows().size() - 1;
		HtmlTableRow row = table.getRow(indexLatest);		
		utils.closeSale(row.getCell(0).asText());
		//Assert that the sale is closed
		final HtmlTable tableAfter = utils.getCustomerSales(VAT_1);
		HtmlTableRow rowAfter = tableAfter.getRow(indexLatest);		
		assertEquals("C", rowAfter.getCell(3).asText());
		// Tear down
		utils.removeCustomer(VAT_1);
	}
	
	@Test
	public void narrativeE() throws IOException {
		// Add Customer
		HtmlPage addCustomerPage = utils.addCustomer(VAT_1, DESIG_1, PHONE_1);
		String textReportPage = addCustomerPage.asText();
		assertTrue(textReportPage.contains(DESIG_1));
		assertTrue(textReportPage.contains(PHONE_1));
		
		//Add address	
		HtmlPage addAddressPage = utils.addAddress(VAT_1, ADDRESS_1, DOOR_1, POSTAL_CODE_1, LOCALITY_1);
		HtmlTable tableAddresses = (HtmlTable) addAddressPage.getElementById("addresses");
		int indexLatestAddresses = tableAddresses.getRows().size() - 1;
		HtmlTableRow rowAddress = tableAddresses.getRow(indexLatestAddresses);
		final String addressId = rowAddress.getCell(0).asText();
		assertEquals(ADDRESS_1, rowAddress.getCell(1).asText());
		assertEquals(DOOR_1, rowAddress.getCell(2).asText());
		assertEquals(POSTAL_CODE_1, rowAddress.getCell(3).asText());
		assertEquals(LOCALITY_1, rowAddress.getCell(4).asText());
		
		// Add Sale
		HtmlPage addASalePage = utils.addSale(VAT_1);
		HtmlTable tableSales = (HtmlTable) addASalePage.getElementById("sales");
		int indexLatestSales = tableSales.getRows().size() - 1;
		HtmlTableRow row = tableSales.getRow(indexLatestSales);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");
		final String saleId = row.getCell(0).asText();
		assertEquals(formatter.format(LocalDate.now()), row.getCell(1).asText());
		assertEquals("0.0", row.getCell(2).asText());
		assertEquals("O", row.getCell(3).asText());
		assertEquals(VAT_1, row.getCell(4).asText());	
		
		//Add Sale Delivery
		HtmlPage addSaleDeliveryPage = utils.addSaleDelivery(VAT_1, saleId, addressId);		
		HtmlTable saleDeliTable = (HtmlTable) addSaleDeliveryPage.getElementById("salesDelivery");
		int indexLatestSaleDeliv = saleDeliTable.getRows().size() - 1;
		HtmlTableRow rowSaleDeli = saleDeliTable.getRow(indexLatestSaleDeliv);
		assertEquals(saleId, rowSaleDeli.getCell(1).asText());
		assertEquals(addressId, rowSaleDeli.getCell(2).asText());
		//Tear down
		utils.removeCustomer(VAT_1);
	}
	
	

}
