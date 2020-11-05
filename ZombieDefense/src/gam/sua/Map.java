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

    private final Player Gringo = new Player(new int[]{13, 7}, 100, 3, 0, "Gringo");
    private final Player David = new Player(new int[]{18, 7}, 100, 5, 1, "David" );
    private final Player Amalia = new Player(new int[]{18, 8}, 150, 3, 2, "Amalia" );
    private final Player[] Players = {Gringo,David,Amalia};

    private final Menu Menu = new Menu();

    private int[][] matrix;// = new int[50][30];

    private final JFrame frame = new JFrame();
    private final JPanel panel = new JPanel();

    private int playersTurn = 0; //0,1,2
    private int turn = 0; //0,1
    //Move all the creatures simultaneously?

    //Images
    private final JLabel bg = new JLabel(new ImageIcon("images\\C-1.png"));
    private final JLabel[] playerImgs = new JLabel[3];
    private final JLabel move = new JLabel(new ImageIcon("images\\move.png")); //This image is going to be used many times//Can expand it to the area, would include the areas that you cannot walk but it shows the whole area

    public Map() throws IOException, FontFormatException {
        //Basic Stuff
        int Width=1200+14,Height=720+24; //24x24 Pixels//720+37

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

        Menu.setBounds(980,25,185,345);
        Menu.setLayout(null);
        Menu.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f)); //Transparent

    }

    public void initMatrix(){ //0 can move, 1 occupied, 2 teleport, 3 chest, 4 player, 5 slime, 6 ghost, 7 zombie, 8 skeleton, 9 boss
        matrix = new int[][]{
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,3,3,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,0,1,1,1,1,1,1,1,1,1,1,2,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,0,0,0,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,0,0,1,1,1,1,1,1,0,0,0,0,0,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,0,0,0,0,0,0,0,0,0,1,0,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,0,0,0,0,1,1,1,0,0,1,1,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,0,0,0,0,1,1,1,1,0,0,1,1,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,0,1,1,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,0,0,0,0,1,1,1,1,1,1,0,1,0,1,1,0,1,1,0,0,0,0,0,0,1,1,0,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,0,0,0,0,1,1,1,1,1,1,0,1,0,1,1,0,0,0,0,0,1,1,0,1,1,1,0,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,0,0,0,0,1,1,1,1,1,1,0,1,0,1,1,0,0,0,0,1,1,1,0,1,1,1,0,0,0,0,0,1,1,0,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,0,1,1,1,1,1,0,0,0,1,1,0,1,1,1,1,1,1,1,1,1,1},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0,0,0,1,1,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1},
        {1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,0,1,1,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,1,0,1,1,0,0,0,1,1,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,1,1,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,0},
        {1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,0},
        {1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,1,0,0,0,0,0,0},
        {1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,1,0,0,0,0,0,0},
        {1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,0,0,0,0,1,0,0,0,0,0,0},
        {1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,1,0},
        {1,0,0,0,0,0,1,1,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0},
        {1,0,0,0,0,0,1,1,0,0,0,1,1,0,0,0,0,0,0,0,1,1,0,1,1,1,1,1,1,1,1,1,0,2,2,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0},
        {1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,1,1,1,1,1,1,1,1,1,0,2,2,0,1,1,1,0,0,0,0,0,0,0,0,0,1,0}
        };
    }

    void initImgs(){
        for (int i = 0; i < 3; i++){
            playerImgs[i] = new JLabel(new ImageIcon("images\\PlayerIMG\\"+i+".png"));
        }
    }

    //I should do an automatic update matrix that puts 0 in the place that the player was before. Cleans.
    void updateMatrix(int r, int c, int matrixID){
        matrix[r][c] = matrixID;
    }

    //Called when a character moves
    void cleanLeftBehind(Character Char){
        int r = Char.getOldPos()[0], c = Char.getOldPos()[1];
        matrix[r][c] = 0;
    }

    void moveTEST(){
        Scanner in = new Scanner(System.in);

        System.out.println("Enter the row: ");
        int r = in.nextInt();

        System.out.println("Enter the col: ");
        int c = in.nextInt();

        Players[0].setPosition(new int[]{r,c});
        cleanLeftBehind(Players[0]);
        updateMatrix(r,c,4);
    }

    void charMatrix(){
        for (int r = 0; r < 30; r++){
            for (int c = 0; c < 50; c++){

                //Players
                if (matrix[r][c] == 4){
                    for (int i = 0; i < 3; i++){


                        if (Players[i].getPosition()[0] == r && Players[i].getPosition()[1] == c){
                            //System.out.println("Here!");
                            playerImgs[i].setBounds(c*24,r*24,24,24);
                            panel.add(playerImgs[i]);
                            //break;
                        }

                    }
                }
            }
        }
        //frame.add(panel);
    }

    void startingPos(){
        updateMatrix(13,7,4);
        updateMatrix(18,8,4);
        updateMatrix(18,7,4);
    }

    void updateFrame(){
        frame.getContentPane().removeAll();
        panel.removeAll();
        Menu.removeAll();

        charMatrix();
        Menu.showMenu(David);

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

            System.out.println("x = " + x + ", y = " + y);

            c = (int) x/24;
            r = (int) y/24 - 1;
            System.out.println("Row: " + r + " Column: " + c);

            if (matrix[r][c] == 0)
                System.out.println("Can walk");
            if (matrix[r][c] == 4)
                System.out.println("Player");
            if (matrix[r][c] == 1)
                System.out.println("Hit");

        }

        @Override //PRESS ONLY ONCE TO TEST
        public void mousePressed(MouseEvent e) {
            //moveTEST();
            //updateFrame();
            //return;
        }

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
            System.out.println("A key has been pressed: "+e.getKeyChar());
        }
    };


    public void spawners(){

    }

    public void defeat(){

    }

    public void victory(){

    }

    public void updatePos(){

    }

    public void changeTurn(){

    }

    public void showImage(){

    }

    public static void main(String[] args) throws IOException, FontFormatException {
        new Map();
    }
}