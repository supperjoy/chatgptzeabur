package com.chatgpt.service;

import cn.dev33.satoken.util.SaResult;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chatgpt.dto.CommodityandOrder;
import com.chatgpt.entity.Order;
import com.chatgpt.entity.Role;

import java.text.ParseException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author young
 * @since 2023年04月21日
 */
public interface OrderService extends IService<Order> {

    SaResult queryOrder(CommodityandOrder commodityandOrder) throws AlipayApiException, InterruptedException, ParseException;

    SaResult getPage(int page, int pageSize, String search);

    SaResult deleteOrder(Long id);
}
