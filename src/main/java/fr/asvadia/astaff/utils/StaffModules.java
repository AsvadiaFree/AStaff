package fr.asvadia.astaff.utils;

import fr.asvadia.astaff.modules.*;

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

    public static StaffModules getByName(String name) {
        for (StaffModules staffModule : StaffModules.values())
            if (staffModule.getName().equals(name))
                return staffModule;
        return null;
    }

    public static StaffModules getByModule(Module module) {
        for (StaffModules staffModule : StaffModules.values())
            if (staffModule.getModule().equals(module))
                return staffModule;
        return null;
    }
}
