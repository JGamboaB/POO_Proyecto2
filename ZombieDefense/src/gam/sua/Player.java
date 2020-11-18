package gam.sua;

public class Player extends Character{
    private final String name;
    private String[] specialAb;

    private Boolean doubleDamage = false;
    private Boolean lessSound = false;
    private Boolean canEvade = false;
    private Boolean luck = false;
    private int isPoisoned;

    //Changes depending on the weapon equipped
    private int DMG;
    private int weaponRange;
    private boolean sound;


    /** Constructor
     * @param _position int[] of the position in the matrix
     * @param _health health
     * @param _range range
     * @param _id id
     * @param _name name
     */
    Player(int[] _position, int _health, int _range, int _id, String _name){
        super(_position,_health,_range,_id);
        name = _name;
        isPoisoned = 0;
        sound = false;

        // Abilities
        switch (name) {
            case "Gringo" -> {
                doubleDamage = true;
                lessSound = true;
                specialAb = new String[]{"Double Damage", "Less Sound", "Initial Crossbow"};
                DMG = 5;
                weaponRange = 3;
            }
            case "David" -> {
                luck = true;
                specialAb = new String[]{"Extra Movement", "Luck", "Initial Sword"};
                DMG = 5;
                weaponRange = 2;
            }
            case "Amalia" -> {
                canEvade = true;
                specialAb = new String[]{"Extra Health", "Can Evade", "Initial Bow"};
                DMG = 5;
                weaponRange = 4;
                sound = true;
            }
        }
    }


    /** Returns the name attribute
     * @return name */
    public String getName(){ return name;}


    /** Returns the specialAb attribute
     * @return specialAb */
    public String[] getSpecialAb(){return specialAb;}


    /** Returns the isPoisoned attribute
     * @return isPoisoned */
    public int getIsPoisoned() { return isPoisoned; }


    /** Returns the DMG attribute
     * @return DMG */
    public int getDMG(){
        int multi = (doubleDamage)? 2:1;
        return DMG*multi;
    }

    /** Returns the weaponRange attribute
     * @return weaponRange */
    public int getWeaponRange(){ return weaponRange; }

    /** Returns the luck attribute
     * @return luck */
    public Boolean getLuck() {return luck;}

    /** Returns the doubleDamage attribute
     * @return doubleDamage */
    public boolean getDoubleDamage(){return doubleDamage;}

    /** Returns the sound attribute
     * @return sound */
    public boolean getSound(){return sound;}

    /** Returns the lessSound attribute
     * @return lessSound */
    public boolean getLessSound(){return lessSound;}


    /** Set the DMG attribute to the player
     * @param DMG int */
    public void setDMG(int DMG){this.DMG = DMG;}

    /** Sets the weaponRange attribute
     * @param range int */
    public void setWeaponRange(int range){weaponRange = range;}


    /** Sets the isPoisoned attribute
     * @param bool isPoisoned */
    public void setIsPoisoned(boolean bool){
        int multi = (bool)? 0:1;
        isPoisoned*=multi;
    }

    /** Sets the sound attribute
     * @param sound boolean */
    public void setSound(boolean sound){this.sound = sound;}


    /** Poison damage, decreases the players health. */
    public void poisonDamage(){
        if (this.isPoisoned != 0){  // starts in 3
            isPoisoned--;
            this.subtractHealth(10);   // final damage
        }
    }


    /** Sets the isPoison attribute
     * @param _isPoisoned int */
    public void newIsPoisoned(int _isPoisoned) {this.isPoisoned = _isPoisoned;}

}
