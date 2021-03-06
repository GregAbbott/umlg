package org.umlg.tests.collectiontest;

import org.junit.Assert;
import org.junit.Test;
import org.umlg.collectiontest.SequenceRoot;
import org.umlg.collectiontest.SequenceTestListMany;
import org.umlg.runtime.collection.UmlgSequence;
import org.umlg.runtime.collection.memory.UmlgMemoryCollection;
import org.umlg.runtime.collection.memory.UmlgMemorySequence;
import org.umlg.runtime.test.BaseLocalDbTest;

/**
 * Date: 2013/03/03
 * Time: 12:33 PM
 */
public class OclStdLibCollectionTest extends BaseLocalDbTest {

    @Test
    public void testMax() {
        UmlgMemoryCollection<Integer> collection = new UmlgMemorySequence<Integer>();
        collection.add(Integer.valueOf(1));
        collection.add(Integer.valueOf(2));
        collection.add(Integer.valueOf(3));
        Assert.assertEquals(Integer.valueOf(3), collection.max());
    }

    @Test
    public void testSum() {
        UmlgMemoryCollection<Integer> collection = new UmlgMemorySequence<Integer>();
        collection.add(Integer.valueOf(1));
        collection.add(Integer.valueOf(2));
        collection.add(Integer.valueOf(3));
        Assert.assertEquals(Integer.valueOf(6), collection.sum());
    }

    @Test
    public void testIncludes() {
        SequenceRoot sequenceRoot = new SequenceRoot(true);
        sequenceRoot.setName("sequenceRoot");
        SequenceTestListMany sequenceTestListMany1 = new SequenceTestListMany(sequenceRoot);
        sequenceTestListMany1.setName("sequenceTestListMany1");
        SequenceTestListMany sequenceTestListMany2 = new SequenceTestListMany(sequenceRoot);
        sequenceTestListMany2.setName("sequenceTestListMany2");
        SequenceTestListMany sequenceTestListMany3 = new SequenceTestListMany(sequenceRoot);
        sequenceTestListMany3.setName("sequenceTestListMany3");
        SequenceTestListMany sequenceTestListMany4 = new SequenceTestListMany(sequenceRoot);
        sequenceTestListMany4.setName("sequenceTestListMany4");
        db.commit();

        Assert.assertEquals(4, countVertices());
        Assert.assertEquals(4, countEdges());

        Assert.assertTrue(sequenceRoot.getSequenceTestListMany().includes(sequenceTestListMany2));

        Assert.assertEquals(4, countVertices());
        Assert.assertEquals(4, countEdges());
    }

    @Test
    public void testExcludes() {
        SequenceRoot sequenceRoot = new SequenceRoot(true);
        sequenceRoot.setName("sequenceRoot");
        SequenceTestListMany sequenceTestListMany1 = new SequenceTestListMany(sequenceRoot);
        sequenceTestListMany1.setName("sequenceTestListMany1");
        SequenceTestListMany sequenceTestListMany2 = new SequenceTestListMany(sequenceRoot);
        sequenceTestListMany2.setName("sequenceTestListMany2");
        SequenceTestListMany sequenceTestListMany3 = new SequenceTestListMany(sequenceRoot);
        sequenceTestListMany3.setName("sequenceTestListMany3");
        SequenceTestListMany sequenceTestListMany4 = new SequenceTestListMany(sequenceRoot);
        sequenceTestListMany4.setName("sequenceTestListMany4");
        db.commit();

        Assert.assertEquals(4, countVertices());
        Assert.assertEquals(4, countEdges());

        SequenceTestListMany sequenceTestListMany5 = new SequenceTestListMany(true);
        sequenceTestListMany5.setName("sequenceTestListMany5");
        Assert.assertTrue(sequenceRoot.getSequenceTestListMany().excludes(sequenceTestListMany5));
    }

    @Test
    public void testCount() {
        SequenceRoot sequenceRoot = new SequenceRoot(true);
        sequenceRoot.setName("sequenceRoot");
        SequenceTestListMany sequenceTestListMany1 = new SequenceTestListMany(sequenceRoot);
        sequenceTestListMany1.setName("sequenceTestListMany1");
        SequenceTestListMany sequenceTestListMany2 = new SequenceTestListMany(sequenceRoot);
        sequenceTestListMany2.setName("sequenceTestListMany2");
        SequenceTestListMany sequenceTestListMany3 = new SequenceTestListMany(sequenceRoot);
        sequenceTestListMany3.setName("sequenceTestListMany3");
        SequenceTestListMany sequenceTestListMany4 = new SequenceTestListMany(sequenceRoot);
        sequenceTestListMany4.setName("sequenceTestListMany4");
        db.commit();

        Assert.assertEquals(4, countVertices());
        Assert.assertEquals(4, countEdges());


        Assert.assertEquals(1, sequenceRoot.getSequenceTestListMany().count(sequenceTestListMany2));

        Assert.assertEquals(4, countVertices());
        Assert.assertEquals(4, countEdges());
    }

