package org.colorcoding.ibas.bobas.test.logics;

import java.math.RoundingMode;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.repository.BORepository4File;
import org.colorcoding.ibas.bobas.repository.IBORepository4File;
import org.colorcoding.ibas.bobas.test.bo.IMaterials;
import org.colorcoding.ibas.bobas.test.bo.Materials;
import org.colorcoding.ibas.bobas.test.repository.BORepositoryTest;

import junit.framework.TestCase;

public class testLogics extends TestCase {

    public void testBOStatusLogic() {
        // 测试BO状态对业务逻辑执行的影响
        // 创建物料数据
        IMaterials materials01 = new Materials();
        materials01.setItemCode(String.format("A%s", DateTime.getNow().getTime()));
        materials01.setItemDescription("CPU i7");
        IMaterials materials02 = new Materials();
        materials02.setItemCode(String.format("B%s", DateTime.getNow().getTime()));
        materials02.setItemDescription("Disk 5T");
        // 保存物料到文件系统
        IBORepository4File fileRepository = new BORepository4File();
        // fileRepository.setRepositoryFolder("D:\\WorkTemp\\borepository");
        BORepositoryTest boRepository = new BORepositoryTest();
        boRepository.setUseCache(false);// 缓存会导致数据检索不到
        boRepository.setRepository(fileRepository);
        IOperationResult<?> operationResult;

        operationResult = boRepository.saveMaterials(materials01);
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        operationResult = boRepository.saveMaterials(materials02);
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);

        // 创建采购订单
        PurchaseOrder order = new PurchaseOrder();
        order.setApprovalStatus(emApprovalStatus.Processing);// 父项审批中，子项逻辑不执行
        order.setDocumentStatus(emDocumentStatus.Planned);// 父项计划状态，子项逻辑不执行
        order.setCustomerCode("C00001");
        order.setCustomerName("宇宙无敌影业");
        PurchaseOrderItem item01 = order.getPurchaseOrderItems().create();
        item01.setItemCode(materials01.getItemCode());
        item01.setItemDescription(materials01.getItemDescription());
        item01.setQuantity(1);
        item01.setPrice(999999.99);
        // item01.setLineStatus(emDocumentStatus.Planned);// 计划状态，逻辑不执行
        // item01.setCanceled(emYesNo.Yes);// 取消的，逻辑不执行
        PurchaseOrderItem item02 = order.getPurchaseOrderItems().create();
        item02.setItemCode(materials02.getItemCode());
        item02.setItemDescription(materials02.getItemDescription());
        item02.setQuantity(2);
        item02.setPrice(99.00);
        PurchaseOrderItem item03 = order.getPurchaseOrderItems().create();
        item03.setItemCode(materials02.getItemCode());
        item03.setItemDescription(materials02.getItemDescription());
        item03.setQuantity(9);
        item03.setPrice(99.00);

