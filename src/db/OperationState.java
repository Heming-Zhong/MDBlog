package db;

public class OperationState {
    public enum State {
        normal,
        warning,
        error,
    };
    public State retState;
    public String msg;
    public String ret;

    public OperationState(State state, String message, String returns) {
        retState = state;
        msg = message;
        ret = returns;
    }
}
