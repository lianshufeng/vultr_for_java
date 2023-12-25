package com.github.vultr.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建实例计划
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateInstancesPlant {

    private String[] city;

    private String[] country;

    private String[] continent;

    //每个月最低的价格
    private Double minMonthlyCost;

    //每个月最高的价格
    private Double maxMonthlyCost;

    //快照id
    private String snapshot_id;

    //是否备份
    private boolean backups;

}
