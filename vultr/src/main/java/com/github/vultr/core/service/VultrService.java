package com.github.vultr.core.service;

import com.github.vultr.core.conf.VultrConf;
import com.github.vultr.core.helper.VultrApiHelper;
import com.github.vultr.core.model.CreateInstancesModel;
import com.github.vultr.core.model.CreateInstancesPlant;
import com.github.vultr.core.model.PlantModel;
import com.github.vultr.core.model.RegionsModel;
import com.github.vultr.core.util.GroovyUtil;
import com.github.vultr.core.util.JsonUtil;
import com.github.vultr.core.util.RandomUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Log
@Service
public class VultrService {

    @Autowired
    @Delegate
    private VultrApiHelper vultrApiHelper;

    @Autowired
    private VultrConf vultrConf;

    private PlantContainer plantContainer = new PlantContainer();

    private RegionsContainer regionsContainer = new RegionsContainer();

    //满足创建实例的计划
    private List<PlantModel> instancesPlants = new ArrayList<>();


    @Autowired
    private void init(ApplicationContext applicationContext) {
        initPlans();
        initRegions();
//        initInstancesPlants();
    }

    private void initPlans() {
        var ret = this.vultrApiHelper.plans();

        plantContainer.plansMap = new HashMap<>();
        var plansOrders = new ArrayList<PlantModel>();
        int size = (int) GroovyUtil.runScript(ret, "plans.size()");
        for (int i = 0; i < size; i++) {
            Map<String, Object> item = (Map<String, Object>) GroovyUtil.runScript(ret, "plans[" + i + "]");
            PlantModel plantModel = new PlantModel();
            BeanMap.create(plantModel).putAll(item);

            plantContainer.plansMap.put(plantModel.getId(), plantModel);
            plansOrders.add(plantModel);
        }

        plantContainer.plansOrders = plansOrders.stream().sorted((it1, it2) -> {
            Double cost = it1.getMonthly_cost() - it2.getMonthly_cost();
            return cost.intValue();
        }).toList();
        log.info("刷新计划完成 : " + plantContainer.plansOrders.size());
    }

    private void initRegions() {
        var ret = vultrApiHelper.regions();
        List<Map<String, Object>> items = (List<Map<String, Object>>) ret.get("regions");

        //所有项
        regionsContainer.items = items.stream().map((it) -> {
            RegionsModel regionsModel = new RegionsModel();
            BeanMap.create(regionsModel).putAll(it);
            return regionsModel;
        }).toList();

        // 城市
        regionsContainer.city = regionsContainer.items.stream().collect(Collectors.groupingBy(RegionsModel::getCity, Collectors.toList()));
        // 国家
        regionsContainer.country = regionsContainer.items.stream().collect(Collectors.groupingBy(RegionsModel::getCountry, Collectors.toList()));
        //id
        regionsContainer.id = new HashMap<>();
        regionsContainer.items.forEach(it -> {
            regionsContainer.id.put(it.getId(), it);
        });


        log.info("刷新区域 ---> : " + regionsContainer.items.size());
    }


    public Double getHoursCost(String plantId) {
        PlantModel plantModel = plantContainer.plansMap.get(plantId);
        if (plantModel == null) {
            return null;
        }
        return plantModel.getMonthly_cost() / 30 / 24;
    }


    /**
     * 随机抽取一个满足条件可以创建实例的计划
     *
     * @return
     */
    public CreateInstancesModel getCreateInstancesPlant() {
        final CreateInstancesPlant createInstancesPlant = this.vultrConf.getCreateInstancesPlant();
        if (createInstancesPlant == null) {
            return null;
        }

        final AtomicReference<List<PlantModel>> instancesPlants = new AtomicReference<>(this.plantContainer.plansOrders);
        //过滤月资费，最低
        Optional.ofNullable(createInstancesPlant.getMinMonthlyCost()).ifPresent((min) -> {
            instancesPlants.set(instancesPlants.get().stream().filter(it -> {
                return it.getMonthly_cost() >= min;
            }).toList());
        });
        //过滤月资费,最高
        Optional.ofNullable(createInstancesPlant.getMaxMonthlyCost()).ifPresent((max) -> {
            instancesPlants.set(instancesPlants.get().stream().filter(it -> {
                return it.getMonthly_cost() <= max;
            }).toList());
        });


        final String country = RandomUtil.random(createInstancesPlant.getCountry());
        final String city = RandomUtil.random(createInstancesPlant.getCity());
        final String continent = RandomUtil.random(createInstancesPlant.getContinent());
        final CreateInstancesModel createInstancesModel = new CreateInstancesModel();

        AtomicBoolean pass = new AtomicBoolean(false);
        for (PlantModel instancesPlant : instancesPlants.get()) {
            List<RegionsModel> locations = new ArrayList<>() {{
                addAll(instancesPlant.getLocations().stream().map(it -> regionsContainer.getId().get(it)).filter(it -> it != null).toList());
            }};
            // 随机地址
            Collections.shuffle(locations);


            for (RegionsModel regionsModel : locations) {
                if (!filterCreateInstancesPlant(regionsModel, "city", city)) {
                    continue;
                }
                if (!filterCreateInstancesPlant(regionsModel, "country", country)) {
                    continue;
                }
                if (!filterCreateInstancesPlant(regionsModel, "continent", continent)) {
                    continue;
                }
                createInstancesModel.setPlan(instancesPlant.getId());
                createInstancesModel.setRegion(regionsModel.getId());
                pass.set(true);
                break;
            }
            if (pass.get()) {
                break;
            }
        }


        createInstancesModel.setBackups(createInstancesPlant.isBackups() ? null : "disabled");
        createInstancesModel.setSnapshot_id(createInstancesPlant.getSnapshot_id());

        return createInstancesModel;
    }


    private boolean filterCreateInstancesPlant(RegionsModel regionsModel, String name, String value) {
        if (!StringUtils.hasText(value)) {
            return true;
        }
        Map<String, Object> map = BeanMap.create(regionsModel);
        final Object ret = map.get(name);
        if (ret == null) {
            return true;
        }
        return String.valueOf(ret).equalsIgnoreCase(value);
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private class PlantContainer {

        @Getter
        private Map<String, PlantModel> plansMap;

        @Getter
        private List<PlantModel> plansOrders;


    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private class RegionsContainer {

        @Getter
        private Map<String, List<RegionsModel>> city;

        @Getter
        private Map<String, List<RegionsModel>> country;

        @Getter
        private Map<String, RegionsModel> id;


        @Getter
        private List<RegionsModel> items;


    }

}
