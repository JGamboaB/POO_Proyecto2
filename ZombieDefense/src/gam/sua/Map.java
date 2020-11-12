package gam.sua;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Map{

    //Players
    private final Player Gringo = new Player(new int[]{13, 7}, 100, 3, 0, "Gringo");
    private final Player David = new Player(new int[]{18, 7}, 100, 5, 1, "David" );
    private final Player Amalia = new Player(new int[]{18, 8}, 150, 3, 2, "Amalia" );
    private final Player[] Players = {Gringo,David,Amalia};

    //Enemies
    private final Enemy[] Skeletons = new Enemy[10];
    private final Enemy[] Slimes = new Enemy[8];
    private final Enemy[] Zombies = new Enemy[6];
    private final Enemy[] Ghosts = new Enemy[6];
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
    private int[] doneActs = new int[]{0,0};
    private final int[] numEnemies = new int[]{0,0,0,0}; //Skeleton, Slime, Zombie, Ghost
    private int playersTurn = 0; //0,1,2
    private boolean turn = true;
    private int round = 0;
    private int steps = Gringo.getSteps();

    private final Random random = new Random();
    //Move all the creatures simultaneously?

    //Images
    private final JLabel bg = new JLabel(new ImageIcon("images\\MapV2.png"));
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
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,1,1,0,0,0,0,0,0,1,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,1,1,0,0,0,1,1,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,0,0,1,1,1,1,1,1,0,0,0,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,0,0,0,0,1,1,1,0,0,0,0,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,1,0,0,0,0,1,1,0,0,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,0,1,1,0,1,1,1,0,1,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,0,1,1,0,0,1,1,0,0,1,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,0,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0,0,1,1,1,0,1,1,1,1,0,0,0,0,1,0,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,0,1,1,0,0,0,0,0,1,1,0,1,1,1,1,0,0,0,0,1,0,0,0,0,0,0,0,1,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,1,1,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0},
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

    void startingPos(){
        updateMatrix(13,7,2);
        updateMatrix(18,8,2);
        updateMatrix(18,7,2);

        //Item on the floor to test
        updateMatrix(24,44,10);

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
        Menu.showMenu(Players[playersTurn],doneActs);

        frame.add(Menu);
        frame.add(panel);
        frame.add(bg);

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
                if (doneActs[1] == 0)
                    showAttackRange(Players[playersTurn]);
                action = 1;
            }

            //Inventory
            if (e.getKeyChar() == 'i'){
                if (action == -1){
                    showInv();
                    action = 2;
                } else if (action == 2) {
                    resetMenu();
                }
            }

            //Equip Item
            if (action == 2){
                if (java.lang.Character.isDigit(e.getKeyChar())){
                    equipItem(Integer.parseInt(String.valueOf(e.getKeyChar()))-1);
                    resetMenu();
                }
            }

            //Next Turn
            if (e.getKeyChar() == 'n') {
                changeTurn();
                doneActs = new int[]{0, 0};
                action = -1;
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

    public void movePlayer2(int r, int c, Character character){
        int[] posPlayer = character.getPosition();
        if (character.getSteps() < Math.abs(r-posPlayer[0]) || character.getSteps() < Math.abs(c-posPlayer[1])) {
            //System.out.println("Above range");
            doneActs[0] = 0;
            return;
        } if (matrix[r][c] != 0){
            //System.out.println("Can't Walk Over There");
            doneActs[0] = 0;
            return;
        }
        character.setPosition(new int[]{r,c});
        cleanLeftBehind(character);
        updateMatrix(r,c,2);
    }

    public static void wait(int ms){
        try {
            Thread.sleep(ms);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public void animMove(Character character, int matrixId, List<int[]> steps){
        for (int[] coordinates : steps) {
            character.setPosition(new int[]{coordinates[0], coordinates[1]});
            cleanLeftBehind(character);
            updateMatrix(coordinates[0], coordinates[1], matrixId);
            charMatrix();
            wait(1000);
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
        Menu.showMenu(Players[playersTurn],doneActs);

        frame.add(Menu);
        frame.add(panel);
        frame.add(bg);

        frame.revalidate();
        frame.repaint();

    }

    void showAttackRange2(Player player){ //Print Area that the Player can Attack.
        int range = player.getWeaponRange();
        int r = player.getPosition()[0], c = player.getPosition()[1];
        int x, y;


        for (int r2 = r-range; r2<=(r+range); r2++){
            for (int c2 = c-range; c2<=(c+range);c2++){
                if (matrix[r2][c2] > 2 && matrix[r2][c2] < 8){
                    x = (c2*32); y = (r2*32);
                    JLabel rangeAtt = new JLabel(new ImageIcon(attackArea.getImage()));
                    rangeAtt.setBounds(x,y,32,32);
                    panel.add(rangeAtt);
                }
            }
        }

        charMatrix();
        Menu.showMenu(Players[playersTurn],doneActs);

        frame.add(Menu);
        frame.add(panel);
        frame.add(bg);

        frame.revalidate();
        frame.repaint();

    }

    public void attack(int r, int c){
        Player player = Players[playersTurn];
        int[] pos = player.getPosition();
        if ((matrix[r][c] > 2 && matrix[r][c] < 7) && (player.getWeaponRange() >= Math.abs(r-pos[0]) && player.getWeaponRange() >= Math.abs(c-pos[1]))){
            Enemy enemy = getEnemyByPos(r,c);
            enemy.subtractHealth(player.getDMG());
            enemyDeath(enemy);
        } else {
            doneActs[1] = 0;
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
        sharedInventory.add(item);
        if (item.getId() < 7) //Only removes the Weapons from the possible items
            inv.remove(item);
    }

    public void equipItem(int num){
        if (num > sharedInventory.size())
            return;

        Items item = sharedInventory.get(num);
        Player player = Players[playersTurn];

        if (item instanceof Weapons){
            player.setDMG(((Weapons) item).getDamage());
            player.setWeaponRange(((Weapons) item).getRange());
        } else {
            if (item.getId() == 8) //REVIVE
                player = lowestHealthPlayer(); //Gives the effects to the player on the lowest health or dead

            player.addHealth(((Consumable) item).getHealth());
            player.setIsPoisoned(((Consumable) item).curesPoison());
            sharedInventory.remove(num);
        }
    }

    public void resetMenu(){
        Menu.removeAll();
        Menu.showMenu(Players[playersTurn],doneActs);

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
            if (activeEnemy.getPosition()[0] == r && activeEnemy.getPosition()[1] == c)
                return activeEnemy;
        }
        return null;
    }

    public void enemyDeath(Enemy enemy){
        if (enemy.isDead()){
            int drop = random.nextInt(11);
            int matrixId = (drop < 2)? 10:0;

            if (Players[playersTurn].getLuck()) //If LUCK, always drop loot
                matrixId = 10;

            matrix[enemy.getPosition()[0]][enemy.getPosition()[1]] = matrixId;
        }
    }

    public void createEnemy(int[] position, int matrixId, int id){
        switch (matrixId) {
            case 3 -> {
                Skeletons[numEnemies[0]] = new Enemy(position, 10, 2, id, " ");
                activeEnemies.add(Skeletons[numEnemies[0]]);
                numEnemies[0]++;
            }
            case 4 -> {
                Slimes[numEnemies[1]] = new Enemy(position, 5, 3, id, " ");
                activeEnemies.add(Slimes[numEnemies[1]]);
                numEnemies[1]++;
            }
            case 5 -> {
                Zombies[numEnemies[2]] = new Enemy(position, 15, 2, id, " ");
                activeEnemies.add(Zombies[numEnemies[2]]);
                numEnemies[2]++;
            }
            case 6 -> {
                Ghosts[numEnemies[3]] = new Enemy(position, 20, 4, id, " ");
                activeEnemies.add(Ghosts[numEnemies[3]]);
                numEnemies[3]++;
            }
        }
    }

    public void spawners(){ //Validate that there is no player in the spawn points
        switch (round){
            case 5:
                createEnemy(new int[]{1,23},6,numEnemies[3]);
                updateMatrix(1,23,6);
                createEnemy(new int[]{1,25},6,numEnemies[3]);
                updateMatrix(1,25,6);

            case 4: //Ghosts
                createEnemy(new int[]{13,34},6,numEnemies[3]);
                updateMatrix(13,34,6);
                createEnemy(new int[]{13,36},6,numEnemies[3]);
                updateMatrix(13,36,6);

            case 3: //Zombies
                createEnemy(new int[]{19,39},5,numEnemies[2]);
                updateMatrix(19,39,5);
                createEnemy(new int[]{18,37},5,numEnemies[2]);
                updateMatrix(18,37,5);

            case 2: //Slimes
                createEnemy(new int[]{16,43},4,numEnemies[1]);
                updateMatrix(16,43,4);
                createEnemy(new int[]{14,42},4,numEnemies[1]);
                updateMatrix(14,42,4);

            case 1: //Skeletons
                createEnemy(new int[]{21,41},3,numEnemies[0]);
                updateMatrix(21,41,3);
                createEnemy(new int[]{22,42},3,numEnemies[0]);
                updateMatrix(22,42,3);
        }
    }


    // / / / / / / / / / / TURN

    public void changeTurn(){
        playersTurn++;
        if (playersTurn == 3){
            playersTurn = 0;
            if (enemiesOnMatrix() == 0){
                round++;
                spawners();
            }
            turn = false;
        } steps = Players[playersTurn].getSteps();
    }


    // / / / / / / / / / / END GAME

    public void defeat(){}

    public void victory(){}


     // / / / / / / / / / / CLASS

    public static void main(String[] args) throws IOException, FontFormatException {
        new Map();
    }
}

//Menu->Current Round, DMG, Weapon Range
//Movement steps player WASD
//Cancel Movement,Attack
//INV Errors (Equipping weapons twice in the same turn, LIST in order that has all unique items first and then rep)
//AI Movement to base by using equation of distance between two points

