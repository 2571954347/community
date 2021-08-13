package com.huo.community.dao;

import com.huo.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType, int entityId);//查询对应类型评论的数量

    int insertComment(Comment comment); //增加评论
}
