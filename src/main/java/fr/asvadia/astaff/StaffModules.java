package fr.asvadia.astaff;

public enum StaffModules {
    VANISH("vanish");

    private final String module;

    StaffModules(String module) {
        this.module = module;
    }

    public String getModule() {
        return module;
    }

    public static StaffModules getByModule(String module) {
        for (StaffModules staffModule : StaffModules.values())
            if (staffModule.getModule().equals(module))
                return staffModule;
        return null;
    }
}
