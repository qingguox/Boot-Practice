package com.xlg.component.ks.cleanCode.pag3;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author wangqingwei
 * Created on 2022-04-18
 */
@Lazy
@Service
public class SalariedEmployee extends EmployeeNew {
    @Override
    public EmployeeType getType() {
        return EmployeeType.SALARIED;
    }

    @Override
    public boolean isPayDay() {
        return false;
    }

    @Override
    public void calculatePay() {
    }

    @Override
    public void deliverPay(Money pay) {

    }
}
