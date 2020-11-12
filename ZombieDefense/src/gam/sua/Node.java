package gam.sua;

import java.util.List;

public class Node {
    private int gCost;  // Distancia a Inicio
    private int hCost;  // Distancia a Final
    private int fCost;  // gCost + hCost
    private int[] coords;
    private Node anterior;

    public Node(){
        this.coords = null;
        this.anterior = null;
        this.gCost = 0;
        this.hCost = 0;
        this.fCost = 0;
    }
    public Node(int[] cor){
        this.coords = cor;
        this.anterior = null;
        this.gCost = 0;
        this.hCost = 0;
        this.fCost = 0;
    }


    // getters y setters
    public int[] getCoords() {
        return coords;
    }
    public int getfCost() {
        return fCost;
    }
    public int getgCost() {
        return gCost;
    }
    public Node getAnterior() {
        return anterior;
    }
    public void setAnterior(Node anterior) {
        this.anterior = anterior;
    }
    public void setgCost(int gCost) {
        this.gCost = gCost;
    }

    public void calculateFCost (){
        if (coords == null){
            fCost = 0;
        }
        fCost = gCost + hCost;
    }


    /*  1 2 3
        4 5 6
        7 8 9

     5 = posicion actual  */

    public int calculateHCost (int[] end){
        boolean flag = true;
        int res = 0;
        while (flag){
            if (coords[0] > end[0] && coords[1] > end[1]){     // 1
                coords = new int[]{coords[0]-1, coords[1]-1};
                res += 14;
                continue;
            }
            if (coords[0] > end[0] && coords[1] == end[1]){    // 2
                coords = new int[]{coords[0]-1, coords[1]};
                res += 10;
                continue;
            }
            if (coords[0] > end[0] && coords[1] < end[1]){     // 3
                coords = new int[]{coords[0]-1, coords[1]+1};
                res += 14;
                continue;
            }
            if (coords[0] == end[0] && coords[1] > end[1]){    // 4
                coords = new int[]{coords[0], coords[1]-1};
                res += 10;
                continue;
            }
            if (coords[0] == end[0] && coords[1] == end[1]){   // 5
                flag = false;
                break;
            }
            if (coords[0] == end[0] && coords[1] < end[1]){    // 6
                coords = new int[]{coords[0], coords[1]+1};
                res += 10;
                continue;
            }
            if (coords[0] < end[0] && coords[1] > end[1]){     // 7
                coords = new int[]{coords[0]+1, coords[1]-1};
                res += 14;
                continue;
            }
            if (coords[0] < end[0] && coords[1] == end[1]){    // 8
                coords = new int[]{coords[0]+1, coords[1]};
                res += 10;
                continue;
            }
            if (coords[0] == end[0] && coords[1] > end[1]){    // 9
                coords = new int[]{coords[0]+1, coords[1]+1};
                res += 14;
                continue;
            }
        }
        return res;
    }
}
