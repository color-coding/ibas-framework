package bobas.businessobjectscommon.demo.test;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.ISort;
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.demo.bo.materials.IMaterials;
import org.colorcoding.ibas.demo.bo.materials.Materials;
import org.colorcoding.ibas.demo.bo.salesorder.ISalesOrderItem;
import org.colorcoding.ibas.demo.bo.salesorder.SalesOrder;
import org.colorcoding.ibas.demo.repository.BORepositoryTrainingTesting;

import junit.framework.TestCase;

public class TestMultitask extends TestCase {

	public void testSaveBO() throws Exception {
		try (BORepositoryTrainingTesting boRepository = new BORepositoryTrainingTesting()) {
			boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
			// 检查物料是否存在
			IOperationResult<IMaterials> opRsltMM;
			Criteria criteria = new Criteria();
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(Materials.PROPERTY_ITEMCODE.getName());
			IMaterials materials = new Materials();
			materials.setItemCode("T800;");
			materials.setItemDescription("终结者机器人-T800");
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
			}

			materials = new Materials();
			materials.setItemCode("S001");
			materials.setItemDescription("绝地武士-剑");
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
			}

			materials = new Materials();
			materials.setItemCode("S003");
			materials.setItemDescription("绝地武士-头盔");
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
			}

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
			order.delete();
			operationResult = boRepository.saveSalesOrder(order);
			System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
					operationResult.getMessage(), operationResult.getResultObjects().size()));
			assertEquals(operationResult.getResultCode(), 0);
		}
	}

	public void testFetchBO() throws Exception {
		try (BORepositoryTrainingTesting boRepository = new BORepositoryTrainingTesting()) {
			boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
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
			condition.setValue(emDocumentStatus.RELEASED);
			condition.setRelationship(ConditionRelationship.OR);
			// (CardCode != ''')
			condition = criteria.getConditions().create();
			condition.setAlias(SalesOrder.PROPERTY_CUSTOMERCODE.getName());
			condition.setValue("'C");
			condition.setOperation(ConditionOperation.NOT_EQUAL);
			condition.setRelationship(ConditionRelationship.AND);
			// ORDER BY "DocEntry" DESC, "CardCode" ASC
			ISort sort = criteria.getSorts().create();
			sort.setAlias(SalesOrder.PROPERTY_DOCENTRY.getName());
			sort.setSortType(SortType.DESCENDING);
			sort = criteria.getSorts().create();
			sort.setAlias(SalesOrder.PROPERTY_CUSTOMERCODE.getName());
			sort.setSortType(SortType.ASCENDING);

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

	static boolean FLAG_STOP = false;

	public void testExtremeTask() {
		try {
			for (int i = 0; i < 10; i++) {
				Thread ts = new Thread() {
					@Override
					public void run() {
						TestMultitask test = new TestMultitask();
						while (!FLAG_STOP) {
							try {
								test.testSaveBO();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				};
				ts.start();
				Thread tf = new Thread() {
					@Override
					public void run() {
						TestMultitask test = new TestMultitask();
						while (!FLAG_STOP) {
							try {
								test.testFetchBO();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				};
				tf.start();
			}
			Thread.sleep(180000);
			FLAG_STOP = true;
			System.gc();
			System.out.println("all done.");
			Thread.sleep(600000);// 继续等待，资源释放
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
