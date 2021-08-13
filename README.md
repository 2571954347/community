# 牛客社区项目

**使用技术栈：Spring Boot**

​					   **Spring、Spring MVC、MyBatis** 

​                       **Redis、Kafka、Elasticsearch** 

​                       **Spring Security、Spring Actuator**

## 一、社区首页开发



### **1.用户持久层的开发，用户的增删改查** 

分析：对于重复查询的字段可以使用<sql id=""></sql>

```c
<sql id="insertFields">
    username,password,salt,email,type,status,activation_code,header_url,create_time
</sql>
```

### 2.社区首页的帖子的分页展示并进行、排序

分析：1.若没有userid传入查询所有帖子，否则查询用户对应的帖子，并按发布时间降序展示。

​            2.分页过程编写page类，核心参数

```java
//当前页码
private int current = 1;
//每页显示几条
private int limit = 10;
//数据总数 用于计算总页数
private int rows;
//查询路径
private String path;
//获取当前页的起始行
public int getOffset() {
  return (current - 1) * limit;
}
```

当前页码current默认为1，每页显示默认10条，总记录数为rows（从数据库中查询count），总页数计算公式

rows % limit == 0 ? rows / limit : rows / limit + 1;

```java
//获取起始页码 		设置显示5个页码
public int getFrom() {
    int from = current - 2;
    return from < 1 ? 1 : from;
}

//获取终止页码
public int getTo() {
    int to = current + 2;
    int total = getTotal();
    return to > total ? total : to;
}
```

## 二、社区注册登录模块的开发



### **1.发送邮件**

分析：用于用户注册后发送邮件激活, 配置文件中mail相关配置，导入spring-boot-starter-mail，核心类JavaMailSender

```java
@Autowired
private JavaMailSender mailSender;
/**
 * 发送邮件方法
 * @param to      发给谁
 * @param subject 邮件主题
 * @param content 邮件内容
 */

public void sendMail(String to, String subject, String content) {
    try {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(helper.getMimeMessage());
    } catch (Exception e) {
        logger.error("发送邮件失败" + e.getMessage());
    }
}
```

###       **2.注册功能**

分析：前端通过form表单传入User数据，后端进行分解，先进行判空操作，不为空通过set方法设置相关属性并插入数据库中

之后向用户邮箱发送激活邮件 把信息装到context中前端可获取 

```
//激活邮件
Context context = new Context();
context.setVariable("email", user.getEmail());
// http://localhost:8080/community/activation/101/code
String url = domain + contextPath + "/activation/" + user.getId() +"/"+ user.getActivationCode();
context.setVariable("url", url);
String content = templateEngine.process("/mail/activation", context); 
mailClient.sendMail(user.getEmail(), "激活账号", content);//发送邮件
```

发送后用户点击     http://localhost:8080/community/activation/userId/code 进行激活，相对激活状态返回给前端

```java
//激活成功
int ACTIVATION_SUCCESS = 0;
//重复激活
int ACTIVATION_REPEAT = 1;
//激活失败
int ACTIVATION_FAIL = 2;
```

###  **3.登录功能**

**验证码功能开发：**导入kaptcha jar包，配置KaptchaConfig，设置验证码相关格式并返回“kaptcha”，

用户登录后同时往login_ticket表中存放登录凭证信息，前端通过form表单传入用户名和密码和验证码以及 是否 “记住我”  计算expiredSecond 单位是毫秒 不记住我是保存12小时 记住我是100天，之后调用servicelogin方法

```Java
//默认状态登录凭证超时时间
int DEFAULT_EXPIRED_SECOND = 3600 * 12; 
//记住我状态下的登录凭证超时时间
int REMEMBER_EXPIRED_SECOND = 3600 * 24 * 100;
```

**LoginCotroller中login方法进行登录：**

```java
public String login(String username, String password, String code, boolean rememberMe,
                    Model model, HttpSession session, HttpServletResponse response) {}
```

先判断从session获取的验证码 (忽略大小写)的 正确与否，不对直接回到login页面，之后计算登录过期时间，若点击了 ’记住我‘ 按钮，

