package gam.sua;

public class Weapons extends Items{
    private final int damage;
    private final boolean sound;
    private final int range;

    /** Constructor
     * @param i id
     * @param name name
     * @param _damage DMG
     * @param _sound if it produces sound
     * @param _range the range of the weapon
     */
    Weapons(int i, String name, int _damage, boolean _sound, int _range){
        super(i, name);
        damage = _damage;
        sound = _sound;
        range = _range;
    }

    /** Returns the damage attribute
     * @return damage */
    public int getDamage() {return damage;}

    /** Returns the sound attribute
     * @return sound */
    public boolean getSound() {return sound;}

    /** Returns the range attribute
     * @return range */
    public int getRange() {return range;}
}
