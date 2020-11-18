package gam.sua;

public class Items {
    private final int id;
    private final String name;

    /** Constructor
     * @param i id
     * @param n name
     */
    Items(int i, String n){
        id = i;
        name = n;
    }

    /** Returns the id attribute
     * @return id */
    public int getId() {return id;}

    /** Returns the name attribute
     * @return name */
    public String getName() {return name;}
}
