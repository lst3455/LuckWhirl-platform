package org.example.trigger.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.example.trigger.api.IDCCService;
import org.example.types.enums.ResponseCode;
import org.example.types.model.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * In the application, an API provides a method to modify properties.
 * Whenever a modification operation is triggered, the `setData()` method of ZooKeeper (zk) is called.
 * Triggers the reporting of the property value to ZooKeeper.
 * Once the data is updated in ZooKeeper, it will notify all clients listening to that node.
 * This node is monitored by the listener of `DCCValueBeanFactory`.
 * The listener will be triggered, read the updated data, locate the corresponding node from the map.
 * Then update the data for that node, thus achieving real-time dynamic configuration updates.
 */

@Slf4j
@RestController()
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/dcc")
public class DCCController implements IDCCService {

    private static final String BASE_CONFIG_PATH = "/BigMarket-dcc";
    private static final String BASE_CONFIG_PATH_CONFIG = BASE_CONFIG_PATH + "/config";

    @Resource
    private CuratorFramework curatorFramework;

    /**
     * update config
     * <a href="http://localhost:8091/api/v1/raffle/dcc/update_config">/api/v1/raffle/dcc/update_config?key=degradeSwitch&value=open</a>
     * <a href="http://localhost:8091/api/v1/raffle/dcc/update_config">/api/v1/raffle/dcc/update_config?key=rateLimitSwitch&value=open</a>
     * @param key
     * @param value
     * @return
     */
    @Override
    @RequestMapping(value = "update_config", method = RequestMethod.GET)
    public Response<Boolean> updateConfig(@RequestParam String key, @RequestParam String value) {
        try {
            log.info("DCC dynamic config update start, key:{} value:{}", key, value);
            String keyPath = BASE_CONFIG_PATH_CONFIG.concat("/").concat(key);
            // if nodePath does not exist, create path
            if (null == curatorFramework.checkExists().forPath(keyPath)) {
                curatorFramework.create().creatingParentsIfNeeded().forPath(keyPath);
                log.info("DCC dynamic config update - base node {} not absent create new done!", keyPath);
            }
            // set value for this node
            Stat stat = curatorFramework.setData().forPath(keyPath, value.getBytes(StandardCharsets.UTF_8));
            log.info("DCC dynamic config update complete, key:{} value:{} time:{}", key, value, stat.getCtime());
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("DCC dynamic config update fail, key:{} value:{}", key, value, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}
