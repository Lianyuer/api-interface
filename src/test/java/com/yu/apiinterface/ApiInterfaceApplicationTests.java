package com.yu.apiinterface;

import com.yu.yuapiclientsdk.client.YuApiClient;
import com.yu.yuapiclientsdk.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
class ApiInterfaceApplicationTests {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private YuApiClient yuApiClient;

    @Test
    void contextLoads() {
    }

    @Test
    void test() {
//        ApiClient apiClient = new ApiClient("testAccessKey", "testSecretKey");

        String result1 = yuApiClient.getNameByGet("lisi");
        String result2 = yuApiClient.getNameByPost("lisi");
        User user = new User();
        user.setUserName("李四维");
        String result3 = yuApiClient.getUserNameByPost(user);
        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);
    }

}
