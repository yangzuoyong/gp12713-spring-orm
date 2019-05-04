package com.gp12713.spring.orm;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Slf4j
public class ORMTest {
    public static void main(String[] args) {
        UserVO userVO = new UserVO();
        userVO.setId(1);
        List<UserVO> userVOS = DaoSupport.select(userVO);
        for (UserVO vo : userVOS) {
            log.info("id:"+vo.getId()+",name:"+vo.getName(),",age:"+vo.getAge());
        }
    }
}
