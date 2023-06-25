package com.chatgpt.config;

import com.chatgpt.Interceptor.LoginInterceptor;
import com.chatgpt.utils.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport{

    /**
     * 扩展mvc框架的消息转换器
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//        创建消息转换器
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
//        设置对象转换器，底层使用Jackson将Java对象转换为Json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
//        将上面的消息转换器追加到mvc框架集合中
        converters.add(0,messageConverter);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 设置允许跨域的路径
        registry.addMapping("/**")
                // 设置允许跨域请求的域名
                .allowedOriginPatterns("*")
                // 是否允许cookie
                .allowCredentials(true)

                // 设置允许的请求方式
                .allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS")
                // 设置允许的header属性
                .allowedHeaders("*")
                // 跨域允许时间
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截所有请求路径

        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(
                        "/common/login",
                        "/common/captcha",
                        "/common/adminlogin",
                        "/user/checklogin",
                        "/user/getexpire",
                        "/common/register",
                        "/order/query",
                        "/redemption/getRedem"
                );

    }
}
