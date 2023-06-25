package com.chatgpt.utils;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCancelRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.chatgpt.dto.CommodityandOrder;
import com.chatgpt.entity.Order;
import com.chatgpt.entity.User;
import com.chatgpt.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.TimeUnit;

public class AlipayUtil {
    // 设置支付宝网关地址，通常不需要修改
    public static final String GATEWAY_URL = "https://openapi.alipay.com/gateway.do";

    // 支付宝应用的 APPID
    public static final String APP_ID = AlipayConstants.APP_ID;

    // 商户私钥
    public static final String APP_PRIVATE_KEY = AlipayConstants.APP_PRIVATE_KEY;

    // 支付宝公钥
    public static final String ALIPAY_PUBLIC_KEY = AlipayConstants.ALIPAY_PUBLIC_KEY;

    // 轮询等待时长
    public static final int WAITING_TIME = 5;

    // 轮询次数
    public static final int MAX_TRY_TIMES = 60;

    // 查询支付结果
    public static SaResult queryTrade(CommodityandOrder commodityandOrder) throws AlipayApiException, InterruptedException {
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, APP_ID, APP_PRIVATE_KEY, "json", "UTF-8", ALIPAY_PUBLIC_KEY, "RSA2");
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent("{" +
                "\"out_trade_no\":\"" +  commodityandOrder.getOrderId() + "\"" +
                "}");
        int tryTimes = 0;
        while (tryTimes < MAX_TRY_TIMES) {
            TimeUnit.SECONDS.sleep(WAITING_TIME);
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                String tradeStatus = response.getTradeStatus();
                if (tradeStatus.equals("TRADE_SUCCESS")) {
                    return SaResult.data("success");
                } else if (tradeStatus.equals("TRADE_CLOSED")) {
                    return SaResult.data("交易已关闭");
                }
            }
            tryTimes++;
        }
        // 最后一次查询仍然返回等待用户付款的情况下，必须立即调用 alipay.trade.cancel 将这笔交易撤销
        request = new AlipayTradeQueryRequest();
        request.setBizContent("{" +
                "\"out_trade_no\":\"" + commodityandOrder.getOrderId() + "\"" +
                "}");
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        if (response.isSuccess()) {
            String tradeStatus = response.getTradeStatus();
            if (tradeStatus.equals("WAIT_BUYER_PAY")) {
                return cancelTrade(commodityandOrder.getOrderId());
            }
        }
        return SaResult.error("出错了");
    }

    // 撤销支付交易
    public static SaResult cancelTrade(String outTradeNo) throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, APP_ID, APP_PRIVATE_KEY, "json", "UTF-8", ALIPAY_PUBLIC_KEY, "RSA2");
        AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
        request.setBizContent("{" +
                "\"out_trade_no\":\"" + outTradeNo + "\"" +
                "}");
        AlipayTradeCancelResponse response = alipayClient.execute(request);
        if (response.isSuccess()) {
            String resultCode = response.getCode();
            if (resultCode.equals("10000")) {
                return SaResult.data("撤销成功");
            }
        }
        return SaResult.data("撤销失败");
    }
}
