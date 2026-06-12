package org.colorcoding.ibas.demo.test;

import java.util.Random;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.common.IChildCriteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.ISort;
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.logic.common.BOInstanceLogService;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.demo.bo.materials.IMaterials;
import org.colorcoding.ibas.demo.bo.materials.Materials;
import org.colorcoding.ibas.demo.bo.materialsjournal.MaterialsJournal;
import org.colorcoding.ibas.demo.bo.salesorder.ISalesOrderItem;
import org.colorcoding.ibas.demo.bo.salesorder.SalesOrder;
import org.colorcoding.ibas.demo.bo.salesorder.SalesOrderItem;
import org.colorcoding.ibas.demo.repository.BORepositoryTrainingTesting;

import junit.framework.TestCase;

/**
 * 多任务并发测试
 *
 * 测试范围：
 * 1. 物料保存（新增/更新）
 * 2. 销售订单保存（新增/修改/删除）
 * 3. 销售订单查询（含条件/排序/子项查询）
 * 4. 极端并发压力测试
 */
public class TestMultitask extends TestCase {

	public static Random random = new Random();

	// ==================== 1. 物料保存 ====================

	/**
	 * 保存单个物料
	 * 若不存在则新增，若存在则随机更新备注
	 *
	 * @param boRepository 仓库实例
	 * @param itemCode     物料编码
	 * @param itemDescription 物料描述
	 */
	private void saveMaterial(BORepositoryTrainingTesting boRepository, String itemCode, String itemDescription)
			throws Exception {
		IOperationResult<IMaterials> opRsltMM;
		Criteria criteria = new Criteria();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(Materials.PROPERTY_ITEMCODE.getName());
		IMaterials materials = new Materials();
		materials.setItemCode(itemCode);
		materials.setItemDescription(itemDescription);
		condition.setValue(materials.getItemCode());
		opRsltMM = boRepository.fetchMaterials(criteria);
		if (opRsltMM.getError() != null) {
			throw opRsltMM.getError();
		}
		if (opRsltMM.getResultObjects().isEmpty()) {
			opRsltMM = boRepository.saveMaterials(materials);
			if (opRsltMM.getError() != null) {
				throw opRsltMM.getError();
			}
		} else {
			materials = opRsltMM.getResultObjects().firstOrDefault();
			if (random.nextBoolean()) {
				materials.setRemarks(DateTimes.now().toString());
				opRsltMM = boRepository.saveMaterials(materials);
				if (opRsltMM.getError() != null) {
					throw opRsltMM.getError();
				}
			}
		}
	}

	/**
	 * 测试物料保存
	 * 覆盖：新增T800/S001/S003三种物料，若已存在则随机更新
	 */
	public void testSaveMaterials() throws Exception {
		// 打开日志
		BOInstanceLogService.BO_LOGST_SETTING.put(Materials.BUSINESS_OBJECT_CODE, true);

		try (BORepositoryTrainingTesting boRepository = new BORepositoryTrainingTesting()) {
			boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
			saveMaterial(boRepository, "T800;", "终结者机器人-T800");
			saveMaterial(boRepository, "S001", "绝地武士-剑");
			saveMaterial(boRepository, "S003", "绝地武士-头盔");
		}
	}

	// ==================== 2. 销售订单保存 ====================

