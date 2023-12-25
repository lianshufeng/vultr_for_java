package com.github.vultr.core.type;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
public enum TableColumn {
    id("id"),
    区域("region"),
    ipv4("main_ip"),
    ipv6("v6_main_ip"),
    状态("status"),
    费用("plan"),
    时间("date_created"),
    ;

    @Getter
    private String columnName;


}
