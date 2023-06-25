package com.chatgpt.service;

import cn.dev33.satoken.util.SaResult;
import com.alipay.api.AlipayApiException;
import com.chatgpt.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.zxing.WriterException;

import java.io.IOException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author young
 * @since 2023年04月21日
 */
public interface UserService extends IService<User> {

    SaResult getPages(int page, int pageSize, String search);

    SaResult addUser(User user);

    SaResult setStatus(User user);

    SaResult deleteUser(Long id);

    SaResult editUser(User user);

    SaResult getCode(Long commodityId) throws AlipayApiException, IOException, WriterException;

    SaResult getOrderStatus(String orderId) throws AlipayApiException;

}
