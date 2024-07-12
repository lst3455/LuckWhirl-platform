package org.example.test.infrastructure;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.infrastructure.persistent.dao.IAwardDao;
import org.example.infrastructure.persistent.po.Award;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/** award persistent unit test */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class IAwardDaoTest {

    @Resource
    private IAwardDao awardDao;

    @Test
    public void test_queryAwardList(){
        List<Award> awards = awardDao.queryAwardList();
        log.info("test result:{}", JSON.toJSONString(awards));
    }

    @Test
    public void test2_queryAwardList(){
        List<Award> awards = awardDao.queryAwardListById();
        log.info("test result:{}", JSON.toJSONString(awards));
    }
}
