package gam.sua;

public class Player extends Character{
    private Boolean doubleDamage;
    private Boolean lessSound;
    private Boolean moreMov;
    private Boolean moreHealth;
    private Boolean canEvade;
    private Boolean luck;
    private int isPoisoned;

    Player(int[] p, int h, int m,int i){
        super(p, h, m, i);
    }
}
