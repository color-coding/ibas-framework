package bobas.businessobjectscommon.demo.test;

import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.expression.JudmentOperationException;
import org.colorcoding.ibas.demo.bo.materialsjournal.MaterialsJournal;

import junit.framework.TestCase;

public class TestPerformance extends TestCase {

	public void testFetchInCache() throws JudmentOperationException {
		ICriteria criteria = Criteria.create();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(MaterialsJournal.PROPERTY_BASEDOCUMENTTYPE.getName());
		condition.setValue("ORDER");
		condition = criteria.getConditions().create();
		condition.setAlias(MaterialsJournal.PROPERTY_BASEDOCUMENTENTRY.getName());
		condition.setValue(200);
		condition = criteria.getConditions().create();
		condition.setAlias(MaterialsJournal.PROPERTY_BASEDOCUMENTLINEID.getName());
		condition.setValue(1);

		MaterialsJournal journal;
		ArrayList<MaterialsJournal> journals = new ArrayList<>(2000);
		// 阶梯时间
		DateTime beginTime = DateTimes.now();
		for (int i = 0; i < 2000; i++) {
			journal = new MaterialsJournal();
			journal.setBaseDocumentType("ORDER");
			journal.setBaseDocumentEntry(100);
			journal.setBaseDocumentLineId(i);
			journals.add(journal);
			BOUtilities.fetch(journals, criteria);
		}
		DateTime endTime = DateTimes.now();
		System.out.println();
		System.out.println(Strings.format("+++++++++++++ fetch bo: P(%s) time %s +++++++++++++", journals.size(),
				endTime.getTime() - beginTime.getTime()));
		System.out.println();
		// 一次时间
		beginTime = DateTimes.now();
		BOUtilities.fetch(journals, criteria);
		endTime = DateTimes.now();
		System.out.println();
		System.out.println(Strings.format("+++++++++++++ fetch bo: A(%s) time %s +++++++++++++", journals.size(),
				endTime.getTime() - beginTime.getTime()));
		System.out.println();
	}

}
