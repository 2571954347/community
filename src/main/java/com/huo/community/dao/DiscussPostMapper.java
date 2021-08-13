package com.huo.community.dao;

import com.huo.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    /**
     * 可以查询我的账户的帖子 userId为 0显示网站所有帖子  动态sql
     * @param userId 用户ID 为0查询所有帖子
     * @param offset 每一页起始行的行号
     * @param limit  每页显示多少数据
     * @return
     */
    List<DiscussPost> selectDiscussPost(int userId,int offset,int limit);

    /**
     * @Param 注解用于给参数取别名 如果只有一个参数并且在<if>里使用必须加别名
     * @param userId
     * @return
     */
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost); //新增贴子

    DiscussPost selectDiscussPostById(int id); //根据id查询帖子

    int updateCommentCount(int id,int commentCount);
}
