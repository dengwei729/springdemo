package my.lrn.squirrel.trade.state;

import lombok.Getter;

@Getter
public enum OrderState {
    INIT(1,"开始"),
    WAIT_PAY(2,"待支付"),
    WAIT_SEND(3,"待发送"),
    PART_SEND(4,"配送"),
    WAIT_RECEIVE(5,"待接收"),
    COMPLETE(6,"完成"),
    CANCELED(7,"取消");
    
    private String desc;
    private int code;

    public static OrderState getState(String state) {
        for (OrderState orderState : OrderState.values()) {
            if (orderState.name().equalsIgnoreCase(state)) {
                return orderState;
            }
        }
        return null;
    }
    
    public static OrderState getState(int code) {
        for (OrderState orderState : OrderState.values()) {
            if (orderState.ordinal()+1 == code) {
                return orderState;
            }
        }
        return null;
    }
}
