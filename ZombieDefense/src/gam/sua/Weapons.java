package gam.sua;

public class Weapons extends Items{
    private int damage;
    private int sound;
    private int range;

    Weapons(int i, int _damage, int _sound, int _range){
        super(i);
        damage = _damage;
        sound = _sound;
        range = _range;
    }
}
