/*
 * Copyright (c) 2020 Blaumeise03
 */

package de.blaumeise03.toolbox;

public enum AfkMode {
    NONE(0), MANUEL_AFK(1), PRE_AFK(2), FINAL_AFK(3);

    int priority;

    AfkMode(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
