package com.luol.test.disruptor.demo;

/**
 * @author luol
 * @date 2018/3/29
 * @time 16:40
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class TradeTransaction {
    private String id;//交易ID
    private double price;//交易金额

    public TradeTransaction() {
    }
    public TradeTransaction(String id, double price) {
        super();
        this.id = id;
        this.price = price;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
}
