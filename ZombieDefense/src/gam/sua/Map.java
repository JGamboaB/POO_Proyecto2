package gam.sua;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.lang.Math;

public class Map{

    //Players
    private final Player Gringo = new Player(new int[]{13, 7}, 100, 7, 0, "Gringo");
    private final Player David = new Player(new int[]{18, 7}, 100, 12, 1, "David" );
    private final Player Amalia = new Player(new int[]{18, 9}, 150, 7, 2, "Amalia" );
    private final List<Player> Players = new ArrayList<>();
    private final Player[] totalPlayers = {Gringo,David,Amalia};

    //Enemies
    private final Enemy[] Skeletons = new Enemy[10]; //Normal
    private final Enemy[] Slimes = new Enemy[10];    //Weak/Revive (2 hits to kill)
    private final Enemy[] Zombies = new Enemy[6];    //Poison
    private final Enemy[] Ghosts = new Enemy[4];     //Can walk through
    private final List<Enemy> activeEnemies = new ArrayList<>();

    //Map & Inventory
    private int[][] matrix;

    private final List<Items> sharedInventory = new ArrayList<>();
    private final Inventory InvObject = new Inventory();
    private final List<Items> inv = InvObject.getInventory();

    //Frame and Panels
    private final JFrame frame = new JFrame();
    private final JPanel panel = new JPanel();
    private final Menu Menu = new Menu();

    //Images
    private final JLabel bg = new JLabel(new ImageIcon("images\\MapV2.png"));
    private final JLabel victoryScreen = new JLabel(new ImageIcon("images\\victory.png"));
    private final JLabel defeatScreen = new JLabel(new ImageIcon("images\\defeat.png"));
    private final boolean[] state = new boolean[]{false,false};
    private final JLabel[] playerImgs = new JLabel[3];
    private final ImageIcon[] enemyImgs = new ImageIcon[4];

    private final ImageIcon attackArea = new ImageIcon("images\\attack.png");
    private final ImageIcon shine = new ImageIcon("images\\shine.png");
    private final ImageIcon soundIMG = new ImageIcon("images\\sound.png");
    private final ImageIcon attackedIMG = new ImageIcon("images\\attackIMG.png");

    //Variables
    private int round = 0;
    private int action = -1; //0 move, 1 attack, 2 equip
    private int[] doneActs = new int[]{0,0,0};
    private int[] attacked = new int[]{0,0,0};

    private int playersTurn = 0; //0,1,2
    private final int[] numEnemies = new int[]{0,0,0,0}; //Skeleton, Slime, Zombie, Ghost

    private int steps = Gringo.getSteps();
    private int valueBefore = 0;

    private final Random random = new Random();

    /**
     * Constructor
     * @throws IOException if there's an error by getting the font.
     * @throws FontFormatException if there's an error by getting the font.
     */
    public Map() throws IOException, FontFormatException {
        //Basic Stuff
        int Width=1440+14,Height=800+35;

        frame.setTitle("Monster Defense");
        frame.setSize(Width,Height);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Players.add(Gringo);
        Players.add(David);
        Players.add(Amalia);

        initPanels(Width,Height);
        initMatrix();
        initImgs();
        frame.add(bg);

        startingPos();
        frame.addMouseListener(mouse);
        frame.addKeyListener(keyboard);
        updateFrame();

        frame.setVisible(true);

    }


