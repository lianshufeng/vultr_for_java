package com.github.vultr.core.conf;

import com.github.vultr.core.model.CreateInstancesPlant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("vultr")
public class VultrConf {

    private String host = "https://api.vultr.com/";

    private String apiKey;

    //创建实例计划
    private CreateInstancesPlant createInstancesPlant;


}
