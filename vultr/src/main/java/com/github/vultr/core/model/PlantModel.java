package com.github.vultr.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlantModel {
    private String id;
    private int disk;
    private int disk_count;
    private double vcpu_count;
    private double ram;
    private double bandwidth;
    private double monthly_cost;
    private String type;
    private List<String> locations;
}
