package gam.sua;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Menu extends JPanel{

    //Image
    private final JLabel playerLabel = new JLabel();

    //Font
    private final Font font = Font.createFont(Font.TRUETYPE_FONT, new File("Font\\pixelmix.ttf"));

    //Text
    private final JLabel tNormal = new JLabel();
    private final JLabel tNotes = new JLabel();
    private final JLabel[] tNormalText = {tNormal,tNotes};


    /**Constructor
     * @throws IOException if there's an error by getting the font.
     * @throws FontFormatException if there's an error by getting the font.
     */
    Menu() throws IOException, FontFormatException {initText();}

    public void initText(){
        for (int i = 0; i < 2; i++){

            float size = (i != 1) ? 16f:12f;
            tNormalText[i].setFont(font.deriveFont(size));
            tNormalText[i].setForeground(Color.BLACK);
        }
    }

    /** The menu panel displays the normal state of the menu
     * @param player Player
     * @param done int[]
     * @param round int
     * @param steps int
     */
    public void showMenu(Player player, int[] done, int round, int steps){//An int array parameter [0,0,0] should modify "used"
        String[] used = {"","",""};
        String poison = (player.getIsPoisoned() > 0)?"<br>*Is Poisoned ("+player.getIsPoisoned()+")":"<br>";

        for (int i = 0; i < 3; i++){
            if (done[i] == 1)
                used[i] = "USED";
        }

        tNormal.setText("<html>Round - "+ round +"<br><br>"+ player.getName() +"<br>*Health: "+ player.getHealth() +
                "<br>*Steps Left: "+steps+"<br>*DMG: "+ player.getDMG() +" Range: "+ player.getWeaponRange() +poison+"<br><br>+"+
                player.getSpecialAb()[0] +"<br>+"+ player.getSpecialAb()[1] +"<br>+"+ player.getSpecialAb()[2] +
                "<br><br>[M] Move      "+ used[0] +"<br>[SPACE] Attack      "+ used[1] +"<br>[I] Inventory      "+
                used[2] +"<br>[N] Next Turn</html>");

        tNotes.setText("<html>*Can only do each action once per turn</html>");

        playerLabel.setIcon(new ImageIcon("images\\CharIMG\\"+player.getId()+".png"));

        tNormal.setBounds(0,-30,230,320);
        tNotes.setBounds(0,20*13,230,50);
        playerLabel.setBounds(170,10,32,32);

        this.add(tNormal);
        this.add(tNotes);
        this.add(playerLabel);
    }

    /**
     * The menu panel displays the inventory.
     * @param player Player
     * @param sharedInv List<Items>
     */
    public void showInventory(Player player, List<Items> sharedInv){
        StringBuilder items = new StringBuilder(" ");
        String equipped = equippedWeapon(player,sharedInv);
        List<String> alreadyIn = new ArrayList<>();
        int i = 0;

        for (Items item:sharedInv){
            String equ = " ";

            if (item.getName().equals(equipped))
                equ = " (Equipped)";

            if (item instanceof Weapons){
                items.append("[").append(i).append("] ").append(item.getName()).append(" DMG: ").append(((Weapons) item).getDamage()).append(" Range: ").append(((Weapons) item).getRange()).append(equ).append("<br><br>");
            } else {
                if (!alreadyIn.contains(item.getName())){
                    items.append("[").append(i).append("] ").append(item.getName()).append(" ").append(amountInInv(sharedInv, item.getId())).append(" in Inventory<br><br>");
                }
                alreadyIn.add(item.getName());
                //items += "["+i+"] "+ item.getName() + "<br><br>";
            }
            i++;
        }

        tNotes.setText("<html>Shared Inventory<br><br>"+ items + "</html>");

        tNotes.setBounds(0,0,230,300);
        this.add(tNotes);
    }

    /**Returns the amount of items in the inventory List
     * @param sharedInv List<Items>
     * @param id int
     * @return res
     */
    public int amountInInv(List<Items> sharedInv, int id){
        int res = 0;

        for (Items item: sharedInv){
            if (item.getId() == id)
                res++;
        }

        return res;
    }

    /** Returns the name of the equipped weapon by a player
     * @param player Player
     * @param sharedInv List<Items>
     * @return name
     */
    public String equippedWeapon(Player player, List<Items> sharedInv){
        int DMG = player.getDMG();
        if (player.getDoubleDamage())
            DMG/=2;

        int wRange = player.getWeaponRange();

        for (Items item:sharedInv) {
            if (item instanceof Consumable)
                continue;
            if (((Weapons) item).getDamage() == DMG && ((Weapons) item).getRange() == wRange)
                return item.getName();
        } return null;
    }
}