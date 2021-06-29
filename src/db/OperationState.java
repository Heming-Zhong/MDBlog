package db;

import java.util.*;
public class OperationState {
    public enum State {
        normal,
        warning,
        error,
    };
    public State retState;
    public String msg;
    public String ret;
    public List<String> retList;

    public OperationState(State state, String message, String returns) {
        retState = state;
        msg = message;
        ret = returns;
        retList = null;
    }

    public OperationState(State state, String message, List<String> returns) {
        retState = state;
        msg = message;
        ret = null;
        retList = returns;
    }

}
