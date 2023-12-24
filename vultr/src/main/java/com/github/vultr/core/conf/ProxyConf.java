package com.github.vultr.core.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("proxy")
public class ProxyConf {
    private String host;
    private int port;
}
