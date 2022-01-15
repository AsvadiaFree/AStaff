package fr.asvadia.astaff.utils;

import org.bukkit.Material;

public enum TopLuckOres {
    ALL("", null),
    ASVADIUM("Asvadium", Material.CYAN_GLAZED_TERRACOTTA),
    TOPAZE("Topaze", Material.ORANGE_GLAZED_TERRACOTTA),
    RUBIS("Rubis", Material.BLACK_GLAZED_TERRACOTTA),
    SAPHIR("Saphir", Material.BLUE_GLAZED_TERRACOTTA),
    DIAMOND("Diamant", Material.DIAMOND_ORE),
    EMERALD("Emeraude", Material.EMERALD_ORE);

    private final String name;
    private final Material ore;

    TopLuckOres(String name, Material ore) {
        this.name = name;
        this.ore = ore;
    }

    public Material getOre() {
        return ore;
    }

    public String getName() {
        return name;
    }
}
