package my.lrn.spring.controller;

import my.lrn.squirrel.trade.domain.OrderDTO;
import my.lrn.squirrel.trade.state.OrderContext;
import my.lrn.squirrel.trade.state.OrderEvent;
import my.lrn.squirrel.trade.state.OrderState;
import my.lrn.squirrel.trade.state.OrderStateMachineEngine;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/order/")
public class OrderController {

    // 注入状态机
    @Autowired
    OrderStateMachineEngine orderStateMachineEngine;


    @RequestMapping("/test")
    public void test(){
        OrderDTO orderDTO = new OrderDTO(OrderState.INIT);
        OrderContext orderContext = new OrderContext(orderDTO);
        // 启动状态机，触发某事件，发生状态变化
        orderStateMachineEngine.fire(OrderEvent.SUBMIT_ORDER, orderContext);
    }
}