package gam.sua;

import java.util.List;

public class Player extends Character{
    private String name;
    private String[] specialAb;

    private Boolean doubleDamage = false;
    private Boolean lessSound = false;
    private Boolean canEvade = false;
    private Boolean luck = false;
    private int isPoisoned = 0;

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
            specialAb = new String[]{"Double Damage","Less Sound","Initial Crossbow"};
            DMG = 5;
            weaponRange = 3;
        }
        if (name == "David"){
            luck = true;
            specialAb = new String[]{"Extra Movement","Luck","Initial Sword"};
            DMG = 5;
            weaponRange = 2;
        }
        if (name == "Amalia"){
            canEvade = true;
            specialAb = new String[]{"Extra Health","Can Evade","Initial Bow"};
            DMG = 5;
            weaponRange = 4;
        }
    }

    public String getName(){ return name;}

    public String[] getSpecialAb(){return specialAb;}

    public int getDMG(){
        int multi = (doubleDamage)? 2:1;
        return DMG*multi;
    }

    public void setDMG(int DMG){this.DMG = DMG;}

    public int getWeaponRange(){ return weaponRange; }

    public void setWeaponRange(int range){weaponRange = range;}

    public void setIsPoisoned(boolean bool){
        int multi = (bool)? 0:1;
        isPoisoned*=multi;
    }

    public Boolean getLuck() {return luck;}

    public boolean getDoubleDamage(){return doubleDamage;}
}
