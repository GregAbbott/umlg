package org.tuml.ocl.test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.junit.Assert;
import org.junit.Test;
import org.tuml.framework.ModelLoader;
import org.tuml.ocl.StandaloneFacade;

import tudresden.ocl20.pivot.model.IModel;
import tudresden.ocl20.pivot.pivotmodel.Constraint;
import tudresden.ocl20.pivot.tools.codegen.ocl2java.IOcl2JavaSettings;
import tudresden.ocl20.pivot.tools.codegen.ocl2java.Ocl2JavaFactory;
import tudresden.ocl20.pivot.tools.template.exception.TemplateException;

public class TestOcl {

	final static File rlModel = new File("src/main/resources/model/royalsandloyals.uml");
	final static File rlOclConstraints = new File("src/main/resources/constraints/rl_allConstraints.ocl");

	@Test
	public void testOcl() throws MalformedURLException, TemplateException {
		StandaloneFacade.INSTANCE.initialize(new URL("file:" + new File("src/main/resources/log4j.properties").getAbsolutePath()));
		try {
			URLClassLoader loader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
			URI uri = URI.createURI(ModelLoader.findLocation(loader, true, "org/eclipse/uml2/uml/resources", "org.eclipse.uml2.uml.resources"));
			IModel model = StandaloneFacade.INSTANCE.loadUMLModel(rlModel, uri);
			List<Constraint> constraintList = StandaloneFacade.INSTANCE.parseOclConstraints(model, rlOclConstraints);
			IOcl2JavaSettings settings = Ocl2JavaFactory.getInstance().createJavaCodeGeneratorSettings();
			settings.setSourceDirectory("src/main/generated-ocl");
			List<String> result = StandaloneFacade.INSTANCE.generateJavaCode(constraintList, settings);
			int i  = 0;
			for (String string : result) {
				System.out.println(constraintList.get(i++));
				System.out.println(string);
				System.out.println("========");
			}
			System.out.println("Finished code generation.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
//	@Test
	public void testFail() {
		Assert.fail();
	}
	
}
