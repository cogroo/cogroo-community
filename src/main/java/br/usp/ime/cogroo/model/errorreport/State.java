package br.usp.ime.cogroo.model.errorreport;

public enum State {
	OPEN("open"), 
	INPROGRESS("inProgress"), 
	RESOLVED("resolved"), 
	FEEDBACK("feedback"), 
	CLOSED("closed"), 
	REJECTED("reject");

    private final String value;

    State(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static State fromValue(String v) {
        for (State c: State.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
