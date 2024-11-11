package my.lrn.squirrel.telepante;


public enum TaskEvent {
    HANDLE_OPEN(1, "开门请求"),
    HANDLE_PUT(2, "放入大象请求"),
    HANDLE_CLOSE(3, "关门请求");

    private int event;
    private String desc;

    TaskEvent(int event, String desc) {
        this.event = event;
        this.desc = desc;
    }
}
