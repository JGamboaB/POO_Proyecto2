package gam.sua;

public class Character {
    private int[] position;
    private int health;
    private int range;
    private int id;

    Character(int[] _position, int _health, int _range, int _id) {
        position = _position;
        health = _health;
        range = _range;
        id = _id;
    }

    public Boolean isDead(Character character) {
        return (character.health == 0);
    }
}