	/**
	 * 测试销售订单保存
	 * 覆盖：新增订单→修改订单（删除行/修改数量/新增行）→删除订单
	 */
	public void testSaveOrders() throws Exception {
		// 打开日志
		BOInstanceLogService.BO_LOGST_SETTING.put(SalesOrder.BUSINESS_OBJECT_CODE, true);
		BOInstanceLogService.BO_LOGST_SETTING.put(MaterialsJournal.BUSINESS_OBJECT_CODE, true);

		try (BORepositoryTrainingTesting boRepository = new BORepositoryTrainingTesting()) {
			boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());

			// 创建订单
			SalesOrder order = new SalesOrder();
			order.setCustomerCode("'C00001");
			order.setCustomerName("宇宙无敌影业");
			ISalesOrderItem item = order.getSalesOrderItems().create();
			item.setItemCode("T800;");
			item.setItemDescription("终结者机器人-T800");
			item.setQuantity(Decimals.valueOf(1));
			item.setPrice(Decimals.valueOf(999999.99));
			item = order.getSalesOrderItems().create();
			item.setItemCode("S001");
			item.setItemDescription("绝地武士-剑");
			item.setQuantity(Decimals.valueOf(2));
			item.setPrice(Decimals.valueOf(99.00));
			IOperationResult<?> operationResult = boRepository.saveSalesOrder(order);
			System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
					operationResult.getMessage(), operationResult.getResultObjects().size()));
			assertEquals(operationResult.getResultCode(), 0);

			// 修改订单：删除第一行、修改第二行数量、新增第三行
			order.setCustomerName("宇宙无敌影业--");
			order.getSalesOrderItems().get(0).delete();
			item = order.getSalesOrderItems().get(1);
			item.setQuantity(Decimals.valueOf(20));
			item = order.getSalesOrderItems().create();
			item.setItemCode("S003");
			item.setItemDescription("绝地武士-头盔");
			item.setQuantity(Decimals.valueOf(3));
			item.setPrice(Decimals.valueOf(299.00));
			operationResult = boRepository.saveSalesOrder(order);
			System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
					operationResult.getMessage(), operationResult.getResultObjects().size()));
			assertEquals(operationResult.getResultCode(), 0);

			// 删除订单
			order.delete();
			operationResult = boRepository.saveSalesOrder(order);
			System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
					operationResult.getMessage(), operationResult.getResultObjects().size()));
			assertEquals(operationResult.getResultCode(), 0);
		}
	}

	// ==================== 3. 销售订单查询 ====================

	/**
	 * 创建标准测试用查询Criteria
	 * 包含：括号条件、多种操作符、子项查询、排序
	 */
	private ICriteria createFetchCriteria() {
		ICriteria criteria = new Criteria();
		criteria.setResultCount(100);
		// ("DocStatus" = 'P' OR "DocStatus" = 'F')
		ICondition condition = criteria.getConditions().create();
		condition.setBracketOpen(1);
		condition.setAlias(SalesOrder.PROPERTY_DOCUMENTSTATUS.getName());
		condition.setValue(emDocumentStatus.PLANNED);
		condition = criteria.getConditions().create();
		condition.setBracketClose(1);
		condition.setAlias(SalesOrder.PROPERTY_DOCUMENTSTATUS.getName());
		condition.setValue(emDocumentStatus.RELEASED.ordinal());
		condition.setRelationship(ConditionRelationship.OR);
		// (CardCode != ''C')
		condition = criteria.getConditions().create();
		condition.setAlias(SalesOrder.PROPERTY_CUSTOMERCODE.getName());
		condition.setValue("'C");
		condition.setOperation(ConditionOperation.NOT_EQUAL);
		condition.setRelationship(ConditionRelationship.AND);
		// (DocEntry like '%A%')
		condition = criteria.getConditions().create();
		condition.setAlias(SalesOrder.PROPERTY_DOCENTRY.getName());
		condition.setValue("A");
		condition.setOperation(ConditionOperation.CONTAIN);
		condition.setRelationship(ConditionRelationship.OR);
		// 子项查询
		IChildCriteria childCriteria = criteria.getChildCriterias().create();
		childCriteria.setNoChilds(false);
		childCriteria.setIncludingOtherChilds(false);
		childCriteria.setOnlyHasChilds(false);
		childCriteria.setPropertyPath(SalesOrder.PROPERTY_SALESORDERITEMS);
		condition = childCriteria.getConditions().create();
		condition.setAlias(SalesOrderItem.PROPERTY_QUANTITY);
		condition.setOperation(ConditionOperation.GREATER_EQUAL);
		condition.setValue(Decimals.valueOf(3));
		// ORDER BY "DocEntry" DESC, "CardCode" ASC
		ISort sort = criteria.getSorts().create();
		sort.setAlias(SalesOrder.PROPERTY_DOCENTRY.getName());
		sort.setSortType(SortType.DESCENDING);
		sort = criteria.getSorts().create();
		sort.setAlias(SalesOrder.PROPERTY_CUSTOMERCODE.getName());
		sort.setSortType(SortType.ASCENDING);
		return criteria;
	}

	/**
	 * 测试销售订单查询
	 * 覆盖：含条件/排序/子项查询的订单查询，多次重复查询
	 */
	public void testFetchOrders() throws Exception {
		try (BORepositoryTrainingTesting boRepository = new BORepositoryTrainingTesting()) {
			boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
			ICriteria criteria = createFetchCriteria();

			IOperationResult<?> operationResult = boRepository.fetchSalesOrder(criteria);
			System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
					operationResult.getMessage(), operationResult.getResultObjects().size()));
			assertEquals(operationResult.getResultCode(), 0);

			for (int i = 0; i < 3; i++) {
				operationResult = boRepository.fetchSalesOrder(criteria);
				System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
						operationResult.getMessage(), operationResult.getResultObjects().size()));
				assertEquals(operationResult.getResultCode(), 0);
			}
		}
	}

	// ==================== 4. 极端并发压力测试 ====================

	static boolean FLAG_STOP = false;

	/**
	 * 测试极端并发压力
	 * 覆盖：按CPU核心数启动多线程，并行执行物料保存/订单保存/订单查询
	 */
	public void testExtremeTask() throws InterruptedException {
		Thread thread;
		TestMultitask test = new TestMultitask();
		int cpuCount = Runtime.getRuntime().availableProcessors();
		for (int i = 0; i < cpuCount; i++) {
			thread = new Thread() {
				@Override
				public void run() {
					while (!FLAG_STOP) {
						try {
							test.testSaveMaterials();
						} catch (Exception e) {
							System.err.println(e);
						}
					}
				}
			};
			thread.start();
			thread = new Thread() {
				@Override
				public void run() {
					while (!FLAG_STOP) {
						try {
							test.testSaveOrders();
						} catch (Exception e) {
							System.err.println(e);
						}
					}
				}
			};
			thread.start();
			thread = new Thread() {
				@Override
				public void run() {
					while (!FLAG_STOP) {
						try {
							test.testFetchOrders();
						} catch (Exception e) {
							System.err.println(e);
						}
					}
				}
			};
			thread.start();
		}
		Thread.sleep(300000);
		FLAG_STOP = true;
		System.out.println("all done.");
		Thread.sleep(600000);// 继续等待，资源释放
	}
}
