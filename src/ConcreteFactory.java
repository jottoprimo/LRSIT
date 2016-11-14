import java.util.Random;

/**
 * Created by Evgenij on 15.10.2016.
 */
public class ConcreteFactory implements AbstractFactory{
    int height, width;
    Random random;

    public ConcreteFactory(int width, int height){
        this.height = height;
        this.width = width;
        random = new Random(System.currentTimeMillis());
    }

    @Override
    public TenementHouse CreateTenementHouse() {
        return new TenementHouse(Math.round(random.nextFloat()*width), Math.round(random.nextFloat()*height));
    }

    @Override
    public PrivateHouse CreatePrivateHouse() {
        return new PrivateHouse(Math.round(random.nextFloat()*width), Math.round(random.nextFloat()*height));
    }
}
