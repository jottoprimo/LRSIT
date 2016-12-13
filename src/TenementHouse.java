import java.util.Random;

/**
 * Created by Evgenij on 15.10.2016.
 */
public class TenementHouse extends House implements IBehaviour {

    public TenementHouse(int x, int y){
        super(x, y);
    }

    public TenementHouse(){
        super();
    }

    @Override
    public String getImagePath() {
        return "TenementHouse.png";
    }

    @Override
    public String getObjectName() {
        return "Капитальный дом";
    }

    @Override
    public void goTo(int toX, int toY, double v) {
        if (toX==-1 || toY==-1) return;
        double len = Math.sqrt(Math.pow(x-toX,2)+Math.pow(y-toY,2));
        if (len<=v) return;
        double dx = v*(toX-x)/len;
        double dy = v*(toY-y)/len;

        x+=dx;
        y+=dy;
    }
}