    /** Initialize all the panels used.
     * @param width int
     * @param height int
     */
    public void initPanels(int width, int height){
        panel.setBounds(0,0,width,height);
        panel.setLayout(null);
        panel.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f)); //Transparent

        Menu.setBounds(1150,50,230,305);
        Menu.setLayout(null);
        Menu.setBackground(new Color(229, 225, 225));

    }


    /** Initializes all the images used of the players and enemies. */
    void initImgs(){
        for (int i = 0; i < 7; i++){
            if (i < 3)
                playerImgs[i] = new JLabel(new ImageIcon("images\\CharIMG\\"+i+".png"));
            else
                enemyImgs[i-3] = new ImageIcon("images\\CharIMG\\"+i+".png");
        }
    }


    // / / / / / / / / / / MATRIX

    /**
     * Initialize the matrix
     * Numbers used:
     * 0. walkable space
     * 1. occupied
     * 2. player
     * 3. skeleton, 4. slime, 5. zombie, 6. ghost, 7. boss
     * 8. chest
     * 9. sound
     * 10. item
     * 11. walkable space for players
     */
    public void initMatrix(){
        matrix = new int[][]{
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,1,8,8,0,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,1,1,1,0,0,0,0,0,1,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,1,1,0,0,0,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,0,0,1,1,1,1,1,1,0,0,0,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,0,0,0,0,1,1,1,0,0,0,0,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,11,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,11,1,1,1,1,0,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,1,0,1,1,0,1,1,1,0,1,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,0,1,1,0,0,1,1,0,0,1,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,0,0,1,1,1,1,0,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,0,0,0,0,0,0,0,0,0,11,0,1,1,1,1,1,1,0,0,1,1,1,0,1,1,1,1,0,0,0,0,1,0,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,0,1,1,0,0,0,0,0,1,1,0,1,1,1,1,0,0,0,0,1,0,0,0,0,0,0,0,1,0},
                {0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0,1,1,0,0,1,1,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0},
                {1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,0,0,1,1,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,1,0,0,0,0,1,0},
                {1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,1,1,1,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,1,1,1,1,1,0,0,0,1,0,0,0,1,0,0},
                {1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,1,1,1,1,1,1,0,0,1,1,1,1,1,1,0,0,1,0,0,0,0,0,0},
                {1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,1,0,0,0,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,0,0,1,0,0,0,1,0,0},
                {1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,0,0,0,0,0,1,0,0},
                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1},
                {1,0,0,0,0,0,1,1,0,0,0,0,1,1,1,1,1,0,0,0,1,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,1,1,1,1,0,0,1,0,1,0,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,0,0,1,0,0,0,1,1},
                {1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,8,8,0,1,1}
        };
    }


    /** Change space in the matrix to matrixID
     * @param r int
     * @param c int
     * @param matrixID int
     */
    void updateMatrix(int r, int c, int matrixID){
        matrix[r][c] = matrixID;
    }


    /** The old position of the Character entered is cleaned in the matrix by using the variable 'valueBefore'
     * @param Char Character
     */
    void cleanLeftBehind(Character Char){ //Called when a character moves
        int r = Char.getOldPos()[0], c = Char.getOldPos()[1];
        matrix[r][c] = valueBefore;
    }


    /** Represent the numbers in the matrix with their respective images. */
    void charMatrix(){
        for (int r = 0; r < 25; r++){
            for (int c = 0; c < 45; c++){

                switch (matrix[r][c]){
                    case 2:
                        for (Player player : Players) {

                            if (player.getPosition()[0] == r && player.getPosition()[1] == c) {
                                playerImgs[player.getId()].setBounds(c * 32, r * 32, 32, 32);
                                //panel.add(playerImgs[player.getId()]);

                                if (attacked[player.getId()] == 1){
                                    JLabel attackedLabel = new JLabel();
                                    attackedLabel.setIcon(attackedIMG);
                                    attackedLabel.setBounds(c*32,r*32,32,32);
                                    panel.add(attackedLabel);
                                }panel.add(playerImgs[player.getId()]);

                            }

                        } break;

                    case 3:
                        JLabel skeleton = new JLabel();
                        skeleton.setBounds(c*32,r*32,32,32);
                        skeleton.setIcon(enemyImgs[0]);
                        panel.add(skeleton);
                        break;

                    case 4:
                        JLabel slime = new JLabel();
                        slime.setBounds(c*32,r*32,32,32);
                        slime.setIcon(enemyImgs[1]);
                        panel.add(slime);
                        break;

                    case 5:
                        JLabel zombie = new JLabel();
                        zombie.setBounds(c*32,r*32,32,32);
                        zombie.setIcon(enemyImgs[2]);
                        panel.add(zombie);
                        break;

                    case 6:
                        JLabel ghost = new JLabel();
                        ghost.setBounds(c*32,r*32,32,32);
                        ghost.setIcon(enemyImgs[3]);
                        panel.add(ghost);
                        break;

                    case 9:
                        JLabel sound = new JLabel();
                        sound.setBounds(c*32,r*32,32,32);
                        sound.setIcon(soundIMG);
                        panel.add(sound);
                        break;

                    case 10:
                        JLabel itemOnFloor = new JLabel();
                        itemOnFloor.setBounds(c*32,r*32,32,32);
                        itemOnFloor.setIcon(shine);
                        panel.add(itemOnFloor);
                        break;
                }

            }
        }
    }

    //EXTRA
    void charMatrix2(){
        for (int r = 0; r < 25; r++){
            for (int c = 0; c < 45; c++){

                switch (matrix[r][c]){
                    case 2:
                        for (int i = 0; i < 3; i++){

                            if (Players.get(i).getPosition()[0] == r && Players.get(i).getPosition()[1] == c){
                                playerImgs[i].setBounds(c*32,r*32,32,32);
                                panel.add(playerImgs[i]);
                            }

                        } break;

                    case 3:
                        Enemy enemy = getEnemyByPos(r,c);
                        if (enemy == null){
                            break;
                        }

                        JLabel skeleton = new JLabel();
                        skeleton.setBounds(c*32,r*32,32,32);
                        skeleton.setIcon(enemyImgs[0]);
                        panel.add(skeleton);
                        break;

                    case 4:
                        JLabel slime = new JLabel();
                        slime.setBounds(c*32,r*32,32,32);
                        slime.setIcon(enemyImgs[1]);
                        panel.add(slime);
                        break;

                    case 5:
                        JLabel zombie = new JLabel();
                        zombie.setBounds(c*32,r*32,32,32);
                        zombie.setIcon(enemyImgs[2]);
                        panel.add(zombie);
                        break;

                    case 6:
                        JLabel ghost = new JLabel();
                        ghost.setBounds(c*32,r*32,32,32);
                        ghost.setIcon(enemyImgs[3]);
                        panel.add(ghost);
                        break;

                    case 10:
                        JLabel itemOnFloor = new JLabel();
                        itemOnFloor.setBounds(c*32,r*32,32,32);
                        itemOnFloor.setIcon(shine);
                        panel.add(itemOnFloor);
                        break;
                }

            }
        }
    }


    /** Starting position of the players and the starting inventory */
    void startingPos(){
        updateMatrix(13,7,2);
        updateMatrix(18,9,2);
        updateMatrix(18,7,2);

        //Starting Weapons
        addToSharedInv(InvObject.getItemById(0));
        addToSharedInv(InvObject.getItemById(1));
        addToSharedInv(InvObject.getItemById(2));
    }


    /** Updates the frame and each one of its panels */
    void updateFrame(){
        frame.getContentPane().removeAll();
        panel.removeAll();
        Menu.removeAll();

        charMatrix();
        //charMatrix2();

        Player player = (Players.size() == 0)? Gringo: Players.get(playersTurn);

        Menu.showMenu(player,doneActs,round,steps);

        if (!state[0] && !state[1]){ //NORMAL
            frame.add(Menu);
            frame.add(panel);
            frame.add(bg);
        } if (state[0])
            frame.add(victoryScreen); //VICTORY
        if (state[1])
            frame.add(defeatScreen); //DEFEAT

        frame.revalidate();
        frame.repaint();
    }

    public int valorPos(int r, int c){return matrix[r][c];}


    // / / / / / / / / ACTIONS / / / / / / / / //

    /** Actions based on pressed keys. Changes the variable 'action' */
    KeyListener keyboard = new KeyListener() { //ACTIONS KEYBOARD
        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {}

        @Override
        public void keyReleased(KeyEvent e) {
            //System.out.println("A key has been pressed: "+e.getKeyChar());

            //Move
            if (e.getKeyChar() == 'm')
                action = 0;

            if (action == 0 && steps > 0)
                movePlayerWASD(Players.get(playersTurn),e.getKeyChar());


            //Attack
            if (e.getKeyChar() == ' '){
                if (doneActs[1] == 0){
                    showAttackRange(Players.get(playersTurn));
                }action = 1;
            }

            //Inventory
            if (e.getKeyChar() == 'i'){
                if (action == -1 && doneActs[2] == 0){
                    showInv();
                    action = 2;
                } else if (action == 2) {
                    resetMenu();
                }
            }

            //Equip Item
            if (action == 2){
                if (java.lang.Character.isDigit(e.getKeyChar())){
                    if (Integer.parseInt(String.valueOf(e.getKeyChar())) <= sharedInventory.size()){
                        equipItem(Integer.parseInt(String.valueOf(e.getKeyChar())));
                        resetMenu();
                    }
                }
            }

            //Next Turn
            if (e.getKeyChar() == 'n') {
                changeTurn();
                action = -1;
                doneActs = new int[]{0, 0, 0};
                updateFrame();
                attacked = new int[]{0, 0, 0}; //
            }
        }
    };


    /** Actions based on the mouse. If variable action = 1 and the player hasn't attacked yet, attack function is called. */
    MouseListener mouse = new MouseListener(){ //ACTIONS MOUSE CLICK
        @Override
        public void mouseClicked(MouseEvent e) {
            double x = e.getX(), y = e.getY();
            int r, c;

            c = (int) x/32;
            r = (int) y/32 - 1;

            //System.out.println("x = " + x + ", y = " + y);
            //System.out.println("Row: " + r + " Column: " + c + " Value: "+ matrix[r][c]);

            //ATTACK
            if (action == 1 && doneActs[1] == 0){
                action = -1;
                doneActs[1] = 1;
                attack(r,c);
                updateFrame();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}
    };


    // / / / / / / / / / / MOVEMENT

    /** Move player by entering the input (keys WASD) and updates the Frame.
     * @param player Player
     * @param dir char
     */
    public void movePlayerWASD(Player player, char dir){
        int r = player.getPosition()[0], c = player.getPosition()[1];
        int rAdd = 0, cAdd = 0;
        switch (dir){
            case 'w':
                rAdd = -1;
                break;
            case 's':
                rAdd = 1;
                break;
            case 'a':
                cAdd = -1;
                break;
            case 'd':
                cAdd = 1;
                break;
            case 'i':
                showInv();
                action = 2;
                return;
            default:
                return;
        }

        valueBefore = matrix[r+rAdd][c+cAdd];

        //Map boundaries
        if (!(r+rAdd >= 0 && r+rAdd <= 25 && c+cAdd >= 0 && c+cAdd <= 45))
            return;

        //Chest
        if (matrix[r+rAdd][c+cAdd] == 8){
            int pos = random.nextInt(inv.size());
            addToSharedInv(inv.get(pos));
            matrix[r+rAdd][c+cAdd] = 1;
            return;
        }

        //Non walking Spaces
        if (matrix[r+rAdd][c+cAdd] != 0 && matrix[r+rAdd][c+cAdd] != 9 && matrix[r+rAdd][c+cAdd] != 10 && matrix[r+rAdd][c+cAdd] != 11)
            return;

        //Item On The Floor
        if (matrix[r+rAdd][c+cAdd] == 10){
            int pos = random.nextInt(inv.size());
            addToSharedInv(inv.get(pos));
            valueBefore = 0;
        }

        player.setPosition(new int[]{r+rAdd,c+cAdd});
        cleanLeftBehind(player);
        updateMatrix(r+rAdd,c+cAdd,2);

        --steps;
        if (steps == 0){
            doneActs[0] = 1;
            action = -1;
        }updateFrame();

    }

    public static void wait(int ms){
        try {
            Thread.sleep(ms);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }


    // / / / / / / / / / / ATTACK

    /** Show in screen the range that the player has to attack.
     * @param player Player
     */
    void showAttackRange(Player player){ //Print Area that the Player can Attack.
        int range = player.getWeaponRange();
        int r = player.getPosition()[0], c = player.getPosition()[1];
        int x = (c-range)*32, y = (r-range)*32;
        int side = 32*(range*2+1);

        JLabel rangeAtt = new JLabel();
        rangeAtt.setBounds(x,y,side,side);
        rangeAtt.setIcon(new ImageIcon(attackArea.getImage().getScaledInstance(side,side,Image.SCALE_FAST)));
        panel.add(rangeAtt);

        charMatrix();
        Menu.showMenu(Players.get(playersTurn),doneActs,round,steps);

        frame.add(Menu);
        frame.add(panel);
        frame.add(bg);

        frame.revalidate();
        frame.repaint();

    }


    /** Attack function. By getting the row and column of the matrix, attacks the enemy in it.
     * @param r int row
     * @param c int column
     */
    public void attack(int r, int c){
        Player player = Players.get(playersTurn);
        int[] pos = player.getPosition();

        //Sound
        if (player.getSound()){
            int[] soundCoords = isNear(player.getPosition(),1,0);

            if (soundCoords == null)
                soundCoords = isNear(player.getPosition(),2,0);

            if (soundCoords == null)
                return;

            matrix[soundCoords[0]][soundCoords[1]] = 9;
        }


        if ((player.getWeaponRange() >= Math.abs(r-pos[0]) && player.getWeaponRange() >= Math.abs(c-pos[1]))){
            if (matrix[r][c] > 2 && matrix[r][c] < 7){
                Enemy enemy = getEnemyByPos(r,c);
                enemy.subtractHealth(player.getDMG());
                enemyDeath(enemy);
            } else if (matrix[r][c] == 8){ //Open Chest
                addToSharedInv(InvObject.getItemById(8));
                matrix[r][c] = 1;
            }
        } else {
            doneActs[1] = 0;
        }
    }


    /** Clean all the sound spaces in the matrix. It is called after the enemy's turn */
    public void cleanSound(){
        for (int r = 0; r < 25; r++){
            for (int c = 0; c < 45; c++){
                if (matrix[r][c] == 9)
                    matrix[r][c] = 0;
            }
        }
    }


    // / / / / / / / / / / INVENTORY

    /** Show the inventory in the menu panel */
    public void showInv(){
        Menu.removeAll();
        Menu.showInventory(Players.get(playersTurn),sharedInventory);

        frame.add(Menu);
        frame.add(panel);
        frame.add(bg);

        frame.revalidate();
        frame.repaint();
    }


    /** Add the item entered into the List of Items 'sharedInventory' and remove it from the possible
     * options that can be obtained
     * @param item Items
     */
    public void addToSharedInv(Items item){
        int index = (!sharedInventory.contains(item))?0:sharedInventory.size(); //Change page with R if it fails //-1
        sharedInventory.add(index,item);

        if (item.getId() < 7) //Only removes the Weapons from the possible items
            inv.remove(item);

        if (item.getId() == 9){ //Validation for maximum 2 revives
            if (listAmount(item) == 2)
                inv.remove(item);
        }
    }


    /** Checks the amount of appearances of the entered item inside the sharedInventory.
     * @param item Items
     * @return res. Integer of the amount of appearances of the Item.
     */
    public int listAmount(Items item){
        int res = 0;
        for (Items items : sharedInventory){
            if (items.getId() == item.getId())
                res++;
        } return res;
    }


    /** Checks if the weapon attributes are already used in a character to determine if the weapon is being used
     * @param weapon Weapon
     * @param player Player
     * @return boolean. Return if the weapon is equipped in a player.
     */
    public boolean weaponAlreadyEquipped(Weapons weapon, Player player){
        int DMG;
        for (Player otherPlayer: Players){
            if (otherPlayer.getId() == player.getId())
                continue;

            DMG = (otherPlayer.getDoubleDamage())? otherPlayer.getDMG()/2 : otherPlayer.getDMG();

            if (weapon.getDamage() == DMG && weapon.getRange() == otherPlayer.getWeaponRange())
                return true;

        } return false;
    }


    /** Equips an item and it's attributes into the specified player.
     * @param num int
     */
    public void equipItem(int num){
        Items item = sharedInventory.get(num);
        Player player = Players.get(playersTurn);

        if (item instanceof Weapons){

            if (weaponAlreadyEquipped((Weapons) item,player))
                return;

            player.setDMG(((Weapons) item).getDamage());
            player.setWeaponRange(((Weapons) item).getRange());
            player.setSound(((Weapons) item).getSound());

            if (player.getLessSound())
                player.setSound(false);

        } else {
            if (item.getId() == 8){ //REVIVE
                player = lowestHealthPlayer(); //Gives the effects to the player on the lowest health or dead

                if (player.isDead()){
                    int[] pos = isNear(new int[]{11,4},1,0);
                    player.setPosition(pos);
                    updateMatrix(pos[0],pos[1],2);
                    Players.add(player);
                    updateFrame();
                }
            }

            player.addHealth(((Consumable) item).getHealth());
            player.setIsPoisoned(((Consumable) item).curesPoison());
            sharedInventory.remove(num);
        }

        doneActs[2] = 1;//
        action = -1; //
    }


    /** Resets the menu panel to the normal state. */
    public void resetMenu(){
        Menu.removeAll();
        Menu.showMenu(Players.get(playersTurn),doneActs,round,steps);

        frame.add(Menu);
        frame.add(panel);
        frame.add(bg);

        frame.revalidate();
        frame.repaint();
        action = -1;
    }


    // / / / / / / / / / / PLAYERS

    /** Gets the player with the lowest health. Called when the consumable revive is used.
     * @return player with the lowest health. */
    public Player lowestHealthPlayer(){
        Player player = totalPlayers[0];

        for (int i = 1; i < 3; i++){
            if (totalPlayers[i].isDead() || totalPlayers[i].getHealth() < player.getHealth()){
                player = totalPlayers[i];
            }
        }

        return player;
    }


    // / / / / / / / / / / ENEMIES

    /** Returns the amount of enemies inside the matrix.
     * @return res */
    public int enemiesOnMatrix(){
        int res = 0;
        for (int r = 0; r < 25; r++){
            for (int c = 0; c < 45; c++){
                if (matrix[r][c] > 2 && matrix[r][c] < 7)
                    res++;
            }
        } return res;
    }

    /** Removes the enemies of the List 'activeEnemies' if they are dead. */
    public void checkActiveEnemies(){
        for (int i = 0; i < activeEnemies.size(); i++){
            if (activeEnemies.get(i).isDead())
                activeEnemies.remove(i);
        }
    }

    /** Returns the enemy that is in the matrix space by entering the row and column.
     * @param r int row
     * @param c int column
     * @return enemy
     */
    public Enemy getEnemyByPos(int r, int c){
        for (Enemy activeEnemy : activeEnemies) {
            if (activeEnemy.getPosition()[0] == r && activeEnemy.getPosition()[1] == c) {
                return activeEnemy;
            }
        }
        return null;
    }

    /** Enemy death and it's effects on the matrix.
     * @param enemy Enemy
     */
    public void enemyDeath(Enemy enemy){
        if (enemy.isDead()){

            if (enemy.getRevive()){     // Revive
                enemy.setRevive(false);
                enemy.setHealth(5);
                //System.out.println("REVIVE!");
                return;
            }

            int drop = random.nextInt(11);
            int matrixId = (drop < 2)? 10:0;

            if (Players.get(playersTurn).getLuck()) //If LUCK, always drop loot
                matrixId = 10;

            matrix[enemy.getPosition()[0]][enemy.getPosition()[1]] = matrixId;
            checkActiveEnemies();
        }
    }

    /** Creates an enemy depending on the parameters entered (position in matrix, matrixId & id).
     * @param position int[]
     * @param matrixId int
     * @param id int
     */
    public void createEnemy(int[] position, int matrixId, int id){
        switch (matrixId) {
            case 3 -> {
                Skeletons[numEnemies[0]] = new Enemy(position, 15, 2, id, "skeleton", new int[] {12,7}); //h:10
                activeEnemies.add(Skeletons[numEnemies[0]]);
                numEnemies[0]++;
            }
            case 4 -> {
                Slimes[numEnemies[1]] = new Enemy(position, 10, 3, id, "slime", new int[] {3,14}); //h:5
                activeEnemies.add(Slimes[numEnemies[1]]);
                numEnemies[1]++;
            }
            case 5 -> {
                Zombies[numEnemies[2]] = new Enemy(position, 20, 2, id, "zombie", new int[] {11,14}); //h:15
                activeEnemies.add(Zombies[numEnemies[2]]);
                numEnemies[2]++;
            }
            case 6 -> {
                Ghosts[numEnemies[3]] = new Enemy(position, 25, 4, id, "ghost", new int[] {8,14}); //h:20
                activeEnemies.add(Ghosts[numEnemies[3]]);
                numEnemies[3]++;
            }
        }
    }

    /** Moves the enemy step by step.
     * @param character Character
     * @param matrixId int
     * @param steps List<Node>
     */
    public void animMove(Character character, int matrixId, List<Node> steps){
        for (Node coordinates : steps) {
            character.setPosition(new int[]{coordinates.getCoords()[0], coordinates.getCoords()[1]});
            cleanLeftBehind(character);
            updateMatrix(coordinates.getCoords()[0], coordinates.getCoords()[1], matrixId);
        } updateFrame();
    }

    /** Attack function of the enemy if possible.
     * @param enemy Enemy
     * @param playerPos int[]
     */
    public void enemyAttack (Enemy enemy, int[] playerPos){
        Player objective = null;
        for (Player player : Players){
            if (Arrays.equals(player.getPosition(),playerPos)){
                objective = player;
            }
        }

        if (objective == null)
            return;

        if (enemy.getPoison()){
            objective.newIsPoisoned(3);
        }

        int[] possibleMulti = {0,1,1};
        int multi = (objective.getCanEvade())? possibleMulti[Math.abs(random.nextInt()%3)]:1;

        if (multi == 1)
            attacked[objective.getId()] = 1;


        objective.subtractHealth(enemy.getDamage()*multi);   // Attack

        if (objective.isDead()){
            objective.setHealth(0);
            int[] pos = objective.getPosition();
            matrix[pos[0]][pos[1]] = 0;
            Players.remove(objective);
        }
    }

    /** Spawners that activate depending on the round. */
    public void spawners(){ //Validate that there is no player in the spawn points
        switch (round){
            case 5:
                createEnemy(new int[]{1,23},4,4);
                updateMatrix(1,23,4);
                createEnemy(new int[]{1,25},4,4);
                updateMatrix(1,25,4);

            case 4: //Ghosts
                createEnemy(new int[]{13,34},6,6);
                updateMatrix(13,34,6);
                createEnemy(new int[]{13,36},6,6);
                updateMatrix(13,36,6);

            case 3: //Zombies
                createEnemy(new int[]{19,39},5,5);
                updateMatrix(19,39,5);
                createEnemy(new int[]{18,37},5,5);
                updateMatrix(18,37,5);

            case 2: //Slimes
                createEnemy(new int[]{16,43},4,4);
                updateMatrix(16,43,4);
                createEnemy(new int[]{14,42},4,4);
                updateMatrix(14,42,4);

            case 1: //Skeletons
                createEnemy(new int[]{21,41},3,3);
                updateMatrix(21,41,3);
                createEnemy(new int[]{22,42},3,3);
                updateMatrix(22,42,3);
        }
    }


    // / / / / / / / / / / TURN

    /** Changes the turn of the character. If all the players already had their turn, the enemies turn starts */
    public void changeTurn(){
        playersTurn++;

        if (playersTurn >= Players.size()){
            playersTurn = 0;

            if (enemiesOnMatrix() == 0){
                round++;
                spawners();

            } else {
                enemiesTurn();

                for (Player player : Players){      // Poison Damage if player is poisoned
                    player.poisonDamage();
                }

                defeat(); //Check if defeat
            }
            cleanSound();

            //VICTORY
            if (round == 6){ //6
                state[0] = true;
                updateFrame();
            }

        } steps = (Players.size() == 0)? 0:Players.get(playersTurn).getSteps();
    }

    /** Enemies turn and actions */
    public void enemiesTurn(){
        //System.out.println("---");
        for (Enemy enemy: activeEnemies){
            enemy.getObjectivePos(this);
            //System.out.println(enemy.getoObjectivePos()[0] + " , " + enemy.getoObjectivePos()[1]);
            List<Node> path = enemy.ai(this);

            if (isNear(enemy.getPosition(),2,2) != null){
                enemyAttack (enemy, isNear(enemy.getPosition(),2,2));
                defeat(); //
                //wait(1000);
            }

            if (path == null)
                continue;

            animMove(enemy, enemy.getId(), path);
        }
        //updateFrame();
    }


    // / / / / / / / / / / END GAME

    /** Checks if the conditions of defeat are met and if they are, the variable 'state' changes. This variable is
     * used on updateFrame() to show the defeat image. */
    public void defeat(){
        if (Gringo.isDead() && David.isDead() && Amalia.isDead()){
            state[1] = true;
            return;
        }

        //int[] r = {3,11,12,10,4,2,12,12,12,12}, c = {14,14,14,14,14,14,8,7,6,10};
        int[] r = {11,11,12,12,12,12,12,12,12,11,12,12,11,12,10,9,8,5,4,3,2,1}, c = {4,5,4,5,6,7,8,9,10,10,11,12,14,14,14,14,14,14,14,14,14,14};

        for (int i = 0; i < 22; i++){
            if (matrix[r[i]][c[i]] > 2 && matrix[r[i]][c[i]] < 7){
                state[1] = true;
                updateFrame();
                break;
            }
        }
    }


    // / / / / / / / / / / AI

    public int[] findMatrix(int num){
        for (int r = 0; r < 25; r++) {
            for (int c = 0; c < 45; c++) {
                if (valorPos(r,c) == num){
                    return new int[] {r,c};
                }
            }
        }
        return null;
    }

    public int[] isNear(int[] pos, int range, int val){     // returns the first value appearance or null if there is none
        for (int r = 0; r < 25; r++) {
            for (int c = 0; c < 45; c++) {
                if (r == pos[0] && c == pos[1]){
                    for (int ri = r-range; ri <= r+range && ri < 25 && ri >= 0; ri++){
                        for (int ci = c-range; ci <= c+range && ci < 45 && ci >= 0; ci++){
                            if (valorPos(ri,ci) == val){
                                return new int[] {ri,ci};
                            }
                        }
                    }
                    return null;
                }
            }
        }
        return null;
    }

    public boolean isNearCoord(int[] pos, int range, int[] val){    // true if the coords are in range
        for (int r = 0; r < 25; r++) {
            for (int c = 0; c < 45; c++) {
                if (r == pos[0] && c == pos[1]){
                    for (int ri = r-range; ri <= r+range && ri < 25 && ri >= 0; ri++){
                        for (int ci = c-range; ci <= c+range && ci < 45 && ci >= 0; ci++){
                            if (ri == val[0] && ci == val[1]){
                                return true;
                            }
                        }
                    }
                    return false;
                }
            }
        }
        return false;
    }

    // / / / / / / / / / / CLASS

    public static void main(String[] args) throws IOException, FontFormatException {
        new Map();
    }
}


/*
OPTIONAL:
1. ADDITIONAL EFFECTS (ATTACK, SOUND)                      * oldHealth != health -> Image of the player on red
2. MUSIC
3. TELEPORTATION
4. MOVEMENT ANIMATIONS (PLAYER, ENEMY) *ENEMY CHECK IF WAIT FUNCTION IS COMPATIBLE WITH SWING, AND SWING VISUALS WITH LOOPS.
5. BOSS
6. MENU IMAGES WEAPONS
*SLIME REVIVE?????
*BALANCE ENEMY DAMAGE & ABILITIES
 */