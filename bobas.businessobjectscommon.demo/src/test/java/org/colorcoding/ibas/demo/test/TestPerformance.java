package org.colorcoding.ibas.demo.test;

import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.expression.JudgmentOperationException;
import org.colorcoding.ibas.demo.bo.materialsjournal.MaterialsJournal;

import junit.framework.TestCase;

/**
 * 性能测试
 *
 * 测试范围：
 * 1. 缓存查询性能（阶梯增长 vs 一次性查询）
 */
public class TestPerformance extends TestCase {

	/**
	 * 创建标准测试用Criteria
	 * 查询条件：BaseDocumentType=ORDER, BaseDocumentEntry=200, BaseDocumentLineId=1
	 */
	private ICriteria createTestCriteria() {
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
		return criteria;
	}

	// ==================== 1. 缓存查询性能 ====================

	/**
	 * 测试缓存查询性能
	 * 覆盖：阶梯增长（每次新增一个对象后查询）vs 一次性查询（2000个对象一次查询）
	 */
	public void testFetchInCache() throws JudgmentOperationException {
		ICriteria criteria = createTestCriteria();

		MaterialsJournal journal;
		ArrayList<MaterialsJournal> journals = new ArrayList<>(2000);
		// 阶梯时间：每次新增一个对象后查询
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

		// 一次时间：2000个对象一次查询
		beginTime = DateTimes.now();
		BOUtilities.fetch(journals, criteria);
		endTime = DateTimes.now();
		System.out.println();
		System.out.println(Strings.format("+++++++++++++ fetch bo: A(%s) time %s +++++++++++++", journals.size(),
				endTime.getTime() - beginTime.getTime()));
		System.out.println();
	}
}
