package cn.springcloud.eureka.controller;

import cn.springcloud.eureka.ResultMap;
import cn.springcloud.eureka.http.HttpUtil;
import cn.springcloud.eureka.utils.eurekaExt.EurekaUtil2;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("eureka")
public class EurekaClientController {

    @Resource
    private EurekaClient eurekaClient;

    /**
     * @description 获取服务数量和节点数量
     */
    @RequestMapping(value = "home", method = RequestMethod.GET)
    public ResultMap home() {
        List<Application> apps = eurekaClient.getApplications().getRegisteredApplications();
        int appCount = apps.size();
        int nodeCount = 0;
        int enableNodeCount = 0;
        for (Application app : apps) {
            nodeCount += app.getInstancesAsIsFromEureka().size();
            List<InstanceInfo> instances = app.getInstances();
            for (InstanceInfo instance : instances) {
                if (instance.getStatus().name().equals(InstanceStatus.UP.name())) {
                    enableNodeCount++;
                }
            }
        }
        return ResultMap.buildSuccess().put("appCount", appCount).put("nodeCount", nodeCount).put("enableNodeCount", enableNodeCount);
    }

    /**
     * @description 获取所有服务节点
     */
    @RequestMapping(value = "apps", method = RequestMethod.GET)
    public ResultMap apps() {
        List<Application> apps = eurekaClient.getApplications().getRegisteredApplications();
        Collections.sort(apps, new Comparator<Application>() {
            public int compare(Application l, Application r) {
                return l.getName().compareTo(r.getName());
            }
        });
        for (Application app : apps) {
            Collections.sort(app.getInstances(), new Comparator<InstanceInfo>() {
                public int compare(InstanceInfo l, InstanceInfo r) {
                    return l.getPort() - r.getPort();
                }
            });
        }
        return ResultMap.buildSuccess().put("list", apps);
    }

    /**
     * 刷新本地注册服务信息
     * --------------------------------------
     * 目前（2019-01-17）
     * Dalston版本不支持
     * Finchley版本可以正常使用
     * --------------------------------------
     *
     * @return
     */
    @RequestMapping("refreshRegistry")
    public String refreshRegistry() {
        EurekaUtil2.refreshRegistry();
        return "ok";
    }

    /**
     * @description 界面请求转到第三方服务进行状态变更
     */
    @RequestMapping(value = "status/{appName}", method = RequestMethod.POST)
    public ResultMap status(@PathVariable String appName, String instanceId, String status) {
        Application application = eurekaClient.getApplication(appName);
        InstanceInfo instanceInfo = application.getByInstanceId(instanceId);
        //只人为的修改本地的服务状态，但实际的服务状态，并没有通知到其他客户端。
        //其他客户端还需要等待一段时间才可以获取到状态的变更。
        //这种操作会给开发一个假象，认为服务状态已经变更完成。
        instanceInfo.setStatus(InstanceStatus.toEnum(status));
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
//		HttpUtil.post(instanceInfo.getHomePageUrl() + "eureka-admin-client/status", "status=" + status);
        HttpUtil.post(instanceInfo.getHomePageUrl() + "service-registry/instance-status", status, headers);

//		List<InstanceInfo> instanceInfos = application.getInstances();
//		for(InstanceInfo item : instanceInfos){
//			HttpUtil.post(item.getHomePageUrl() + "eureka-admin-client/status/" + appName, "instanceId=" + instanceId + "&status=" + status);
//		}
//		Set<String> regions = eurekaClient.getAllKnownRegions();
//		for(String region : regions){
//			Applications applications = eurekaClient.getApplicationsForARegion(region);
//			List<Application> apps = applications.getRegisteredApplications();
//			for(Application app : apps){
//				eurekaClient.getApplications().addApplication(app);
//			}
//		}
        return ResultMap.buildSuccess();
    }
}
