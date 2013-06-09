RegionShop
==========

RegionShop is a WorldGuard depending player shop plugin which was built to offer comfort and simplicity for the main part. Create a WorldGuard region, follow our naming convention for the region name and that's it. The shop is ready to get stocked with items!

This can be easily done with the innovative "quick equip mode" to bulk add several items in one step and quote them afterwards. Of course you can use the good ol' command method as well. 

Another neat feature lets you search for a specific item so you don't have to warp to ten shops just to check their item list and to get disappointed if they don't offer your desired Diamond Sword. Just type `/shop search Diamond Sword` to get a list of shops which offer your item, another command allows you to warp to that shop in seconds. 

Feel free to try out this plugin to convince yourself. For more informations take a look in the wiki pages above.

# Permissions

    rs.help             # /shop, /shop help
    rs.list             # /shop list
    rs.warp.owner       # /shop warp <owner>
    rs.warp.region      # /shop warp <region>
    rs.stock.add        # /shop add <sell> <buy> <amount>
    rs.stock.set        # /shop set <shopitemid> <sell> <buy> <amount>
    rs.stock.remove     # /shop remove <shopitemid>
    rs.stock.equip      # /shop equip
    rs.detail           # /shop detail <shopitemid>
    rs.search           # /shop search <itemname/itemid>
    rs.sell             # /shop sell
    rs.buy              # /shop buy <shopitemid> <amount>


# config.yml
    debug: false                    # Debug for finding or reporting Errors
    only-ascii: false               # Users can only give ASCII valid names for their Shops

    chat:
        prefix: '[RS] '             # Prefix for Chatmessages

    features:
        addToShopViaDropItem: true  # Allows to be able to drop items into a Shop


# Depencies

* [WorldGuard](http://dev.bukkit.org/bukkit-mods/worldguard)
* [WorldEdit](http://dev.bukkit.org/bukkit-mods/worldedit)