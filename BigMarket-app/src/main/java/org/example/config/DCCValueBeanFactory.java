package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.example.types.annotation.DCCValue;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Configuration
public class DCCValueBeanFactory implements BeanPostProcessor {

    private static final String BASE_CONFIG_PATH = "/BigMarket-dcc";
    private static final String BASE_CONFIG_PATH_CONFIG = BASE_CONFIG_PATH + "/config";

    private final CuratorFramework curatorFramework;
    private final Map<String, Object> dccObjectMap = new HashMap<>();

    public DCCValueBeanFactory(CuratorFramework curatorFramework) throws Exception {
        this.curatorFramework = curatorFramework;

        // create root directory if not exist
        if (null == curatorFramework.checkExists().forPath(BASE_CONFIG_PATH_CONFIG)) {
            curatorFramework.create().creatingParentsIfNeeded().forPath(BASE_CONFIG_PATH_CONFIG);
            log.info("DCC node listener - base node: {} not absent create new done!", BASE_CONFIG_PATH_CONFIG);
        }

        // start cache
        CuratorCache curatorCache = CuratorCache.build(curatorFramework, BASE_CONFIG_PATH_CONFIG);
        curatorCache.start();

        curatorCache.listenable().addListener((type, oldData, data) -> {
            switch (type) {
                case NODE_CHANGED:
                    String dccValuePath = data.getPath();
                    Object objectBean = dccObjectMap.get(dccValuePath);
                    if (null == objectBean) return;
                    try {
                        Class<?> objectBeanClass = objectBean.getClass();
                        if (AopUtils.isAopProxy(objectBeanClass)) {
                            objectBeanClass = AopUtils.getTargetClass(objectBean);
                        }
                        // 1. The getDeclaredField method is used to retrieve all declared fields in the specified class, including private, protected, and public fields.
                        // 2. The getField method is used to retrieve public fields in the specified class, meaning it can only access fields with the public access modifier.
                        Field field = objectBeanClass.getDeclaredField(dccValuePath.substring(dccValuePath.lastIndexOf("/") + 1));
                        field.setAccessible(true);
                        field.set(objectBean, new String(data.getData()));
                        field.setAccessible(false);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                default:
                    break;
            }
        });

    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targertBeanClass = bean.getClass();
        Object targertBeanObejct = bean;

        if (AopUtils.isAopProxy(bean)){
            targertBeanClass = AopUtils.getTargetClass(bean);
            targertBeanObejct = AopProxyUtils.getSingletonTarget(bean);
        }

        Field[] fields = targertBeanClass.getDeclaredFields();

        for (Field field : fields) {
            if (!field.isAnnotationPresent(DCCValue.class)) {
                continue;
            }

            DCCValue dccValue = field.getAnnotation(DCCValue.class);

            String value = dccValue.value();
            if (StringUtils.isBlank(value)) {
                throw new RuntimeException(field.getName() + " @DCCValue is not config value config case 「isSwitch/isSwitch:1」");
            }

            String[] splits = value.split(":");
            String key = splits[0];
            String defaultValue = splits.length == 2 ? splits[1] : null;

            try {
                // check if node exist, if no it will create node with default value
                String keyPath = BASE_CONFIG_PATH_CONFIG.concat("/").concat(key);
                if (null == curatorFramework.checkExists().forPath(keyPath)) {
                    curatorFramework.create().creatingParentsIfNeeded().forPath(keyPath);
                    if (StringUtils.isNotBlank(defaultValue)) {
                        field.setAccessible(true);
                        field.set(targertBeanObejct, defaultValue);
                        field.setAccessible(false);
                    }
                    log.info("DCC node listener - create node: {}", keyPath);
                } else {
                    // check if node exist, if yse it will load the value
                    String configValue = new String(curatorFramework.getData().forPath(keyPath));
                    if (StringUtils.isNotBlank(configValue)) {
                        field.setAccessible(true);
                        field.set(targertBeanObejct, configValue);
                        field.setAccessible(false);
                        log.info("DCC node listener - set configuration: {} {} {}", keyPath, field.getName(), configValue);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // store in map, for future to get targertBeanObejct
            dccObjectMap.put(BASE_CONFIG_PATH_CONFIG.concat("/").concat(key), targertBeanObejct);
        }
        return bean;
    }
}
