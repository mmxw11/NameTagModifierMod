package com.mmxw11.nametags;

public enum NameTagMode {
    
    HIDE("HIDE"),
    EDIT("EDIT");

    private final String name;
    private static final NameTagMode[] v = values();

    private NameTagMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static NameTagMode getByName(String name) {
        for (NameTagMode mode : v) {
            if (mode.name.equalsIgnoreCase(name)) {
                return mode;
            }
        }
        return null;
    }

    public NameTagMode nextEnum() {
        return v[(ordinal() + 1) % v.length];
    }
}