package com.btjf.distributed.common.enums;

/**
 * @author luol
 * @date 2018/1/29
 * @time 16:15
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public enum  CoordinatorActionEnum {

    SAVE("保存", 0),
    DELETE("删除", 1),
    UPDATE("更新", 2),
    ROLLBACK("回滚", 3),
    COMPENSATION("补偿", 4),
    UPDATE_STATUS("更新状态", 5);


    private int value;

    private String content;

    CoordinatorActionEnum(String content, int value) {
        this.value = value;
        this.content = content;
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
