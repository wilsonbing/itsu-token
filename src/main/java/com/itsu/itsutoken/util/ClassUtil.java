package com.itsu.itsutoken.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import com.itsu.itsutoken.annotation.PrivateKey;
import com.itsu.itsutoken.annotation.PublicKey;
import com.itsu.itsutoken.annotation.SimpleToken;
import com.itsu.itsutoken.annotation.SysName;
import com.itsu.itsutoken.annotation.TableId;
import com.itsu.itsutoken.domain.IdType;
import com.itsu.itsutoken.table.TableSample;

import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import cn.hutool.core.annotation.AnnotationUtil;

public class ClassUtil {

    public static String getId(@NonNull Class<? extends TableSample> clazz) throws Exception {
        return getValue(clazz, TableId.class);
    }

    public static IdType getIdStrategy(@NonNull Class<? extends TableSample> clazz) throws Exception {
        IdType idType = null;
        List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
        for (Field field : fields) {
            if (field.isAnnotationPresent(TableId.class)) {
                idType = AnnotationUtil.getAnnotationValue(field, TableId.class, "type");
                break;
            }
        }
        return idType;
    }

    public static String getSysValue(@NonNull Class<? extends TableSample> clazz) throws Exception {
        return getValue(clazz, SysName.class);
    }

    public static String getSimpleTokenValue(@NonNull Class<? extends TableSample> clazz) throws Exception {
        return getValue(clazz, SimpleToken.class);
    }

    public static String getPrivateKeyValue(@NonNull Class<? extends TableSample> clazz) throws Exception {
        return getValue(clazz, PrivateKey.class);
    }

    public static String getPublicKeyValue(@NonNull Class<? extends TableSample> clazz) throws Exception {
        return getValue(clazz, PublicKey.class);
    }

    private static String getValue(Class<? extends TableSample> clazz, Class<? extends Annotation> annotationType) {
        String value = null;
        List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
        for (Field field : fields) {
            if (!StringUtils.isEmpty(AnnotationUtil.getAnnotationValue(field, annotationType))) {
                value = AnnotationUtil.getAnnotationValue(field, annotationType);
                break;
            }
        }
        return value;
    }
}