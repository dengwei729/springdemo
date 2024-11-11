package my.lrn.squirrel;

import org.junit.jupiter.api.Test;

/**
 * @author 邓伟
 * @date 2023/02/17
 * @since 2023/2/17 5:23 PM
 */
public class SquirrelTest {
    @Test
    public void test() {
        FSMTaskExecutorBuilder builder = new FSMTaskExecutorBuilder();

        FSM fsm = builder.build(TaskState.WAIT_OPEN);

        TaskContext taskContext = new TaskContext();
        System.out.println(fsm.getCurrentState());
        fsm.fire(TaskEvent.HANDLE_OPEN, taskContext);
        System.out.println(fsm.getCurrentState());
    }
}
