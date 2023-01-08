package com.db.easylearning.models;

public class PaymentHistoryModel {
    String transactionIdApi,planId,planName,startDate,planPrice, transactionIdRPay,planDuration,type,receive_wallet;

    public String getTransactionIdApi() {
        return transactionIdApi;
    }

    public void setTransactionIdApi(String transactionIdApi) {
        this.transactionIdApi = transactionIdApi;
    }

    public String getReceive_wallet() {
        return receive_wallet;
    }

    public void setReceive_wallet(String receive_wallet) {
        this.receive_wallet = receive_wallet;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getPlanPrice() {
        return planPrice;
    }

    public void setPlanPrice(String planPrice) {
        this.planPrice = planPrice;
    }

    public String getTransactionIdRPay() {
        return transactionIdRPay;
    }

    public void setTransactionIdRPay(String transactionIdRPay) {
        this.transactionIdRPay = transactionIdRPay;
    }

    public String getPlanDuration() {
        return planDuration;
    }

    public void setPlanDuration(String planDuration) {
        this.planDuration = planDuration;
    }
}
