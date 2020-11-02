package gam.sua;

public class Enemy extends Character{
    private int damage;
    private Boolean ghost;
    private Boolean poison;
    private Boolean revive;
    private int visionRange;
    private int[] objectivePos;

    Enemy(int[] _position, int _health, int _range, int _id){
        super(_position, _health, _range, _id);
    }

    public void dropItem(){

    }

    public void move(Enemy enemy, int[] newPos){
        
    }

    public void attack(){

    }

    public void ai(){

    }
}
