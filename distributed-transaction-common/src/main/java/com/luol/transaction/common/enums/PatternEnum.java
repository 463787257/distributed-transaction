package com.luol.transaction.common.enums;

import com.luol.transaction.common.enums.base.BaseEnum;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * @author luol
 * @date 2018/3/7
 * @time 16:04
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public enum PatternEnum implements BaseEnum {

    NOTICE_ROLLBACK("优先通知---不成功再回滚", NumberUtils.INTEGER_ZERO),
    ONLY_ROLLBACK("不通知---直接回滚", NumberUtils.INTEGER_ONE);

    private String content;
    private Integer value;

    PatternEnum(String content, Integer value) {
        this.content = content;
        this.value = value;
    }

    @Override
    public String getContent() {
        return this.content;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    public static PatternEnum objectOf(Object object) {
        Optional<PatternEnum> enums = Arrays.stream(PatternEnum.values())
                .filter(v -> Objects.equals(v.getContent(), object) || Objects.equals(v.getValue(), object))
                .findFirst();
        return enums.orElse(PatternEnum.NOTICE_ROLLBACK);
    }
}
