package org.dragonet.bukkit.lnations;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.dragonet.bukkit.menuapi.ItemMenuInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 2017/11/18.
 */
public class GUIUtils {

    public static void showConfirmationBox(Player player, String name, List<String> display, Runnable action) {
        List<String> prompt;
        if(display != null) {
            prompt = new ArrayList<>();
            display.forEach((s) -> prompt.add("&f" + s));
        } else {
            prompt = Collections.emptyList();
        }

        ItemMenuInstance box = new ItemMenuInstance(Lang.build("confirmation.title"), 18);
        box.setButton(4, Material.PAPER, name, display, ((humanEntity, itemMenuInstance) -> {}));

        box.setButton(9+2, Material.STAINED_CLAY, (short) 5, Lang.build("confirmation.option-yes"), Collections.emptyList(), ((humanEntity, itemMenuInstance) -> {
            player.closeInventory();
            action.run();
        }));
        box.setButton(9+6, Material.STAINED_CLAY, (short) 14, Lang.build("confirmation.option-no"), Collections.emptyList(), ((humanEntity, itemMenuInstance) -> {
            player.closeInventory();
            Lang.sendMessage(player, "confirmation.cancelled");
        }));

        LegendaryNationsPlugin.getInstance().getMenus().open(player, box);
    }

}
