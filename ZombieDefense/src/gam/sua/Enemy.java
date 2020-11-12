package gam.sua;
import java.util.List;
import java.util.Collections;

public class Enemy extends Character{
    private int damage;
    private Boolean ghost;
    private Boolean poison;
    private Boolean revive;
    private int[] objectivePos;
    private int[] lastPositions;
    private int steps;

    Enemy(int[] _position, int _health, int _range, int _id, String name){
        super(_position, _health, _range, _id);
        if (name == " "){
        }
    }

    public void dropItem(){

    }

    public void attack(){

    }

    public void getObjectivePos(Map map, int[] base){
        if (map.isNear(getPosition(), 2, 2) != null){
            objectivePos = getPosition();
        } else if (map.isNear(getPosition(), getSteps(), 2) != null){
            objectivePos = map.isNear(getPosition(), getSteps(), 2);
        } else if (map.findMatrix(9) != null){
            objectivePos = map.findMatrix(9);
        } else {
            objectivePos = base;
        }
    }

    // ========================___AI___===========================


    public Node lowestFCost (List<Node> list){
        Node lowest = new Node();
        for (int i = 0; i < list.size(); i++){
            if (list.get(i).getfCost() < lowest.getfCost()){
                lowest = list.get(i);
            }
        }
        return lowest;
    }

    public Node[] getNeighbours(Node node){
        Node[] array = null;
        int i = 0;
        for (int r = 0; r < 25; r++) {
            for (int c = 0; c < 45; c++) {
                if (new int[] {r,c} == node.getCoords()){
                    for (int ri = r-getSteps(); ri <= r+getSteps(); ri++){
                        for (int ci = c-getSteps(); ci <= c+getSteps(); ci++){
                            if (new int[]{ri,ci} != node.getCoords()){
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
        List<Node> path = null;
        path.add(end);
        Node current = end;
        while (current.getAnterior() != null){
            path.add(current.getAnterior());
            current = current.getAnterior();
        }
        Collections.reverse(path);
        return path;
    }

    // retorna una lista de los pasos del enemigo o null si no se mueve o no encuentra camino
    public List<Node> ai(Map map){
        List<Node> open = null;
        List<Node> closed = null;

        Node startNode = new Node(getPosition());
        Node endNode = new Node(objectivePos);
        startNode.calculateHCost(objectivePos);
        startNode.calculateFCost();
        open.add(startNode);        // Beginning gam.sua.Node
        boolean flag = true;        // Ends loop

        if (map.isNear(getPosition(), 1, 2) != null){       // Is 1 position away
            return null;
        }

        while (flag){
            Node current = lowestFCost (open);

            if (current.getCoords() == objectivePos){   // The end is reached
                List<Node> path = finalPath(endNode);
                for (int i = 0; i < path.size(); i++){
                    if (i >= getSteps()){
                        path.remove(i);
                    }
                }
                return path;
            }

            open.remove(current);
            closed.add(current);

            for (Node neighbour:getNeighbours(current)){
                if (closed.contains(neighbour)){
                    continue;
                }

                int tentativeGCost = current.getgCost() + gCostAnterior(current,neighbour);
                if (tentativeGCost < neighbour.getgCost()){
                    neighbour.setAnterior(current);
                    neighbour.setgCost(tentativeGCost);
                    neighbour.calculateHCost(objectivePos);
                    neighbour.calculateFCost();

                    if (!open.contains(neighbour)){
                        open.add(neighbour);
                    }
                }
            }
        }
        return null;    // No path found
    }
}
