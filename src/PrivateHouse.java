import java.util.Random;

/**
 * Created by Evgenij on 15.10.2016.
 */
class PrivateHouse extends House {

    public PrivateHouse(int x, int y){
        super(x,y);
    }

    public PrivateHouse(){
        super();
    }


    @Override
    public String getImagePath() {
        return "privateHouse.png";
    }

    @Override
    public String getObjectName() {
        return "Частный дом";
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
