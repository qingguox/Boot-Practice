package com.xlg.component.ks.jexl;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author wangqingwei
 * Created on 2022-05-27
 */
@Lazy
@Service
public class JexlService {

    private static final String DEFAULT_STRING = Strings.EMPTY;
    private static final JexlEngine ENGINE = new JexlBuilder().create();

    public Object convertExpression(final String ruleExpression, final Map<String, Object> paramMap) {
        JexlExpression expression = ENGINE.createExpression(ruleExpression);
        JexlContext context = new MapContext(paramMap);
        return Optional.ofNullable(expression.evaluate(context)).orElse(DEFAULT_STRING);
    }
}
