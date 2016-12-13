import java.io.Serializable;

/**
 * Created by Evgenij on 15.10.2016.
 */
public abstract class House implements IBehaviour, Serializable{
    protected int x, y;

    protected int toY;
    public House(int x, int y){
        this.x = x;
        this.y = y;
        toX=-1;
        toY=-1;
    }
    public House(){
        this(0,0);
    }

    public abstract String getImagePath();
    public abstract String getObjectName();
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }

    public int getToX() {
        return toX;
    }

    public void setToX(int toX) {
        this.toX = toX;
    }

    protected int toX;

    public int getToY() {
        return toY;
    }

    public void setToY(int toY) {
        this.toY = toY;
    }

}
