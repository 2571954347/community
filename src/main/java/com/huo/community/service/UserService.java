package com.huo.community.service;

import com.huo.community.dao.UserMapper;
import com.huo.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User selectById(Integer id) {
        return userMapper.selectById(id);
    }


    public User selectByName(String name) {
        return null;
    }

    public User selectByEmail(String email) {
        return null;
    }

    public int insertUser(User user) {
        return 0;
    }

    public int updateStatus(int id, int status) {
        return 0;
    }

    public int updateHeaderUrl(int id, String email) {
        return 0;
    }

    public int updatePassword(int id, String password) {
        return 0;
    }
}
