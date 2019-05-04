package com.gp12713.spring.orm;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "user_t")
public class UserVO implements Serializable {
    private Integer id;
    private String name;
    private Integer age;
    private Date createDate;
}
