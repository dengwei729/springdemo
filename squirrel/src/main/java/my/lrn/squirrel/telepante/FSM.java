package my.lrn.squirrel.telepante;

import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;

/**
 * @author 邓伟
 * @date 2023/02/17
 * @since 2023/2/17 5:06 PM
 */
public class FSM extends AbstractStateMachine<FSM, TaskState, TaskEvent, TaskContext> {
    private void print(TaskState from, TaskState to) {
        System.out.println(from.getDesc() + " -> " + to.getDesc());
    }
    protected void open(TaskState from, TaskState to, TaskEvent event, TaskContext ctx) {
        print(from, to);
        this.fire(TaskEvent.HANDLE_PUT, ctx);
    }

    protected void put(TaskState from, TaskState to, TaskEvent event, TaskContext ctx) {
        print(from, to);
        this.fire(TaskEvent.HANDLE_CLOSE, ctx);
    }
    protected void close(TaskState from, TaskState to, TaskEvent event, TaskContext ctx) {
        print(from, to);
    }
}
