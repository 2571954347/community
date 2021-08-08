package com.huo.community;

import com.huo.community.dao.DiscussPostMapper;
import com.huo.community.dao.LoginTicketMapper;
import com.huo.community.dao.UserMapper;
import com.huo.community.entity.DiscussPost;
import com.huo.community.entity.User;
import com.huo.community.util.CommunityUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(11);
        System.out.println(user);
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setUsername("test");
        user.setSalt("abc");
        user.setPassword("123456");
        user.setEmail("123456@163.com");
        user.setHeaderUrl("http:www.huoblog,vip");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        System.out.println(userMapper.selectById(user.getId()));
    }

    @Test
    public void testDiscussPost() {
        System.out.println(discussPostMapper.selectDiscussPostRows(111));
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPost(101, 0, 1);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }
    }

    @Test
    public void loginTicketMapper() {
        /*LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date());
        loginTicketMapper.insertLoginTicket(loginTicket);*/
        System.out.println(loginTicketMapper.updateStatus("abc", 1));
    }
    @Test
    public void testMd5(){
        System.out.println(CommunityUtil.md5("111111"));
    }
}
