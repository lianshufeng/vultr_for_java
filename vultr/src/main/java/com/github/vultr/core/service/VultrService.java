package com.github.vultr.core.service;

import com.github.vultr.core.helper.VultrApiHelper;
import com.github.vultr.core.model.PlantModel;
import com.github.vultr.core.util.GroovyUtil;
import com.github.vultr.core.util.JsonUtil;
import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

@Service
@Log
public class VultrService {

    @Autowired
    @Delegate
    private VultrApiHelper vultrApiHelper;

    @Getter
    private Map<String, PlantModel> plans = new HashMap<>();

    @Autowired
    private void initPlans(ApplicationContext applicationContext) {
        log.info("开始刷新计划列表 ---> ");
        var ret = this.vultrApiHelper.plans();
        plans.clear();
        int size = (int) GroovyUtil.runScript(ret, "plans.size()");
        for (int i = 0; i < size; i++) {
            Map<String, Object> item = (Map<String, Object>) GroovyUtil.runScript(ret, "plans[" + i + "]");
            PlantModel plantModel = new PlantModel();
            BeanMap.create(plantModel).putAll(item);
            plans.put(plantModel.getId(), plantModel);
        }
        log.info("刷新计划完成 : " + plans.size());
    }


    public Double getHoursCost(String plantId) {
        PlantModel plantModel = plans.get(plantId);
        if (plantModel == null) {
            return null;
        }
        BigDecimal roundedNumber = new BigDecimal(plantModel.getMonthly_cost() / 30 / 24).setScale(2, BigDecimal.ROUND_HALF_UP);
        return roundedNumber.doubleValue();
    }


}
