package com.chatgpt.service;

import cn.dev33.satoken.util.SaResult;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chatgpt.entity.Commodity;
import com.chatgpt.entity.Order;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author young
 * @since 2023年04月21日
 */
public interface CommodityService extends IService<Commodity> {

    SaResult getPage(int page, int pageSize, String search);

    SaResult addCommodity(Commodity commodity);

    SaResult deleteCommodity(Long id);

    SaResult editCommodity(Commodity commodity);
}
