package Cards;

import java.io.Serializable;

public abstract class Card implements Serializable{
    protected int id;
    protected String name;
    public int posX;
    public int posY;

    public Card(int id, String name, int x, int y) {
        this.id = id;
        this.name = name;
        this.posX = x;
        this.posY = y;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

}
