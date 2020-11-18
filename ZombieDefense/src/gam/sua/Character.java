package gam.sua;

import java.util.Arrays;

public class Character {
    private int[] position;
    private int[] oldPos;
    private int health;
    private final int steps;
    private final int id;

    /**Constructor
     * @param _position int[]
     * @param _health int
     * @param _steps int
     * @param _id int
     */
    Character(int[] _position, int _health, int _steps, int _id) {
        position = _position;
        health = _health;
        steps = _steps;
        id = _id;
        oldPos = new int[] {0,0};
    }


    /**Returns the position attribute
     * @return position */
    public int[] getPosition() {
        return position;
    }


    /**Returns the oldPos attribute
     * @return oldPos */
    public int[] getOldPos() {
        return oldPos;
    }


    /**Sets the position attribute
     * @param position int[] */
    public void setPosition(int[] position) {
        if (!Arrays.equals(this.position,position))
            this.oldPos = this.position;
        this.position = position;
    }


    /**Returns the health attribute
     * @return health int */
    public int getHealth(){
        return health;
    }


    /**Sets the health attribute
     * @param health int */
    public void setHealth(int health){
        this.health = health;
    }


    /**Adds health to the player
     * @param health int */
    public void addHealth(int health){
        this.health += health;
        if (this.health > 100)
            this.health = 100;
    }


    /**Subtracts health to the player
     * @param dmg int */
    public void subtractHealth(int dmg) {this.health -= dmg; }


    /**Returns the steps attribute
     * @return steps */
    public int getSteps(){
        return steps;
    }


    /**Returns true if the health is 0 or less
     * @return boolean */
    public Boolean isDead() {
        return (health <= 0);
    }


    /**Returns the id attribute
     * @return id */
    public int getId() {return id;}
}