package org.colorcoding.ibas.demo.test;

import java.util.Random;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.logic.common.BOInstanceLogService;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.demo.bo.materials.IMaterials;
import org.colorcoding.ibas.demo.bo.materials.Materials;
import org.colorcoding.ibas.demo.bo.salesorder.ISalesOrder;
import org.colorcoding.ibas.demo.bo.salesorder.ISalesOrderItem;
import org.colorcoding.ibas.demo.bo.salesorder.SalesOrder;
import org.colorcoding.ibas.demo.bo.user.IUser;
import org.colorcoding.ibas.demo.bo.user.User;
import org.colorcoding.ibas.demo.repository.BORepositoryTrainingTesting;

import junit.framework.TestCase;

/**
 * 数据仓库（Repository）功能测试
 *
 * 测试范围：
 * 1. 事务管理（开启/回滚，回滚后数据不可见）
 * 2. 自动关闭资源（try-with-resources）
 */
public class TestRepository extends TestCase {

	// ==================== 1. 事务管理 ====================

	/**
	 * 测试事务管理
	 * 覆盖：开启事务→保存物料→保存订单（含2000行子项）→回滚事务→查询验证数据不可见
	 */
	public void testTransaction() throws Exception {
		try (BORepositoryTrainingTesting boRepository = new BORepositoryTrainingTesting()) {
			boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());
			// 打开事务
			boRepository.beginTransaction();

			// 创建物料
			IMaterials materials = new Materials();
			materials.setItemCode(Strings.format("T-%s", DateTimes.now().toString("yyyyMMddhhmm")));
			materials.setItemCode("测试" + materials.getItemCode().substring(2));
			IOperationResult<IMaterials> opRsltMM = boRepository.saveMaterials(materials);
			if (opRsltMM.getError() != null) {
				throw opRsltMM.getError();
			}
			opRsltMM = boRepository.fetchMaterials(materials.getCriteria());
			if (opRsltMM.getError() != null) {
				throw opRsltMM.getError();
			}
			materials = opRsltMM.getResultObjects().firstOrDefault();
			assertNotNull("not saved materials.", materials);
			System.out.println();
			System.out.println(Strings.format("+++++++++++++ new bo: %s +++++++++++++", materials.toString()));
			System.out.println();

			// 创建订单（含2000行子项）
			ISalesOrder salesOrder = new SalesOrder();
			salesOrder.setCustomerCode("C0001");
			ISalesOrderItem item = salesOrder.getSalesOrderItems().create();
			item.setItemCode(materials.getItemCode());
			item.setQuantity(Decimals.valueOf(10));
			item.setPrice(Decimals.valueOf(99));
			Random random = new Random();
			for (int i = 0; i < 1999; i++) {
				item = salesOrder.getSalesOrderItems().create();
				item.setItemCode(materials.getItemCode());
				item.setQuantity(Decimals.valueOf(random.nextInt(199)));
				item.setPrice(Decimals.valueOf(random.nextDouble() * 10));
			}
			DateTime beginTime = DateTimes.now();
			IOperationResult<ISalesOrder> opRsltOD = boRepository.saveSalesOrder(salesOrder);
			DateTime endTime = DateTimes.now();
			if (opRsltOD.getError() != null) {
				throw opRsltOD.getError();
			}
			opRsltOD = boRepository.fetchSalesOrder(salesOrder.getCriteria());
			if (opRsltOD.getError() != null) {
				throw opRsltOD.getError();
			}
			salesOrder = opRsltOD.getResultObjects().firstOrDefault();
			assertNotNull("not saved salesOrder.", salesOrder);
			System.out.println();
			System.out.println(
					Strings.format("+++++++++++++ new bo: %s ,line %s time %s +++++++++++++", salesOrder.toString(),
							salesOrder.getSalesOrderItems().size(), endTime.getTime() - beginTime.getTime()));
			System.out.println();

			// 回滚事务，重新查询应查不到
			boRepository.rollbackTransaction();
			opRsltMM = boRepository.fetchMaterials(materials.getCriteria());
			if (opRsltMM.getError() != null) {
				throw opRsltMM.getError();
			}
			materials = opRsltMM.getResultObjects().firstOrDefault();
			assertNull("rollback materials.", materials);
			opRsltOD = boRepository.fetchSalesOrder(salesOrder.getCriteria());
			if (opRsltOD.getError() != null) {
				throw opRsltOD.getError();
			}
			salesOrder = opRsltOD.getResultObjects().firstOrDefault();
			assertNull("rollback salesOrder.", salesOrder);
		}
	}

	// ==================== 2. 自动关闭资源 ====================

	/**
	 * 测试自动关闭资源（try-with-resources）
	 * 覆盖：使用try-with-resources管理BORepository，保存并查询用户
	 */
	public void testAutoClose() throws Exception {
		// 打开日志
		BOInstanceLogService.BO_LOGST_SETTING.put(User.BUSINESS_OBJECT_CODE, true);

		try (BORepositoryTrainingTesting boRepository = new BORepositoryTrainingTesting()) {
			boRepository.setUserToken(OrganizationFactory.SYSTEM_USER.getToken());

			IUser user = new User();
			user.setUserCode(Strings.format("U-%s", DateTimes.now().toString("ddhhmm")));
			user.setUserName(Strings.format("用户'%s'", DateTimes.now().toString("ddhhmm")));
			IOperationResult<IUser> opRsltUS = boRepository.saveUser(user);
			if (opRsltUS.getError() != null) {
				throw opRsltUS.getError();
			}
			user = opRsltUS.getResultObjects().firstOrDefault();
			assertNotNull("not saved user.", user);
			System.out.println();
			System.out.println(Strings.format("+++++++++++++ new bo: %s +++++++++++++", user.toString()));
			System.out.println();
			opRsltUS = boRepository.fetchUser(new Criteria());
			if (opRsltUS.getError() != null) {
				throw opRsltUS.getError();
			}
			System.out.println();
			for (IUser item : opRsltUS.getResultObjects()) {
				System.out.println(Strings.format("+++++++++++++ get bo: %s %s +++++++++++++", item.getUserCode(),
						item.getUserName()));
			}
			System.out.println();
		}
	}
}