        operationResult = boRepository.savePurchaseOrder(order);
        if (operationResult.getResultCode() != 0) {
            System.err.println(operationResult.getMessage());
        }
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);

        operationResult = boRepository.fetchMaterials(materials01.getCriteria());
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        IMaterials materials01s = (IMaterials) operationResult.getResultObjects().firstOrDefault();
        // 检索的物料是否一致
        assertEquals("materials not same.", materials01.getItemCode(), materials01s.getItemCode());
        // 订购数量不增加，单据状态没有执行业务逻辑
        assertEquals(String.format("wrong matrials [%s] order quantity.", materials01.getItemCode()),
                materials01.getOnOrder().floatValue(), materials01s.getOnOrder().floatValue());

        // 测试状态改变后，业务逻辑执行状况
        operationResult = boRepository.fetchPurchaseOrder(order.getCriteria());
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        order = (PurchaseOrder) operationResult.getResultObjects().firstOrDefault();
        order.setApprovalStatus(emApprovalStatus.Approved);
        order.setDocumentStatus(emDocumentStatus.Planned);// 父项计划状态，子项逻辑不执行

        operationResult = boRepository.savePurchaseOrder(order);
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        operationResult = boRepository.fetchMaterials(materials01.getCriteria());
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        materials01s = (IMaterials) operationResult.getResultObjects().firstOrDefault();
        // 检索的物料是否一致
        assertEquals("materials not same.", materials01.getItemCode(), materials01s.getItemCode());
        // 订购数量不增加，单据状态没有执行业务逻辑
        assertEquals(String.format("wrong matrials [%s] order quantity.", materials01.getItemCode()),
                materials01.getOnOrder().floatValue(), materials01s.getOnOrder().floatValue());

        operationResult = boRepository.fetchPurchaseOrder(order.getCriteria());
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        order = (PurchaseOrder) operationResult.getResultObjects().firstOrDefault();
        order.setApprovalStatus(emApprovalStatus.Approved);
        order.setDocumentStatus(emDocumentStatus.Released);
        operationResult = boRepository.savePurchaseOrder(order);
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        operationResult = boRepository.fetchMaterials(materials01.getCriteria());
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        materials01s = (IMaterials) operationResult.getResultObjects().firstOrDefault();
        // 检索的物料是否一致
        assertEquals("materials not same.", materials01.getItemCode(), materials01s.getItemCode());
        // 订购数量增加
        assertEquals(String.format("wrong matrials [%s] order quantity.", materials01.getItemCode()),
                materials01.getOnOrder().add(item01.getQuantity()).floatValue(),
                materials01s.getOnOrder().floatValue());

    }

    public void testMaterialsOrderQuantity() {
        // 创建物料数据
        IMaterials materials01 = new Materials();
        materials01.setItemCode(String.format("A%s", DateTime.getNow().getTime()));
        materials01.setItemDescription("CPU i7");
        IMaterials materials02 = new Materials();
        materials02.setItemCode(String.format("B%s", DateTime.getNow().getTime()));
        materials02.setItemDescription("Disk 5T");
        // 保存物料到文件系统
        IBORepository4File fileRepository = new BORepository4File();
        // fileRepository.setRepositoryFolder("D:\\WorkTemp\\borepository");
        BORepositoryTest boRepository = new BORepositoryTest();
        boRepository.setUseCache(false);// 缓存会导致数据检索不到
        boRepository.setRepository(fileRepository);
        IOperationResult<?> operationResult;

        operationResult = boRepository.saveMaterials(materials01);
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        operationResult = boRepository.saveMaterials(materials02);
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);

        // 创建采购订单
        PurchaseOrder order = new PurchaseOrder();
        order.setCustomerCode("C00001");
        order.setCustomerName("宇宙无敌影业");
        PurchaseOrderItem item01 = order.getPurchaseOrderItems().create();
        item01.setItemCode(materials01.getItemCode());
        item01.setItemDescription(materials01.getItemDescription());
        item01.setQuantity(1);
        item01.setPrice(999999.99);
        PurchaseOrderItem item02 = order.getPurchaseOrderItems().create();
        item02.setItemCode(materials02.getItemCode());
        item02.setItemDescription(materials02.getItemDescription());
        item02.setQuantity(2);
        item02.setPrice(99.00);
        PurchaseOrderItem item03 = order.getPurchaseOrderItems().create();
        item03.setItemCode(materials02.getItemCode());
        item03.setItemDescription(materials02.getItemDescription());
        item03.setQuantity(9);
        item03.setPrice(99.00);

        operationResult = boRepository.savePurchaseOrder(order);
        if (operationResult.getResultCode() != 0) {
            System.err.println(operationResult.getMessage());
        }
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);

        operationResult = boRepository.fetchMaterials(materials01.getCriteria());
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        IMaterials materials01s = (IMaterials) operationResult.getResultObjects().firstOrDefault();
        // 检索的物料是否一致
        assertEquals("materials not same.", materials01.getItemCode(), materials01s.getItemCode());
        // 订购数量是否增加
        assertEquals(String.format("wrong matrials [%s] order quantity.", materials01.getItemCode()),
                materials01.getOnOrder().add(item01.getQuantity()).floatValue(),
                materials01s.getOnOrder().floatValue());
        operationResult = boRepository.fetchMaterials(materials02.getCriteria());
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        IMaterials materials02s = (IMaterials) operationResult.getResultObjects().firstOrDefault();
        assertEquals("materials not same.", materials02.getItemCode(), materials02s.getItemCode());
        assertEquals(String.format("wrong matrials [%s] order quantity.", materials02.getItemCode()),
                materials02.getOnOrder().add(item02.getQuantity().add(item03.getQuantity())).floatValue(),
                materials02s.getOnOrder().floatValue());

        // 修改数量
        item01.setQuantity(20);
        operationResult = boRepository.savePurchaseOrder(order);
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);

        operationResult = boRepository.fetchMaterials(materials02.getCriteria());
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        materials02s = (IMaterials) operationResult.getResultObjects().firstOrDefault();
        assertEquals("materials not same.", materials02.getItemCode(), materials02s.getItemCode());
        assertEquals(String.format("wrong matrials [%s] order quantity.", materials02.getItemCode()),
                materials02.getOnOrder().add(item02.getQuantity().add(item03.getQuantity())).floatValue(),
                materials02s.getOnOrder().floatValue());

        operationResult = boRepository.fetchMaterials(materials01.getCriteria());
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        materials01s = (IMaterials) operationResult.getResultObjects().firstOrDefault();
        assertEquals(String.format("wrong matrials [%s] order quantity.", materials01.getItemCode()),
                materials01.getOnOrder().add(item01.getQuantity()).floatValue(),
                materials01s.getOnOrder().floatValue());

        // 删除数据
        item03.delete();
        operationResult = boRepository.savePurchaseOrder(order);
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);

        operationResult = boRepository.fetchMaterials(materials02.getCriteria());
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        materials02s = (IMaterials) operationResult.getResultObjects().firstOrDefault();
        assertEquals("materials not same.", materials02.getItemCode(), materials02s.getItemCode());
        assertEquals(String.format("wrong matrials [%s] order quantity.", materials02.getItemCode()),
                materials02.getOnOrder().add(item02.getQuantity()).floatValue(),
                materials02s.getOnOrder().floatValue());

    }

    public void testMaterialsJournal() {
        // 创建物料数据
        IMaterials materials01 = new Materials();
        materials01.setItemCode(String.format("A%s", DateTime.getNow().getTime()));
        materials01.setItemDescription("CPU i7");
        IMaterials materials02 = new Materials();
        materials02.setItemCode(String.format("B%s", DateTime.getNow().getTime()));
        materials02.setItemDescription("Disk 5T");
        // 保存物料到文件系统
        IBORepository4File fileRepository = new BORepository4File();
        BORepositoryTest boRepository = new BORepositoryTest();
        boRepository.setUseCache(false);// 缓存会导致数据检索不到
        boRepository.setRepository(fileRepository);
        IOperationResult<?> operationResult;

        operationResult = boRepository.saveMaterials(materials01);
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        operationResult = boRepository.saveMaterials(materials02);
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);

        // 创建采购订单
        PurchaseOrder order = new PurchaseOrder();
        order.setCustomerCode("C00001");
        order.setCustomerName("宇宙无敌影业");
        PurchaseOrderItem item01 = order.getPurchaseOrderItems().create();
        item01.setItemCode(materials01.getItemCode());
        item01.setItemDescription(materials01.getItemDescription());
        item01.setQuantity(1);
        item01.setPrice(999999.99);
        PurchaseOrderItem item02 = order.getPurchaseOrderItems().create();
        item02.setItemCode(materials02.getItemCode());
        item02.setItemDescription(materials02.getItemDescription());
        item02.setQuantity(2);
        item02.setPrice(99.00);
        PurchaseOrderItem item03 = order.getPurchaseOrderItems().create();
        item03.setItemCode(materials02.getItemCode());
        item03.setItemDescription(materials02.getItemDescription());
        item03.setQuantity(9);
        item03.setPrice(99.00);

        operationResult = boRepository.savePurchaseOrder(order);
        if (operationResult.getResultCode() != 0) {
            System.err.println(operationResult.getMessage());
        }
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);

        operationResult = boRepository.fetchMaterials(materials01.getCriteria());
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        IMaterials materials01s = (IMaterials) operationResult.getResultObjects().firstOrDefault();
        assertEquals("materials not same.", materials01.getItemCode(), materials01s.getItemCode());
        assertEquals(String.format("wrong matrials [%s] order quantity.", materials01.getItemCode()),
                materials01.getOnOrder().add(item01.getQuantity()).floatValue(),
                materials01s.getOnOrder().floatValue());
        assertEquals(String.format("wrong matrials [%s] hand quantity.", materials01.getItemCode()),
                materials01.getOnHand().add(item01.getQuantity()).floatValue(), materials01s.getOnHand().floatValue());
        operationResult = boRepository.fetchMaterials(materials02.getCriteria());
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        IMaterials materials02s = (IMaterials) operationResult.getResultObjects().firstOrDefault();
        assertEquals("materials not same.", materials02.getItemCode(), materials02s.getItemCode());
        assertEquals(String.format("wrong matrials [%s] order quantity.", materials02.getItemCode()),
                materials02.getOnOrder().add(item02.getQuantity().add(item03.getQuantity())).floatValue(),
                materials02s.getOnOrder().floatValue());
        assertEquals(String.format("wrong matrials [%s] hand quantity.", materials02.getItemCode()),
                materials02.getOnHand().add(item02.getQuantity().add(item03.getQuantity())).floatValue(),
                materials02s.getOnHand().floatValue());

        ICriteria criteria = Criteria.create();
        ICondition condition = criteria.getConditions().create();
        condition.setAlias(MaterialsQuantityJournal.BaseDocumentTypeProperty.getName());
        condition.setCondVal(item01.getDocumentType());
        condition = criteria.getConditions().create();
        condition.setAlias(MaterialsQuantityJournal.BaseDocumentEntryProperty.getName());
        condition.setCondVal(item01.getDocumentEntry());
        condition = criteria.getConditions().create();
        condition.setAlias(MaterialsQuantityJournal.BaseDocumentLineIdProperty.getName());
        condition.setCondVal(item01.getDocumentLineId());
        operationResult = boRepository.fetchMaterialsQuantityJournal(criteria);
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        MaterialsQuantityJournal journal01 = (MaterialsQuantityJournal) operationResult.getResultObjects()
                .firstOrDefault();
        assertEquals("materials not same.", item01.getItemCode(), journal01.getItemCode());
        assertEquals(String.format("wrong matrials [%s] order quantity.", item01.getItemCode()),
                item01.getQuantity().floatValue(), journal01.getQuantity().floatValue());

        item01.setQuantity(20);
        item03.delete();
        operationResult = boRepository.savePurchaseOrder(order);
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);

        operationResult = boRepository.fetchMaterials(materials02.getCriteria());
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        materials02s = (IMaterials) operationResult.getResultObjects().firstOrDefault();
        assertEquals("materials not same.", materials02.getItemCode(), materials02s.getItemCode());
        assertEquals(String.format("wrong matrials [%s] order quantity.", materials02.getItemCode()),
                materials02.getOnOrder().add(item02.getQuantity()).floatValue(),
                materials02s.getOnOrder().floatValue());
        assertEquals(String.format("wrong matrials [%s] hand quantity.", materials02.getItemCode()),
                materials02.getOnHand().add(item02.getQuantity()).floatValue(), materials02s.getOnHand().floatValue());

        operationResult = boRepository.fetchMaterials(materials01.getCriteria());
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        materials01s = (IMaterials) operationResult.getResultObjects().firstOrDefault();
        assertEquals(String.format("wrong matrials [%s] order quantity.", materials01.getItemCode()),
                materials01.getOnOrder().add(item01.getQuantity()).floatValue(),
                materials01s.getOnOrder().floatValue());
        assertEquals(String.format("wrong matrials [%s] hand quantity.", materials01.getItemCode()),
                materials01.getOnHand().add(item01.getQuantity()).floatValue(), materials01s.getOnHand().floatValue());

        criteria = Criteria.create();
        condition = criteria.getConditions().create();
        condition.setAlias(MaterialsQuantityJournal.BaseDocumentTypeProperty.getName());
        condition.setCondVal(item03.getDocumentType());
        condition = criteria.getConditions().create();
        condition.setAlias(MaterialsQuantityJournal.BaseDocumentEntryProperty.getName());
        condition.setCondVal(item03.getDocumentEntry());
        condition = criteria.getConditions().create();
        condition.setAlias(MaterialsQuantityJournal.BaseDocumentLineIdProperty.getName());
        condition.setCondVal(item03.getDocumentLineId());
        operationResult = boRepository.fetchMaterialsQuantityJournal(criteria);
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        journal01 = (MaterialsQuantityJournal) operationResult.getResultObjects().firstOrDefault();
        assertNull(journal01);

    }

    public void testRules() {
        // 保存物料到文件系统
        IBORepository4File fileRepository = new BORepository4File();
        BORepositoryTest boRepository = new BORepositoryTest();
        boRepository.setUseCache(false);// 缓存会导致数据检索不到
        boRepository.setRepository(fileRepository);
        IOperationResult<?> operationResult;
        // 创建物料数据
        IMaterials materials01 = new Materials();
        materials01.setItemCode(String.format("A%s", DateTime.getNow().getTime()));
        materials01.setItemDescription("CPU i7");
        operationResult = boRepository.saveMaterials(materials01);
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
        // 创建采购订单
        PurchaseOrder order = new PurchaseOrder();
        order.setCustomerCode("C00001");// 测试点，不能为空
        order.setCustomerName("宇宙无敌影业");
        PurchaseOrderItem item01 = order.getPurchaseOrderItems().create();// 测试点，要求有元素
        item01.setItemCode(materials01.getItemCode());// 测试点，子项检查，要求值
        item01.setItemDescription(materials01.getItemDescription());
        item01.setQuantity(100);// 测试点，数量大于0
        item01.setPrice("999999.99");
        System.out.println(String.format("line 1 total %s", item01.getLineTotal()));
        assertEquals(item01.getLineTotal().equals(item01.getQuantity().multiply(item01.getPrice())), true);
        PurchaseOrderItem item02 = order.getPurchaseOrderItems().create();// 测试点，要求有元素
        item02.setItemCode(materials01.getItemCode());// 测试点，子项检查，要求值
        item02.setItemDescription(materials01.getItemDescription());
        item02.setQuantity(33.33);// 测试点，数量大于0
        item02.setLineTotal(10000);
        System.out.println(String.format("line 2 price %s", item02.getPrice()));
        assertEquals(item02.getPrice().equals(item02.getLineTotal().divide(item02.getQuantity(), RoundingMode.CEILING)),
                true);// 注意四舍五入
        System.out.println(String.format("line 2 total %s", item02.getLineTotal()));
        System.out.println(String.format("document total %s", order.getDocumentTotal()));
        operationResult = boRepository.savePurchaseOrder(order);
        if (operationResult.getResultCode() != 0) {
            System.err.println(operationResult.getMessage());
        }
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
    }
}
