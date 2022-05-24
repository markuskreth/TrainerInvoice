package de.kreth.invoice.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.kreth.invoice.data.Invoice;

public class InvoiceBusinessTest {

    private InvoiceBusiness business;

    @BeforeEach
    void initBusiness() {
	business = new InvoiceBusiness(null, null);
    }

    @Test
    void testFirstRechnungNoCreation() {
	String invoiceId = business.createNextInvoiceId(Collections.emptyList(), "Rechnung_{0}");
	assertEquals("Rechnung_1", invoiceId);
    }

    @Test
    void testSimpleIncrementRechnungNoCreation() {
	Invoice invoice1 = new Invoice();
	invoice1.setInvoiceId("Rechnung_1");

	Invoice invoice2 = new Invoice();
	invoice2.setInvoiceId("Rechnung_2");

	String invoiceId = business.createNextInvoiceId(Arrays.asList(invoice1, invoice2), "Rechnung_{0}");
	assertEquals("Rechnung_3", invoiceId);
    }

    @Test
    void testIgnoreNotMatching() {

	Invoice invoice1 = new Invoice();
	invoice1.setInvoiceId("Rechnung_1");

	Invoice invoice2 = new Invoice();
	invoice2.setInvoiceId("Rechnung_2");

	Invoice invoice3 = new Invoice();
	invoice3.setInvoiceId("Rechnung_2_zusatz");

	String invoiceId = business.createNextInvoiceId(Arrays.asList(invoice1, invoice2, invoice3), "Rechnung_{0}");
	assertEquals("Rechnung_3", invoiceId);
    }

    @Test
    void testFilterMatching() {
	String invoiceNo = "Rechnung_324";
	assertTrue(business.filter(invoiceNo, "Rechnung_{0}"));
    }

    @Test
    void testFilterMatchingSuffix() {
	String invoiceNo = "Rechnung_324_Test";
	assertTrue(business.filter(invoiceNo, "Rechnung_{0}_Test"));
    }

    @Test
    void testFilterSuffixNotMatching() {

	String invoiceNo = "Rechnung_3_sonder";
	assertFalse(business.filter(invoiceNo, "Rechnung_{0}"));
    }

    @Test
    void testFilterWrongSuffixNotMatching() {

	String invoiceNo = "Rechnung_3_sonder";
	assertFalse(business.filter(invoiceNo, "Rechnung_{0}_Test"));
    }
}
