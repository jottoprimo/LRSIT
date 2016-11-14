import javax.swing.*;
import java.awt.*;

/**
 * Created by Evgenij on 15.10.2016.
 */
public class ImagePanel extends JPanel {
    private Image image;
    public ImagePanel(){
        super();
    }
    public ImagePanel(Image image){
        super();
        this.image = image;
    }
    public Image getImage() {
        return image;
    }
    public void setImage(Image image) {
        this.image = image;
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(image != null){
            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        }
    }
}
