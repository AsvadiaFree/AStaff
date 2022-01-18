package fr.asvadia.astaff.utils.file;

public enum Files {
    Config("config"),
    Staff("staff"),
    Lang("lang"),
    WorldScanner("ws");

    private final String name;

    Files(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
