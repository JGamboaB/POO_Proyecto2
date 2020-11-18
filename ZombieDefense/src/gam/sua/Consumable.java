package gam.sua;

public class Consumable extends Items{
    private final int health;
    private final boolean poison;

    /**Constructor
     * @param i int id
     * @param name String
     * @param h int health
     * @param p boolean poison
     */
    Consumable(int i, String name, int h, boolean p){
        super(i, name);
        health = h;
        poison = p;
    }

    /**Returns the health attribute
     * @return health */
    public int getHealth() {return health;}


    /**Returns the poison attribute
     * @return poison */
    public boolean curesPoison() {return poison;}
}
