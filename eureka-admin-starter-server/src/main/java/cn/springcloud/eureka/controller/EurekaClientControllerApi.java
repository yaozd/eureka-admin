package cn.springcloud.eureka.controller;

import cn.springcloud.eureka.ResultMap;
import cn.springcloud.eureka.http.HttpUtil;
import com.google.common.base.Preconditions;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("api/eureka")
public class EurekaClientControllerApi {
    @Resource
    private EurekaClient eurekaClient;
    /**
     * @description 界面请求转到第三方服务进行状态变更
     */
    @RequestMapping(value = "status/{appName}", method = RequestMethod.POST)
    public ResultMap status(@PathVariable String appName, String instanceId, String status){
        Preconditions.checkNotNull(instanceId, "Illegal Argument passed: instanceId parameter is Null.");
        Preconditions.checkNotNull(status, "Illegal Argument passed: status parameter is Null.");
        Application application = eurekaClient.getApplication(appName);
        InstanceInfo instanceInfo = application.getByInstanceId(instanceId);
        InstanceInfo.InstanceStatus instanceStatus=InstanceInfo.InstanceStatus.toEnum(status);
        setInstanceStatus(instanceInfo,instanceStatus);
        return ResultMap.buildSuccess();
    }

    private void setInstanceStatus(InstanceInfo instanceInfo, InstanceInfo.InstanceStatus instanceStatus) {
        String eurekaServerUrl= eurekaClient.getEurekaClientConfig().getEurekaServerServiceUrls(null).get(0);
        eurekaServerUrl=StringUtils.removeEnd(eurekaServerUrl,"/");
        String appName=instanceInfo.getAppName();
        String instanceId=instanceInfo.getInstanceId();
        if(InstanceInfo.InstanceStatus.OUT_OF_SERVICE.equals(instanceStatus)){
            String url="%s/apps/%s/%s/status?value=OUT_OF_SERVICE";
            url=String.format(url,eurekaServerUrl,appName,instanceId);
            log.info("OUT_OF_SERVICE url:"+url);
            HttpUtil.put(url, null);
            return;
        }
        if(InstanceInfo.InstanceStatus.UP.equals(instanceStatus)){
            String url="%s/apps/%s/%s/status?value=UP";
            url=String.format(url,eurekaServerUrl,appName,instanceId);
            log.info("UP url:"+url);
            HttpUtil.delete(url, null);
            return;
        }
    }
}
