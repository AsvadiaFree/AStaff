package fr.asvadia.astaff.utils;

import org.bukkit.Material;

public enum TopLuckOres {
    ALL("", null),
    ASVADIUM("Asvadium", Material.COAL_ORE),
    TOPAZE("Topaze", Material.DIAMOND_ORE),
    RUBIS("Rubis", Material.COPPER_ORE),
    SAPHIR("Saphir", Material.EMERALD_ORE);

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

    public static TopLuckOres getByOre(Material material) {
        for (TopLuckOres topLuckOres : TopLuckOres.values())
            if (topLuckOres.getOre() == material)
                return topLuckOres;
        return null;
    }
}
