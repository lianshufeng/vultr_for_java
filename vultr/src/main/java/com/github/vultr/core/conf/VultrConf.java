package com.github.vultr.core.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("vultr")
public class VultrConf {

    private String host = "https://api.vultr.com/";


    private String apiKey ;



}
