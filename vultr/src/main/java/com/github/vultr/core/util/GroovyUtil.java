package com.github.vultr.core.util;

import groovy.lang.GroovyShell;

import java.util.Map;

public class GroovyUtil {


    /**
     * 执行脚本
     *
     * @param var
     * @param script
     * @return
     */
    public static Object runScript(Map<String, Object> var, String script) {
        GroovyShell groovyShell = new GroovyShell();
        for (Map.Entry<String, Object> entry : var.entrySet()) {
            groovyShell.setVariable(entry.getKey(), entry.getValue());
        }
        return groovyShell.evaluate(script);
    }


}