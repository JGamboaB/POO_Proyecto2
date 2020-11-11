package gam.sua;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Menu extends JPanel{

    //private int opt = 0; //0 Menu - 1 Inventory
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

    public void showMenu(Player player, int[] done){//An int array parameter [0,0,0] should modify "used"
        String[] used = {"",""};

        for (int i = 0; i < 2; i++){
            if (done[i] == 1)
                used[i] = "USED";
        }

        tNormal.setText("<html>Name: " + player.getName() + "<br>Health: " + player.getHealth() + "<br>Move Range: " +
                player.getRange() + "<br><br>Special Abilities: <br>+" + player.getSpecialAb()[0] + "<br>+" +
                player.getSpecialAb()[1] + "<br>+" + player.getSpecialAb()[2] +
                "<br><br>Actions: <br>[M] Move      "+used[0]+"<br> [A] Attack      "+used[1]+"<br> [I] Inventory" +
                "<br> [N] Next Turn</html>");

        tNotes.setText("<html>*Can only do each action once per turn</html>");

        tNormal.setBounds(0,-30,230,300);
        tNotes.setBounds(0,20*13,230,50);

        this.add(tNormal);
        this.add(tNotes);
    }

    public void showInventory(List<Items> sharedInv){
        String items = " ";
        List<String> alreadyIn = new ArrayList<>();
        int i = 1;

        for (Items item:sharedInv){
            if (item instanceof Weapons){
                items += "["+i+"] "+ item.getName() + " DMG: " + ((Weapons) item).getDamage() + " Range: " + ((Weapons) item).getRange() + "<br><br>";
                //i++;
            } else {
                if (!alreadyIn.contains(item.getName())){
                    items += "["+i+"] "+ item.getName() + " " + amountInInv(sharedInv, item.getId())+ " in Inventory<br><br>";
                    //i++;
                }
                alreadyIn.add(item.getName());
            } i++;
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
}

//https://stackoverflow.com/questions/685521/multiline-text-in-jlabel