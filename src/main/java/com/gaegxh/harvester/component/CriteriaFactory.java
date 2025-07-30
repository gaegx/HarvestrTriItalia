package com.gaegxh.harvester.component;

import com.gaegxh.harvester.model.Criteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CriteriaFactory {
    private static final Logger logger = LoggerFactory.getLogger(CriteriaFactory.class);



    public Criteria create(String order,int offset, int limit) {
        logger.info("Создание объекта Criteria с вводом пользователя");
        return Criteria.builder()
                .order(order)
                .offset(offset)
                .limit(limit)
                .build();
    }
}