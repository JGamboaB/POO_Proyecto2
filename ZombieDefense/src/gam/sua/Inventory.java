package gam.sua;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private final List<Items> inventory = new ArrayList<>();
    private final String[] name = {"Crossbow","Mighty Sword","Long Bow","Throwable Spears","Shurikens",
            "War Axe", "Slingshot","Health Potion","Revive Potion","Antidote"};
    private final int[] DMG = {5,5,5,10,15,15,5};
    private final int[] range = {3,2,4,3,3,2,5};
    private final boolean[] sound = {true,false,true,true,false,false,false};
    private final int[] health = {25,100,0};
    private final boolean[] curesPoison = {false,true,true};


    /** Creates the items and adds them to the inventory List */
    public void createItems(){
        for (int i = 0; i < 10; i++){
            if (i < 7){
                Weapons item = new Weapons(i,name[i],DMG[i],sound[i],range[i]);
                inventory.add(item);
            } else {
                Consumable item = new Consumable(i,name[i],health[i-7],curesPoison[i-7]);
                inventory.add(item);
            }
        }
    }


    /** Calls the function createItems() */
    public Inventory(){createItems();}


    /** Returns the inventory attribute
     * @return inventory */
    public List<Items> getInventory(){return inventory;}


    /** Returns the item in the inventory list by their id
     * @param id
     * @return item */
    public Items getItemById(int id){
        for (Items item: inventory){
            if (item.getId() == id)
                return item;
        } return null;
    }
}
