package gam.sua;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Enemy extends Character{
    private int damage;
    private Boolean ghost;
    private Boolean poison;
    private Boolean revive;
    private int[] objectivePos;
    private int[] lastPositions;
    private int[] base;

    Enemy(int[] _position, int _health, int _range, int _id, String name, int[] objective){
        super(_position, _health, _range, _id);
        base = objective;
        objectivePos = new int[] {0,0};
        ghost = false;
        if (name == "ghost"){
            ghost = true;
        }
    }

    public void dropItem(){

    }

    public void attack(){

    }

    public int[] getoObjectivePos() {
        return objectivePos;
    }

    public void getObjectivePos(Map map){
        if (map.isNear(this.getPosition(), 2, 2) != null){
            this.objectivePos = this.getPosition();
        } else if (map.isNear(this.getPosition(), 5, 2) != null){
            this.objectivePos = map.isNear(this.getPosition(), 5, 2);
        } else if (map.findMatrix(9) != null){
            this.objectivePos = map.findMatrix(9);
        } else {
            this.objectivePos = this.base;
        }
    }

    // ========================___AI___===========================


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

    // vertical || horizontal = 10, diagonal = 14
    public int gCostAnterior(Node node, Node nodeAnterior){
        if (node.getCoords()[0] == nodeAnterior.getCoords()[0] || node.getCoords()[1] == nodeAnterior.getCoords()[1]){
            return 10;      // Horizontal o Vertical
        }
        return 14;          // Diagonal
    }

    public int calculateGCost (Node node){
        int res = 0;
        while (node.getAnterior() != null){
            res += gCostAnterior(node,node.getAnterior());
            node = node.getAnterior();
        }
        return res;
    }

    public boolean shorterPath(Node neighbour, Node current){
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

    // retorna una lista de los pasos del enemigo o null si no se mueve o no encuentra camino
    // A* PathFinding
    public List<Node> ai (Map map){
        List<Node> open = new ArrayList<>();
        List<Node> closed = new ArrayList<>();

        Node startNode = new Node(this.getPosition());
        startNode.calculateHCost(objectivePos);
        startNode.calculateFCost();
        open.add(startNode);        // Beginning gam.sua.Node

        if (map.isNear(this.getPosition(), 1, 2) != null){       // Is 1 position away
            return null;
        }
        if (map.valorPos(this.objectivePos[0], this.objectivePos[1]) != 0 || this.getPosition() == this.objectivePos){
            return null;
        }

        while (open.size() > 0){
            Node current = lowestFCost (open);
            open.remove(current);
            closed.add(current);

            if ((current.getCoords()[0] == this.objectivePos[0] && current.getCoords()[1] == this.objectivePos[1])){   // The end is reached
                List<Node> path = finalPath(current);
                return path;
            }

            for (Node neighbour:getNeighbours(current)){
                if (neighbour.getCoords()[0] < 0 || neighbour.getCoords()[0] >= 25 || neighbour.getCoords()[1] < 0 || neighbour.getCoords()[1] >= 45){      // Out of Map
                    continue;
                }
                if ((map.valorPos(neighbour.getCoords()[0],neighbour.getCoords()[1]) != 0 && !this.ghost) || closed.contains(neighbour)){
                    continue;
                }

                if (shorterPath(neighbour, current) || !open.contains(neighbour)){
                    neighbour.setAnterior(current);
                    neighbour.setgCost(calculateGCost(neighbour));
                    neighbour.calculateHCost(this.objectivePos);
                    neighbour.calculateFCost();

                    if (!open.contains(neighbour)){
                        open.add(neighbour);
                    }
                }
            }
        }
        return null;    // No path found
    }

    public List<Node> ai2 (Map map){
        List<Node> open = new ArrayList<>();
        List<Node> closed = new ArrayList<>();

        Node startNode = new Node(this.getPosition());
        Node endNode = new Node(this.objectivePos);
        startNode.calculateHCost(objectivePos);
        startNode.calculateFCost();
        open.add(startNode);        // Beginning gam.sua.Node

        int gCostTotal = 0;

        if (map.isNear(this.getPosition(), 1, 2) != null){       // Is 1 position away
            return null;
        }

        while (open.size() > 0){
            Node current = lowestFCost (open);

            if ((current.getCoords()[0] == this.objectivePos[0] && current.getCoords()[1] == this.objectivePos[1])){   // The end is reached
                List<Node> path = finalPath(endNode);
                return path;
            }

            open.remove(current);
            closed.add(current);

            for (Node neighbour:getNeighbours(current)){
                if ((map.valorPos(neighbour.getCoords()[0],neighbour.getCoords()[1]) != 0 && !this.ghost) || closed.contains(neighbour)){
                    continue;
                }
                if (neighbour.getCoords()[0] < 0 || neighbour.getCoords()[0] >= 25 || neighbour.getCoords()[1] < 0 || neighbour.getCoords()[1] >= 45){      // Out of Map
                    continue;
                }
                int tentativeGCost = current.getgCost() + gCostAnterior(current,neighbour);
                if (tentativeGCost < (gCostTotal + gCostAnterior(current,neighbour)) || !open.contains(neighbour)){
                    neighbour.setAnterior(current);
                    neighbour.setgCost(tentativeGCost);
                    neighbour.calculateHCost(this.objectivePos);
                    neighbour.calculateFCost();
                    endNode = neighbour;
                    gCostTotal = gCostAnterior(current,neighbour);

                    if (!open.contains(neighbour)){
                        open.add(neighbour);
                    }
                }
            }
        }
        return null;    // No path found
    }
}
