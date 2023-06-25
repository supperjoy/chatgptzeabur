package com.chatgpt.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chatgpt.dto.PayInfo;
import com.chatgpt.entity.Commodity;
import com.chatgpt.entity.Role;
import com.chatgpt.entity.User;
import com.chatgpt.mapper.UserDao;
import com.chatgpt.service.RoleService;
import com.chatgpt.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chatgpt.utils.AlipayConstants;
import com.chatgpt.utils.OrderUtils;
import com.chatgpt.utils.QrCodeUtil;
import com.chatgpt.utils.TimeUtils;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author young
 * @since 2023年04月21日
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {
//    @Value("${payInfo.alipay_public_key}")
    private final String alipay_public_key = AlipayConstants.ALIPAY_PUBLIC_KEY;
//    @Value("${payInfo.private_key}")
    private final String private_key = AlipayConstants.APP_PRIVATE_KEY;
//    @Value("${payInfo.app_id}")
    private final String app_id = AlipayConstants.APP_ID;
    AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",app_id,private_key,"json","GBK",alipay_public_key,"RSA2");
    @Autowired
    private UserService userService;
    @Autowired
    private CommodityServiceImpl commodityService;

    @Autowired
    private RoleService roleService;
    @Override
    public SaResult getPages(int page, int pageSize, String search) {
        if(StpUtil.getPermissionList().contains("admin")){
            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
            lqw.ne(User::getId, StpUtil.getLoginId());
            lqw.like(StringUtils.isNotBlank(search),User::getUsername,search)
                    .or().like(StringUtils.isNotBlank(search),User::getTelephone,search)
                    .or().like(StringUtils.isNotBlank(search),User::getEmail,search);

            IPage<User> pageInfo = new Page<>(page,pageSize);
            IPage<User> page1 = userService.page(pageInfo, lqw);


//        Page pages = PageListUtils.getPages(page, pageSize, userService.list());
            return SaResult.data(page1);
        }
        return SaResult.error("您没有权限");

    }

    @Override
    public SaResult addUser(User user) {
        if(StpUtil.getPermissionList().contains("admin")){
            if(!user.isNotEmpty()){
                return SaResult.error("请勿通过非法手段注册");
            }
            //首先检验用户名是否存在
            LambdaQueryWrapper<User> lqw3 = new LambdaQueryWrapper<>();
            lqw3.eq(User::getUsername,user.getUsername());
            if(userService.getOne(lqw3)!=null){
                return SaResult.error("该用户名已被注册");
            }
            LambdaQueryWrapper<User> lqw1 = new LambdaQueryWrapper<>();
            lqw1.eq(User::getTelephone,user.getTelephone());
            if(userService.getOne(lqw1)!=null){
                return SaResult.error("该手机号已被注册");
            }
            LambdaQueryWrapper<User> lqw2 = new LambdaQueryWrapper<>();
            lqw2.eq(User::getEmail,user.getEmail());
            if(userService.getOne(lqw2)!=null){
                return SaResult.error("该邮箱已被注册");
            }

            // 创建 SimpleDateFormat 对象并指定日期格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            // 获取当前时间并格式化
            user.setCreateTime(sdf.format(new Date()));

            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            //如果expireTime为空或空字符串的话  则设置为当前日期
            if(StringUtils.isBlank(user.getExpireTime())){
                user.setExpireTime(sdf1.format(new Date()));
            }
            else{
                user.setExpireTime(user.getExpireTime());
            }


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
            return SaResult.error("添加失败");
        }
        return SaResult.error("您没有权限");

    }

    @Override
    public SaResult setStatus(User user) {
        if(StpUtil.getPermissionList().contains("admin")){
            user.setStatus(user.getStatus()==0 ? 1 : 0);
            userService.updateById(user);
            return SaResult.ok();
        }
        return SaResult.error("您没有权限");
    }

    @Override
    public SaResult deleteUser(Long id) {
        if(StpUtil.getPermissionList().contains("admin")){
            if(userService.removeById(id) && roleService.removeById(id)){
                return SaResult.ok();
            }
            else{
                return SaResult.error("删除失败");
            }
        }
        return SaResult.error("您没有权限");

    }

    @Override
    public SaResult editUser(User user) {
        if(StpUtil.getPermissionList().contains("admin")){
            if(!user.isNotEmpty()){
                return SaResult.error("数据异常");
            }
            User checkUser = userService.getById(user.getId());
            if(!user.getUsername().equals(checkUser.getUsername())){
                LambdaQueryWrapper<User> lqw3 = new LambdaQueryWrapper<>();
                lqw3.eq(User::getUsername,user.getUsername());
                if(userService.getOne(lqw3)!=null){
                    return SaResult.error("用户名已存在");
                }
            }
            if(!user.getTelephone().equals(checkUser.getTelephone())){
                LambdaQueryWrapper<User> lqw1 = new LambdaQueryWrapper<>();
                lqw1.eq(User::getTelephone,user.getTelephone());
                if(userService.getOne(lqw1)!=null){
                    return SaResult.error("手机号已存在");
                }
            }
            if(!user.getEmail().equals(checkUser.getEmail())){
                LambdaQueryWrapper<User> lqw2 = new LambdaQueryWrapper<>();
                lqw2.eq(User::getEmail,user.getEmail());
                if(userService.getOne(lqw2)!=null){
                    return SaResult.error("邮箱已存在");
                }
            }

            // 设置时区为东八区，即北京时间
            ZoneId zoneId = ZoneId.of("Asia/Shanghai");
            // 获取当前时间
            LocalDateTime now = LocalDateTime.now(zoneId);
            // 定义日期时间格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // 格式化日期时间
            String timeString = now.format(formatter);

            //如果expireTime为空或空字符串的话  则设置为当前日期
            if(StringUtils.isBlank(user.getExpireTime())){
                user.setExpireTime(timeString);
            }
            else{
                user.setExpireTime(user.getExpireTime());
            }

            boolean isUpdate = userService.updateById(user);
            if(isUpdate){
                return SaResult.ok();
            }
            return SaResult.error("更新失败");
        }
        return SaResult.error("您没有权限");


    }

    @Override
    public SaResult getCode(Long commodityId) throws AlipayApiException, IOException, WriterException {
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest ();
        request.setNotifyUrl("");
        JSONObject bizContent = new JSONObject();
        String orderId = OrderUtils.generateOrderId();
        System.out.println(app_id+alipay_public_key+private_key);
//        String orderId = "20230425194733912123";
        Commodity commodity = commodityService.getById(commodityId);
        String productName = commodity.getProductName();
        bizContent.put("out_trade_no", orderId);
        bizContent.put("total_amount", commodity.getMoney());
        bizContent.put("subject", productName);
        request.setBizContent(bizContent.toString());
        AlipayTradePrecreateResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            PayInfo payInfo = new PayInfo();
            payInfo.setOrderId(response.getOutTradeNo());
            long timestamp = System.currentTimeMillis();
            String currentTime = TimeUtils.timeStamp2Date(timestamp, "yyyy-MM-dd HH:mm:ss");
            payInfo.setCreateTime(currentTime);
            payInfo.setUrl(response.getQrCode());
            payInfo.setProductName(productName);
            payInfo.setImgCode(QrCodeUtil.generateQrCode(response.getQrCode()));
            return SaResult.data(payInfo);
        } else {
            return SaResult.error("出错了");
        }

    }

    @Override
    public SaResult getOrderStatus(String orderId) throws AlipayApiException {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no",orderId);
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = alipayClient.execute(request);
//        return SaResult.data(response.getTradeStatus());
        if(response.isSuccess()){
            return SaResult.data(response.getTradeStatus());
        } else {
            return SaResult.error("出错了");
        }
    }


}
