package my.lrn.squirrel.trade.state;

public enum OrderEvent {

    SUBMIT_ORDER,    //提交订单；INIT ==> WAIT_PAY
    PAY,            //支付完成；WAIT_PAY ==> WAIT_SEND
    PART_SEND,        //等待配送；WAIT_SEND ==> PART_SEND
    SEND,            //配送中；PART_SEND ==> WAIT_RECEIVE
    COMPLETE        //完成；WAIT_RECEIVE ==> COMPLETE
}
