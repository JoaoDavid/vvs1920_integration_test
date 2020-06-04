package vvs_webapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class HtmlUnitTest {

	private static final String APPLICATION_URL = "http://localhost:8080/VVS_webappdemo/";
	private static final int APPLICATION_NUMBER_USE_CASES = 11;

	private static HtmlPage page;
	private static TestUtil util;

	@BeforeClass
	public static void setUpClass() throws Exception {
		try (final WebClient webClient = new WebClient(BrowserVersion.getDefault())) { 

			// possible configurations needed to prevent JUnit tests to fail for complex HTML pages
			webClient.setJavaScriptTimeout(15000);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webClient.getOptions().setCssEnabled(false);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setThrowExceptionOnScriptError(false);

			page = webClient.getPage(APPLICATION_URL);
			util = new TestUtil(page);

			assertEquals(200, page.getWebResponse().getStatusCode()); // OK status
		}
	}

	@Test
	public void myTest() throws Exception {
		final String vat = "244377090";
		util.addCustomer(vat, "batata assada", "918576089");
		util.addAddress(vat, "rua da batata", "9", "4242-225", "lisboa");
		/*final HtmlTable tableAfter = util.getCustomerAddresses(vat);
		final int numRowsAfter = tableAfter.getRowCount();
		//Verify row count increased by 2
		//Verify that the table of addresses includes the new ones
		for (final HtmlTableRow row : tableAfter.getRows()) {
			System.out.println(row.asText());
		}*/
		util.addSale(vat);
		//util.removeCustomer(vat);
	}

	//@Test
	public void narrativeA() throws IOException {
		//Set Up
		final String vat = "244377090";
		final String desig = "José";
		final String phone = "910576931";
		util.addCustomer(vat, desig, phone);
		int confirmedInfoCount = 0;
		//State of the table before adding the 2 new addresses
		final HtmlTable tableBefore = util.getCustomerAddresses(vat);
		final int numRowsBefore = tableBefore.getRowCount();
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
		for (final HtmlTableRow row : tableAfter.getRows()) {
			if (row.getCell(0).asText().equals(address1) 
					&& row.getCell(1).asText().equals(door1)
					&& row.getCell(2).asText().equals(postalCode1)
					&& row.getCell(3).asText().equals(locality1)) {
				confirmedInfoCount++;
			} else if (row.getCell(0).asText().equals(address2) 
					&& row.getCell(1).asText().equals(door2)
					&& row.getCell(2).asText().equals(postalCode2)
					&& row.getCell(3).asText().equals(locality2)) {
				confirmedInfoCount++;
			}
		}
		assertEquals(2, confirmedInfoCount);
		// Tear down
		util.removeCustomer(vat);
	}

	//@Test
	public void narrativeB() throws IOException {
		HtmlAnchor getCustomersLink = page.getAnchorByHref("GetAllCustomersPageController");
		String vat1 = "244377090";
		String desig1 = "José";
		String phone1 = "910576931";
		String vat2 = "217485367";
		String desig2 = "Mário";
		String phone2 = "960872610";
		int confirmedInfoCount = 0;
		//-------------before---------
		HtmlPage nextPageBefore = (HtmlPage) getCustomersLink.openLinkInNewWindow();		
		final HtmlTable tableBefore = nextPageBefore.getHtmlElementById("clients");
		int countBefore = tableBefore.getRowCount();
		//------------add customers---------
		util.addCustomer(vat1, desig1, phone1);
		util.addCustomer(vat2, desig2, phone2);
		//-------------after---------
		HtmlPage nextPageAfter = (HtmlPage) getCustomersLink.openLinkInNewWindow();		
		final HtmlTable tableAfter = nextPageAfter.getHtmlElementById("clients");
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



}





