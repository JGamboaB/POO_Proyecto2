package gam.sua;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

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

            float size = (i != 1) ? 14f:12f;
            tNormalText[i].setFont(font.deriveFont(size));
            tNormalText[i].setForeground(Color.BLACK);
        }
    }

    public void showMenu(Player player){//An int array parameter [0,0,0] should modify "used"
        String[] used = {"","",""};

        tNormal.setText("<html>Menu<br><br>Name: " + player.getName() + "<br>Health: " + player.getHealth() + "<br>Range: " +
                player.getRange() + "<br><br>Special Abilities: <br>+" + player.getSpecialAb()[0] + "<br>+" +
                player.getSpecialAb()[1] + "<br>+" + player.getSpecialAb()[2] +
                "<br><br>Actions: <br>[M] Move\t"+used[0]+"<br> [A] Attack\t"+used[1]+"<br> [I] Inventory\t"+used[2]+"<br> [N] Next Turn</html>");

        tNotes.setText("<html>*Can only do each action once per turn</html>");

        tNormal.setBounds(10,-20,180,300);
        tNotes.setBounds(10,20*14,180,50);

        //Need to add USED

        this.add(tNormal);
        this.add(tNotes);
    }

    public void showInventory(Player Player){

    }

    public void movement(){

    }

    public void attack(){

    }

    public void useItem(){

    }

    public void pickItem(){

    }
}

//https://stackoverflow.com/questions/685521/multiline-text-in-jlabel