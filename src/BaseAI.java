import javafx.util.Pair;

import java.io.*;
import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Evgenij on 12.12.2016.
 */
abstract public class BaseAI extends Thread {
    Thread parentThread;
    PipedReader inPipe;
    PipedWriter outPipe;
    boolean interrupt = false;
    boolean stop = false;

    public void stoped(boolean stop){
        this.stop = stop;
    }

    public boolean isStop() {
        return stop;
    }

    public double getV() {
        return v;
    }

    public void setV(double v) {
        this.v = v;
    }

    double v;
    int height;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    int width;

    public BaseAI(){
        v = 0;
        height = 0;
        width = 0;
        parentThread = Thread.currentThread();
    }

    public BaseAI(int width, int height, double v){
        this();
        this.height = height;
        this.width = width;

        this.v = v;
    }

    protected abstract void step();

    public void setPipe(PipedReader pipe) throws IOException {
        this.inPipe = pipe;
        outPipe = new PipedWriter(pipe);

    }

    public void setInterrupt(boolean interrupt){
        this.interrupt = interrupt;
    }
}
