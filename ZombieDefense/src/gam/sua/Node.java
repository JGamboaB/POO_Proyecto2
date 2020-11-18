package gam.sua;

import java.util.List;

public class Node {
    private int gCost;  // Distancia a Inicio
    private int hCost;  // Distancia a Final
    private int fCost;  // gCost + hCost
    private int[] coords;
    private Node anterior;

    /** Constructor
     * @param cor
     */
    public Node(int[] cor){
        this.coords = cor;
        this.anterior = null;
        this.gCost = 0;
        this.hCost = 0;
        this.fCost = 0;
    }


    /** Getters & Setters
     * @return
     */
    public int[] getCoords() {
        return coords;
    }
    public int getfCost() {
        return fCost;
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


    /** Calculates and updates the FCost
     */
    public void calculateFCost (){
        if (coords == null){
            fCost = 0;
        }
        fCost = gCost + hCost;
    }


    /** Calculates and updates the HCost
     *
     * 1 2 3
     * 4 5 6
     * 7 8 9
     *
     * 5 = current position
     *
     * @param end
     */
    public void calculateHCost (int[] end){
        boolean flag = true;
        int[] c = coords;
        int res = 0;
        while (flag){
            if (c[0] > end[0] && c[1] > end[1]){     // 1
                c = new int[]{c[0]-1, c[1]-1};
                res += 14;
                continue;
            }
            if (c[0] > end[0] && c[1] == end[1]){    // 2
                c = new int[]{c[0]-1, c[1]};
                res += 10;
                continue;
            }
            if (c[0] > end[0] && c[1] < end[1]){     // 3
                c = new int[]{c[0]-1, c[1]+1};
                res += 14;
                continue;
            }
            if (c[0] == end[0] && c[1] > end[1]){    // 4
                c = new int[]{c[0], c[1]-1};
                res += 10;
                continue;
            }
            if (c[0] == end[0] && c[1] == end[1]){   // 5
                flag = false;
                break;
            }
            if (c[0] == end[0] && c[1] < end[1]){    // 6
                c = new int[]{c[0], c[1]+1};
                res += 10;
                continue;
            }
            if (c[0] < end[0] && c[1] > end[1]){     // 7
                c = new int[]{c[0]+1, c[1]-1};
                res += 14;
                continue;
            }
            if (c[0] < end[0] && c[1] == end[1]){    // 8
                c = new int[]{c[0]+1, c[1]};
                res += 10;
                continue;
            }
            if (c[0] < end[0] && c[1] < end[1]){    // 9
                c = new int[]{c[0]+1, c[1]+1};
                res += 14;
                continue;
            }
        }
        hCost = res;
    }
}
