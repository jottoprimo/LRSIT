import java.util.Random;

/**
 * Created by Evgenij on 15.10.2016.
 */
class PrivateHouse extends House implements IBehaviour {

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
}
