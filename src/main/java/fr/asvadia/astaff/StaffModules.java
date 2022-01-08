package fr.asvadia.astaff;

import fr.asvadia.astaff.modules.Module;
import fr.asvadia.astaff.modules.Vanish;

public enum StaffModules {
    VANISH("vanish", new Vanish());

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
