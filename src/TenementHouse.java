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
}
