package my.lrn.squirrel.trade.state;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.GenericTypeResolver;
import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;
import org.squirrelframework.foundation.fsm.UntypedStateMachine;
import org.squirrelframework.foundation.fsm.UntypedStateMachineBuilder;

/**
 * 创建无类型化状态机，简化状态机，防止过多泛化导致代码不易阅读
 * 通过Spring创建StateMachineBuilder实例，通过buidler创建状态机(单例)
 * 业务函数中通过StateMachineBuilder实例创建StateMachine实例，并向StateMachine暴露SpringApplicationContext，以便于StateMachine通过ApplicationContext获取数据层的对象
*/
@Slf4j
public class AbstractStateMachineEngine <T extends UntypedStateMachine> implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    protected UntypedStateMachineBuilder stateMachineBuilder = null;

    @SuppressWarnings("unchecked")
    public AbstractStateMachineEngine() {
        //识别泛型参数
        Class<T> genericType = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(),
                                                                                  AbstractStateMachineEngine.class);
        stateMachineBuilder = StateMachineBuilderFactory.create(genericType, ApplicationContext.class);
    }
    //注入applicationContext，并在创建StateMachine实例时注入
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
    *    可以通过向OrderContext 上下文传递一些业务参数，比如orderId等等
    */
    public boolean fire(EOrderEvents event, OrderContext context) {
        T stateMachine = stateMachineBuilder.newUntypedStateMachine(
                context.geteOrder().getOrderStatus(),applicationContext);
        
        // 添加监听器
//        stateMachine.addStateMachineListener(new StateMachineListener<UntypedStateMachine, Object, Object, Object>() {
//            @Override
//            public void stateMachineEvent(StateMachineEvent<UntypedStateMachine, Object, Object, Object> event) {
//                log.info("lastState: " + event.getStateMachine().getLastState());
//            }
//        });
//        stateMachine.addDeclarativeListener(new DeclarativeEventListener());
        // 源码中的日志 demo
//        StateMachineLogger logger = new StateMachineLogger(stateMachine);
//        logger.startLogging();
        
        //由于StateMachine实例不是由Spring容器创建，所以这个过程中无法通过注解方式开启事务(Spring没有机会去创建事务代理)，因此采用了编程式事务
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager)applicationContext.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            stateMachine.fire(event, context);
            transactionManager.commit(status);
            //这里会返回状态机是否出错，如果出错可用于通知Controller层
            return stateMachine.isError();
        } catch (Exception ex) {
            //用于事务回滚
            transactionManager.rollback(status);
            return true;
        }
    }
}