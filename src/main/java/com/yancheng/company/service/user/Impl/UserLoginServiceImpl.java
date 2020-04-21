package com.yancheng.company.service.user.Impl;

import com.yancheng.company.dal.user.BaseUser;
import com.yancheng.company.dal.user.mapper.BaseUserMapper;
import com.yancheng.company.service.user.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLoginServiceImpl implements UserLoginService {
    @Autowired
   private BaseUserMapper baseUserMapper;

    @Override
    public BaseUser getUserInfo() {
        BaseUser user= baseUserMapper.selectByPrimaryKey(1);
        return user;
    }
}
