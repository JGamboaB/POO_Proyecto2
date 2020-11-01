package gam.sua;

public class Character {
    private int[] position;
    private int health;
    private int movementRange;
    private int id;

    Character(int[] p, int h, int m, int i) {
        position = p;
        health = h;
        movementRange = m;
        id = i;
    }

    public Boolean isDead(Character character) {
        return (character.health == 0);
    }
}