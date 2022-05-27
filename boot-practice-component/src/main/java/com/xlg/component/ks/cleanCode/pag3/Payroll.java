package com.xlg.component.ks.cleanCode.pag3;

/**
 * @author wangqingwei
 * Created on 2022-04-18
 * 旧的方式: 有个好几个问题
 * 1. 违反单一职责原则, 比如这个函数就是计算啥, 为啥要根据不同给到type返回不同函数, 函数需要做到统一抽象层级.
 * 2. 新增一个类型, 需要修改代码
 * 3. 如果还有类似这样一个函数, isPayday(Employee e)
 */
public class Payroll {

    public Money calculatePay(Employee e) throws Exception {
        switch (e.getType()) {
            case COMMISSIONED:
                return calculateCommissionedPay(e);
            case HOURLY:
                return calculateHourlyPay(e);
            case SALARIED:
                return calculateSalariedPay(e);
            default:
                throw new Exception(String.valueOf(e.getType()));
        }
    }

    private Money calculateHourlyPay(Employee e) {
        return null;
    }

    private Money calculateSalariedPay(Employee e) {
        return null;
    }

    private Money calculateCommissionedPay(Employee e) {
        return null;
    }

}
