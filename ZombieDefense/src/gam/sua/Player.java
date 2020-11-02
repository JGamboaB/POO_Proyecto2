package gam.sua;

public class Player extends Character{
    private Boolean doubleDamage;
    private Boolean lessSound;
    private Boolean canEvade;
    private Boolean luck;
    private int isPoisoned;

    // String hab = abilities

    Player(int[] _position, int _health, int _range, int _id, String name){
        super(_position,_health,_range,_id);
        isPoisoned = 0;
        // Abilities
        if (name == "Gringo"){
            doubleDamage = true;
            lessSound = true;
        }
        if (name == "David"){
            luck = true;
        }
        if (name == "Amalia"){
            canEvade = true;
        }
    }
}
