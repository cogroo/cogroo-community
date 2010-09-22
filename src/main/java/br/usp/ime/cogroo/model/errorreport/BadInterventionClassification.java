package br.usp.ime.cogroo.model.errorreport;


public enum BadInterventionClassification {

    FALSE_ERROR("falseError"),
    INAPPROPRIATE_DESCRIPTION("inappropriateDescription"),
    INAPPROPRIATE_SUGGESTION("inappropriateSuggestion");

    private final String value;

    BadInterventionClassification(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BadInterventionClassification fromValue(String v) {
        for (BadInterventionClassification c: BadInterventionClassification.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}