**UserService的login方法进行登录逻辑编写：**先进行各项为空判断，若都不符合，判断是否存在该用户、用户状态是否正常

（0-未激活; 1-已激活），将传来的密码加密+Salt进行MD5加密后与数据库中比较，都正确后生成登录凭证(login_ticket表)，设置

status（0-有效; 1-无效），将随机生成的ticket返回给controller，LoginCotroller判断是否有ticket凭证，没有返回错误信息，否则将凭证存入cookie中并设置过期时间

```java
if (map.containsKey("ticket")) {
    Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
    cookie.setPath(contextPath);
    cookie.setMaxAge(expiredSeconds);
    response.addCookie(cookie);
    return "redirect:/index";
```

### **4.退出登录&显示登录信息**

需求：用户点击退出登录清楚登录状态&用户登陆后显示头像、用户名

用户点击退出登录后service会得到ticket并且传入status = 1(无效)，之后重定向到登录页

定义拦截器**LoginTicketInterceptor**，将定义的拦截器加入到**WebMvcConfig**中(同时要排除需要访问的静态资源等)，定义通用的**CookieUtil**工具类，目的是用户登陆后可以从**cookie**中获取用户的相关信息，定义**hostHolder** （为了持有用户信息 代替 session对象），之后通过拦截器的**preHandle**方法，从**request**中的到ticket，通过ticket查到**loginTicket**，又通过userId查出用户，最后放到**hostHolder.setUsers(user)** 使得本次请求持有用户，**postHandle**利用**modelAndView**将用户信息放入，前端就可以获取到了，就可在模板视图上显示用户数据，最后clear清除信息；

### 5.更改用户头像和密码

更改头像：用户每次请求时都会先到**LoginTicketInterceptor**得到用户信息，service收到userId，和更改头像的路径（headerUrl）后进行更改，前端传入上传图片的路径到**UserController**的**uploadHeader**，**MultipartFile**进行上传  ，先点击上传到浏览器上

```java
public String uploadHeader(MultipartFile headerImage, Model model) {}
```

如果头像传的为空就回到当前页面，通过getOriginalFilename获取文件的原始名，判断头像图片的后缀是否符合要求，如果没问题就随机生成新的文件名，利用file类确定头像图片的存放路径，**transferTo(file)**传到浏览器上或客户端，之后更新数据库中的用户新的头像路径。之后重定向到首页并显示用户更改后的新头像，通过**response**响应图片，通过**输入输出流**进行相应读写

```java
//更新当前用户头像路径(Web访问路径) http://localhost:8080/community/user/header/xxx.png
User user = hostHolder.getUsers();
String headerUrl = domain + contextPath + "/user/header/" + fileName;
userService.updateHeader(user.getId(), headerUrl);
```

# 三、社区核心功能开发

### 1.敏感词过滤

### 2.发布帖子

### 3.帖子详情

### 4.显示评论

**comment**表中**user_id**代表谁发的帖子，**entity_type**是评论的类型 1代表对帖子的评论，2代表对评论的评论(回复)，

**target_id**是回复给谁的id，**entity_id**是被评论帖子的id.

查询出所有评论后，需要查询帖子下的评论和评论的回复，

```java
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
```

### 5.添加评论

commentMapper中增加插入评论方法，同时DiscussPost表中更新评论数量(帖子的评论)，涉及到两张表的数据变动，需要进行事务的管理。

commentController接受前端传来的帖子Id和comment实体

1.给帖子评论 通过隐藏输入框传入entityType默认为1 entityId即帖子的id

```html
<input type="hidden" name="entityType" value="1"> 
<input type="hidden" name="entityId" th:value="${post.id}">
```

2.给帖子普通评论的回复

```html
<input type="hidden" name="entityType" value="2">
<input type="hidden" name="entityId" th:value="${cvo.comment.id}">
```

3.回复给固定的某个人的评论

```html
<div>
    <input type="text" class="input-size" name="content" th:placeholder="回复${rvo.user.username}"/>
</div>
<input type="hidden" name="entityType" value="2">
<input type="hidden" name="entityId" th:value="${cvo.comment.id}">
<input type="hidden" name="targetId" th:value="${rvo.user.id}">
```

6.私信里欸啊