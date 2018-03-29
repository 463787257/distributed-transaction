package com.luol.transaction.common.enums;

import com.luol.transaction.common.enums.base.BaseEnum;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author luol
 * @date 2018/3/7
 * @time 16:35
 * @function 功能：事物当前状态枚举
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public enum NtcStatusEnum implements BaseEnum {

    TRY_BEGIN("try阶段开始", 1),
    TRY_END("try阶段完成", 2),
    CANCEL("cancel阶段", 3),
    NOTIFY("通知阶段", 4),
    SUCCESS("成功阶段", 5);

    private String content;
    private Integer value;

    NtcStatusEnum(String content, Integer value) {
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

    public static NtcStatusEnum objectOf(Object object) {
        if (object instanceof String) {
            NtcStatusEnum ntcStatusEnum = NtcStatusEnum.valueOf(object.toString());
            if (Objects.nonNull(ntcStatusEnum)) {
                return ntcStatusEnum;
            }
        }
        Optional<NtcStatusEnum> enums = Arrays.stream(NtcStatusEnum.values())
                .filter(v -> Objects.equals(v.getContent(), object) || Objects.equals(v.getValue(), object))
                .findFirst();
        return enums.orElse(NtcStatusEnum.TRY_BEGIN);
    }
}
