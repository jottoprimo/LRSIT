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
import java.util.HashMap;

public class Application implements Runnable, ActionListener {
    private final String START_SIMULATION="start simulation";
    private final String STOP_SIMULATION="stop simulation";
    private final String SHOW_TIME="show time";
    private final String DONTSHOW_TIME="-show time";
    private final String SWITCH_SHOW_TIME="switch time";
    private final String SHOW_INFO="show info";
    private final String DONTSHOW_INFO="-show info";

    private boolean simulationIsStart = false;
    private boolean showTime = true;
    private boolean showInfo = true;


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

    private Timer timer;
    private JPanel world;
    private JFrame f;
    private JButton startSimulationButton, stopSimulationButton;
    private JLabel timeLabel;
    private ButtonGroup showTimeSimulationGroup;
    private JRadioButton showTimeSimulation, dontShowTimeSimulation;
    private JCheckBox showInformation;
    private JTextField tPrivateGenerationField
                      ,tTenementGenerationField;
    private JComboBox<Integer> pPrivateGeneration, pTenementGeneration;

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
        f = new JFrame ("Main");
        world = new JPanel();
        JPanel panelViewSettings = new JPanel();
        panelViewSettings.setLayout(new BoxLayout(panelViewSettings,BoxLayout.Y_AXIS));
        JPanel panelGenerationSettings = new JPanel();
        panelGenerationSettings.setLayout(new BoxLayout(panelGenerationSettings, BoxLayout.Y_AXIS));

        world.setMinimumSize(new Dimension(800,600));
        world.setPreferredSize(new Dimension(800,600));

        world.setLayout(null);
        timeLabel = new JLabel("Время: ");
        timeLabel.setLocation(0, 0);
        timeLabel.setSize(100,20);
        timeLabel.setBorder(new BevelBorder(1));
        world.add(timeLabel);

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
            world.add(imagePanel);
            f.repaint();
        });

        f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

        timer = new Timer(1000, new AbstractAction() {
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
                    world.removeAll();
                    f.repaint();
                }
                // Нажатие на E
                if (e.getKeyCode()==69) {
                    stopSimulation();
                }

                if (e.getKeyCode() == 84){
                    timeLabel.setVisible(!timeLabel.isVisible());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        world.setBorder(new BevelBorder(1));
        f.add(world, BorderLayout.CENTER);

        //ControlPanel created
        startSimulationButton = new JButton("Старт");
        startSimulationButton.setActionCommand(START_SIMULATION);
        startSimulationButton.addActionListener(this);
        stopSimulationButton = new JButton("Стоп");
        stopSimulationButton.setActionCommand(STOP_SIMULATION);
        stopSimulationButton.setEnabled(false);
        stopSimulationButton.addActionListener(this);
        showTimeSimulationGroup = new ButtonGroup();
        showTimeSimulation = new JRadioButton("Показывать время",true);
        showTimeSimulation.setActionCommand(SHOW_TIME);
        showTimeSimulation.addActionListener(this);
        dontShowTimeSimulation = new JRadioButton("Не показывать время");
        dontShowTimeSimulation.setActionCommand(DONTSHOW_TIME);
        dontShowTimeSimulation.addActionListener(this);
        showInformation = new JCheckBox("Выводить информацию",true);
        tTenementGenerationField = new JTextField(3);

        tPrivateGenerationField = new JTextField(3);
        tPrivateGenerationField.setPreferredSize(new Dimension(80,20));
        //tPrivateGenerationField.setMaximumSize(new Dimension(80,20));
        pPrivateGeneration = new JComboBox<>();
        pTenementGeneration = new JComboBox<>();
        showTimeSimulationGroup.add(showTimeSimulation);
        showTimeSimulationGroup.add(dontShowTimeSimulation);
        panelViewSettings.add(startSimulationButton);
        panelViewSettings.add(stopSimulationButton);
        panelViewSettings.add(showInformation);
        panelViewSettings.add(showTimeSimulation);
        panelViewSettings.add(dontShowTimeSimulation);
        Box privateBox = Box.createHorizontalBox();
        privateBox.add(new JLabel("Период генерации деревянного дома"));
        privateBox.add(tPrivateGenerationField);
        panelGenerationSettings.add(privateBox);
        panelGenerationSettings.add(tPrivateGenerationField);
        panelGenerationSettings.add(tTenementGenerationField);
        panelGenerationSettings.add(pPrivateGeneration);
        panelGenerationSettings.add(pTenementGeneration);

        f.add(panelViewSettings, BorderLayout.WEST);
        f.add(panelGenerationSettings, BorderLayout.SOUTH);

        //---МЕНЮ---
        //---Меню генерации---
        JMenuBar menuBar = new JMenuBar();
        JMenu generationMenu = new JMenu("Генерация");
        JMenuItem startItem = new JMenuItem("Старт");
        startItem.setActionCommand(START_SIMULATION);
        startItem.addActionListener(this);
        JMenuItem stopItem = new JMenuItem("Стоп");
        stopItem.setActionCommand(STOP_SIMULATION);
        stopItem.addActionListener(this);
        generationMenu.add(startItem);
        generationMenu.add(stopItem);
        menuBar.add(generationMenu);

        //---Меню настроек---
        JMenu settingsMenu = new JMenu("Настройки");
        JCheckBoxMenuItem showTimeItem = new JCheckBoxMenuItem("Отображать время",true);
        showTimeItem.setActionCommand(SWITCH_SHOW_TIME);
        showTimeItem.addActionListener(this);
        JCheckBoxMenuItem showInfoItem = new JCheckBoxMenuItem("Отображать информацию");
        settingsMenu.add(showTimeItem);
        settingsMenu.add(showInfoItem);
        menuBar.add(settingsMenu);

        f.setJMenuBar(menuBar);
        f.setSize(1000, 600);
        f.setVisible(true);
    }

    private void stopSimulation() {
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
        world.add(report);
        habitat.ClearList();
        stopSimulationButton.setEnabled(false);
        startSimulationButton.setEnabled(true);
        time = 0;
    }

    private void startSimulation(){
        timer.start();
        world.removeAll();
        startSimulationButton.setEnabled(false);
        stopSimulationButton.setEnabled(true);
        world.add(timeLabel);
        f.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()){
            case START_SIMULATION:
                startSimulation();
                break;
            case STOP_SIMULATION:
                stopSimulation();
                break;
            case SHOW_INFO:
                break;
            case DONTSHOW_INFO:
                break;
            case SHOW_TIME:
                timeLabel.setVisible(true);
                break;
            case DONTSHOW_TIME:
                timeLabel.setVisible(false);
                break;
            case SWITCH_SHOW_TIME:
                timeLabel.setVisible(!timeLabel.isVisible());
                break;
        }
    }
}
