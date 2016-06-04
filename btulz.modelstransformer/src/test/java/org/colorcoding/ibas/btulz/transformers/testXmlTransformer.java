package org.colorcoding.ibas.btulz.transformers;

import org.colorcoding.ibas.btulz.transformers.XmlTransformer;

import junit.framework.TestCase;

public class testXmlTransformer extends TestCase {

	public void testOldXml() {
		String workFolder = System.getProperty("user.dir")
				+ String.format("%starget%stest-classes%sclub%sibas%sbtulz%smodelstransformer%stransformers%s",
						System.getProperty("file.separator"));
		XmlTransformer xmlTransformer = new XmlTransformer();
		xmlTransformer.input(workFolder + "old.xml");

	}
}
