package com.github.vultr.core.helper;

import com.github.vultr.core.conf.ProxyConf;
import com.github.vultr.core.conf.VultrConf;
import com.github.vultr.core.util.HttpClient;
import com.github.vultr.core.util.JsonUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
public class VultrApiHelper {

    @Autowired
    private VultrConf vultrConf;

    @Autowired
    private ProxyConf proxyConf;


    /**
     * 刷新实例列表
     *
     * @return
     */
    public Map<String, Object> instances() {
        return get("v2/instances");
    }


    public Map<String, Object> plans() {
        return get("v2/plans");
    }

    public Map<String, Object> deleteInstances(String id) {
        return delete("v2/instances/%s".formatted(id));
    }


    private String url(String url) {
        String spaceName = "/";
        if (vultrConf.getHost().endsWith("/")) {
            spaceName = "";
        }
        return vultrConf.getHost().trim() + spaceName + url.trim();
    }


    private Map<String, Object> get(String uri) {
        String url = url(uri);
        HttpClient httpClient = createHttpClient();
        HttpClient.ResultBean resultBean = httpClient.ReadDocuments(url, "GET", null, Map.of("Authorization", "Bearer " + vultrConf.getApiKey().trim()));
        return resultToObject(resultBean);
    }

    private Map<String, Object> post(String uri, Object dataObject) {
        HttpClient httpClient = createHttpClient();
        byte[] bin = dataObject == null ? null : JsonUtil.toJson(dataObject).getBytes();
        HttpClient.ResultBean resultBean = httpClient.ReadDocuments(url(uri), "POST", bin, Map.of("Authorization", "Bearer " + vultrConf.getApiKey().trim()));
        return resultToObject(resultBean);
    }

    private Map<String, Object> delete(String uri) {
        HttpClient httpClient = createHttpClient();
        HttpClient.ResultBean resultBean = httpClient.ReadDocuments(url(uri), "DELETE", null, Map.of("Authorization", "Bearer " + vultrConf.getApiKey().trim()));
        return resultToObject(resultBean);
    }


    private HttpClient createHttpClient() {
        final HttpClient httpClient = new HttpClient();
        if (StringUtils.hasText(proxyConf.getHost())) {
            httpClient.setProxyHost(proxyConf.getHost());
            httpClient.setPorxyPort(proxyConf.getPort());
        }
        return httpClient;
    }

    @SneakyThrows
    private Map<String, Object> resultToObject(HttpClient.ResultBean resultBean) {
        String json = null;
        if (StringUtils.hasText(resultBean.getCharset())) {
            json = new String(resultBean.getData(), resultBean.getCharset());
        } else {
            json = new String(resultBean.getData());
        }
        if (StringUtils.hasText(json)) {
            return JsonUtil.toObject(json, Map.class);
        }
        return null;
    }


}
