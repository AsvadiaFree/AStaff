package fr.asvadia.astaff.utils;

import fr.asvadia.astaff.modules.*;
import fr.asvadia.astaff.modules.Module;

public enum StaffModules {
    VANISH("Vanish", new Vanish()),
    RANDOM_TP("RandomTp", new RandomTp()),
    PLAYER_VIEWER("PlayerViewer", new PlayerViewer()),
    FREEZE("Freeze", new Freeze()),;

    private final String name;
    private final Module module;

    StaffModules(String name, Module module) {
        this.name = name;
        this.module = module;
    }

    public Module getModule() {
        return module;
    }

    public String getName() {
        return name;
    }
}
