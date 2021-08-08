package com.huo.community.controller.interceptor;

import com.huo.community.entity.LoginTicket;
import com.huo.community.entity.User;
import com.huo.community.service.UserService;
import com.huo.community.util.CookieUtil;
import com.huo.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtil.getValue(request, "ticket");
        if (ticket != null) {//表示登录了
            LoginTicket loginTicket = userService.findLoginTicket(ticket);//查询凭证
            if (loginTicket != null && loginTicket.getStatus() == 0
                    && loginTicket.getExpired().after(new Date())) { //检查凭证是否有效  after过期时间晚于当前时间
                User user = userService.selectById(loginTicket.getUserId());//根据凭证查询用户
                //让本次请求持有用户
                hostHolder.setUsers(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUsers();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
