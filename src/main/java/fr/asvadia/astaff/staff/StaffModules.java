package fr.asvadia.astaff.staff;

import fr.asvadia.astaff.staff.modules.*;
import fr.asvadia.astaff.staff.modules.Module;

public enum StaffModules {
    VANISH("Vanish", new Vanish()),
    RANDOM_TP("RandomTp", new RandomTp()),
    PLAYER_VIEWER("PlayerViewer", new PlayerViewer()),
    FREEZE("Freeze", new Freeze()),
    XRay("XRay", new XRay()),
    TopLuck("TopLuck", new TopLuck());

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
