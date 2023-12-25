package com.github.vultr.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegionsModel {

    private String id;

    private String city;

    private String country;

    private String continent;

    private List<String> options;

}
