package com.chatgpt.controller;


import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chatgpt.entity.Role;
import com.chatgpt.entity.User;
import com.chatgpt.service.RoleService;
import com.chatgpt.service.UserService;
import com.google.zxing.WriterException;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.TimeZone;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author young
 * @since 2023年04月21日
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @GetMapping
    public SaResult getUsers(){
        return SaResult.data(userService.list());
    }

    @GetMapping("checkexpire")
    public SaResult checkExpire() throws ParseException {

        String idStr = StpUtil.getLoginId().toString();
        Long id = Long.valueOf(idStr);
        User user = userService.getById(id);
        String lastQuestionTimeStr = "";
        String expireTimeStr = user.getExpireTime();
        if(user.getLastQuestionTime()==null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            lastQuestionTimeStr = LocalDateTime.now().format(formatter);
        }else{
            lastQuestionTimeStr = user.getLastQuestionTime();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));  // 设置时区为东八区
        Date expireTime = sdf.parse(expireTimeStr);
        Date lastQuestionTime = sdf.parse(lastQuestionTimeStr);

        long expireTimestamp = expireTime.getTime() / 1000; // 获取秒级时间戳
        long lastQuestionTimestamp = lastQuestionTime.getTime()/1000;

        Instant targetInstant = Instant.ofEpochSecond(expireTimestamp);
        Instant lastQuestionTimeInstant = Instant.ofEpochSecond(lastQuestionTimestamp);

        Instant nowInstant = Instant.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentDatetime = LocalDateTime.now().format(formatter);

        if (targetInstant.isBefore(nowInstant)) {
            return SaResult.error("您的时长已经用完，请充值");
        }

        if(user.getTotalNumber()==-1){
            user.setLastQuestionTime(currentDatetime);
            userService.updateById(user);
            return SaResult.ok();
        }


        Duration duration = Duration.between(lastQuestionTimeInstant,nowInstant);
        //如果超过三个小时 重置次数
        if(duration.toHours() >=3){
            user.setCurrentNumber(user.getTotalNumber()-1);
        }
        //如果没有超过三个小时
        else{
            //首先判断currentNumber是否用完
            if(user.getCurrentNumber()<=0){
                return SaResult.error("当前次数已用完，请稍后尝试");
            }
            //如果没有用完
            else{
                user.setCurrentNumber(user.getCurrentNumber()-1);
            }
        }
        user.setLastQuestionTime(currentDatetime);

        if(userService.updateById(user))
            return SaResult.ok();
        else
            return SaResult.error("出错了，请联系管理员");
    }

    @GetMapping("checkexpirethree")
    public SaResult checkexpirethree() throws ParseException {
        String idStr = StpUtil.getLoginId().toString();
        Long id = Long.valueOf(idStr);
        User user = userService.getById(id);

        String expireTimeStr = user.getExpireTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));  // 设置时区为东八区
        Date expireTime = sdf.parse(expireTimeStr);
        long expireTimestamp = expireTime.getTime() / 1000; // 获取秒级时间戳
        Instant targetInstant = Instant.ofEpochSecond(expireTimestamp);
        Instant nowInstant = Instant.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentDatetime = LocalDateTime.now().format(formatter);
        if (targetInstant.isBefore(nowInstant)) {
            return SaResult.error("您的时长已经用完，请充值");
        }
        user.setLastQuestionTime(currentDatetime);
        userService.updateById(user);
        return SaResult.ok();
    }

    @GetMapping("getCurrentNumber")
    public SaResult getCurrentNumber() throws ParseException {
        String idStr = StpUtil.getLoginId().toString();
        Long id = Long.valueOf(idStr);
        User user = userService.getById(id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentDatetime = LocalDateTime.now().format(formatter);

        String expireTimeStr = user.getExpireTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));  // 设置时区为东八区
        Date expireTime = sdf.parse(expireTimeStr);

        long expireTimestamp = expireTime.getTime() / 1000; // 获取秒级时间戳

        Instant targetInstant = Instant.ofEpochSecond(expireTimestamp);

        Instant nowInstant = Instant.now();
        if (targetInstant.isBefore(nowInstant)) {
            return SaResult.data("0次");
        }

        if(user.getTotalNumber()==-1){
            return SaResult.data("无限次数");
        }

        String lastQuestionTimeStr = user.getLastQuestionTime();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));  // 设置时区为东八区
        Date lastQuestionTime = sdf.parse(lastQuestionTimeStr);
        long lastQuestionTimestamp = lastQuestionTime.getTime()/1000;
        Instant lastQuestionTimeInstant = Instant.ofEpochSecond(lastQuestionTimestamp);
//        Instant nowInstant = Instant.now();
        Duration duration = Duration.between(lastQuestionTimeInstant,nowInstant);

        //如果超过三个小时 重置次数
        if(duration.toHours() >=3){
            user.setCurrentNumber(user.getTotalNumber());
//            user.setLastQuestionTime(currentDatetime);
        }
        if(userService.updateById(user))
            return SaResult.data(user.getCurrentNumber()+"次");
        else
            return SaResult.error("出错了，请联系管理员");
    }

    @GetMapping("checklogin")
    public SaResult checkLogin(){
        if(StpUtil.isLogin()){
            return SaResult.ok();
        }
            return SaResult.error("请首先进行登录操作");
    }

    @GetMapping("getexpire")
    public SaResult getExpire() throws ParseException {
        String idStr = StpUtil.getLoginId().toString();
        Long id = Long.valueOf(idStr);
        System.out.println(StpUtil.getTokenValue());
        //获取当前用户
        User user = userService.getById(id);


        String expireTimeStr = user.getExpireTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));  // 设置时区为东八区
        Date expireTime = sdf.parse(expireTimeStr);
        long expireTimestamp = expireTime.getTime() / 1000; // 获取秒级时间戳
        Instant targetInstant = Instant.ofEpochSecond(expireTimestamp);

        Instant nowInstant = Instant.now();
        if (targetInstant.isBefore(nowInstant)) {
            return SaResult.data("已过期");
        } else {
            Duration duration = Duration.between(nowInstant, targetInstant);
            long hours = duration.toHours();

            if (hours >= 24) {
                long days = hours / 24;
                return SaResult.data(days + "天");
            } else if (hours >= 1) {
                return SaResult.data(hours + "小时");
            } else {
                System.out.println("不足一小时");
                return SaResult.data("不足一小时");
            }
        }

    }

    @GetMapping("/page")
    public SaResult userAdmin(int page,int pageSize,String search){
        return userService.getPages(page,pageSize,search);
    }



    @PostMapping
    public SaResult addUser(@RequestBody User user){
        return userService.addUser(user);
    }

    @PostMapping("status")
    public SaResult setStatus(@RequestBody User user){
        return userService.setStatus(user);
    }

    @DeleteMapping("{id}")
    public SaResult delete(@PathVariable Long id){
        return userService.deleteUser(id);
    }

    @PutMapping
    public SaResult edit(@RequestBody User user){
        return userService.editUser(user);
    }

    @GetMapping("getCode")
    public SaResult getCode(Long commodityId) throws AlipayApiException, IOException, WriterException {
        return userService.getCode(commodityId);
    }

    @GetMapping("getOrderStatus")
    public SaResult getOrderStatus(String orderId) throws AlipayApiException {
        return userService.getOrderStatus(orderId);
    }

}

