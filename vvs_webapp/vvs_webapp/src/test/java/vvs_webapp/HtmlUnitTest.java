package vvs_webapp;

import static org.junit.Assert.*;
import org.junit.*;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

import java.net.MalformedURLException;

import java.io.*;
import java.util.*;

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

	//@Test
	public void myTest() throws Exception {
		final String vat = "244377090";
		util.addCustomer(vat, "batata assada", "918576089");
		util.addAddress(vat, "rua da batata", "9", "4242-225", "lisboa");
		util.removeCustomer(vat);
	}

	@Test
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
		int countBefore = tableBefore.getRows().size();
		//------------add customers---------
		util.addCustomer(vat1, desig1, phone1);
		util.addCustomer(vat2, desig2, phone2);
		//-------------after---------
		HtmlPage nextPageAfter = (HtmlPage) getCustomersLink.openLinkInNewWindow();		
		final HtmlTable tableAfter = nextPageAfter.getHtmlElementById("clients");
		int countAfter = tableAfter.getRows().size();
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
		//TODO tearUp/Down
		util.removeCustomer(vat1);
		util.removeCustomer(vat2);
	}



}





