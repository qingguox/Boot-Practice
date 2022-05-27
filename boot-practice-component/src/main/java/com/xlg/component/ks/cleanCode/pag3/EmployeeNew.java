package com.xlg.component.ks.cleanCode.pag3;

/**
 * @author wangqingwei
 * Created on 2022-04-18
 */
public abstract class EmployeeNew {
    public abstract EmployeeType getType();
    public abstract boolean isPayDay();
    public abstract void calculatePay() throws Exception;
    public abstract void deliverPay(Money pay);
}
