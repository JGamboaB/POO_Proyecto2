package gam.sua;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

public class Enemy extends Character{
    private int damage;
    private boolean ghost;
    private boolean poison;
    private boolean revive;
    private int[] objectivePos;
    private int[] base;

    /** Constructor
     * @param _position int[] of the position in the matrix
     * @param _health int health
     * @param _range int range
     * @param _id int id
     * @param name String name
     * @param objective int[] objective. Coordinates in the matrix of objective
     */
    Enemy(int[] _position, int _health, int _range, int _id, String name, int[] objective){
        super(_position, _health, _range, _id);
        base = objective;
        objectivePos = new int[] {0,0};
        ghost = false;
        poison = false;
        revive = false;
        damage = 0;

        switch (name) {
            case "ghost" -> {
                ghost = true;
                damage = 75; //10
            }
            case "skeleton" -> damage = 25; //15
            case "zombie" -> {
                poison = true;
                damage = 50; //10
            }
            case "slime" -> {
                revive = true;
                damage = 15; //5
            }
        }
    }

    // Getters & Setters
    /** Returns the poison attribute
     * @return poison
     */
    public boolean getPoison() {return poison;}

    /** Returns the revive attribute
     * @return revive boolean
     */
    public boolean getRevive() {return revive;}

    /** Returns the damage attribute
     * @return damage int
     */
    public int getDamage() {return damage;}


    /** Set the revive attribute
     * @param revive boolean
     */
    public void setRevive(boolean revive) {this.revive = revive;}


    /** Changes the Objective Position depending on the priority
     * @param map
     */
    public void getObjectivePos(Map map){
        if (map.isNear(this.getPosition(), 1, 2) != null){              // Jugador Cercano (Ataque, No Moverse)
            this.objectivePos = this.getPosition();
        } else if (map.isNear(this.getPosition(), 5, 2) != null){       // Jugador Distancia Media (Acercarse)
            this.objectivePos = map.isNear(this.getPosition(), 5, 2);
        } else if (map.findMatrix(9) != null){                              //  Sonido (Acercarse)
            this.objectivePos = map.findMatrix(9);
        } else {
            this.objectivePos = this.base;                                       //  Ir a Base
        }
    }


    // ========================___AI___===========================


    /** Returns the Node with the lowest FCost from a Node List
     * @param list
     * @return
     */
    public Node lowestFCost (List<Node> list){
        if (list.size() == 1){
            return list.get(0);
        }
        Node lowest = list.get(0);
        for (int i = 1; i < list.size(); i++){
            if (list.get(i).getfCost() < lowest.getfCost()){
                lowest = list.get(i);
            }
        }
        return lowest;
    }

    /** Returns a Node Array of the neighbour Nodes of a Node
     * @param node
     * @return
     */
    public Node[] getNeighbours(Node node){
        Node[] array = new Node[8];
        int i = 0;
        for (int r = 0; r < 25; r++) {
            for (int c = 0; c < 45; c++) {
                if (r == node.getCoords()[0] && c == node.getCoords()[1]){
                    for (int ri = r-1; ri <= r+1; ri++){
                        for (int ci = c-1; ci <= c+1; ci++){
                            if (ri != node.getCoords()[0] || ci != node.getCoords()[1]){
                                array[i] = new Node(new int[]{ri, ci});
                                i++;
                            }
                        }
                    }
                    return array;
                }
            }
        }
        return null;
    }

    /** Returns the GCost value of the last movement from Node to neighbour Node
     * Vertical & Horizontal = 10
     * Diagonal = 14
     * @param node
     * @param nodeAnterior
     * @return
     */
    public int gCostAnterior(Node node, Node nodeAnterior){
        if (node.getCoords()[0] == nodeAnterior.getCoords()[0] || node.getCoords()[1] == nodeAnterior.getCoords()[1]){
            return 10;      // Horizontal o Vertical
        }
        return 14;          // Diagonal
    }

    /** Returns the total GCost of a Node
     * @param node
     * @return
     */
    public int calculateGCost (Node node){
        int res = 0;
        while (node.getAnterior() != null){
            res += gCostAnterior(node,node.getAnterior());
            node = node.getAnterior();
        }
        return res;
    }

