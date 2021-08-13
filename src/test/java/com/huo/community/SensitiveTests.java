package com.huo.community;

import com.huo.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Test
    public void test(){
        String text = "这里可以赌⭐⭐博，可以⭐⭐⭐嫖⭐娼，哈⭐哈⭐哈哈";
        String filter = sensitiveFilter.filter(text);
        System.out.println(filter);
    }
}
