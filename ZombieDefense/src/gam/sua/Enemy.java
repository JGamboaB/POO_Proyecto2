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
        if (list.size() == 0){
            return null;
        }
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

    public List<Node> finalPath(Node end){
        List<Node> path = new ArrayList<>();
        path.add(end);
        while (end.getAnterior() != null){
            path.add(end.getAnterior());
            end = end.getAnterior();
        }
        Collections.reverse(path);
        return path;
    }

    // retorna una lista de los pasos del enemigo o null si no se mueve o no encuentra camino
    // A* PathFinding
    public List<Node> ai(Map map){
        List<Node> open = new ArrayList<>();
        List<Node> closed = new ArrayList<>();

        Node startNode = new Node(this.getPosition());
        Node endNode = new Node(this.objectivePos);
        startNode.calculateHCost(objectivePos);
        startNode.calculateFCost();
        open.add(startNode);        // Beginning gam.sua.Node
        boolean flag = true;        // Ends loop

        int gCostTotal = 0;

        if (map.isNear(this.getPosition(), 1, 2) != null){       // Is 1 position away
            return null;
        }

        while (flag){
            if (lowestFCost(open) == null){
                return null;
            }

            Node current = lowestFCost (open);
            List<Node> path = finalPath(endNode);

            if ((current.getCoords()[0] == this.objectivePos[0] && current.getCoords()[1] == this.objectivePos[1]) || path.size() >= 5){   // The end is reached
                return path;
            }

            open.remove(current);
            closed.add(current);

            for (Node neighbour:getNeighbours(current)){
                if ((map.valorPos(neighbour.getCoords()[0],neighbour.getCoords()[1]) != 0 && !this.ghost) || closed.contains(neighbour)){
                    continue;
                }
                neighbour.setgCost(gCostTotal + gCostAnterior(current,neighbour));
                int tentativeGCost = current.getgCost() + neighbour.gethCost();
                if (tentativeGCost < neighbour.getgCost()){
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
