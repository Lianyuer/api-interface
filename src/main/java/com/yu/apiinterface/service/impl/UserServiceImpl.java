package com.yu.apiinterface.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.apiinterface.service.UserService;
import com.yu.apiinterface.model.entity.User;
import com.yu.apiinterface.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author liany
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2026-01-19 02:13:38
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

}




