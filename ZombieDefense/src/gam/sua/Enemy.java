package gam.sua;

public class Enemy extends Character{
    private int damage;
    private Boolean ghost;
    private Boolean poison;
    private Boolean revive;
    private int visionRange;
    private int[] objectivePos;
    private int[] lastPositions;

    Enemy(int[] _position, int _health, int _range, int _id, String name){
        super(_position, _health, _range, _id);
        if (name == ""){
        }
    }

    public void dropItem(){

    }

    public void attack(){

    }

    public int[] getObjectivePos(){
        int[] newObjective = objectivePos;

        return newObjective;
    }

    public int[] firstEmpty(Map map,int r,int c){   // Izquierda arriba
        boolean flag = true;
        r -= getRange();
        c -= getRange();
        while (flag){
            if (r > getPosition()[0] + getRange() || r > 25){   // Si ya no esta en el range o en el mapa
                return getPosition();
            }
            if (map.valorPos(r,c) == 0 && c < 46){  // Si encuentra un espacio vacio en el mapa, lo retorna
                break;
            }
            if (c == getPosition()[1] + getRange()){    // Si c se pasa del range, siguiente row
                c = getPosition()[1] - getRange();
                r++;
            }else{
                c++;
            }
        }
        return new int[] {r,c};
    }

    public int[] look4Pos(Map map, int r, int c, int code){
        switch (code){
            case 1:

            case 2:
                r -= getRange();
                while (r != getPosition()[0]){
                    if (r == objectivePos[0]){break;}
                    if (r < 0 || map.valorPos(r,c) != 0){
                        r++;
                    }
                }
        }
        return new int[] {r,c};
    }

    /*  1 2 3
        4 5 6
        7 8 9

     5 = posicion actual  */

    public int[] ai(Map map){
        int[] position = getPosition();
        int r = position[0],c = position[1];
        int[] coords = {r,c};

        if (position[0] > objectivePos[0] && position[1] > objectivePos[1]){     // 1
            coords = look4Pos(map, r, c, 1);
        }
        if (position[0] > objectivePos[0] && position[1] == objectivePos[1]){    // 2

        }
        if (position[0] > objectivePos[0] && position[1] < objectivePos[1]){     // 3

        }
        if (position[0] == objectivePos[0] && position[1] > objectivePos[1]){    // 4

        }
        if (position[0] == objectivePos[0] && position[1] == objectivePos[1]){}  // 5

        if (position[0] == objectivePos[0] && position[1] < objectivePos[1]){    // 6

        }
        if (position[0] < objectivePos[0] && position[1] > objectivePos[1]){     // 7

        }
        if (position[0] < objectivePos[0] && position[1] == objectivePos[1]){    // 8

        }
        if (position[0] == objectivePos[0] && position[1] > objectivePos[1]){    // 9

        }

        if (coords == position) {
            return firstEmpty(map, r, c);
        } return coords;
    }
}
