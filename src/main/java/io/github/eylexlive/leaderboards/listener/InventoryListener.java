package io.github.eylexlive.leaderboards.listener;

import io.github.eylexlive.leaderboards.util.inventory.InventoryUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListener implements Listener {

    @EventHandler
    public void handleUIEvent(final InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof InventoryUI.InventoryUIHolder) || event.getCurrentItem() == null)
            return;
        final InventoryUI.InventoryUIHolder inventoryUIHolder = (InventoryUI.InventoryUIHolder) event.getInventory().getHolder();
        event.setCancelled(true);
        if (event.getClickedInventory() == null || !event.getInventory().equals(event.getClickedInventory()))
            return;
        final InventoryUI ui = inventoryUIHolder.getInventoryUI();
        final InventoryUI.ClickableItem item = ui.getCurrentUI().getItem(event.getSlot());
        if (item == null)
            return;
        item.onClick(event);
    }
}
