package com.luol.transaction.common.bean.adapter;

import org.bson.types.ObjectId;

import java.io.Serializable;

/**
 * @author luol
 * @date 2018/4/2
 * @time 10:43
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class MongoAdapter extends CoordinatorRepositoryAdapter implements Serializable {

    private static final long serialVersionUID = 8804188145680989204L;

    private ObjectId objectID;
}
