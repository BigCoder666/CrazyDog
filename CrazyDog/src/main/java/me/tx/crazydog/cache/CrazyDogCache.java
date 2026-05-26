package me.tx.crazydog.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Field;

public class CrazyDogCache<T> {
    private final SharedPreferences sp;
    private final Class<T> tClass;
    private Field[] cachedFields;

    public CrazyDogCache(Context context, Class<T> tClass) {
        this.tClass = tClass;
        sp = context.getSharedPreferences(tClass.getName(), Context.MODE_PRIVATE);
        cachedFields = tClass.getDeclaredFields();
    }

    public void save(T t) {
        if (t == null) return;

        SharedPreferences.Editor editor = sp.edit();

        for (Field field : cachedFields) {
            try {
                field.setAccessible(true);
                Object value = field.get(t);
                String fieldName = field.getName();
                if (value == null) {
                    editor.putString(fieldName, null);
                    continue;
                }
                editor.putString(fieldName, String.valueOf(value));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        editor.apply();
    }

    public T getCache() {
        T instance = JSON.parseObject("{}", tClass);
        for (Field field : cachedFields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String valueStr = sp.getString(fieldName, null);

            if (valueStr == null) continue;

            try {
                Class<?> type = field.getType();
                // 根据类型自动转换
                if (type == int.class || type == Integer.class) {
                    field.set(instance, Integer.parseInt(valueStr));
                } else if (type == boolean.class || type == Boolean.class) {
                    field.set(instance, Boolean.parseBoolean(valueStr));
                } else if (type == long.class || type == Long.class) {
                    field.set(instance, Long.parseLong(valueStr));
                } else if (type == float.class || type == Float.class) {
                    field.set(instance, Float.parseFloat(valueStr));
                } else {
                    field.set(instance, valueStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    // 清空缓存
    public void clear() {
        sp.edit().clear().apply();
    }
}