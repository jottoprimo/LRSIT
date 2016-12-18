import javax.swing.*;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedReader;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.util.*;

/**
 * Created by Evgenij on 15.10.2016.
 */
public class Habitat implements Serializable {
    private Properties properties;
    private IHabitatListener listener;
    private ConcreteFactory factory;
    private Random random;
    private PrivateHouseAI privateAI;
    private TenementHouseAI tenementAI;
    private PipedReader inPipe;
    private PipedReader inPipeTenement;
    private boolean privateSleep;
    private boolean tenementSleep;

    private int width, height;

    public Habitat(){
        random = new Random(System.currentTimeMillis());
        properties = new Properties();
    }

    public Habitat(int width, int height) throws IOException {
        this();
        this.width = width;
        this.height = height;
        factory = new ConcreteFactory(width, height);


        inPipe = null;
        privateAI = new PrivateHouseAI(width, height, 5);
        tenementAI = new TenementHouseAI(width, height, 5);
        privateSleep = false;
        tenementSleep = false;
    }

    public float getpTenement() {
        return properties.getpTenement();
    }

    public void setpTenement(float pTenement) {
        properties.setpTenement(pTenement);
    }

    public float getpPrivate() {
        return properties.getpPrivate();
    }

    public void setpPrivate(float pPrivate) {
        properties.setpPrivate(pPrivate);
    }

    public int gettTenemnt() {
        return properties.gettTenemnt();
    }

    public void settTenemnt(int tTenemnt) {
         properties.settTenemnt(tTenemnt);
    }

    public int gettPrivate() {
        return properties.gettPrivate();
    }

    public void settPrivate(int tPrivate) {
        properties.settPrivate(tPrivate);
    }

    public void Update(int time){
        House house;

        synchronized (Singleton.getInstance()) {
            ArrayList<House> houses = Singleton.getInstance().getHouses();
            if (time % properties.gettTenemnt()== 0) {
                if (random.nextFloat() < properties.getpTenement()) {
                    house = factory.CreateTenementHouse();
                    houses.add(house);

                    listener.onCreateHouse(house);
                }
            }
            if (time % properties.gettPrivate() == 0) {
                if (random.nextFloat() < properties.getpPrivate()) {
                    house = factory.CreatePrivateHouse();
                    houses.add(house);

                    listener.onCreateHouse(house);
                }
            }
        }
    }

    public void ClearList(){
        Singleton.getInstance().getHouses().clear();
    }

    public void setCreteHouseListener(IHabitatListener listener){
        this.listener = listener;
    }

    public HashMap<String, Integer> getHousesReport(){
        HashMap <String, Integer> report = new HashMap<>();
        ArrayList<House> houses = Singleton.getInstance().getHouses();
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

    public void start(){
        try {
            inPipe = new PipedReader();
            inPipeTenement = new PipedReader();
            privateAI.setPipe(inPipe);
            tenementAI.setPipe(inPipeTenement);
            privateAI.start();
            tenementAI.start();
            int pipeCmd =0;
            char[] buffer = new char[1];
           // System.out.println("---MAIN: "+pipeCmd);
            while (pipeCmd != -1) {
                Thread.sleep(30);
                if (!privateSleep)
                {
                    synchronized (inPipe){
                        try {
                            inPipe.read(buffer);
                            pipeCmd = Integer.parseInt(String.valueOf(buffer[0]));
                            if (pipeCmd == 1 && listener != null) {
                                listener.onRedraw();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (!tenementSleep) {
                    synchronized (inPipeTenement) {
                        try {
                            inPipeTenement.read(buffer);
                            pipeCmd = Integer.parseInt(String.valueOf(buffer[0]));
                            if (pipeCmd == 1 && listener != null) {
                                listener.onRedraw();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setTenementPriority(int priority) {
        if (tenementAI==null) return;
        tenementAI.setPriority(priority);
    }

    public void setPrivatePriority(int priority) {
        if (privateAI==null) return;
        privateAI.setPriority(priority);
    }

    public void wakeUpPrivate() {

        synchronized (inPipe) {
            inPipe.notifyAll();
            privateSleep = false;
        }
    }

    public void sleepPrivate() {
        synchronized (inPipe) {
            privateSleep = true;
            privateAI.stoped(true);
        }
    }

    public void wakeUpTenement() {
        synchronized (inPipeTenement) {
            inPipeTenement.notifyAll();
            tenementSleep = false;
        }
    }

    public void sleepTenement() {
        synchronized (inPipeTenement) {
            tenementSleep = true;
            tenementAI.stoped(true);
        }
    }

    public Properties getProperties(){
        return properties;
    }

    public void setProperties(Properties properties){
        this.properties = properties;
    }

    public void interrupt(){
        synchronized (inPipe){
            inPipe.notifyAll();
        }
        synchronized (inPipeTenement){
            inPipeTenement.notifyAll();
        }
        privateAI.setInterrupt(true);
        tenementAI.setInterrupt(true);
    }
}
