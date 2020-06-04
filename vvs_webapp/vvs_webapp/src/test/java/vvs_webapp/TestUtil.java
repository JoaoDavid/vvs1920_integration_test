package vvs_webapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

public class TestUtil {

	private HtmlPage page;

	public TestUtil(HtmlPage page) {
		this.page = page;
	}

	public void addCustomer(String vat, String desig, String phone) throws IOException {
		// get a specific link
		HtmlAnchor addCustomerLink = page.getAnchorByHref("addCustomer.html");
		// click on it
		HtmlPage nextPage = (HtmlPage) addCustomerLink.openLinkInNewWindow();
		// check if title is the one expected
		assertEquals("Enter Name", nextPage.getTitleText());

		// get the page first form:
		HtmlForm addCustomerForm = nextPage.getForms().get(0);

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
		assertTrue(textReportPage.contains(vat));
		assertTrue(textReportPage.contains(desig));
		assertTrue(textReportPage.contains(phone));
	}
	
	public void addAddress(String vat, String address, String door, String postalCode, String locality) throws IOException {
		// get a specific link
		HtmlAnchor addAddressLink = page.getAnchorByHref("addAddressToCustomer.html");
		// click on it
		HtmlPage nextPage = (HtmlPage) addAddressLink.openLinkInNewWindow();
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
		HtmlPage reportPage = submit.click();
		String textReportPage = reportPage.asText();
		assertTrue(textReportPage.contains(vat));
		assertTrue(textReportPage.contains(address));
		assertTrue(textReportPage.contains(door));
		assertTrue(textReportPage.contains(postalCode));
		assertTrue(textReportPage.contains(locality));
	}
	
	public void removeCustomer(String vat) throws IOException {
		HtmlAnchor removeCustomerLink = page.getAnchorByHref("RemoveCustomerPageController");
		HtmlPage nextPage = (HtmlPage) removeCustomerLink.openLinkInNewWindow();
		assertTrue(nextPage.asText().contains(vat));
		
		HtmlForm removeCustomerForm = nextPage.getForms().get(0);
		HtmlInput vatInput = removeCustomerForm.getInputByName("vat");
		vatInput.setValueAttribute(vat);
		HtmlInput submit = removeCustomerForm.getInputByName("submit");
		submit.click();
	}
	
	public HtmlTable getCustomerAddresses(String vat) throws IOException {
		HtmlAnchor findCustomerLink = page.getAnchorByHref("getCustomerByVAT.html");
		HtmlPage nextPage = (HtmlPage) findCustomerLink.openLinkInNewWindow();
		// get the page first form:
		HtmlForm findCustomerForm = nextPage.getForms().get(0);

		// place data at form
		HtmlInput vatInput = findCustomerForm.getInputByName("vat");
		vatInput.setValueAttribute(vat);
		// submit form
		HtmlInput submit = findCustomerForm.getInputByName("submit");
		HtmlPage reportPage = submit.click();
		System.out.println(reportPage.asText());
		return null;//TODO
	}
	
	public void addSale(String vat) throws IOException {
		// get a specific link
		HtmlAnchor addSaleLink = page.getAnchorByHref("addSale.html");
		// click on it
		HtmlPage nextPage = (HtmlPage) addSaleLink.openLinkInNewWindow();
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



}
