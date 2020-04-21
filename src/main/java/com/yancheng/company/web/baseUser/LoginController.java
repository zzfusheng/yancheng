package com.yancheng.company.web.baseUser;

import com.yancheng.company.dal.user.BaseUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.yancheng.company.service.user.UserLoginService;

@Controller
@RequestMapping(value = "/baseLogin")
public class LoginController {
    @Autowired
    UserLoginService userLoginService;

    @RequestMapping("/login/info.json")
    @ResponseBody
    public BaseUser login(){
       BaseUser user= userLoginService.getUserInfo();
        return  user;
    }

}
