package com.chatgpt.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chatgpt.entity.Redemption;
import com.chatgpt.entity.User;
import com.chatgpt.mapper.RedemptionDao;
import com.chatgpt.service.RedemptionService;
import com.chatgpt.service.UserService;
import com.chatgpt.utils.CodeGenerateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class RedemptionServiceImpl extends ServiceImpl<RedemptionDao, Redemption> implements RedemptionService {

    @Autowired
    private RedemptionService redemptionService;

    @Autowired
    private UserService userService;

    @Override
    public SaResult add(Redemption redemption) {
        if(StpUtil.getPermissionList().contains("admin")){
            if(!StringUtils.isNotBlank(redemption.getType())){
                return SaResult.error("请选择卡密类型");
            }
            String code;
            while(true){
                code = CodeGenerateUtils.getCode();
                LambdaQueryWrapper<Redemption> lqw = new LambdaQueryWrapper<>();
                lqw.eq(Redemption::getCode,code);
                //如果兑换码不存在
                if(redemptionService.list(lqw).size()==0){
                    redemption.setCode(code);
                    break;
                }
            }

            // 创建 SimpleDateFormat 对象并指定日期格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 获取当前时间并格式化
            redemption.setCreateTime(sdf.format(new Date()));
            redemption.setStatus(1);
            redemptionService.save(redemption);
            return SaResult.data(code);
        }
        return SaResult.error("您没有权限");
    }

    @Override
    public SaResult delete(Long id) {
        if(StpUtil.getPermissionList().contains("admin")){
            if(redemptionService.removeById(id)){
                return SaResult.ok();
            }
            else{
                return SaResult.error("删除失败");
            }
        }
        return SaResult.error("您没有权限");
    }

    @Override
    public SaResult getPage(int page, int pageSize, String search) {
        if(StpUtil.getPermissionList().contains("admin")){
            LambdaQueryWrapper<Redemption> lqw = new LambdaQueryWrapper<>();
            lqw.like(StringUtils.isNotBlank(search),Redemption::getUsername,search)
                    .or().like(StringUtils.isNotBlank(search),Redemption::getCode,search);
            IPage<Redemption> pageInfo = new Page<>(page,pageSize);
            IPage<Redemption> page1 = redemptionService.page(pageInfo, lqw);
            return SaResult.data(page1);
        }
        return SaResult.error("您没有权限");
    }

    @Override
    public SaResult getRedem(Redemption redemption) throws ParseException {
        if(!StringUtils.isNotBlank(redemption.getCode())){
            return SaResult.error("兑换码不能为空");
        }
        String code = redemption.getCode();
        String username = redemption.getUsername();
        LambdaQueryWrapper<Redemption> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Redemption::getCode,code);
        Redemption one = redemptionService.getOne(lqw);
        if(one==null){
            return SaResult.error("该兑换码不存在");
        }
        if(one.getStatus()==2){
            return SaResult.error("该兑换码已被使用");
        }
        //查询用户表 获取使用该兑换码的用户
        LambdaQueryWrapper<User> lqw2 = new LambdaQueryWrapper<>();
        lqw2.eq(User::getUsername,username);
        User user = userService.getOne(lqw2);
        if(user==null){
            return SaResult.error("该用户不存在，请重新登录后再次尝试");
        }


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date1 = dateFormat.parse(user.getExpireTime());
        Date today = new Date(); // 今天的日期
        int compare = date1.compareTo(today);
        if (compare < 0) {
            // 如果在今天日期之前
            String todayStr = dateFormat.format(today);
            user.setExpireTime(todayStr);
        }

        Date date2 = dateFormat.parse(user.getExpireTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date2);

        switch (one.getType()) {
            case "hour" -> {
                calendar.add(Calendar.HOUR, 1);
                user.setExpireTime(dateFormat.format(calendar.getTime()));
            }
            case "day" -> {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                user.setExpireTime(dateFormat.format(calendar.getTime()));
            }
            case "week" -> {
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
                user.setExpireTime(dateFormat.format(calendar.getTime()));
            }
            case "month" -> {
                calendar.add(Calendar.MONTH, 1);
                user.setExpireTime(dateFormat.format(calendar.getTime()));
            }
            case "year" -> {
                calendar.add(Calendar.YEAR, 1);
                user.setExpireTime(dateFormat.format(calendar.getTime()));
            }
        }

        // 创建 SimpleDateFormat 对象并指定日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        user.setCurrentNumber(one.getTime());
        user.setTotalNumber(one.getTime());

        one.setStatus(2);
        one.setUseTime(sdf.format(new Date()));
        one.setUsername(username);


        if(userService.updateById(user) && redemptionService.updateById(one)){
            switch (one.getType()) {
                case "hour":
                    return SaResult.data("兑换成功!兑换时长：1小时");
                case "day":
                    return SaResult.data("兑换成功!兑换时长：1天");
                case "week":
                    return SaResult.data("兑换成功!兑换时长：1周");
                case "month":
                    return SaResult.data("兑换成功!兑换时长：1月");
                case "year":
                    return SaResult.data("兑换成功!兑换时长：1年");
            }
        }

        return SaResult.error("兑换失败，请联系管理员");
    }


}
