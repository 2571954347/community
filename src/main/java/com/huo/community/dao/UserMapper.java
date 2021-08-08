package com.huo.community.dao;

import com.huo.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User selectById(Integer id); //根据ID查询用户

    User selectByName(String name); //根据姓名查询用户

    User selectByEmail(String email); //根据邮箱查询用户

    int insertUser(User user);//增加用户

    int updateStatus(int id, int status);//更新用户状态

    int updateHeaderUrl(int id, String headerUrl);//更新用户头像

    int updatePassword(int id, String password);//更新用户密码

}
