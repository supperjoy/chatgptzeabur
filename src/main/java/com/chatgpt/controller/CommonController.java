package com.chatgpt.controller;


import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chatgpt.dto.UserDto;
import com.chatgpt.entity.Role;
import com.chatgpt.entity.User;
import com.chatgpt.service.RoleService;
import com.chatgpt.service.UserService;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.chatgpt.utils.RedisConstants.CAPTCHA_CODE_KEY;
import static com.chatgpt.utils.RedisConstants.LOGIN_CODE_TTL;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RoleService roleService;

//    @CrossOrigin(origins = "*", maxAge = 3600)
    @GetMapping("captcha")
    public SaResult captcha(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        // 三个参数分别为宽、高、位数
        SpecCaptcha specCaptcha = new SpecCaptcha(150, 40, 5);
        // 设置字体
        specCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        // 设置类型，纯数字、纯字母、字母数字混合
        specCaptcha.setCharType(Captcha.TYPE_ONLY_NUMBER);

        String verCode = specCaptcha.text().toLowerCase();
        String key = CAPTCHA_CODE_KEY+UUID.randomUUID();

        stringRedisTemplate.opsForValue().set(key,verCode,LOGIN_CODE_TTL, TimeUnit.MINUTES);

        String image = specCaptcha.toBase64();
        HashMap<String,String> map = new HashMap<>();
        map.put("key", key);
        map.put("image", image);
        return SaResult.data(map);
    }

//    @CrossOrigin(origins = "*", maxAge = 3600)
    @PostMapping("login")
    public SaResult login(@RequestBody UserDto user) {
        String username = user.getUsername();
        String password = user.getPassword();
        if(username.trim().isEmpty()){
            return SaResult.error("用户名不能为空");
        }
        if(password.trim().isEmpty()){
            return SaResult.error("密码不能为空");
        }

        String code;
        try {
            code = stringRedisTemplate.opsForValue().get(user.getKey());
        } catch (Exception e) {
            return SaResult.error("验证码超时,请刷新重试");
        }
        if(!user.getCode().equals(code)){
            return SaResult.error("验证码错误，请重新输入");
        }

        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUsername,username);
        User checkUser = userService.getOne(lqw);
        if(checkUser==null){
            return SaResult.error("用户名不存在");
        }
        if(!checkUser.getPassword().equals(password)){
            return SaResult.error("用户名或密码错误");
        }
        if(checkUser.getStatus()==0){
            return SaResult.error("账号已被停用");
        }
        StpUtil.login(checkUser.getId());
        stringRedisTemplate.delete(user.getKey());
        return SaResult.data(checkUser);
    }

    @PostMapping("adminlogin")
    public SaResult adminLogin(@RequestBody User user){
        String username = user.getUsername();
        String password = user.getPassword();
        if(username.trim().isEmpty()){
            return SaResult.error("用户名不能为空");
        }
        if(password.trim().isEmpty()){
            return SaResult.error("密码不能为空");
        }
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUsername,username);
        User checkUser = userService.getOne(lqw);
        if(checkUser==null){
            return SaResult.error("用户名不存在");
        }
        if(!checkUser.getPassword().equals(password)){
            return SaResult.error("用户名或密码错误");
        }
        StpUtil.login(checkUser.getId());
        return SaResult.data(checkUser);
    }

    @GetMapping("logout")
    public SaResult logout() {
        StpUtil.logout();
        return SaResult.ok();
    }

    @PostMapping("register")
    public SaResult register(@RequestBody UserDto user){
        if(!user.isNotEmpty()){
            return SaResult.error("请将信息填写完整");
        }
        String code;
        try {
            code = stringRedisTemplate.opsForValue().get(user.getKey());
        } catch (Exception e) {
            return SaResult.error("验证码超时,请刷新重试");
        }
        if(!user.getCode().equals(code)){
            return SaResult.error("验证码错误，请重新输入");
        }

        //首先检验用户名是否存在
        LambdaQueryWrapper<User> lqw3 = new LambdaQueryWrapper<>();
        lqw3.eq(User::getUsername,user.getUsername());
        if(userService.getOne(lqw3)!=null){
            return SaResult.error("该用户名已被注册");
        }
        if(user.getUsername().trim().length()<6){
            return SaResult.error("用户名长度不得小于6个字符");
        }
        if(user.getUsername().trim().length()>20){
            return SaResult.error("用户名长度不得大于20个字符");
        }
        if(user.getUsername().contains(" ")){
            return SaResult.error("用户名中不能含有空格");
        }

        LambdaQueryWrapper<User> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(User::getTelephone,user.getTelephone());
        if(userService.getOne(lqw1)!=null){
            // 如果匹配成功，则返回 true，否则返回 false
            return SaResult.error("该手机号已被注册");
        }
        String regex = "^1\\d{10}$"; // 手机号正则表达式，以 1 开头的 11 位数字
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(user.getTelephone());
        if(!matcher.matches()){
            return SaResult.error("手机号码有误，请检查是否填写正确");
        }

        String regex1 = "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$"; // 邮箱正则表达式
        Pattern pattern1 = Pattern.compile(regex1);
        Matcher matcher1 = pattern1.matcher(user.getEmail());
        if(!matcher1.matches()){
            // 如果匹配成功，则返回 true，否则返回 false
            return SaResult.error("邮箱填写有误，请检查是否填写正确");
        }

        LambdaQueryWrapper<User> lqw2 = new LambdaQueryWrapper<>();
        lqw2.eq(User::getEmail,user.getEmail());
        if(userService.getOne(lqw2)!=null){
            return SaResult.error("该邮箱已被注册");
        }
        if(StringUtils.isNotBlank(user.getRecommender())){
            LambdaQueryWrapper<User> lqw4 = new LambdaQueryWrapper<>();
            lqw4.eq(User::getUsername,user.getRecommender());
            if(userService.getOne(lqw4)==null){
                return SaResult.error("该推荐人不存在");
            }
        }


        // 创建 SimpleDateFormat 对象并指定日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // 获取当前时间并格式化
        user.setCreateTime(sdf.format(new Date()));

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf1.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置时区为东八区
        user.setExpireTime(sdf1.format(new Date()));
        user.setStatus(1);
        Role role = new Role();
        boolean isSave = userService.save(user);
        if (isSave){
            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
            lqw.eq(User::getUsername,user.getUsername());
            User newUser = userService.getOne(lqw);
            role.setId(newUser.getId());
            role.setRoleName("普通用户");
            role.setPer("user");
            boolean save = roleService.save(role);
            if (save){
                return SaResult.ok();
            }
        }
        return SaResult.error("注册失败请联系管理员");
    }
}
