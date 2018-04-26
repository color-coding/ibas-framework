package org.colorcoding.ibas.bobas.test.serialization;

import org.colorcoding.ibas.bobas.serialization.structure.Analyzer;
import org.colorcoding.ibas.bobas.serialization.structure.AnalyzerGetter;
import org.colorcoding.ibas.bobas.serialization.structure.AnalyzerSetter;
import org.colorcoding.ibas.bobas.serialization.structure.Element;
import org.colorcoding.ibas.bobas.serialization.structure.ElementRoot;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrder;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;

import junit.framework.TestCase;

public class testStructure extends TestCase {

	public void testAnalyzer() {
		System.out.println("Analyzer");
		Analyzer analyzer = new Analyzer();
		ElementRoot elementRoot = analyzer.analyse(SalesOrder.class);
		for (Element item : elementRoot.allElements()) {
			for (int i = 0; i < item.getLevel(); i++) {
				System.out.print("  ");
			}
			System.out.println(item.toString());
		}
		System.out.println("AnalyzerSetter");
		analyzer = new AnalyzerSetter();
		elementRoot = analyzer.analyse(SalesOrder.class);
		for (Element item : elementRoot.allElements()) {
			for (int i = 0; i < item.getLevel(); i++) {
				System.out.print("  ");
			}
			System.out.println(item.toString());
		}
		System.out.println("AnalyzerGetter");
		analyzer = new AnalyzerGetter();
		elementRoot = analyzer.analyse(ISalesOrder.class);
		for (Element item : elementRoot.allElements()) {
			for (int i = 0; i < item.getLevel(); i++) {
				System.out.print("  ");
			}
			System.out.println(item.toString());
		}
	}
}
