package cn.springcloud.eureka.utils.eurekaExt;

import cn.springcloud.eureka.utils.springExt.SpringUtil2;
import com.netflix.discovery.DiscoveryClient;

import java.lang.reflect.Method;

public class EurekaUtil2 {
    /***
     * 刷新本地注册服务信息
     * --------------------------------------
     * 目前（2019-01-17）
     * Dalston版本不支持
     * Finchley版本可以正常使用
     * --------------------------------------
     * Eureka+Ribbon源码解析及负载均衡缓存的优化
     * https://www.jianshu.com/p/07c2e0d59dc9
     * ======
     * 自定义DiscoveryClient比较复杂.
     * 通过代码分析可以知道只需要能调到DiscoveryClient的refreshRegistry就可以实时刷新了, 但它是个private方法, 那就反射嘛, 配合Bus的refresh功能同时刷新注册信息
     * @return
     */
    public static boolean refreshRegistry() {
        DiscoveryClient t = SpringUtil2.getBean(DiscoveryClient.class);
        try {
            Method method = DiscoveryClient.class.getDeclaredMethod("refreshRegistry");
            method.setAccessible(true);
            method.invoke(SpringUtil2.getBean(DiscoveryClient.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
