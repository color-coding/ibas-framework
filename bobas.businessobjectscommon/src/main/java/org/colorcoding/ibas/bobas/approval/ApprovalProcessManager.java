package org.colorcoding.ibas.bobas.approval;

import java.util.Iterator;

import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBODocumentLine;
import org.colorcoding.ibas.bobas.bo.IBOTagCanceled;
import org.colorcoding.ibas.bobas.bo.IBOTagDeleted;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

/**
 * 默认流程管理员
 * 
 * @author Niuren.Zhu
 *
 */
public abstract class ApprovalProcessManager implements IApprovalProcessManager {

    @Override
    public IApprovalProcess checkProcess(IApprovalData data) {
        if (data == null) {
            return null;
        }
        if (!data.isNew()) {
            // 不是新建查看是否存在审批
            IApprovalProcess aProcess = this.loadApprovalProcess(data.getIdentifiers());
            if (aProcess != null) {
                aProcess.setApprovalData(data);
                return aProcess;
            }
        }
        // 创建审批流程并尝试开始
        if (this.checkDataStatus(data)) {
            Iterator<IApprovalProcess> process = this.createApprovalProcess(data.getObjectCode());
            while (process != null && process.hasNext()) {
                IApprovalProcess aProcess = process.next();
                if (aProcess.start(data)) {
                    RuntimeLog.log(RuntimeLog.MSG_APPROVAL_PROCESS_STARTED, data, aProcess.getName());
                    return aProcess;// 审批流程开始
                }
            }
        }
        // 没有符合的审批流程
        if (data.getApprovalStatus() != emApprovalStatus.Unaffected) {
            // 重置数据状态
            data.setApprovalStatus(emApprovalStatus.Unaffected);
        }
        return null;
    }

    /**
     * 检查数据状态，是否进行审批流程
     * 
     * @param data
     * @return
     */
    protected boolean checkDataStatus(IApprovalData data) {
        if (data.isDeleted()) {
            return false;
        }
        if (data instanceof IBODocument) {
            // 单据类型
            IBODocument docData = (IBODocument) data;
            if (docData.getDocumentStatus() == emDocumentStatus.Planned) {
                // 计划状态
                return false;
            }
        }
        if (data instanceof IBODocumentLine) {
            // 单据行
            IBODocumentLine lineData = (IBODocumentLine) data;
            if (lineData.getLineStatus() == emDocumentStatus.Planned) {
                // 计划状态
                return false;
            }
        }
        if (data instanceof IBOTagDeleted) {
            // 引用数据，已标记删除的，不影响业务逻辑
            IBOTagDeleted refData = (IBOTagDeleted) data;
            if (refData.getDeleted() == emYesNo.Yes) {
                return false;
            }
        }
        if (data instanceof IBOTagCanceled) {
            // 引用数据，已标记取消的，不影响业务逻辑
            IBOTagCanceled refData = (IBOTagCanceled) data;
            if (refData.getCanceled() == emYesNo.Yes) {
                return false;
            }
        }
        return true;
    }

    /**
     * 创建审批流程
     * 
     * @param boCode
     *            业务对象编码
     * @return
     */
    protected abstract Iterator<IApprovalProcess> createApprovalProcess(String boCode);

    /**
     * 加载审批流程
     * 
     * @param boKey
     *            业务对象标记
     * @return
     */
    protected abstract IApprovalProcess loadApprovalProcess(String boKey);
}
