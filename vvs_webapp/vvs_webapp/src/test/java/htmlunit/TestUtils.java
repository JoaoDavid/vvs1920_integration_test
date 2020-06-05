package htmlunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

public class TestUtils {

	private static final String APPLICATION_URL = "http://localhost:8080/VVS_webappdemo/";

	private WebClient webClient;	

	public TestUtils(WebClient webClient) {
		this.webClient = webClient;
	}

	public void addCustomer(String vat, String desig, String phone) throws IOException {
		HtmlPage page = webClient.getPage(APPLICATION_URL + "addCustomer.html");
		// check if title is the one expected
		assertEquals("Enter Name", page.getTitleText());

		// get the page first form:
		HtmlForm addCustomerForm = page.getForms().get(0);

		// place data at form
		HtmlInput vatInput = addCustomerForm.getInputByName("vat");
		vatInput.setValueAttribute(vat);
		HtmlInput designationInput = addCustomerForm.getInputByName("designation");
		designationInput.setValueAttribute(desig);
		HtmlInput phoneInput = addCustomerForm.getInputByName("phone");
		phoneInput.setValueAttribute(phone);
		// submit form
		HtmlInput submit = addCustomerForm.getInputByName("submit");

		// check if report page includes the proper values
		HtmlPage reportPage = submit.click();
		String textReportPage = reportPage.asText();
		assertTrue(textReportPage.contains(desig));
		assertTrue(textReportPage.contains(phone));

	}

	public void addAddress(String vat, String address, String door, String postalCode, String locality) throws IOException {
		HtmlPage nextPage = webClient.getPage(APPLICATION_URL + "addAddressToCustomer.html");
		// check if title is the one expected
		assertEquals("Enter Address", nextPage.getTitleText());

		// get the page first form:
		HtmlForm addAddressForm = nextPage.getForms().get(0);

		// place data at form
		HtmlInput vatInput = addAddressForm.getInputByName("vat");
		vatInput.setValueAttribute(vat);
		HtmlInput addressInput = addAddressForm.getInputByName("address");
		addressInput.setValueAttribute(address);
		HtmlInput doorInput = addAddressForm.getInputByName("door");
		doorInput.setValueAttribute(door);
		HtmlInput postalCodeInput = addAddressForm.getInputByName("postalCode");
		postalCodeInput.setValueAttribute(postalCode);
		HtmlInput localityInput = addAddressForm.getInputByName("locality");
		localityInput.setValueAttribute(locality);
		// submit form
		HtmlInput submit = addAddressForm.getInputByValue("Insert");

		// check if report page includes the proper values
		submit.click();
	}

	public void removeCustomer(String vat) throws IOException {
		HtmlPage nextPage = webClient.getPage(APPLICATION_URL + "RemoveCustomerPageController");
		assertTrue(nextPage.asText().contains(vat));

		HtmlForm removeCustomerForm = nextPage.getForms().get(0);
		HtmlInput vatInput = removeCustomerForm.getInputByName("vat");
		vatInput.setValueAttribute(vat);
		HtmlInput submit = removeCustomerForm.getInputByName("submit");
		submit.click();
	}

	public HtmlTable getCustomerAddresses(String vat) throws IOException {
		HtmlPage nextPage = webClient.getPage(APPLICATION_URL + "getCustomerByVAT.html");
		// get the page first form:
		HtmlForm findCustomerForm = nextPage.getForms().get(0);

		// place data at form
		HtmlInput vatInput = findCustomerForm.getInputByName("vat");
		vatInput.setValueAttribute(vat);

		// submit form
		HtmlInput submit = findCustomerForm.getInputByName("submit");
		HtmlPage reportPage = submit.click();
		HtmlTable table = (HtmlTable) reportPage.getElementById("addresses");
		return table;
	}

	public HtmlTable getCustomers() throws IOException {
		HtmlPage page = webClient.getPage(APPLICATION_URL + "GetAllCustomersPageController");	
		final HtmlTable table = page.getHtmlElementById("clients");
		return table;
	}

	public void addSale(String vat) throws IOException {
		HtmlPage nextPage = webClient.getPage(APPLICATION_URL + "addSale.html");
		// check if title is the one expected
		assertEquals("New Sale", nextPage.getTitleText());

		// get the page first form:
		HtmlForm addSaleForm = nextPage.getForms().get(0);

		// place data at form
		HtmlInput vatInput = addSaleForm.getInputByName("customerVat");
		vatInput.setValueAttribute(vat);

		// submit form
		HtmlInput submit = addSaleForm.getInputByValue("Add Sale");

		// check if report page includes the proper values
		submit.click();
	}
	
	public void closeSale(String id) throws IOException {
		HtmlPage nextPage = webClient.getPage(APPLICATION_URL + "UpdateSaleStatusPageControler");

		// get the page first form:
		HtmlForm closeSaleForm = nextPage.getForms().get(0);

		// place data at form
		HtmlInput idInput = closeSaleForm.getInputByName("id");
		idInput.setValueAttribute(id);

		// submit form
		HtmlInput submit = closeSaleForm.getInputByValue("Close Sale");

		// check if report page includes the proper values
		submit.click();
	}

	public HtmlTable getCustomerSales(String vat) throws IOException {
		HtmlPage page = webClient.getPage(APPLICATION_URL + "getSales.html");
		// get the page first form:
		HtmlForm findCustomerForm = page.getForms().get(0);

		// place data at form
		HtmlInput vatInput = findCustomerForm.getInputByName("customerVat");
		vatInput.setValueAttribute(vat);

		// submit form
		HtmlInput submit = findCustomerForm.getInputByValue("Get Sales");
		HtmlPage reportPage = submit.click();
		HtmlTable table = (HtmlTable) reportPage.getElementById("sales");
		return table;
	}
	
	
	public HtmlTable getCustomerSaleDeliveries(String vat) throws IOException {
		HtmlPage nextPage = webClient.getPage(APPLICATION_URL + "showDelivery.html");
		// get the page first form:
		HtmlForm findCustomerForm = nextPage.getForms().get(0);

		// place data at form
		HtmlInput vatInput = findCustomerForm.getInputByName("vat");
		vatInput.setValueAttribute(vat);

		// submit form
		HtmlInput submit = findCustomerForm.getInputByValue("Get Customer");
		HtmlPage reportPage = submit.click();
		HtmlTable table = (HtmlTable) reportPage.getElementById("salesDelivery");
		return table;
	}


}
