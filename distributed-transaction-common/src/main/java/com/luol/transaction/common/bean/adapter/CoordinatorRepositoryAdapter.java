package com.luol.transaction.common.bean.adapter;

import com.luol.transaction.common.enums.NtcRoleEnum;
import com.luol.transaction.common.enums.NtcStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author luol
 * @date 2018/4/2
 * @time 10:23
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Data
@NoArgsConstructor
public class CoordinatorRepositoryAdapter {

    /**
     * 事务ID
     */
    private String transID;

    /**
     * 事务状态 {@linkplain NtcStatusEnum}
     */
    private int status;

    /**
     * 事务类型 {@linkplain NtcRoleEnum}
     */
    private int role;

    /**
     * 最大重试次数
     * */
    private Integer maxRetryCounts;

    /**
     * 当前重试次数
     * */
    private Integer currentRetryCounts;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date lastTime;

    /**
     * 版本号 乐观锁控制
     */
    private Integer version = 1;

    /**
     * 模式
     */
    private Integer pattern;

    /**
     * 序列化后的二进制信息
     */
    private byte[] contents;

    /**
     * 调用接口名称
     */
    private String targetClass;

    /**
     * 调用方法名称
     */
    private String targetMethod;

    /**
     * rpc调用链的json
     * */
    private String rpcCallChain;

    /**
     * 模块名称
     * */
    private String modelName;

}
