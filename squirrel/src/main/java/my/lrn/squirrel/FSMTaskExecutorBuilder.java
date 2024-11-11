package my.lrn.squirrel;

import org.squirrelframework.foundation.fsm.StateMachineBuilder;
import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;

/**
 * @author 邓伟
 * @date 2023/02/17
 * @since 2023/2/17 5:07 PM
 */
public class FSMTaskExecutorBuilder {

    private StateMachineBuilder<FSM, TaskState, TaskEvent, TaskContext> builder;

    public FSMTaskExecutorBuilder() {
        init();
    }

    private void init() {
        builder = StateMachineBuilderFactory.create(FSM.class, TaskState.class, TaskEvent.class, TaskContext.class);

        builder.externalTransition().from(TaskState.WAIT_OPEN).to(
            TaskState.WAIT_PUT).on(TaskEvent.HANDLE_OPEN).callMethod("open");
        builder.externalTransition().from(TaskState.WAIT_PUT).to(
            TaskState.WAIT_CLOSE).on(TaskEvent.HANDLE_PUT).callMethod("put");
        builder.externalTransition().from(TaskState.WAIT_CLOSE).to(
            TaskState.END).on(TaskEvent.HANDLE_CLOSE).callMethod("close");
    }

    public FSM build(TaskState initTaskState) {
        FSM executor = builder.newStateMachine(initTaskState);
        return executor;
    }
}
