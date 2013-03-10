package org.tuml.tinker.constraint;

import org.junit.Test;
import org.tuml.concretetest.God;
import org.tuml.constraints.ConstraintChild1;
import org.tuml.constraints.ConstraintChild2;
import org.tuml.constraints.ConstraintRoot;
import org.tuml.runtime.test.BaseLocalDbTest;
import org.tuml.runtime.validation.TumlConstraintViolationException;

/**
 * Date: 2013/03/10
 * Time: 9:56 AM
 */
public class ConstrainedPropertyTest extends BaseLocalDbTest {

    @Test(expected = TumlConstraintViolationException.class)
    public void testConstraintedPropertyFail() {
        God g = new God(true);
        g.setName("g");
        g.setConstraintTest("aaaaXaaa");
    }

    @Test
    public void testConstraintedPropertyPass() {
        God g = new God(true);
        g.setName("g");
        g.setConstraintTest("aaaaaaa");
        db.commit();
    }

    @Test
    public void testAssociationOnePropertyPass() {
        ConstraintRoot constraintRoot1 = new ConstraintRoot(true);
        constraintRoot1.setName("constraintRoot");
        ConstraintChild1 constraintChild1 = new ConstraintChild1(constraintRoot1);
        constraintChild1.setName("constraintChild1");
        ConstraintChild1 constraintChild2 = new ConstraintChild1(constraintRoot1);
        constraintChild2.setName("constraintChild2");
        ConstraintChild1 constraintChild3 = new ConstraintChild1(constraintRoot1);
        constraintChild3.setName("constraintChild3");

        ConstraintChild2 constraintChild11 = new ConstraintChild2(constraintRoot1);
        constraintChild11.setName("constraintChild11");
        ConstraintChild2 constraintChild12 = new ConstraintChild2(constraintRoot1);
        constraintChild12.setName("constraintChild12");
        ConstraintChild2 constraintChild13 = new ConstraintChild2(constraintRoot1);
        constraintChild13.setName("constraintChild13");

        ConstraintRoot constraintRoot2 = new ConstraintRoot(true);
        constraintRoot2.setName("constraintRoot");
        ConstraintChild2 constraintChild21 = new ConstraintChild2(constraintRoot2);
        constraintChild21.setName("constraintChild21");
        ConstraintChild2 constraintChild22 = new ConstraintChild2(constraintRoot2);
        constraintChild22.setName("constraintChild22");
        ConstraintChild2 constraintChild23 = new ConstraintChild2(constraintRoot2);
        constraintChild23.setName("constraintChild23");

        constraintChild1.setConstraintChild2(constraintChild11);
        db.commit();
    }

    @Test(expected = TumlConstraintViolationException.class)
    public void testAssociationOnePropertyFail() {
        ConstraintRoot constraintRoot1 = new ConstraintRoot(true);
        constraintRoot1.setName("constraintRoot");
        ConstraintChild1 constraintChild1 = new ConstraintChild1(constraintRoot1);
        constraintChild1.setName("constraintChild1");
        ConstraintChild1 constraintChild2 = new ConstraintChild1(constraintRoot1);
        constraintChild2.setName("constraintChild2");
        ConstraintChild1 constraintChild3 = new ConstraintChild1(constraintRoot1);
        constraintChild3.setName("constraintChild3");

        ConstraintChild2 constraintChild11 = new ConstraintChild2(constraintRoot1);
        constraintChild11.setName("constraintChild11");
        ConstraintChild2 constraintChild12 = new ConstraintChild2(constraintRoot1);
        constraintChild12.setName("constraintChild12");
        ConstraintChild2 constraintChild13 = new ConstraintChild2(constraintRoot1);
        constraintChild13.setName("constraintChild13");

        ConstraintRoot constraintRoot2 = new ConstraintRoot(true);
        constraintRoot2.setName("constraintRoot");
        ConstraintChild2 constraintChild21 = new ConstraintChild2(constraintRoot2);
        constraintChild21.setName("constraintChild21");
        ConstraintChild2 constraintChild22 = new ConstraintChild2(constraintRoot2);
        constraintChild22.setName("constraintChild22");
        ConstraintChild2 constraintChild23 = new ConstraintChild2(constraintRoot2);
        constraintChild23.setName("constraintChild23");

        constraintChild1.setConstraintChild2(constraintChild21);
        db.commit();
    }

}
