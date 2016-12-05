package org.colorcoding.ibas.bobas.db.mssql.test;

import java.math.RoundingMode;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.test.bo.IMaterials;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrderItem;
import org.colorcoding.ibas.bobas.test.bo.Materials;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;
import org.colorcoding.ibas.bobas.test.logics.MaterialsQuantityJournal;
import org.colorcoding.ibas.bobas.test.logics.PurchaseOrder;
import org.colorcoding.ibas.bobas.test.logics.PurchaseOrderItem;
import org.colorcoding.ibas.bobas.test.repository.BORepositoryTest;

import junit.framework.TestCase;

public class testBOLogics extends TestCase {

    public void testSalesOrderLogics() {

        BORepositoryTest boRepository = new BORepositoryTest();

        String sign = String.valueOf(DateTime.getNow().getTime());
        IMaterials material_1 = new Materials();
        material_1.setItemCode(String.format("A%s", sign));
        material_1.setItemDescription(String.format("CPU [%s]", sign));
        IOperationResult<?> operationResult = boRepository.saveMaterials(material_1);
        System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
                operationResult.getMessage(), operationResult.getResultObjects().size()));
        assertEquals(operationResult.getResultCode(), 0);
        sign = String.valueOf(DateTime.getNow().getTime());
        IMaterials material_2 = new Materials();
        material_2.setItemCode(String.format("A%s", sign));
        material_2.setItemDescription(String.format("CPU [%s]", sign));
        operationResult = boRepository.saveMaterials(material_2);
        System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
                operationResult.getMessage(), operationResult.getResultObjects().size()));
        assertEquals(operationResult.getResultCode(), 0);

        SalesOrder order = new SalesOrder();

        order.setCustomerCode("C00001");
        order.setDocumentStatus(emDocumentStatus.Released);
        order.setApprovalStatus(emApprovalStatus.Processing);
        ISalesOrderItem orderItem = order.getSalesOrderItems().create();
        orderItem.setItemCode(material_1.getItemCode());
        orderItem.setItemDescription(material_1.getItemDescription());
        orderItem.setQuantity(new Decimal(2));
        orderItem.setPrice(new Decimal(99.99));
        orderItem = order.getSalesOrderItems().create();
        orderItem.setItemCode(material_2.getItemCode());
        orderItem.setItemDescription(material_2.getItemDescription());
        orderItem.setQuantity(1);
        orderItem.setPrice(199.99);

        operationResult = boRepository.saveSalesOrder(order);
        System.out.println(String.format("code:%s message:%s results:%s", operationResult.getResultCode(),
                operationResult.getMessage(), operationResult.getResultObjects().size()));
        assertEquals(operationResult.getResultCode(), 0);

    }

    public void testMaterialsOrderQuantity() {
        // 创建物料数据
        IMaterials materials01 = new Materials();
        materials01.setItemCode(String.format("A%s", DateTime.getNow().getTime()));
        materials01.setItemDescription("CPU i7");
        IMaterials materials02 = new Materials();
        materials02.setItemCode(String.format("B%s", DateTime.getNow().getTime()));
        materials02.setItemDescription("Disk 5T");
        BORepositoryTest boRepository = new BORepositoryTest();
        boRepository.setUseCache(false);// 缓存会导致数据检索不到
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
        BORepositoryTest boRepository = new BORepositoryTest();
        boRepository.setUseCache(false);// 缓存会导致数据检索不到
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
        BORepositoryTest boRepository = new BORepositoryTest();
        boRepository.setUseCache(false);// 缓存会导致数据检索不到
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
        item01.setQuantity(33);// 测试点，数量大于0
        item01.setPrice(999999.99);
        System.out.println(String.format("line total %s", item01.getLineTotal()));
        assertEquals(item01.getLineTotal().equals(item01.getQuantity().multiply(item01.getPrice())), true);
        item01.setLineTotal(666.77);
        System.out.println(String.format("line price %s", item01.getPrice()));
        assertEquals(item01.getPrice().equals(item01.getLineTotal().divide(item01.getQuantity(), RoundingMode.CEILING)),
                true);// 注意四舍五入
        operationResult = boRepository.savePurchaseOrder(order);
        if (operationResult.getResultCode() != 0) {
            System.err.println(operationResult.getMessage());
        }
        assertEquals(operationResult.getMessage(), operationResult.getResultCode(), 0);
    }

}
