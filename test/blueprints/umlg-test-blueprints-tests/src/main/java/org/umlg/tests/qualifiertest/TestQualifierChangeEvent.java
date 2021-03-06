package org.umlg.tests.qualifiertest;

import org.apache.tinkerpop.gremlin.process.traversal.Compare;
import org.junit.Assert;
import org.junit.Test;
import org.umlg.concretetest.God;
import org.umlg.qualifiertest.Nature;
import org.umlg.runtime.test.BaseLocalDbTest;
import org.umlg.runtime.util.Pair;

public class TestQualifierChangeEvent extends BaseLocalDbTest {

	@Test
	public void testQualifierNeedsChangeEvent() {
		God god = new God(true);
		god.setName("THEGOD");
		
		Nature nature = new Nature(true);
		nature.setName1("nature1");
		nature.setName2("nature2");
		nature.addToGod(god);
		
		Nature nature2 = new Nature(true);
		nature2.setName1("nature2");
		nature2.setName2("nature2");
		nature2.addToGod(god);
        db.commit();
		
		God g = new God(god.getVertex());
		g.getNatureForQualifier2(Pair.of(Compare.eq, "nature2")).iterator().next().setName2("nameSoGonaFail");
        db.commit();
		
		God gg = new God(god.getVertex());
		Assert.assertTrue(!gg.getNatureForQualifier2(Pair.of(Compare.eq, "nameSoGonaFail")).isEmpty());
	}

}
