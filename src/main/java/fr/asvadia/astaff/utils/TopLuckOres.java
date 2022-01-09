package fr.asvadia.astaff.utils;

import org.bukkit.Material;

public enum TopLuckOres {
    ALL(null),
    ASVADIUM(Material.COAL_ORE),
    TOPAZE(Material.DIAMOND_ORE),
    RUBIS(Material.COPPER_ORE),
    SAPHIR(Material.EMERALD_ORE);

    private final Material ore;

    TopLuckOres(Material ore) {
        this.ore = ore;
    }

    public Material getOre() {
        return ore;
    }

    public static TopLuckOres getByOre(Material material) {
        for (TopLuckOres topLuckOres : TopLuckOres.values())
            if (topLuckOres.getOre() == material)
                return topLuckOres;
        return null;
    }
}
