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
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class HtmlUnitTest {

	private static final String APPLICATION_URL = "http://localhost:8080/VVS_webappdemo/";

	private WebClient webClient;
	private TestUtils utils;

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
		final String vat = "244377090";
		final String desig = "José";
		final String phone = "910576931";
		utils.addCustomer(vat, desig, phone);
		//State of the table before adding the 2 new addresses
		final HtmlTable tableBefore = utils.getCustomerAddresses(vat);
		final int numRowsBefore = tableBefore==null ? 1 : tableBefore.getRowCount();
		//Adding the 2 addresses
		final String address1 = "Rua Augusta";
		final String door1 = "9";
		final String postalCode1 = "1100-048";
		final String locality1 = "Lisboa";
		utils.addAddress(vat, address1, door1, postalCode1, locality1);
		final String address2 = "Rua de São João";
		final String door2 = "1";
		final String postalCode2 = "4150-385";
		final String locality2 = "Porto";
		utils.addAddress(vat, address2, door2, postalCode2, locality2);
		//State of the table after adding the 2 new addresses
		final HtmlTable tableAfter = utils.getCustomerAddresses(vat);
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
		utils.removeCustomer(vat);
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
		final HtmlTable tableBefore = utils.getCustomers();
		final int countBefore = tableBefore==null ? 1 : tableBefore.getRowCount();
		//------------add customers---------
		utils.addCustomer(vat1, desig1, phone1);
		utils.addCustomer(vat2, desig2, phone2);
		//-------------after---------
		final HtmlTable tableAfter = utils.getCustomers();
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
		assertEquals(2, confirmedInfoCount);
		assertTrue((countAfter - countBefore) == confirmedInfoCount);
		// Tear down
		utils.removeCustomer(vat1);
		utils.removeCustomer(vat2);
	}

	@Test
	public void narrativeC() throws IOException {		
		//Set Up
		final String vat = "244377090";
		final String desig = "José";
		final String phone = "910576931";
		utils.addCustomer(vat, desig, phone);
		//Add sale
		utils.addSale(vat);
		final HtmlTable tableAfter = utils.getCustomerSales(vat);
		int indexLatest = tableAfter.getRows().size() - 1;
		HtmlTableRow row = tableAfter.getRow(indexLatest);
		assertEquals("O", row.getCell(3).asText());
		// Tear down
		utils.removeCustomer(vat);
	}

	@Test
	public void narrativeD() throws IOException {		
		//Set Up
		final String vat = "244377090";
		final String desig = "José";
		final String phone = "910576931";
		utils.addCustomer(vat, desig, phone);
		utils.addSale(vat);
		//Close the sale		
		final HtmlTable table = utils.getCustomerSales(vat);
		int indexLatest = table.getRows().size() - 1;
		HtmlTableRow row = table.getRow(indexLatest);		
		utils.closeSale(row.getCell(0).asText());
		//Assert that the sale is closed
		final HtmlTable tableAfter = utils.getCustomerSales(vat);
		HtmlTableRow rowAfter = tableAfter.getRow(indexLatest);		
		assertEquals("C", rowAfter.getCell(3).asText());
		// Tear down
		utils.removeCustomer(vat);
	}
	
	@Test
	public void narrativeE() throws IOException {
		// Add Customer
		final String vat = "229122205";
		utils.addCustomer(vat, "narrativeE", "910576931");
		final String address1 = "Rua Augusta";
		final String door1 = "9";
		final String postalCode1 = "1100-048";
		final String locality1 = "Lisboa";
		utils.addAddress(vat, address1, door1, postalCode1, locality1);
		// Add Sale
		utils.addSale(vat);
		final HtmlTable table = utils.getCustomerSales(vat);
		int indexLatest = table.getRows().size() - 1;
		HtmlTableRow row = table.getRow(indexLatest);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd"); 
		assertEquals(formatter.format(LocalDate.now()), row.getCell(1).asText());
		assertEquals("0.0", row.getCell(2).asText());
		assertEquals("O", row.getCell(3).asText());
		assertEquals(vat, row.getCell(4).asText());
		
		//Insert delivery
		HtmlPage page = webClient.getPage(APPLICATION_URL + "saleDeliveryVat.html");
		// get the page first form:
		HtmlForm findCustomerForm = page.getForms().get(0);
		// place data at form
		HtmlInput vatInput = findCustomerForm.getInputByName("vat");
		vatInput.setValueAttribute(vat);
		// submit form
		HtmlInput submit = findCustomerForm.getInputByValue("Get Customer");
		HtmlPage reportPage = submit.click();
		
		HtmlTable tableAddresses = (HtmlTable) reportPage.getElementById("addresses");
		String addressId = tableAddresses.getRow(1).getCell(0).asText();
		
		HtmlTable tableSales = (HtmlTable) reportPage.getElementById("sales");
		String saleId = tableSales.getRow(1).getCell(0).asText();
		
		HtmlForm addSaleDeliveryForm = reportPage.getForms().get(0);
		HtmlInput addressInput = addSaleDeliveryForm.getInputByName("addr_id");
		addressInput.setValueAttribute(addressId);
		HtmlInput saleInput = addSaleDeliveryForm.getInputByName("sale_id");
		saleInput.setValueAttribute(saleId);
		System.out.println(reportPage.asText());
		HtmlInput submitNext = addSaleDeliveryForm.getInputByValue("Insert");
		HtmlPage finalPage = submitNext.click();
		System.out.println(finalPage.asText());
		
		//Verify it in the sale delivery
		HtmlTable saleDeliTable = utils.getCustomerSaleDeliveries(vat);
		int lastIndexsaleDeli = saleDeliTable.getRows().size() - 1;
		HtmlTableRow rowSaleDeli = saleDeliTable.getRow(lastIndexsaleDeli);
		assertEquals(saleId, rowSaleDeli.getCell(1).asText());
		assertEquals(addressId, rowSaleDeli.getCell(2).asText());
		//Tear down
		utils.removeCustomer(vat);
	}
	
	

}
