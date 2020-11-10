package gam.sua;

public class Player extends Character{
    private String name;
    private String[] specialAb;

    private Boolean doubleDamage;
    private Boolean lessSound;
    private Boolean canEvade;
    private Boolean luck;
    private int isPoisoned;

    //changes depending on the weapon equipped
    private int DMG;
    private int weaponRange;

    Player(int[] _position, int _health, int _range, int _id, String _name){
        super(_position,_health,_range,_id);
        name = _name;
        isPoisoned = 0;
        // Abilities
        if (name == "Gringo"){
            doubleDamage = true;
            lessSound = true;
            specialAb = new String[]{"Double Damage","Less Sound","Crossbow"};
            DMG = 10;
            weaponRange = 4;
        }
        if (name == "David"){
            luck = true;
            specialAb = new String[]{"Extra Movement","Luck","Mighty Sword"};
            DMG = 5;
            weaponRange = 2;
        }
        if (name == "Amalia"){
            canEvade = true;
            specialAb = new String[]{"Extra Health","Can Evade","Long Bow"};
            DMG = 5;
            weaponRange = 4;
        }
    }

    public String getName(){ return name;}

    public String[] getSpecialAb(){return specialAb;}

    public int getDMG(){ return DMG; }

    public int getWeaponRange(){ return weaponRange; }
}
