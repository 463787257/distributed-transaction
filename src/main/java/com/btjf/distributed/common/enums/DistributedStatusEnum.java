package com.btjf.distributed.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author luol
 * @date 2018/1/29
 * @time 15:41
 * @function 功能：
 * @contentribe 版本描述：
 * @modifyLog 修改日志：
 */
public enum DistributedStatusEnum {
    
    BEGIN("开始", 1),
    COMMIT("已经提交", 2),
    PRE_FAILURE("失败未重新执行", 3),
    FAILURE("失败", 4),
    SUCCESS("成功", 5);


    private int value;

    private String content;

    DistributedStatusEnum(String content, int value) {
        this.value = value;
        this.content = content;
    }


    public static DistributedStatusEnum acquireByValue(int value) {
        Optional<DistributedStatusEnum> transactionStatusEnum =
                Arrays.stream(DistributedStatusEnum.values())
                        .filter(v -> Objects.equals(v.getValue(), value))
                        .findFirst();
        return transactionStatusEnum.orElse(DistributedStatusEnum.BEGIN);

    }

    public static String acquirecontentByValue(int value) {
        return acquireByValue(value).getContent();
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
