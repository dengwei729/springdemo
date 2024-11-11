package my.lrn.squirrel.telepante;

import lombok.Getter;

public enum TaskState {
    WAIT_OPEN(1, "等待开门"),
    WAIT_PUT(2, "等待放入大象"),
    WAIT_CLOSE(3, "等待关门"),

    END(4, "结束");

    private int state;

    @Getter
    private String desc;

    TaskState(int state, String desc) {
        this.state = state;
        this.desc = desc;
    }
}
