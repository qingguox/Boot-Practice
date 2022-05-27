package com.xlg.component.ks.cleanCode.pag3;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;

/**
 * @author wangqingwei
 * Created on 2022-04-19
 */
@Lazy
@Service
public class EmployeeFactory {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeFactory.class);
    private final Map<EmployeeType, EmployeeNew> map = Maps.newConcurrentMap();

    @Autowired
    private EmployeeFactory(List<EmployeeNew> employeeNewList) throws Exception {
        logger.info("[EmployeeFactory] employeeNewList size = {}", employeeNewList.size());
        //1. 检查null
        employeeNewList = checkTypeNull(employeeNewList);
        //2. checkDuplicate
        checkDuplicate(employeeNewList);
        //3. put
        putEmployee(map, employeeNewList);
    }

    @Nonnull
    public EmployeeNew getByType(EmployeeType type) {
        return map.get(type);
    }

    @Nonnull
    public EmployeeNew getByTypeUnchecked(EmployeeType type) {
        return Optional.ofNullable(map.get(type)).orElseGet(() -> new EmployeeNew() {
            @Override
            public EmployeeType getType() {
                return null;
            }

            @Override
            public boolean isPayDay() {
                return false;
            }

            @Override
            public void calculatePay() throws Exception {
                throw new Exception("[EmployeeFactory] type [" + type + "] not EmployeeNew!");
            }

            @Override
            public void deliverPay(Money pay) {

            }
        });
    }


    private void putEmployee(Map<EmployeeType, EmployeeNew> map, List<EmployeeNew> employeeNewList) {
        for (EmployeeNew employeeNew : employeeNewList) {
            logger.info("[EmployeeFactory] put type : {}, employeeNew : {}", employeeNew.getType(), employeeNew);
            map.put(employeeNew.getType(), employeeNew);
        }
    }

    private void checkDuplicate(List<EmployeeNew> employeeNewList) throws Exception {
        if (CollectionUtils.isEmpty(employeeNewList)) {
            return;
        }

        ArrayListMultimap<EmployeeType, String> typeDuplicateMap = ArrayListMultimap.create();
        employeeNewList.stream().collect(Collectors.groupingBy(EmployeeNew::getType))
                .forEach((key, values) -> {
                    if (values.size() > 1) {
                        values.forEach(value -> typeDuplicateMap.put(key, value.getClass().getCanonicalName()));
                    }
                });
        if (typeDuplicateMap.size() > 0) {
            throw new Exception("[EmployeeFactory] single type has multi implement ====\r\n"
                    + typeDuplicateMap);
        }
    }

    private List<EmployeeNew> checkTypeNull(List<EmployeeNew> employeeNewList) {
        return employeeNewList.stream().filter(employeeNew -> {
            boolean isNullType = employeeNew.getType() == null;
            if (isNullType) {
                logger.error("[EmployeeFactory] null type, code : {}", employeeNew.getClass().getCanonicalName());
            }
            return !isNullType;
        }).collect(Collectors.toList());
    }
}
