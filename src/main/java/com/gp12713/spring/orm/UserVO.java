package com.gp12713.spring.orm;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

@Data
@Table(name = "user_t")
public class UserVO implements Serializable {
    private Integer id;
    private String name;
    private Integer age;
    private Long createDate;
}
