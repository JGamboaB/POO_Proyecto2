package gam.sua;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class Map{

    //Players
    private final Player Gringo = new Player(new int[]{13, 7}, 100, 10, 0, "Gringo");
    private final Player David = new Player(new int[]{18, 7}, 100, 15, 1, "David" );
    private final Player Amalia = new Player(new int[]{18, 9}, 150, 10, 2, "Amalia" );
    private final Player[] Players = {Gringo,David,Amalia};

    //Enemies
    private final Enemy[] Skeletons = new Enemy[10];
    private final Enemy[] Slimes = new Enemy[10];
    private final Enemy[] Zombies = new Enemy[6];
    private final Enemy[] Ghosts = new Enemy[4];
    private final List<Enemy> activeEnemies = new ArrayList<>();
    //private final Enemy[] ActiveEnemies = new Enemy[30];

    private int[][] matrix;// = new int[50][30];

    private final List<Items> sharedInventory = new ArrayList<>();
    private final Inventory InvObject = new Inventory();
    private final List<Items> inv = InvObject.getInventory();

    //Frame and Panels
    private final JFrame frame = new JFrame();
    private final JPanel panel = new JPanel();
    private final Menu Menu = new Menu();


    private int action = -1; //0 move, 1 attack, 2 equip
    private int[] doneActs = new int[]{0,0,0};
    private final int[] numEnemies = new int[]{0,0,0,0}; //Skeleton, Slime, Zombie, Ghost
    private int playersTurn = 0; //0,1,2
    private boolean turn = true;
    private int round = 0;
    private int steps = Gringo.getSteps();

    private final Random random = new Random();
    //Move all the creatures simultaneously?

    //Images
    private final JLabel bg = new JLabel(new ImageIcon("images\\MapV2.png"));
    private final JLabel victoryScreen = new JLabel(new ImageIcon("images\\victory.png"));
    private final JLabel defeatScreen = new JLabel(new ImageIcon("images\\defeat.png"));
    private final boolean[] state = new boolean[]{false,false};
    private final JLabel[] playerImgs = new JLabel[3];
    private final ImageIcon[] enemyImgs = new ImageIcon[4];

    private final ImageIcon rangeArea = new ImageIcon("images\\move.png");
    private final ImageIcon attackArea = new ImageIcon("images\\attack.png");
    private final ImageIcon shine = new ImageIcon("images\\shine.png");


    public Map() throws IOException, FontFormatException {
        //Basic Stuff
        int Width=1440+14,Height=800+35;

        frame.setTitle("Monster Defense");
        frame.setSize(Width,Height);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

    public void initPanels(int width, int height){
        panel.setBounds(0,0,width,height);
        panel.setLayout(null);
        panel.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f)); //Transparent

        Menu.setBounds(1150,50,230,305);
        Menu.setLayout(null);
        Menu.setBackground(new Color(229, 225, 225));

    }

    void initImgs(){
        for (int i = 0; i < 7; i++){
            if (i < 3)
                playerImgs[i] = new JLabel(new ImageIcon("images\\CharIMG\\"+i+".png"));
            else
                enemyImgs[i-3] = new ImageIcon("images\\CharIMG\\"+i+".png");
        }
    }


    // / / / / / / / / / / MATRIX

    public void initMatrix(){ //0 can move, 1 occupied, 2 player, 3 skeleton, 4 slime, 5 zombie, 6 ghost, 7 boss, 8 chest, 9 sound, 10 item
        matrix = new int[][]{
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,1,8,8,0,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,1,1,1,0,0,0,0,0,1,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,1,1,0,0,0,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,0,0,1,1,1,1,1,1,0,0,0,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,0,0,0,0,1,1,1,0,0,0,0,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,1,0,1,1,0,1,1,1,0,1,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,0,1,1,0,0,1,1,0,0,1,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,0,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1},
                {1,1,1,1,0,0,0,0,0,0,0,0,0,1,0,1,1,1,1,1,1,0,0,1,1,1,0,1,1,1,1,0,0,0,0,1,0,1,1,1,1,1,1,1,1},
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

    void updateMatrix(int r, int c, int matrixID){
        matrix[r][c] = matrixID;
    }

    //Called when a character moves
    void cleanLeftBehind(Character Char){
        int r = Char.getOldPos()[0], c = Char.getOldPos()[1];
        matrix[r][c] = 0;
    }

    void charMatrix(){
        for (int r = 0; r < 25; r++){
            for (int c = 0; c < 45; c++){

                switch (matrix[r][c]){
                    case 2:
                        for (int i = 0; i < 3; i++){

                            if (Players[i].getPosition()[0] == r && Players[i].getPosition()[1] == c){
                                playerImgs[i].setBounds(c*32,r*32,32,32);
                                panel.add(playerImgs[i]);
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

    void charMatrix2(){
        for (int r = 0; r < 25; r++){
            for (int c = 0; c < 45; c++){

                switch (matrix[r][c]){
                    case 2:
                        for (int i = 0; i < 3; i++){

                            if (Players[i].getPosition()[0] == r && Players[i].getPosition()[1] == c){
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

    void startingPos(){
        updateMatrix(13,7,2);
        updateMatrix(18,9,2);
        updateMatrix(18,7,2);

        //Starting Weapons
        addToSharedInv(InvObject.getItemById(0));
        addToSharedInv(InvObject.getItemById(1));
        addToSharedInv(InvObject.getItemById(2));
    }

    void updateFrame(){
        frame.getContentPane().removeAll();
        panel.removeAll();
        Menu.removeAll();

        charMatrix();
        //charMatrix2();
        Menu.showMenu(Players[playersTurn],doneActs,round,steps);


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

    public int[][] getMatrix(){return matrix;}

    public int valorPos(int r, int c){return matrix[r][c];}


    // / / / / / / / / ACTIONS / / / / / / / / //

    //ACTIONS KEYBOARD
    KeyListener keyboard = new KeyListener() {
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
                movePlayerWASD(Players[playersTurn],e.getKeyChar());


            //Attack
            if (e.getKeyChar() == ' '){
                if (doneActs[1] == 0){
                    showAttackRange(Players[playersTurn]);
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
            }
        }
    };

    //ACTIONS MOUSE CLICK
    MouseListener mouse = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
            double x = e.getX(), y = e.getY();
            int r, c;

            c = (int) x/32;
            r = (int) y/32 - 1;

            //System.out.println("x = " + x + ", y = " + y);
            //System.out.println("Row: " + r + " Column: " + c);

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

        //Map boundaries
        if (!(r+rAdd >= 0 && r+rAdd <= 25 && c+cAdd >= 0 && c+cAdd <= 45))
            return;

        //Chest
        if (matrix[r+rAdd][c+cAdd] == 8){
            addToSharedInv(InvObject.getItemById(8)); //REVIVE
            matrix[r+rAdd][c+cAdd] = 1;
            return;
        }

        //Non walking Spaces
        if (matrix[r+rAdd][c+cAdd] != 0 && matrix[r+rAdd][c+cAdd] != 9 && matrix[r+rAdd][c+cAdd] != 10)
            return;

        //Item On The Floor
        if (matrix[r+rAdd][c+cAdd] == 10){
            int pos = random.nextInt(inv.size());
            addToSharedInv(inv.get(pos));
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
        Menu.showMenu(Players[playersTurn],doneActs,round,steps);

        frame.add(Menu);
        frame.add(panel);
        frame.add(bg);

        frame.revalidate();
        frame.repaint();

    }

    public void attack(int r, int c){
        Player player = Players[playersTurn];
        int[] pos = player.getPosition();
        if ((player.getWeaponRange() >= Math.abs(r-pos[0]) && player.getWeaponRange() >= Math.abs(c-pos[1]))){
            if (matrix[r][c] > 2 && matrix[r][c] < 7){
                Enemy enemy = getEnemyByPos(r,c);
                enemy.subtractHealth(player.getDMG());
                enemyDeath(enemy);

                //Sound
                if (player.getSound()){
                    int[] soundCoords = isNear(player.getPosition(),1,0);

                    if (soundCoords == null)
                        soundCoords = isNear(player.getPosition(),2,0);

                    if (soundCoords == null)
                        return;

                    matrix[soundCoords[0]][soundCoords[1]] = 9;
                }

            } else if (matrix[r][c] == 8){ //Open Chest
                addToSharedInv(InvObject.getItemById(8));
                matrix[r][c] = 1;
            }
        } else {
            doneActs[1] = 0;
        }
    }

    public void cleanSound(){
        for (int r = 0; r < 25; r++){
            for (int c = 0; c < 45; c++){
                if (matrix[r][c] == 9)
                    matrix[r][c] = 0;
            }
        }
    }


    // / / / / / / / / / / INVENTORY

    public void showInv(){
        Menu.removeAll();
        Menu.showInventory(Players[playersTurn],sharedInventory);

        frame.add(Menu);
        frame.add(panel);
        frame.add(bg);

        frame.revalidate();
        frame.repaint();
    }

    public void addToSharedInv(Items item){
        int index = (!sharedInventory.contains(item))?0:sharedInventory.size(); //Change page with R if it fails //-1
        sharedInventory.add(index,item);

        //sharedInventory.add(item);

        if (item.getId() < 7) //Only removes the Weapons from the possible items
            inv.remove(item);
    }

    public void equipItem(int num){
        Items item = sharedInventory.get(num);
        Player player = Players[playersTurn];

        if (item instanceof Weapons){
            //sout
            player.setDMG(((Weapons) item).getDamage());
            player.setWeaponRange(((Weapons) item).getRange());
            player.setSound(((Weapons) item).getSound());
        } else {
            if (item.getId() == 8) //REVIVE
                player = lowestHealthPlayer(); //Gives the effects to the player on the lowest health or dead

            player.addHealth(((Consumable) item).getHealth());
            player.setIsPoisoned(((Consumable) item).curesPoison());
            sharedInventory.remove(num);
        }

        doneActs[2] = 1;//
        action = -1; //
    }

    public void resetMenu(){
        Menu.removeAll();
        Menu.showMenu(Players[playersTurn],doneActs,round,steps);

        frame.add(Menu);
        frame.add(panel);
        frame.add(bg);

        frame.revalidate();
        frame.repaint();
        action = -1;
    }


    // / / / / / / / / / / PLAYERS

    public Player lowestHealthPlayer(){
        Player player = Players[0];

        for (int i = 1; i < 3; i++){
            if (Players[i].isDead() || Players[i].getHealth() < player.getHealth()){
                player = Players[i];
            }
        }

        return player;
    }


    // / / / / / / / / / / ENEMIES

    public int enemiesOnMatrix(){
        int res = 0;
        for (int r = 0; r < 25; r++){
            for (int c = 0; c < 45; c++){
                if (matrix[r][c] > 2 && matrix[r][c] < 7)
                    res++;
            }
        } return res;
    }

    public void checkActiveEnemies(){
        for (int i = 0; i < activeEnemies.size(); i++){
            if (activeEnemies.get(i).isDead())
                activeEnemies.remove(i);
        }
    }

    public Enemy getEnemyByPos(int r, int c){
        for (Enemy activeEnemy : activeEnemies) {
            if (activeEnemy.getPosition()[0] == r && activeEnemy.getPosition()[1] == c) {
                return activeEnemy;
            }
        }
        return null;
    }

    public void enemyDeath(Enemy enemy){
        if (enemy.isDead()){
            if (enemy.getRevive()){     // Revive
                enemy.setRevive(false);
                enemy.setHealth(5);
                return;
            }

            int drop = random.nextInt(11);
            int matrixId = (drop < 2)? 10:0;

            if (Players[playersTurn].getLuck()) //If LUCK, always drop loot
                matrixId = 10;

            matrix[enemy.getPosition()[0]][enemy.getPosition()[1]] = matrixId;
            checkActiveEnemies();
        }
    }

    public void createEnemy(int[] position, int matrixId, int id){
        switch (matrixId) {
            case 3 -> {
                Skeletons[numEnemies[0]] = new Enemy(position, 10, 2, id, "skeleton", new int[] {12,7});
                activeEnemies.add(Skeletons[numEnemies[0]]);
                numEnemies[0]++;
            }
            case 4 -> {
                Slimes[numEnemies[1]] = new Enemy(position, 5, 3, id, "slime", new int[] {3,14});
                activeEnemies.add(Slimes[numEnemies[1]]);
                numEnemies[1]++;
            }
            case 5 -> {
                Zombies[numEnemies[2]] = new Enemy(position, 15, 2, id, "zombie", new int[] {11,14});
                activeEnemies.add(Zombies[numEnemies[2]]);
                numEnemies[2]++;
            }
            case 6 -> {
                Ghosts[numEnemies[3]] = new Enemy(position, 20, 4, id, "ghost", new int[] {8,14});
                activeEnemies.add(Ghosts[numEnemies[3]]);
                numEnemies[3]++;
            }
        }
    }

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

    public void changeTurn(){
        //System.out.println("Enemies on matrix: " + enemiesOnMatrix());

        playersTurn++;

        //if (Players[playersTurn-1].isDead()){
        //    System.out.println(Players[playersTurn].getName());
        //    playersTurn++;
        //}

        if (playersTurn >= 3){
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

        } steps = Players[playersTurn].getSteps();
    }

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

    public void animMove(Character character, int matrixId, List<Node> steps){
        for (Node coordinates : steps) {
            character.setPosition(new int[]{coordinates.getCoords()[0], coordinates.getCoords()[1]});
            cleanLeftBehind(character);
            updateMatrix(coordinates.getCoords()[0], coordinates.getCoords()[1], matrixId);
        } updateFrame();
    }

    public void enemyAttack (Enemy enemy, int[] playerPos){
        Player objective = null;
        for (Player player : Players){
            if (Arrays.equals(player.getPosition(),playerPos)){
                objective = player;
            }
        }

        if (enemy.getPoison()){
            objective.newIsPoisoned(3);
        }

        objective.subtractHealth(enemy.getDamage());   // Attack

        if (objective.isDead()){
            objective.setHealth(0);
            int[] pos = objective.getPosition();
            matrix[pos[0]][pos[1]] = 0;
        }
    }


    // / / / / / / / / / / END GAME

    public void defeat(){
        if (Gringo.isDead() && David.isDead() && Amalia.isDead()){
            state[1] = true;
            return;
        }

        int[] r = {3,11,12,10,4,2,12,12,12,12}, c = {14,14,14,14,14,14,8,7,6,10};

        for (int i = 0; i < 10; i++){
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
MUST:
PLAYER DEATH                        G
POISON MENU                         G
VIBE CHECK (ABILITIES)
BALANCE ENEMY DAMAGE & ABILITIES    GD
ONLY ONE PERSON CAN EQUIP AN ITEM   G
DELETE EXTRAS                       D


OPTIONAL:
MOVEMENT ANIMATIONS
MUSIC
BOSS
MENU IMAGES WEAPONS
ADDITIONAL EFFECTS (ATTACK, SOUND)
 */