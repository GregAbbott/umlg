package org.umlg.tests.speed;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Test;
import org.umlg.collectiontest.Hand;
import org.umlg.concretetest.God;
import org.umlg.runtime.test.BaseLocalDbTest;

import java.util.Iterator;

/**
 * Date: 2013/02/15
 * Time: 7:45 PM
 */
public class SpeedTest extends BaseLocalDbTest {

//    //0:01:32.267
    @Test
    public void testSpeed() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        God god = new God(true);
        god.setName("god");
        long previousSplitTime = 0;
        for (int i = 0; i < 10000; i++) {
            Hand hand = new Hand(god);
            hand.setName("hand" + i);
            if (i % 1000 == 0) {
                stopWatch.split();
                long splitTime = stopWatch.getSplitTime();
                System.out.println(i + " " + stopWatch.toString() + " 1000 in " + (splitTime - previousSplitTime));
                previousSplitTime = stopWatch.getSplitTime();
                db.commit();
            }
        }
        db.commit();
        stopWatch.stop();
        System.out.println(stopWatch.toString());
    }

    //0:01:32.267
    @Test
    public void testDeleteSpeed() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        God god = new God(true);
        god.setName("god");
        long previousSplitTime = 0;
        for (int i = 0; i < 1000; i++) {
            Hand hand = new Hand(god);
            hand.setName("hand" + i);
            if (i % 1000 == 0) {
                stopWatch.split();
                long splitTime = stopWatch.getSplitTime();
                System.out.println(i + " " + stopWatch.toString() + " 1000 in " + (splitTime - previousSplitTime));
                previousSplitTime = stopWatch.getSplitTime();
                db.commit();
            }
        }
        db.commit();
        int i = 0;
        //TODO its unkewl to clear the inverse list, it clear god's hands, somewhat unoptimized
        Iterator<Hand> hands = god.getHand().iterator();
        while (hands.hasNext()) {
            i++;
            Hand hand = hands.next();
            hands.remove();
            hand.delete();
            if (i % 1000 == 0) {
                stopWatch.split();
                long splitTime = stopWatch.getSplitTime();
                System.out.println(i + " " + stopWatch.toString() + " 1000 in " + (splitTime - previousSplitTime));
                previousSplitTime = stopWatch.getSplitTime();
                db.commit();
            }

        }
        db.commit();
        stopWatch.stop();
        System.out.println(stopWatch.toString());
    }

}
