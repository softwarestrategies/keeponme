package io.softwarestrategies.keeponme.common.data.enumeration;

public enum UserGoalStatus {
    NEW("N"),
    PENDING("P"),
    ACTIVE("A"),
    INACTIVE("I"),
    OPEN("O"),
    CLOSED("C"),
    DELETED("D");

    private final String abbreviation;

    UserGoalStatus(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return this.abbreviation;
    }

    public static UserGoalStatus fromAbbreviation(String abbr) {
        for (UserGoalStatus status : UserGoalStatus.values()) {
            if (status.abbreviation.equalsIgnoreCase(abbr)) {
                return status;
            }
        }
        throw new UnsupportedOperationException("This Status abbreviation is not supported: " + abbr);
    }

    public static UserGoalStatus fromName(String name) {
        for (UserGoalStatus status : UserGoalStatus.values()) {
            if (status.name().equalsIgnoreCase(name)) {
                return status;
            }
        }
        throw new UnsupportedOperationException("This Status name is not supported: " + name);
    }
}
