import javafx.util.Pair;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Evgenij on 13.12.2016.
 */
public class TenementHouseAI extends BaseAI {
    public TenementHouseAI(){
        super();
    }

    public TenementHouseAI(int width, int height, double v){
        super(width, height, v);
        Random random = new Random(System.currentTimeMillis());
    }

    @Override
    protected void step() {
        synchronized (Singleton.getInstance()) {
            ArrayList<House> houses = Singleton.getInstance().getHouses();
            Random random = new Random(System.currentTimeMillis());
            for (int i = 0; i < houses.size(); i++) {
                House house = houses.get(i);
                if (house instanceof TenementHouse) {
                    if (house.getToX() == -1 && house.getToY() == -1) {
                        if (house.getX() < width / 2 && house.getY() < height / 2) {
                            house.setToX(house.getX());
                            house.setToY(house.getY());
                        }
                        house.setToX(random.nextInt(width / 2));
                        house.setToY(random.nextInt(height / 2));
                    }
                    houses.get(i).goTo(house.getToX(), house.getToY(), v);
                }
            }
        }
    }

    @Override
    public void run(){
        ByteBuffer buffer = ByteBuffer.allocate(64);
        buffer.clear();
        while (!interrupt){
            try {
                sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (inPipe==null) continue;
            synchronized (inPipe){
                step();
                try {
                    outPipe.write("1");
                    //inPipe.notifyAll();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (stop) {
                        inPipe.wait();
                        stop = false;
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        try {
            outPipe.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("END TENEMENT");
    }
}
