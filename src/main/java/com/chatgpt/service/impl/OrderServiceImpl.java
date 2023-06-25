package com.chatgpt.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chatgpt.dto.CommodityandOrder;
import com.chatgpt.entity.Commodity;
import com.chatgpt.entity.Order;
import com.chatgpt.entity.User;
import com.chatgpt.mapper.OrderDao;
import com.chatgpt.service.CommodityService;
import com.chatgpt.service.OrderService;
import com.chatgpt.service.UserService;
import com.chatgpt.utils.AlipayUtil;
import com.chatgpt.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
public class OrderServiceImpl extends ServiceImpl<OrderDao, Order> implements OrderService {
    @Autowired
    private UserService userService;
    @Autowired
    private CommodityService commodityService;
    @Autowired
    private OrderService orderService;


    @Override
    public SaResult queryOrder(@RequestBody CommodityandOrder commodityandOrder) throws AlipayApiException, InterruptedException, ParseException {
        SaResult saResult = AlipayUtil.queryTrade(commodityandOrder);
        if(saResult.getData().equals("success")){
            Order order = new Order();
            order.setOrderId(commodityandOrder.getOrderId());
            Commodity commodity = commodityService.getById(commodityandOrder.getCommodityId());
            order.setMoney(commodity.getMoney());
            order.setProduct(commodity.getProductName());
            order.setUsername(commodityandOrder.getUsername());
            order.setNumber(commodity.getNumber());

            long timestamp = System.currentTimeMillis();
            String currentTime = TimeUtils.timeStamp2Date(timestamp, "yyyy-MM-dd HH:mm:ss");
            order.setCreateTime(currentTime);
            boolean save = orderService.save(order);

            //增加用户时长
            String idStr = StpUtil.getLoginId().toString();
            Long id = Long.valueOf(idStr);
            User user = userService.getById(id);

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

            switch (commodity.getTime()) {
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

            user.setCurrentNumber(commodity.getNumber());
            user.setTotalNumber(commodity.getNumber());

            boolean isAddExpire = userService.updateById(user);

            if (save && isAddExpire){
                return SaResult.ok("创建订单成功");
            }
        }
        return SaResult.error("创建订单失败");
    }

    @Override
    public SaResult getPage(int page, int pageSize, String search) {
        if(StpUtil.getPermissionList().contains("admin")){
            LambdaQueryWrapper<Order> lqw = new LambdaQueryWrapper<>();
            lqw.like(StringUtils.isNotBlank(search),Order::getOrderId,search)
                    .or().like(StringUtils.isNotBlank(search),Order::getUsername,search);

            IPage<Order> pageInfo = new Page<>(page,pageSize);
            IPage<Order> page1 = orderService.page(pageInfo, lqw);
            return SaResult.data(page1);
        }
       return SaResult.error("您没有权限");
    }

    @Override
    public SaResult deleteOrder(Long id) {
        if(StpUtil.getPermissionList().contains("admin")){
            if(orderService.removeById(id)){
                return SaResult.ok();
            }
            else{
                return SaResult.error("删除失败");
            }
        }
        return SaResult.error("您没有权限");
    }
}
