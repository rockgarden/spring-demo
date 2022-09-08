package com.example.servingwebcontent;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping("/account")
    public String index(Model m) {
        final String P = "e10adc3949ba59abbe56e";
        List<Account> list = new ArrayList<>();
        list.add(new Account("KangKang", "康康", P, "超级管理员", "17777777777"));
        list.add(new Account("Mike", "麦克", P, "管理员", "13444444444"));
        list.add(new Account("Jane", "简", P, "运维人员", "18666666666"));
        list.add(new Account("Maria", "玛利亚", P, "清算人员", "19999999999"));
        m.addAttribute("accountList", list);
        return "account";
    }

}
