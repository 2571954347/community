package com.huo.community.util;

import com.huo.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息 代替 session对象
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUsers(User user) {
        users.set(user);
    }

    public User getUsers() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