    @Test
    public void testIncludesAll() {
        UmlgSequence<SequenceTestListMany> test = new UmlgMemorySequence<SequenceTestListMany>();
        SequenceRoot sequenceRoot = new SequenceRoot(true);
        sequenceRoot.setName("sequenceRoot");
        SequenceTestListMany sequenceTestListMany1 = new SequenceTestListMany(sequenceRoot);
        sequenceTestListMany1.setName("sequenceTestListMany1");
        SequenceTestListMany sequenceTestListMany2 = new SequenceTestListMany(sequenceRoot);
        sequenceTestListMany2.setName("sequenceTestListMany2");
        SequenceTestListMany sequenceTestListMany3 = new SequenceTestListMany(sequenceRoot);
        sequenceTestListMany3.setName("sequenceTestListMany3");
        SequenceTestListMany sequenceTestListMany4 = new SequenceTestListMany(sequenceRoot);
        sequenceTestListMany4.setName("sequenceTestListMany4");
        test.add(sequenceTestListMany1);
        test.add(sequenceTestListMany2);
        test.add(sequenceTestListMany3);
        test.add(sequenceTestListMany4);
        db.commit();

        Assert.assertEquals(4, countVertices());
        Assert.assertEquals(4, countEdges());

        Assert.assertTrue(sequenceRoot.getSequenceTestListMany().includesAll(test));

        Assert.assertEquals(4, countVertices());
        Assert.assertEquals(4, countEdges());

        sequenceTestListMany3.delete();
        db.commit();

        //This fails with hard delete as test still references a deleted vertex
//        Assert.assertFalse(sequenceRoot.getSequenceTestListMany().includesAll(test));
        Assert.assertEquals(3, countVertices());
        Assert.assertEquals(3, countEdges());
    }

    @Test
    public void testExcludesAll() {
        UmlgSequence<SequenceTestListMany> test = new UmlgMemorySequence<SequenceTestListMany>();
        SequenceRoot sequenceRoot = new SequenceRoot(true);
        sequenceRoot.setName("sequenceRoot");
        SequenceTestListMany sequenceTestListMany1 = new SequenceTestListMany(sequenceRoot);
        sequenceTestListMany1.setName("sequenceTestListMany1");
        SequenceTestListMany sequenceTestListMany2 = new SequenceTestListMany(sequenceRoot);
        sequenceTestListMany2.setName("sequenceTestListMany2");
        SequenceTestListMany sequenceTestListMany3 = new SequenceTestListMany(sequenceRoot);
        sequenceTestListMany3.setName("sequenceTestListMany3");
        SequenceTestListMany sequenceTestListMany4 = new SequenceTestListMany(sequenceRoot);
        sequenceTestListMany4.setName("sequenceTestListMany4");
        test.add(sequenceTestListMany1);
        test.add(sequenceTestListMany2);
        test.add(sequenceTestListMany3);
        test.add(sequenceTestListMany4);
        db.commit();

        Assert.assertEquals(4, countVertices());
        Assert.assertEquals(4, countEdges());


        Assert.assertFalse(sequenceRoot.getSequenceTestListMany().excludesAll(test));

        Assert.assertEquals(4, countVertices());
        Assert.assertEquals(4, countEdges());

        test.remove(3);
        Assert.assertFalse(sequenceRoot.getSequenceTestListMany().excludesAll(test));
        db.commit();

        SequenceTestListMany sequenceTestListMany5 = new SequenceTestListMany(true);
        sequenceTestListMany5.setName("sequenceTestListMany5");
        SequenceTestListMany sequenceTestListMany6 = new SequenceTestListMany(true);
        sequenceTestListMany6.setName("sequenceTestListMany6");
        SequenceTestListMany sequenceTestListMany7 = new SequenceTestListMany(true);
        sequenceTestListMany7.setName("sequenceTestListMany7");
        test.clear();
        test.add(sequenceTestListMany5);
        test.add(sequenceTestListMany6);
        test.add(sequenceTestListMany7);

        Assert.assertTrue(sequenceRoot.getSequenceTestListMany().excludesAll(test));

    }

}
