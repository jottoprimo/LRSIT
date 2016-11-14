import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class Application implements Runnable {
    Habitat habitat;
    int time = 0;

    private HashMap<String, Image> images = new HashMap<>();

    //Для стилей
    final String HEAD_STYLE = "head";
    final String PARAM_STYLE = "param";
    final String VALUE_STYLE = "value";
    final String FONT_BASE = "Comic Sans MS";
    final String FONT_VALUE = "Times New Roman";
    Style head, param, value;

    private void setStyle(JTextPane textPane){
        head = textPane.addStyle(HEAD_STYLE, null);
        StyleConstants.setFontFamily(head, FONT_BASE);
        StyleConstants.setFontSize(head, 16);

        param = textPane.addStyle(PARAM_STYLE, head);
        StyleConstants.setFontSize(param, 14);

        value = textPane.addStyle(VALUE_STYLE, null);
        StyleConstants.setFontFamily(value, FONT_VALUE);
        StyleConstants.setFontSize(value, 14);
        StyleConstants.setForeground(value, new Color(236, 201, 57));
    }

    private void insertText(JTextPane textPane, String text, Style style){
        try {
            Document doc = textPane.getDocument();
            doc.insertString(doc.getLength(), text, style);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater (new Application());
    }

    public void run() {
        JFrame f = new JFrame ("Main");
        JPanel root = new JPanel(f.getContentPane().getLayout());
        JPanel panel = new JPanel();
        JPanel world = new JPanel();



        panel.setLocation(0,0);
        panel.setSize(800,600);
        panel.setLayout(null);
        f.add(panel);
        JLabel timeLabel = new JLabel("Время: ");
        timeLabel.setLocation(0, 0);
       // timeLabel.setMinimumSize(new Dimension(100,100));
        timeLabel.setSize(100,20);
        timeLabel.setBorder(new BevelBorder(1));
        panel.add(timeLabel);

        habitat = new Habitat(800, 600);
        habitat.setCreteHouseListener(house -> {

            ImagePanel imagePanel = new ImagePanel();
            imagePanel.setLayout(null);
            imagePanel.setSize(30,30);
            imagePanel.setLocation(house.getX(), house.getY());
            try {
                Image image = images.get(house.getImagePath());
                if (image==null) {
                    image = ImageIO.read(new File(house.getImagePath()));
                    images.put(house.getImagePath(), image);
                }
                imagePanel.setImage(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
            panel.add(imagePanel);
            f.repaint();
        });

        f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

        Timer timer = new Timer(1000, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                time += 1;
                timeLabel.setText("Время: " + time);
                habitat.Update(time);

            }
        });

        f.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                // Нажатие на B
                if (e.getKeyCode()==66) {
                    timer.start();
                    panel.removeAll();
                    panel.add(timeLabel);
                    f.repaint();
                }
                // Нажатие на E
                if (e.getKeyCode()==69) {
                    timer.stop();
                    JTextPane report = new JTextPane();
                    setStyle(report);
                    insertText(report, "Отчет симуляии\n", head);
                    HashMap<String, Integer> dataReport = habitat.getHousesReport();
                    report.setSize(600,100);
                    report.setEditable(false);
                    report.setBackground(new Color(227, 228, 254));
                    report.setLocation(100,100);
                    for (String key: dataReport.keySet()){
                        insertText(report, key+": ", param);
                        insertText(report, dataReport.get(key).toString()+"\n", value);
                    }
                    insertText(report, "Время: ", param);
                    insertText(report, String.valueOf(time), value);
                    panel.add(report);
                    habitat.ClearList();
                    time = 0;
                }

                if (e.getKeyCode() == 84){
                    timeLabel.setVisible(!timeLabel.isVisible());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        f.setSize(800, 600);
        /*ImagePanel imagePanel = new ImagePanel();
        imagePanel.setLayout(null);
        imagePanel.setSize(30,30);
        try {
            Image image = ImageIO.read(new File("privateHouse.png"));
            imagePanel.setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imagePanel.setLocation(10,10);
        f.add(imagePanel);*/
        // Показать окно
        panel.setBorder(new BevelBorder(1));
        f.setVisible(true);
    }
}