    /** Returns true if the path from the current Node is shorter than the existing path
     * @param neighbour
     * @param current
     * @return
     */
    public boolean shorterPath (Node neighbour, Node current){

        Node node = neighbour;
        node.setgCost(calculateGCost(node));
        node.calculateHCost(this.objectivePos);
        node.calculateFCost();
        int viejo = node.getfCost();

        node.setAnterior(current);
        node.setgCost(calculateGCost(node));
        node.calculateHCost(this.objectivePos);
        node.calculateFCost();
        int nuevo = node.getfCost();

        if (nuevo < viejo){
            return true;
        }
        return false;
    }

    /** Returns true if a Node with certain coordinates is contained in a Node List
     * @param list
     * @param coords
     * @return
     */
    public boolean inList (List<Node> list, int[] coords){
        for (Node node : list){
            if (Arrays.equals(coords,node.getCoords())){
                return true;
            }
        }
        return false;
    }

    /** Returns true if the Node is walkable to a certain enemy
     * @param map
     * @param node
     * @return
     */
    public boolean canWalk (Map map, Node node){
        if (this.ghost){
            if (map.valorPos(node.getCoords()[0],node.getCoords()[1]) == 6){
                return false;
            }
        } else {
            if (map.valorPos(node.getCoords()[0],node.getCoords()[1]) != 0 && map.valorPos(node.getCoords()[0],node.getCoords()[1]) != 9 && map.valorPos(node.getCoords()[0],node.getCoords()[1]) != 10){
                if (map.valorPos(node.getCoords()[0],node.getCoords()[1]) == 2){
                    if (map.isNearCoord(node.getCoords(),1,this.getPosition())){
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    /** Calculates the final path
     * @param end
     * @return
     */
    public List<Node> finalPath(Node end){
        List<Node> path = new ArrayList<>();
        List<Node> res = new ArrayList<>();
        path.add(end);
        while (end.getAnterior() != null){
            path.add(end.getAnterior());
            end = end.getAnterior();
        }
        Collections.reverse(path);
        path.remove(0);
        for (int i = 0; i < path.size(); i++){
            if (i < 5){
                res.add(path.get(i));
            }
        }
        return res;
    }

    /** Returns a Node List with the shortest path, each Node being an step, returns null if there is no possible path or the enemy will not move
     * Uses the A* PathFinding method
     * @param map
     * @return
     */
    public List<Node> ai (Map map){
        List<Node> open = new ArrayList<>();
        List<Node> closed = new ArrayList<>();

        Node startNode = new Node(this.getPosition());
        startNode.calculateHCost(objectivePos);
        startNode.calculateFCost();
        open.add(startNode);        // Beginning gam.sua.Node
        boolean getNear = false;    // Flag to get near

        if (map.isNear(this.getPosition(), 1, 2) != null || this.getPosition() == this.objectivePos){       // Is 1 position away or is in objective
            return null;    // Dont Move
        }

        if (map.isNearCoord(this.getPosition(),5,this.objectivePos) && (map.valorPos(this.objectivePos[0],this.objectivePos[1]) != 0)){   // Has to get near
            getNear = true;
        }

        while (open.size() > 0){
            Node current = lowestFCost (open);
            open.remove(current);
            closed.add(current);
            //System.out.println(current.getCoords()[0] + " , " + current.getCoords()[1]);

            if (map.isNearCoord(current.getCoords(),1,this.objectivePos) && getNear){    // Gets close, not to the exact objective
                return finalPath(current);
            }
            if ((current.getCoords()[0] == this.objectivePos[0] && current.getCoords()[1] == this.objectivePos[1])){   // The end is reached
                return finalPath(current);
            }

            for (Node neighbour:getNeighbours(current)){

                if (Arrays.equals(this.objectivePos,neighbour.getCoords())){    // The end is reached
                    neighbour.setAnterior(current);
                    return finalPath(neighbour);
                }
                if (neighbour.getCoords()[0] < 0 || neighbour.getCoords()[0] >= 25 || neighbour.getCoords()[1] < 0 || neighbour.getCoords()[1] >= 45){
                    continue;   // Out of Map
                }

                if (!canWalk (map,neighbour) || inList(closed, neighbour.getCoords())){
                    continue;   // Cant move there
                }

                if (shorterPath(neighbour, current) || !inList(open,neighbour.getCoords())){
                    neighbour.setAnterior(current);
                    neighbour.setgCost(calculateGCost(neighbour));
                    neighbour.calculateHCost(this.objectivePos);
                    neighbour.calculateFCost();
                    if (!inList(open,neighbour.getCoords())){
                        open.add(neighbour);
                    }
                }
            }
        }
        return null;    // No path found
    }
}
