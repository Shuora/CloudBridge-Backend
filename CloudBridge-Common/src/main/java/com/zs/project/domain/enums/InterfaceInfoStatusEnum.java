package com.zs.project.domain.enums;


import org.springframework.util.ObjectUtils;

/**
 * 用户角色枚举
 *
 * @author ZhuangShuo
 */
public enum InterfaceInfoStatusEnum {

    OFFLINE("关闭", 0),
    ONLINE("开启", 1);

    private final String text;

    private final int value;

    InterfaceInfoStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static InterfaceInfoStatusEnum getEnumByValue(int value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (InterfaceInfoStatusEnum anEnum : InterfaceInfoStatusEnum.values()) {
            if (anEnum.value == value) {
                return anEnum;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
