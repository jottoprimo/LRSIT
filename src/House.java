/**
 * Created by Evgenij on 15.10.2016.
 */
abstract class House {
    protected int x, y;
    public House(int x, int y){
        this.x = x;
        this.y = y;
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
}
