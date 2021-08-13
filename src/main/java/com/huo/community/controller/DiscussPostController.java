package com.huo.community.controller;

import com.huo.community.entity.Comment;
import com.huo.community.entity.DiscussPost;
import com.huo.community.entity.Page;
import com.huo.community.entity.User;
import com.huo.community.service.CommentService;
import com.huo.community.service.DiscussPostService;
import com.huo.community.service.UserService;
import com.huo.community.util.CommunityConstant;
import com.huo.community.util.CommunityUtil;
import com.huo.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUsers();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "您还未登录！");
        }
        if (StringUtils.isBlank(title) || StringUtils.isBlank(content)) {
            return CommunityUtil.getJSONString(405, "标题或内容不能为空");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.insertDiscussPost(discussPost);
        return CommunityUtil.getJSONString(0, "发布成功！");
    }

    @RequestMapping(value = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String selectDiscussPostById(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        DiscussPost discussPost = discussPostService.selectDiscussPostById(discussPostId);
        User user = userService.selectById(discussPost.getUserId());
        model.addAttribute("post", discussPost);
        model.addAttribute("user", user);
        //评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(discussPost.getCommentCount());
        //查询出这个帖子的所有评论
        List<Comment> commentList = commentService.
                selectCommentByEntity(ENTITY_TYPE_POST, discussPost.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentVoList = new ArrayList<>(); //评论的列表

        if (commentList != null) {
            for (Comment comment : commentList) {
                HashMap<String, Object> commentVo = new HashMap<>();//一个评论的vo
                commentVo.put("comment", comment); //帖子对应的评论
                commentVo.put("user", userService.selectById(comment.getUserId()));//帖子评论的作者

                List<Comment> replyList = commentService.selectCommentByEntity //评论的评论  即评论的回复
                        (ENTITY_TYPE_COMMENT, discussPostId, 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVoList = new ArrayList<>(); //评论的回复列表
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        HashMap<String, Object> replyVO = new HashMap<>();
                        replyVO.put("reply", reply); //回复的评论
                        replyVO.put("user", userService.selectById(reply.getUserId()));//回复评论的作者
                        User target = reply.getTargetId() == 0 ? null : userService.selectById(reply.getTargetId());
                        replyVO.put("target", target); //添加回复的目标
                        replyVoList.add(replyVO);
                    }
                }
                commentVo.put("replys", replyVoList);
                //回复的数量
                int replyCount = commentService.selectCountByEntity(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";
    }
}
