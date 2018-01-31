package com.btjf.distributed.annotation;

/**
 * @author luol
 * @date 2018/1/29
 * @time 13:40
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public enum TransactionType {

    MQ_RETRY("MQ重试", 1);

    private Integer code;

    private String desc;

    TransactionType(String desc, Integer code) {
        this.desc = desc;
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
