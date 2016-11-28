package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalStepStatus;

/**
 * 审批流程步骤
 */
public abstract class ApprovalProcessStep implements IApprovalProcessStep {

    public ApprovalProcessStep() {

    }

    protected abstract void setId(int value);

    protected abstract void setStatus(emApprovalStepStatus value);

    protected abstract void setStartedTime(DateTime value);

    protected abstract void setFinishedTime(DateTime value);

    protected abstract void setJudgment(String value);

    @Override
    public String toString() {
        return String.format("{approval process step: %s %s}", this.getId(), this.getStatus());
    }

    /**
     * 恢复为初始状态
     */
    void restore() {
        this.setStatus(emApprovalStepStatus.Pending);
        this.setStartedTime(DateTime.getMaxValue());
        this.setFinishedTime(DateTime.getMaxValue());
        this.setJudgment("");
    }

    /**
     * 开始进入审批
     * 
     * @throws UnlogicalException
     */
    void start() throws UnlogicalException {
        if (this.getStatus() != emApprovalStepStatus.Pending) {
            throw new UnlogicalException();
        }
        this.setStartedTime(DateTime.getNow());
        this.setStatus(emApprovalStepStatus.Processing);
    }

    /**
     * 批准
     * 
     * @param judgment
     *            意见
     * @throws UnlogicalException
     */
    void approve(String judgment) throws UnlogicalException {
        if (this.getStatus() != emApprovalStepStatus.Processing) {
            throw new UnlogicalException();
        }
        this.setFinishedTime(DateTime.getNow());
        this.setStatus(emApprovalStepStatus.Approved);
        this.setJudgment(judgment);
    }

    /**
     * 拒绝
     * 
     * @param judgment
     *            意见
     * @throws UnlogicalException
     */
    void reject(String judgment) throws UnlogicalException {
        if (this.getStatus() != emApprovalStepStatus.Processing) {
            throw new UnlogicalException();
        }
        this.setFinishedTime(DateTime.getNow());
        this.setStatus(emApprovalStepStatus.Rejected);
        this.setJudgment(judgment);
    }

    /**
     * 重置为进行中
     * 
     * @throws UnlogicalException
     */
    void reset() throws UnlogicalException {
        if (this.getStatus() != emApprovalStepStatus.Approved && this.getStatus() != emApprovalStepStatus.Rejected) {
            throw new UnlogicalException();
        }
        this.setFinishedTime(DateTime.getMaxValue());
        this.setStatus(emApprovalStepStatus.Processing);
        this.setJudgment("");
    }

    /**
     * 跳过
     * 
     * @throws UnlogicalException
     */
    void skip() throws UnlogicalException {
        if (this.getStatus() != emApprovalStepStatus.Pending) {
            throw new UnlogicalException();
        }
        this.setStartedTime(DateTime.getNow());
        this.setFinishedTime(DateTime.getNow());
        this.setStatus(emApprovalStepStatus.Skipped);
        this.setJudgment("");
    }

}
