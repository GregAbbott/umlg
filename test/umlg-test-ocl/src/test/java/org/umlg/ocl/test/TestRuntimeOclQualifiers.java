package org.umlg.ocl.test;

import org.apache.tinkerpop.gremlin.process.traversal.Compare;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.umlg.ocl.UmlgOcl2Parser;
import org.umlg.ocl.UmlgOclExecutor;
import org.umlg.qualifier.Bank;
import org.umlg.qualifier.Customer;
import org.umlg.runtime.test.BaseLocalDbTest;
import org.umlg.runtime.util.Pair;

import java.net.URL;
import java.util.Collection;
import java.util.logging.LogManager;

/**
 * Date: 2014/03/07
 * Time: 5:56 PM
 */
public class TestRuntimeOclQualifiers extends BaseLocalDbTest {

    @BeforeClass
    public static void beforeClass() {
        try {
            URL url = BaseLocalDbTest.class.getResource("/logging.properties");
            LogManager.getLogManager().readConfiguration(url.openStream());
            URL umlUrl = BaseLocalDbTest.class.getResource("/test-ocl.uml");
            UmlgOcl2Parser.INSTANCE.init(umlUrl.toURI());
            @SuppressWarnings("unused")
            UmlgOcl2Parser instance = UmlgOcl2Parser.INSTANCE;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void testQualifierOcl() {
        Bank bank = new Bank(true);
        bank.setName("BADASS");
        for (int i = 0; i < 1000; i++) {
            Customer customer1 = new Customer(true);
            customer1.setName("c" + Integer.toString(i));
            customer1.setAccountNumber(i);
            customer1.setBank(bank);
        }

        Customer john1 = new Customer(true);
        john1.setName("john");
        john1.setAccountNumber(1);
        john1.setBank(bank);

        db.commit();
        Assert.assertEquals(1001, new Bank(bank.getVertex()).getCustomer().size());
        Assert.assertNotNull(new Bank(bank.getVertex()).getCustomerForNameQualifierandAccountNumberQualifier(Pair.of(Compare.eq, "c1"), Pair.of(Compare.eq, 1)));
        Assert.assertNotNull(new Bank(bank.getVertex()).getCustomerJohn001());

        Object result = UmlgOclExecutor.executeOclQuery(bank, "self.customer['john', 1].bank.name");
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof String);
        Assert.assertEquals("BADASS", result);
    }

    @Test
    public void testQualifierOclWithoutQualifier() {
        Bank bank = new Bank(true);
        bank.setName("BADASS");
        for (int i = 0; i < 1000; i++) {
            Customer customer1 = new Customer(true);
            customer1.setName("c" + Integer.toString(i));
            customer1.setAccountNumber(i);
            customer1.setBank(bank);
        }

        Customer john1 = new Customer(true);
        john1.setName("john");
        john1.setAccountNumber(1);
        john1.setBank(bank);

        db.commit();
        Assert.assertEquals(1001, new Bank(bank.getVertex()).getCustomer().size());
        Assert.assertNotNull(new Bank(bank.getVertex()).getCustomerForNameQualifierandAccountNumberQualifier(Pair.of(Compare.eq, "c1"), Pair.of(Compare.eq, 1)));
        Assert.assertNotNull(new Bank(bank.getVertex()).getCustomerJohn001());

        Object result = UmlgOclExecutor.executeOclQuery(bank, "self.customer->asSet()");
        //The ->asSet() is there because of ocl multiplicity bug
//        Object result = UmlgOclExecutor.executeOclQuery(bank, "self.customer");
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof Collection);
    }

    @Test
    public void testQualifierOclWithoutQualifier2() {
        Bank bank = new Bank(true);
        bank.setName("BADASS");
        for (int i = 0; i < 1000; i++) {
            Customer customer1 = new Customer(true);
            customer1.setName("c" + Integer.toString(i));
            customer1.setAccountNumber(i);
            customer1.setBank(bank);
        }

        Customer john1 = new Customer(true);
        john1.setName("john");
        john1.setAccountNumber(1);
        john1.setBank(bank);

        db.commit();
        Assert.assertEquals(1001, new Bank(bank.getVertex()).getCustomer().size());
        Assert.assertNotNull(new Bank(bank.getVertex()).getCustomerForNameQualifierandAccountNumberQualifier(Pair.of(Compare.eq, "c1"), Pair.of(Compare.eq, 1)));
        Assert.assertNotNull(new Bank(bank.getVertex()).getCustomerJohn001());

        Object result = UmlgOclExecutor.executeOclQuery(john1, "self.bank->asSet()");
//        Object result = UmlgOclExecutor.executeOclQuery(bank, "self.customer->asSet()");
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof Collection);
    }
}
