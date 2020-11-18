package gam.sua;

import java.util.Arrays;

public class Character {
    private int[] position;
    private int[] oldPos;
    private int health;
    private int steps;
    private int id;

    /** Constructor
     * @param _position
     * @param _health
     * @param _steps
     * @param _id
     */
    Character(int[] _position, int _health, int _steps, int _id) {
        position = _position;
        health = _health;
        steps = _steps;
        id = _id;
        oldPos = new int[] {0,0};
    }

    /** Getters & Setters
     * @return
     */
    public int[] getPosition() {
        return position;
    }
    public int[] getOldPos() {
        return oldPos;
    }
    public int getHealth(){
        return health;
    }
    public int getSteps(){
        return steps;
    }
    public int getId() {return id;}

    public void setPosition(int[] position) {
        if (!Arrays.equals(this.position,position))
            this.oldPos = this.position;
        this.position = position;
    }
    public void setHealth(int health){
        this.health = health;
    }

    /** Adds and updates Health
     * @param health
     */
    public void addHealth(int health){
        this.health += health;
        if (this.health > 100)
            this.health = 100;
    }

    public void subtractHealth(int dmg) {this.health -= dmg; }

    public Boolean isDead() {
        return (health <= 0);
    }
}