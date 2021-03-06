package org.umlg.testbasic;

import org.junit.Assert;

import org.junit.Test;
import org.umlg.OneOne;
import org.umlg.OneTwo;
import org.umlg.runtime.test.BaseLocalDbTest;

public class TestOneToOne extends BaseLocalDbTest {

    @Test
    public void testOneToOne() {
        OneOne oneOne1 = new OneOne(true);
        OneTwo oneTwo1 = new OneTwo(true);
        OneOne oneOne2 = new OneOne(true);
        OneTwo oneTwo2 = new OneTwo(true);
        OneOne oneOne3 = new OneOne(true);
        OneTwo oneTwo3 = new OneTwo(true);
        OneOne oneOne4 = new OneOne(true);
        OneTwo oneTwo4 = new OneTwo(true);
        db.commit();
        Assert.assertEquals(7, countVertices());

        oneOne1.addToOneTwo(oneTwo1);
        oneOne2.addToOneTwo(oneTwo2);
        oneOne3.addToOneTwo(oneTwo3);
        oneOne4.addToOneTwo(oneTwo4);
        db.commit();
        Assert.assertEquals(7, countVertices());
        //There is an edge to the root node for every non composite vertex
        Assert.assertEquals(11, countEdges());

        boolean exception = false;
        try {
            oneOne1.addToOneTwo(oneTwo2);
            db.commit();
        } catch (RuntimeException e) {
            exception = true;
        }
        Assert.assertTrue(exception);
        Assert.assertEquals(7, countVertices());
        Assert.assertEquals(11, countEdges());
    }

    @Test
    public void testOneToOneCheckInverseSides() {
        OneOne oneOne1 = new OneOne(true);
        oneOne1.setName("asd");
        OneTwo oneTwo1 = new OneTwo(true);
        OneOne oneOne2 = new OneOne(true);
        OneTwo oneTwo2 = new OneTwo(true);
        OneOne oneOne3 = new OneOne(true);
        OneTwo oneTwo3 = new OneTwo(true);
        OneOne oneOne4 = new OneOne(true);
        OneTwo oneTwo4 = new OneTwo(true);
        db.commit();
        Assert.assertEquals(7, countVertices());

        oneOne1.addToOneTwo(oneTwo1);
        oneOne2.addToOneTwo(oneTwo2);
        oneOne3.addToOneTwo(oneTwo3);
        oneOne4.addToOneTwo(oneTwo4);
        db.commit();
        Assert.assertEquals(7, countVertices());
        Assert.assertEquals(11, countEdges());

        oneOne1.setOneTwo(oneTwo2);
        Assert.assertEquals(oneOne1.getOneTwo(), oneTwo2);
        Assert.assertEquals(oneTwo2.getOneOne(), oneOne1);
        db.commit();
        Assert.assertEquals(7, countVertices());
        Assert.assertEquals(10, countEdges());

        OneOne oneOne1Test = new OneOne(oneOne1.getVertex());
        Assert.assertEquals(oneTwo2, oneOne1Test.getOneTwo());

        OneTwo oneTwo2Test = new OneTwo(oneTwo2.getVertex());
        Assert.assertEquals(oneOne1, oneTwo2Test.getOneOne());

        OneOne oneOne2Test = new OneOne(oneOne2.getVertex());
        Assert.assertNull(oneOne2Test.getOneTwo());

        OneTwo oneTwo1Test = new OneTwo(oneTwo1.getVertex());
        Assert.assertNull(oneTwo1Test.getOneOne());
    }

}
