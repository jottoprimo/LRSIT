import java.awt.*;
import java.util.*;

/**
 * Created by Evgenij on 15.10.2016.
 */
public class Habitat {
    private ArrayList<House> houses;
    private ICreateHouseListener listener;
    private float pTenement, pPrivate;
    private int tTenemnt, tPrivate;
    private int width, height;
    private ConcreteFactory factory;
    private Random random;

    public Habitat(int width, int height){
        houses = new ArrayList<>();
        this.width = width;
        this.height = height;
        factory = new ConcreteFactory(width, height);
        random = new Random(System.currentTimeMillis());
        tTenemnt = 3;
        tPrivate = 1;
        pTenement = 0.8f;
        pPrivate = 0.6f;
    }

    public float getpTenement() {
        return pTenement;
    }

    public void setpTenement(float pTenement) {
        this.pTenement = pTenement;
    }

    public float getpPrivate() {
        return pPrivate;
    }

    public void setpPrivate(float pPrivate) {
        this.pPrivate = pPrivate;
    }

    public int gettTenemnt() {
        return tTenemnt;
    }

    public void settTenemnt(int tTenemnt) {
        this.tTenemnt = tTenemnt;
    }

    public int gettPrivate() {
        return tPrivate;
    }

    public void settPrivate(int tPrivate) {
        this.tPrivate = tPrivate;
    }

    public void Update(int time){
        House house;
        if (time % tTenemnt == 0) {
            if (random.nextFloat()<pTenement) {
                house = factory.CreateTenementHouse();
                houses.add(house);
                listener.onCreateHouse(house);
            }
        }
        if (time % tPrivate == 0) {
            if (random.nextFloat()<pPrivate) {
                house = factory.CreatePrivateHouse();
                houses.add(house);
                listener.onCreateHouse(house);
            }
        }
    }

    public void ClearList(){
        houses.clear();
    }

    public void setCreteHouseListener(ICreateHouseListener listener){
        this.listener = listener;
    }

    public HashMap<String, Integer> getHousesReport(){
        HashMap <String, Integer> report = new HashMap<>();
        for (House house: houses) {
            String houseType = house.getObjectName();
            if (!report.containsKey(houseType)){
                report.put(house.getObjectName(), 1);
            }
            else {
                Integer count = report.get(houseType);
                report.replace(houseType, count+1);
            }
        }
        return report;
    }


}
