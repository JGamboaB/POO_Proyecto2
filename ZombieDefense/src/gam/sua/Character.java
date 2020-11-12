package gam.sua;

public class Character {
    private int[] position;
    private int[] oldPos;
    private int health;
    private int steps;
    private int id;

    Character(int[] _position, int _health, int _steps, int _id) {
        position = _position;
        health = _health;
        steps = _steps;
        id = _id;
    }

    public int[] getPosition() {
        return position;
    }

    public int[] getOldPos() {
        return oldPos;
    }

    public void setPosition(int[] position) {
        if (this.position != position)
            this.oldPos = this.position;
        this.position = position;
    }

    public int getHealth(){
        return health;
    }

    public void setHealth(int health){
        this.health = health;
    }

    public void addHealth(int health){
        this.health += health;
        if (this.health > 100)
            this.health = 100;
    }

    public void subtractHealth(int dmg) {this.health -= dmg; }

    public int getSteps(){
        return steps;
    }

    public Boolean isDead() {
        return (health <= 0);
    }

}