package gam.sua;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Menu extends JPanel{

    private Items[] inventory;

    //Font
    private final Font font = Font.createFont(Font.TRUETYPE_FONT, new File("Font\\pixelmix.ttf"));

    //Text
    private final JLabel tNormal = new JLabel();
    private final JLabel tNotes = new JLabel();
    private final JLabel[] tNormalText = {tNormal,tNotes};

    Menu() throws IOException, FontFormatException {
        initText();
    }

    public void initText(){
        for (int i = 0; i < 2; i++){

            float size = (i != 1) ? 16f:12f;
            tNormalText[i].setFont(font.deriveFont(size));
            tNormalText[i].setForeground(Color.BLACK);
        }
    }

    public void showMenu(Player player, int[] done, int round, int steps){//An int array parameter [0,0,0] should modify "used"
        String[] used = {"","",""};

        for (int i = 0; i < 3; i++){
            if (done[i] == 1)
                used[i] = "USED";
        }

        tNormal.setText("<html>Round - "+ round +"<br><br>"+ player.getName() +"<br>*Health: "+ player.getHealth() +
                "<br>*Steps Left: "+steps+"<br>*DMG: "+ player.getDMG() +" Range: "+ player.getWeaponRange() +"<br><br>+"+
                player.getSpecialAb()[0] +"<br>+"+ player.getSpecialAb()[1] +"<br>+"+ player.getSpecialAb()[2] +
                "<br><br>[M] Move      "+ used[0] +"<br>[SPACE] Attack      "+ used[1] +"<br>[I] Inventory      "+
                used[2] +"<br>[N] Next Turn</html>");

        tNotes.setText("<html>*Can only do each action once per turn</html>");

        tNormal.setBounds(0,-30,230,300);
        tNotes.setBounds(0,20*13,230,50);

        this.add(tNormal);
        this.add(tNotes);
    }

    public void showInventory(Player player, List<Items> sharedInv){
        String items = " ";
        String equipped = equippedWeapon(player,sharedInv);
        List<String> alreadyIn = new ArrayList<>();
        int i = 1;

        for (Items item:sharedInv){
            String equ = " ";

            if (item.getName().equals(equipped))
                equ = " (Equipped)";

            if (item instanceof Weapons){
                items += "["+i+"] "+ item.getName() + " DMG: " + ((Weapons) item).getDamage() + " Range: " + ((Weapons) item).getRange() + equ +"<br><br>";
                //i++;
            } else {
                if (!alreadyIn.contains(item.getName())){
                    items += "["+i+"] "+ item.getName() + " " + amountInInv(sharedInv, item.getId())+ " in Inventory<br><br>";
                    //i++;
                }
                alreadyIn.add(item.getName());
            }
            i++;
        }

        tNotes.setText("<html>Shared Inventory<br><br>"+ items +
                "</html>");

        tNotes.setBounds(0,0,230,300);
        this.add(tNotes);
    }

    public int amountInInv(List<Items> sharedInv, int id){
        int res = 0;

        for (Items item: sharedInv){
            if (item.getId() == id)
                res++;
        }

        return res;
    }

    public String equippedWeapon(Player player, List<Items> sharedInv){
        int DMG = player.getDMG();
        if (player.getDoubleDamage())
            DMG/=2;

        int wRange = player.getWeaponRange();

        for (Items item:sharedInv) {
            if (((Weapons) item).getDamage() == DMG && ((Weapons) item).getRange() == wRange)
                return item.getName();
        } return null;
    }
}

//https://stackoverflow.com/questions/685521/multiline-text-in-jlabel