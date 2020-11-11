package gam.sua;

public class Weapons extends Items{
    private final int damage;
    private final boolean sound;
    private final int range;

    Weapons(int i, String name, int _damage, boolean _sound, int _range){
        super(i, name);
        damage = _damage;
        sound = _sound;
        range = _range;
    }

    public int getDamage() {return damage;}

    public boolean isSound() {return sound;}

    public int getRange() {return range;}
}
