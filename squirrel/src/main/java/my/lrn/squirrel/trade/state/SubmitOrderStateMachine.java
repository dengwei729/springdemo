package my.lrn.squirrel.trade.state;

import lombok.extern.slf4j.Slf4j;
import my.lrn.squirrel.trade.service.OrderService;
import org.springframework.context.ApplicationContext;
import org.squirrelframework.foundation.fsm.*;
import org.squirrelframework.foundation.fsm.annotation.State;
import org.squirrelframework.foundation.fsm.annotation.StateMachineParameters;
import org.squirrelframework.foundation.fsm.annotation.States;
import org.squirrelframework.foundation.fsm.annotation.Transit;
import org.squirrelframework.foundation.fsm.annotation.Transitions;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

/**
 * 定义 触发事件、状态变化时，调用的方法
 * @States 定义状态列表，里面可以包含多个状态
 * @State定义每个状态，name状态名称，entryStateInit进入状态时调用的方法，exitCallMethod 离开状态是调用的方法，initialState 为true时，为默认状态。
 * */
@States({
        @State(name = "INIT", entryCallMethod = "entryStateInit", exitCallMethod = "exitStateInit", initialState = true),
        @State(name = "WAIT_PAY", entryCallMethod = "entryStateWaitPay", exitCallMethod = "exitStateWaitPay"),
        @State(name = "WAIT_SEND", entryCallMethod = "entryStateWaitSend", exitCallMethod = "exitStateWaitSend"),
        @State(name = "PART_SEND", entryCallMethod = "entryStatePartSend", exitCallMethod = "exitStatePartSend"),
        @State(name = "WAIT_RECEIVE", entryCallMethod = "entryStateWaitReceive", exitCallMethod = "exitStateWaitReceive"),
        @State(name = "COMPLETE", entryCallMethod = "entryStateComplete", exitCallMethod = "exitStateComplete")
})
@Transitions({
        @Transit(from = "INIT", to = "WAIT_PAY", on = "SUBMIT_ORDER", callMethod = "submitOrder"),
        @Transit(from = "WAIT_PAY", to = "WAIT_SEND", on = "PAY", callMethod = "pay"),
        @Transit(from = "WAIT_SEND", to = "PART_SEND", on = "PART_SEND", callMethod = "partSend"),
        @Transit(from = "PART_SEND", to = "WAIT_RECEIVE", on = "SEND", callMethod = "send"),
        @Transit(from = "WAIT_RECEIVE", to = "COMPLETE", on = "COMPLETE", callMethod = "complete")
})
// @StateMachineParameters用来声明状态机泛型参数类型，向AbstractStateMachine传递参数
@StateMachineParameters(stateType = OrderState.class, eventType = OrderEvent.class, contextType = OrderContext.class)
@Slf4j
public class SubmitOrderStateMachine extends AbstractStateMachine<UntypedStateMachine, Object, Object, Object>
    implements UntypedStateMachine {

    private OrderService orderService;
    protected ApplicationContext applicationContext;
    
    //定义构造函数接受ApplicationContext注入
    //([参看New State Machine Instance](http://hekailiang.github.io/squirrel/))
    public SubmitOrderStateMachine(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        // 通过applicationContext注入orderService
        this.orderService = applicationContext.getBean(OrderService.class);
    }

    // 状态转换时调用的方法，需要将方法名配置在 callMethod 内
    // 若【方法名】符合 transitFrom[fromStateName]To[toStateName] 格式，不需要配置 callMethod
    public void submitOrder(OrderState fromState, OrderState toState, OrderEvent Event, OrderContext Context) {
        log.info("转换事件 {}=>{} on {} with {}.", fromState, toState, event, context);
        orderService.submitOrder(toState);
    }
    
    //...

    public void complete(OrderState fromState, OrderState toState, OrderEvent Event, OrderContext Context) {
        log.info("转换事件 {}=>{} on {} with {}.", fromState, toState, event, context);
        System.out.println("complete");
    }

    // 符合 entry[StateName] 格式，不需要配置 callMethod
    public void entryStateInit(OrderState fromState, OrderState toState, OrderEvent Event, OrderContext Context) {
        log.info("进入状态 {}=>{} on {} with {}.", from, to, event, context);
        System.out.println("entryStateInit");
    }

    // 符合 exit[StateName] 格式，不需要配置 callMethod
    public void exitStateInit(OrderState fromState, OrderState toState, OrderEvent Event, OrderContext Context) {
        log.info("退出状态 {}=>{} on {} with {}.", from, to, event, context);
        System.out.println("exitStateInit");
    }
    
    //...
    
    // ==========================================================================================
    // 如果不想用 DeclarativeEventListener 这种声明在单独类里的方法，可以直接重写以下方法，效果是一样的
    // 两者同时用也可以，为了代码方便最好别这样
    // ==========================================================================================
    @Override
    protected void afterTransitionCausedException(Object fromState, Object toState, Object event, Object context) {
        /**
        * 当状态转换过程中出现异常，已执行的action列表将失效并且状态机会进入error状态，意思就是状态机实例不会再处理任何event。假如用户继续向状态机发送event，便会抛出IllegalStateException异常。所有状态转换过程中发生的异常，包括action执行和外部listener调用，会被包装成TransitionException（未检查异常）。目前，默认的异常处理策略非常简单并且粗暴的连续抛出异常，可以参阅AbstractStateMachine.afterTransitionCausedException方法。
        */
        log.info("Override 发生错误 {}", getLastException().getMessage());
        Throwable targeException = getLastException().getTargetException();
    // recover from IllegalArgumentException thrown out from state 'A' to 'B' caused by event 'ToB'
        if(targeException instanceof IllegalArgumentException &&
                fromState.equals("A") && toState.equals("B") && event.equals("ToB")) {
            // do some error clean up job here
            // ...
            // after recovered from this exception, reset the state machine status back to normal
            setStatus(StateMachineStatus.IDLE);
        }
        //else if(...) {
        //    // recover from other exception ...
        //}
        else {
             // afterTransitionCausedException 默认的实现是直接抛异常
            super.afterTransitionCausedException(fromState, toState, event, context);
        }
    }
    @Override
    protected void beforeTransitionBegin(Object fromState, Object event, Object context) {
        // 转换开始时被调用
        System.out.println();
//        super.beforeTransitionBegin(fromState, event, context);
        log.info("Override beforeTransitionBegin");
    }
    @Override
    protected void afterTransitionCompleted(Object fromState, Object toState, Object event, Object context) {
        // 转换完成时被调用
//        super.afterTransitionCompleted(fromState, toState, event, context);
        log.info("Override afterTransitionCompleted");
        if (context instanceof StateMachineContext && toState instanceof State) {
            StateMachineContext stateMachineContext = (StateMachineContext)context;
            //从上下文中获取需要持久化的数据，例如订单ID等
            Rma rma = stateMachineContext.get(MessageKeyEnum.RMA);
            //持久化
            rma.setStatus((State)toState);
            this.applicationContext.get("rmaRepository").updateRma(rma);
        } else {
            throw new Exception("type not support, context expect " + StateMachineContext.class.getSimpleName() + ", actually "
                    + context.getClass().getSimpleName() + ", state expect " + State.class.getSimpleName()
                    + ", actually "
                    + toState.getClass().getSimpleName());
        }
    }
    @Override
    protected void afterTransitionEnd(Object fromState, Object toState, Object event, Object context) {
        // 转换结束时被调用
 //       super.afterTransitionEnd(fromState, toState, event, context);
        log.info("Override afterTransitionEnd");
    }
    @Override
    protected void afterTransitionDeclined(Object fromState, Object event, Object context) {
        // 当转换被拒绝时被调用。实际是调用 callMethod 中的方法被调用时，抛出异常时被调用
//        super.afterTransitionDeclined(fromState, event, context);
        log.info("Override afterTransitionDeclined");
    }
    @Override
    protected void beforeActionInvoked(Object fromState, Object toState, Object event, Object context) {
        // 当转换开始时被调用。实际是 callMethod 中的方法被调用时，先调用该方法。类似于 AOP 的效果
 //       super.beforeActionInvoked(fromState, toState, event, context);
        log.info("Override beforeActionInvoked");
    }
    @Override
    protected void afterActionInvoked(Object fromState, Object toState, Object event, Object context) {
        // 当转换结束时被调用。实际是 callMethod 被调用后，调用该方法。类似于 AOP 的效果
 //       super.afterActionInvoked(fromState, toState, event, context);
        log.info("Override afterActionInvoked");
    }
}