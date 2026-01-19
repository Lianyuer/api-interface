package com.yu.apiinterface.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yu.apiinterface.model.entity.User;
import com.yu.apiinterface.service.UserService;
import com.yu.apiinterface.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/get")
    public String getNameByGet(String name) {
        return "GET 你的名字是" + name;
    }

    @PostMapping("/getName")
    public String getNameByPost(@RequestParam String name) {
        return "Post 你的名字是" + name;
    }

    @PostMapping("/getUserName")
    public String getUserNameByPost(@RequestBody User user, HttpServletRequest request) {
        String accessKey = request.getHeader("accessKey");
        String timestamp = request.getHeader("timestamp");
        String nonceStr = request.getHeader("nonceStr");
        String encodedBody = request.getHeader("body");
        String rawBody = new String(Base64.decode(encodedBody), StandardCharsets.UTF_8);
        String signature = request.getHeader("signature");
        // 从数据库中查询secretKey
        User existUser = userService.getOne(new QueryWrapper<User>().eq("accessKey", accessKey));
        if (existUser == null) {
            throw new RuntimeException("无权限");
        }
        // 构造需要参与签名的参数
        String secretKey = existUser.getSecretKey();
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("accessKey", accessKey);
        paramsMap.put("timestamp", timestamp);
        paramsMap.put("nonceStr", nonceStr);
        paramsMap.put("body", rawBody);
        // 重新生成签名
        String twiceSignature = SignUtils.signature(timestamp, accessKey, secretKey, nonceStr, rawBody, paramsMap);
        if (!twiceSignature.equals(signature)) {
            throw new RuntimeException("签名错误");
        }
        // 请求 5 分钟有效期
        if (System.currentTimeMillis() - Long.parseLong(timestamp) > 5 * 1000 * 60) {
            throw new RuntimeException("请求过期");
        }
        // 从 redis 中获取随机数，如果 5 分钟内获取到相同随机数，则校验不通过
        String nonceStrKey = "api:nonceStr:" + accessKey + ":" + nonceStr;
        String nonceStrCache = stringRedisTemplate.opsForValue().get(nonceStrKey);
        if (nonceStr.equals(nonceStrCache)) {
            throw new RuntimeException("重复请求");
        } else {
            stringRedisTemplate.opsForValue().set(nonceStrKey, nonceStr, 5 * 60, TimeUnit.SECONDS);
        }
        return "Post 用户名字是" + user.getUserName();
    }
}
