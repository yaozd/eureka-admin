package cn.springcloud.eureka.utils.springExt;

import org.springframework.context.ApplicationContext;

/**
 * spring boot 的 ApplicationContext 及 getbean
 * https://www.cnblogs.com/jpfss/p/8421343.html
 *
 * @author zd.yao
 * @description
 * @date 2018/9/16
 **/

public class SpringUtil2 {
    private static ApplicationContext applicationContext = null;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        if (SpringUtil2.applicationContext == null) {
            SpringUtil2.applicationContext = applicationContext;
        }
    }

    /***.
     * //获取applicationContext
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /***.
     * //通过name获取 Bean.
     * @param name
     * @return
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /***.
     * 通过class获取Bean
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /***
     * 通过name,以及Clazz返回指定的Bean
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }


}
