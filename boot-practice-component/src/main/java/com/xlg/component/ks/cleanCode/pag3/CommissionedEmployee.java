package com.xlg.component.ks.cleanCode.pag3;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author wangqingwei
 * Created on 2022-04-18
 */
@Lazy
@Service
public class CommissionedEmployee extends EmployeeNew {
    @Override
    public EmployeeType getType() {
        return EmployeeType.COMMISSIONED;
    }

    @Override
    public boolean isPayDay() {
        System.out.println("测试通过");
        return false;
    }

    @Override
    public void calculatePay() {
        System.out.println("测试通过");
    }

    @Override
    public void deliverPay(Money pay) {

    }
}
