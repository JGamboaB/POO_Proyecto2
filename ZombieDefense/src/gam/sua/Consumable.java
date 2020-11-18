package gam.sua;

public class Consumable extends Items{
    private int health = 0;
    private boolean poison = false;

    /** Constructor
     * @param i
     * @param name
     * @param h
     * @param p
     */
    Consumable(int i, String name, int h, boolean p){
        super(i, name);
        health = h;
        poison = p;
    }

    /** Getters
     * @return
     */
    public int getHealth() {return health;}

    public boolean curesPoison() {return poison;}
}
