package com.huo.community;

import com.huo.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testSendMail() {
        mailClient.sendMail("1600740543@qq.com", "JavaMailSender邮件测试", "你好 我的海里没有州");
    }

    @Test
    public void templateEngine() {
        Context context = new Context();
        context.setVariable("username", "sunday");
        String process = templateEngine.process("/mail/demo", context);
        System.out.println(process);
    }
}
