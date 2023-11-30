package main;

import java.util.Arrays;

public enum LinkType {
    FILM("Film"), SERIE("Série");

    public final String readableName;

    LinkType(String readableName) {
        this.readableName = readableName;
    }

    public static LinkType getByReadableName(String readableName) {
        return Arrays.stream(LinkType.values())
                .filter(t -> t.readableName.equals(readableName))
                .findAny()
                .orElseThrow(() -> new RuntimeException(String.format("Le type %s n'existe pas", readableName)));
    }
}
