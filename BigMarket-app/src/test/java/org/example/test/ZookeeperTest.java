package org.example.test;


import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ZookeeperTest {

    @Resource
    private CuratorFramework curatorFramework;

    /**
     * 创建永久节点
     */
    @Test
    public void createNode() throws Exception {
        String path = "/BigMarket-dcc/config/downgradeSwitch/test/a";
        String data = "0";
        if (null == curatorFramework.checkExists().forPath(path)) {
            curatorFramework.create().creatingParentsIfNeeded().forPath(path);
        }
    }

    @Test
    public void setData() throws Exception {
        curatorFramework.setData().forPath("/BigMarket-dcc/config/downgradeSwitch", "111".getBytes(StandardCharsets.UTF_8));
//        curatorFramework.setData().forPath("/BigMarket-dcc/config/userWhiteList", "222".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void getData() throws Exception {
        String downgradeSwitch = new String(curatorFramework.getData().forPath("/BigMarket-dcc/config/downgradeSwitch"), StandardCharsets.UTF_8);
        log.info("test result: {}", downgradeSwitch);
//        String userWhiteList = new String(curatorFramework.getData().forPath("/BigMarket-dcc/config/userWhiteList"), StandardCharsets.UTF_8);
//        log.info("test result: {}", userWhiteList);
    }

}
