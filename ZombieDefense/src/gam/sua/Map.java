package gam.sua;

import javax.swing.*;

public class Map {
    private int[][] matrix = new int[50][30];

    private final JFrame frame = new JFrame();

    private final JLabel bg = new JLabel(new ImageIcon("images\\Map32.png"));

    private Map(){
        //Basic Stuff
        int Width=1600,Height=960;

        frame.setTitle("Monster Defense");
        frame.setSize(Width,Height);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(bg);

        frame.setVisible(true);

        frame.revalidate();
        frame.repaint();
    }




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

    public static void main(String[] args){
        new Map();
    }
}