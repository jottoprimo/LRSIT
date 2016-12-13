import java.io.Serializable;

/**
 * Created by Evgenij on 14.12.2016.
 */
public class Properties implements Serializable {
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


    private float pTenement, pPrivate;
    private int tTenemnt, tPrivate;

    public Properties(){
        tTenemnt = 3;
        tPrivate = 1;
        pTenement = 0.8f;
        pPrivate = 0.6f;
    }
}
