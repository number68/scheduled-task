package com.cloud.task;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * <功能描述><br/>
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ScheduledTaskTestApplication {

    @Before
    public void init() {
        System.out.println("-----------------" + this.getClass().getSimpleName() + " TEST BEGIN--------------------");
    }

    @After
    public void after() {
        System.out.println("-----------------" + this.getClass().getSimpleName() + " TEST END----------------------");
    }
}
