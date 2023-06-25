package com.chatgpt.service;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chatgpt.entity.Redemption;

import java.text.ParseException;

public interface RedemptionService extends IService<Redemption> {
    SaResult add(Redemption redemption);

    SaResult delete(Long id);

    SaResult getPage(int page, int pageSize, String search);

    SaResult getRedem(Redemption redemption) throws ParseException;
}
