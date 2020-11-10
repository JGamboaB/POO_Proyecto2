package gam.sua;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Scanner; //



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
    private final Enemy[] ActiveEnemies = new Enemy[30];

    private final Menu Menu = new Menu();

    private int[][] matrix;// = new int[50][30];

    private final JFrame frame = new JFrame();
    private final JPanel panel = new JPanel();

    private int action = -1; //0 move, 1 attack, 2 equip
    private int[] doneActs = new int[]{0,0,0};
    private int[] numEnemies = new int[]{0,0,0,0}; //Skeleton, Slime, Zombie, Ghost
    private int playersTurn = 0; //0,1,2
    private boolean turn = true;
    private int round = 0;
    //Move all the creatures simultaneously?

    //Images
    private final JLabel bg = new JLabel(new ImageIcon("images\\MapV2.png"));
    private final JLabel[] playerImgs = new JLabel[3];
    private final ImageIcon[] enemyImgs = new ImageIcon[4];

    private final ImageIcon rangeArea = new ImageIcon("images\\move.png");
    private final ImageIcon attackArea = new ImageIcon("images\\attack.png");


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

    public void initMatrix(){ //0 can move, 1 occupied, 2 player, 3 skeleton, 4 slime, 5 zombie, 6 ghost, 7 boss, 8 chest
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

    void initImgs(){
        for (int i = 0; i < 7; i++){
            if (i < 3)
                playerImgs[i] = new JLabel(new ImageIcon("images\\CharIMG\\"+i+".png"));
            else
                enemyImgs[i-3] = new ImageIcon("images\\CharIMG\\"+i+".png");
        }
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
                }

            }
        }
    }

    void startingPos(){
        updateMatrix(13,7,2);
        updateMatrix(18,8,2);
        updateMatrix(18,7,2);
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

    MouseListener mouse = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
            double x = e.getX(), y = e.getY();
            int r, c;

            //System.out.println("x = " + x + ", y = " + y);

            c = (int) x/32;
            r = (int) y/32 - 1;

            //System.out.println("Row: " + r + " Column: " + c);

            if (action == 0 && doneActs[0] == 0){
                action = -1;
                doneActs[0] = 1;
                movePlayer(r,c);
                updateFrame();
            }

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

    KeyListener keyboard = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {}

        @Override
        public void keyReleased(KeyEvent e) {
            //System.out.println("A key has been pressed: "+e.getKeyChar());

            if (e.getKeyChar() == 'm'){
                if (doneActs[0] == 0)
                    showRangeArea(Players[playersTurn]);
                action = 0;
            }

            if (e.getKeyChar() == 'a'){
                if (doneActs[1] == 0)
                    showAttackRange(Players[playersTurn]);
                action = 1;
            }

            if (e.getKeyChar() == 'n') {
                changeTurn();
                doneActs = new int[]{0, 0, 0};
                action = -1;
                updateFrame();
            }
        }
    };

    void showRangeArea(Player player){
        int range = player.getRange();
        int r = player.getPosition()[0], c = player.getPosition()[1];
        int x = (c-range)*32, y = (r-range)*32;
        int side = 32*(range*2+1);

        JLabel rangeView = new JLabel();
        rangeView.setBounds(x,y,side,side);
        rangeView.setIcon(new ImageIcon(rangeArea.getImage().getScaledInstance(side,side,Image.SCALE_FAST)));
        panel.add(rangeView);


        charMatrix();
        Menu.showMenu(Players[playersTurn],doneActs);

        frame.add(Menu);
        frame.add(panel);
        frame.add(bg);

        frame.revalidate();
        frame.repaint();
    }

    void showAttackRange(Player player){
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

    void movePlayer(int r, int c){
        int[] posPlayer = Players[playersTurn].getPosition();
        if (Players[playersTurn].getRange() < Math.abs(r-posPlayer[0]) || Players[playersTurn].getRange() < Math.abs(c-posPlayer[1])){
            //System.out.println("Above range");
            doneActs[0] = 0;
            return;
        } if (matrix[r][c] != 0){
            //System.out.println("Can't Walk Over There");
            doneActs[0] = 0;
            return;
        }

        Players[playersTurn].setPosition(new int[]{r,c});
        cleanLeftBehind(Players[playersTurn]);
        updateMatrix(r,c,2);
    }

    public void movePlayer2(int r, int c, Character character){
        int[] posPlayer = character.getPosition();
        if (character.getRange() < Math.abs(r-posPlayer[0]) || character.getRange() < Math.abs(c-posPlayer[1])) {
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

    public void attack(int r, int c){
        Player player = Players[playersTurn];
        int[] pos = player.getPosition();
        if ((matrix[r][c] > 2 && matrix[r][c] < 7) && (player.getWeaponRange() >= Math.abs(r-pos[0]) && player.getWeaponRange() >= Math.abs(c-pos[1]))){
            System.out.println("Valid Attack");
            Enemy enemy = getEnemyByPos(r,c);
            enemy.subtractHealth(player.getDMG());
            enemyDeath(enemy);
        } else {
            doneActs[1] = 0;
        }
    }

    public int[][] getMatrix(){return matrix;}

    public int valorPos(int r, int c){return matrix[r][c];}

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
        int enemies = enemiesOnMatrix();

        for (int i = 0; i < enemies; i++){
            if (ActiveEnemies[i].isDead()){
                for (int j = i+1; j<enemies;j++){
                    if (!ActiveEnemies[j].isDead()) {
                        ActiveEnemies[i] = ActiveEnemies[j];
                        break;
                    }
                }
            }
        }
    }

    public Enemy getEnemyByPos(int r, int c){
        int enemies = enemiesOnMatrix();

        for (int i = 0; i < enemies; i++){
            if (ActiveEnemies[i].getPosition()[0] == r && ActiveEnemies[i].getPosition()[1] == c)
                return ActiveEnemies[i];
        }

        return null;
    }

    public void enemyDeath(Enemy enemy){
        if (enemy.isDead()){
            matrix[enemy.getPosition()[0]][enemy.getPosition()[1]] = 0;
        }
    }

    public void createEnemy(int[] position, int matrixId, int id){
        int enemiesOnScreen = enemiesOnMatrix();

        switch (matrixId){
            case 3:
                Skeletons[numEnemies[0]] = new Enemy(position, 10,2,id," ");
                ActiveEnemies[enemiesOnScreen] = Skeletons[numEnemies[0]];
                numEnemies[0]++;
                break;
            case 4:
                Slimes[numEnemies[1]] = new Enemy(position,5,3,id," ");
                ActiveEnemies[enemiesOnScreen] = Slimes[numEnemies[1]];
                numEnemies[1]++;
                break;
            case 5:
                Zombies[numEnemies[2]] = new Enemy(position,15,2,id," ");
                ActiveEnemies[enemiesOnScreen] = Zombies[numEnemies[2]];
                numEnemies[2]++;
                break;
            case 6:
                Ghosts[numEnemies[3]] = new Enemy(position,20,4,id," ");
                ActiveEnemies[enemiesOnScreen] = Ghosts[numEnemies[3]];
                numEnemies[3]++;
                break;
        }
    }

    public void spawners(){
        switch (round){
            case 1: //Skeletons
                createEnemy(new int[]{21,41},3,numEnemies[0]);
                updateMatrix(21,41,3);
                createEnemy(new int[]{22,42},3,numEnemies[0]);
                updateMatrix(22,42,3);
                if (round == 1)
                    break;
        }
    }

    public void changeTurn(){
        playersTurn++;
        if (playersTurn == 3){
            playersTurn = 0;
            //if (enemiesOnMatrix() == 0)
                round++;
            spawners();
            turn = false;
        }
    }

    public void defeat(){}

    public void victory(){}

    public static void main(String[] args) throws IOException, FontFormatException {
        new Map();

    }
}