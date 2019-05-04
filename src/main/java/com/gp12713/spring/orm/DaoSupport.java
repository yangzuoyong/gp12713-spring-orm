package com.gp12713.spring.orm;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class DaoSupport {
    public static <T> List<T> select(T condition) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            //1.加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            //2.获取连接
           // con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/orm_db", "root", "123456");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/orm_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC", "root", "123456");
            //3.获取语句集
            pstm = con.prepareStatement(parseSql(condition));
            //4.得到结果集
            rs = pstm.executeQuery();
            //5.解析并返回结果集
            return parseResult(condition, rs);

        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            // 6.关闭资源
            try {
                if (null != rs) {
                    rs.close();
                }
                if (null != pstm) {
                    pstm.close();
                }
                if (null != con) {
                    con.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static <T> List<T> parseResult(T condition, ResultSet rs) throws Exception {
        Map<String, String> columnFieldMap = parseFieldColumnMap(condition);
        List<T> result = new ArrayList<T>();
        while (rs.next()) {
            Class<T> clazz = (Class<T>) condition.getClass();
            T t = clazz.newInstance();
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                String columnName = rs.getMetaData().getColumnName(i);
                Field field = clazz.getDeclaredField(columnFieldMap.get(columnName));
                field.setAccessible(true);
                field.set(t, rs.getObject(columnName));
            }
            result.add(t);
        }
        return result;
    }

    private static <T> Map<String, String> parseFieldColumnMap(T condition) {
        Map<String, String> map = new HashMap<String, String>();
        Class<T> clazz = (Class<T>) condition.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            map.put(field.getName(), field.getName());
            if (field.isAnnotationPresent(Column.class)) {
                map.put(field.getAnnotation(Column.class).name(), field.getName());
            }
        }
        return map;
    }

    private static <T> String parseSql(T condition) {
        Class<T> clazz = (Class<T>) condition.getClass();
        String tableName = clazz.getSimpleName();
        if (clazz.isAnnotationPresent(Table.class)) {
            tableName = clazz.getAnnotation(Table.class).name();
        }
        StringBuffer sb = new StringBuffer("select * from " + tableName + " where 1 = 1 ");
        for (Field field : clazz.getDeclaredFields()) {
            String columnName = field.getName();
            field.setAccessible(true);//取消 Java 语言访问检查
            Object fieldValue = null;
            try {
                fieldValue = field.get(condition);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (null == fieldValue) {
                continue;
            }
            if (field.isAnnotationPresent(Column.class)) {
                columnName = field.getAnnotation(Column.class).name();
            }
            sb.append(" and " + columnName + " = " + fieldValue);
        }
        log.info("sql: " + sb.toString());
        return sb.toString();
    }
}
