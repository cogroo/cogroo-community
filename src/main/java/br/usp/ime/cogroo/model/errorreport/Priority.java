package br.usp.ime.cogroo.model.errorreport;

public enum Priority {
	LOW("low"), 
	NORMAL("normal"), 
	HIGH("high"), 
	URGENT("urgent"), 
	IMMEDIATE("immediate");
	
    private final String value;

    Priority(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Priority fromValue(String v) {
        for (Priority c: Priority.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}