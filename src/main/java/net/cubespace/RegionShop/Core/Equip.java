package net.cubespace.RegionShop.Core;

public class Equip {
    /* public static Integer equip(ItemStack item, Player player, ItemStorageHolder shop) {
        Group group = ConfigManager.main.getGroup(shop.getItemStorage().getSetting());

        //Check if there is place in the Storage
        if(shop.getItemStorage().getItemAmount() + item.getAmount() >= group.Storage) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Add_FullStorage);

            return -1;
        }

        final Items dbItem = Database.getServer().find(Items.class).
                where().
                    conjunction().
                        eq("itemStorage", shop.getItemStorage()).
                        eq("meta.id.itemID", item.getType().getId()).
                        eq("meta.id.dataValue", item.getData().getData()).
                        eq("durability", item.getDurability()).
                        eq("owner", player.getName()).
                        eq("customName", (item.getItemMeta().hasDisplayName()) ? item.getItemMeta().getDisplayName() : null).
                    endJunction().
                findUnique();

        if (dbItem != null) {
            dbItem.setCurrentAmount(dbItem.getCurrentAmount() + item.getAmount());
            dbItem.getItemStorage().setItemAmount(dbItem.getItemStorage().getItemAmount() + item.getAmount());

            RegionShopPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(RegionShopPlugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    Database.getServer().update(dbItem.getItemStorage());
                    Database.getServer().update(dbItem);             }
            });

            Transaction.generateTransaction(player, com.geNAZt.RegionShop.Database.Table.Transaction.TransactionType.EQUIP, shop.getName(), shop.getWorld(), dbItem.getOwner(), dbItem.getMeta().getId().getItemID(), item.getAmount(), dbItem.getSell().doubleValue(), dbItem.getBuy().doubleValue(), dbItem.getUnitAmount());

            return 0;
        } else {
            final Items newDbItem = Item.toDBItem(item, shop, player.getName(), 0.0F, 0.0F, 0);

            newDbItem.getItemStorage().setItemAmount(newDbItem.getItemStorage().getItemAmount() + item.getAmount());

            RegionShopPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(RegionShopPlugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    Database.getServer().update(newDbItem.getItemStorage());
                }
            });

            Transaction.generateTransaction(player, com.geNAZt.RegionShop.Database.Table.Transaction.TransactionType.EQUIP, shop.getName(), player.getWorld().getName(), player.getName(), newDbItem.getMeta().getId().getItemID(), item.getAmount(), 0.0, 0.0, 0);

            return newDbItem.getId();
        }
    } */
}
