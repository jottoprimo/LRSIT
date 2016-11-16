import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class Application implements Runnable, ActionListener {
    private final String START_SIMULATION="start simulation";
    private final String STOP_SIMULATION="stop simulation";
    private final String SHOW_TIME="show time";
    private final String DONTSHOW_TIME="-show time";
    private final String SWITCH_SHOW_TIME="switch time";
    private final String SWITCH_SHOW_INFO="switch info";
    private final String DONTSHOW_INFO="-show info";

    private boolean simulationIsStart = false;
    private boolean showTime = true;
    private boolean showInfo = true;

    private HashMap<String, Float> percentToFloat;


    final Color unpressedBtnColor = new Color(0xEEEEEE);
    final Color pressedBtnColor = new Color(0xFFFFFF);

    private Color color;

    Habitat habitat;
    int time = 0;

    private HashMap<String, Image> images = new HashMap<>();

    private Timer timer;
    private JPanel world;
    private JFrame f;
    private JButton startSimulationButton, stopSimulationButton;
    private JLabel timeLabel;
    private ButtonGroup showTimeSimulationGroup;
    private JRadioButton showTimeSimulation, dontShowTimeSimulation;
    private JCheckBox showInformationCheckBox;
    private JTextField tPrivateGenerationField
                      ,tTenementGenerationField;
    private JComboBox pPrivateGeneration;
    private JList pTenementGeneration;
    private JMenuBar menuBar;
    private JMenu generationMenu;
    private JCheckBoxMenuItem showInfoItem;
    private JMenuItem startItem;
    private JCheckBoxMenuItem showTimeItem;
    private JMenuItem stopItem;
    private JMenu settingsMenu;
    private JToolBar toolBar;
    private JButton startToolBarBtn;
    private JButton stopToolBarBtn;
    private JButton showTimeToolBarBtn;
    private JButton showInfoToolBarBtn;

    public static void main(String[] args) {

        SwingUtilities.invokeLater (new Application());
    }

    public void run() {
        f = new JFrame ("Main");
        world = new JPanel();
        JPanel panelViewSettings = new JPanel();
        panelViewSettings.setLayout(new BoxLayout(panelViewSettings,BoxLayout.Y_AXIS));
        JPanel panelGenerationSettings = new JPanel();
        panelGenerationSettings.setLayout(new FlowLayout(FlowLayout.LEFT));

        world.setMinimumSize(new Dimension(800,400));
        world.setPreferredSize(new Dimension(800,400));

        world.setLayout(null);
        timeLabel = new JLabel("Время: ");
        timeLabel.setLocation(0, 0);
        timeLabel.setSize(100,20);
        timeLabel.setBorder(new BevelBorder(1));
        world.add(timeLabel);

        habitat = new Habitat(800, 400);
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
            Singleton.getInstance().getImages().add(imagePanel);
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
                if (e.getKeyCode() == 66) {
                    startSimulation();
                }
                // Нажатие на E
                if (e.getKeyCode() == 69) {
                    stopSimulation();
                }
                // Нажатие на T
                if (e.getKeyCode() == 84){
                    setShowTime(!timeLabel.isVisible());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        world.setBorder(new BevelBorder(1));
        f.add(world, BorderLayout.CENTER);

        //---Панель управления отображением---
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
        showInformationCheckBox = new JCheckBox("Выводить информацию",showInfo);
        showInformationCheckBox.addItemListener(e -> {
            setShowInformation(((JCheckBox) e.getSource()).isSelected());
        });
        showTimeSimulationGroup.add(showTimeSimulation);
        showTimeSimulationGroup.add(dontShowTimeSimulation);
        panelViewSettings.add(startSimulationButton);
        panelViewSettings.add(stopSimulationButton);
        panelViewSettings.add(showInformationCheckBox);
        panelViewSettings.add(showTimeSimulation);
        panelViewSettings.add(dontShowTimeSimulation);

        //---Панель управления симуляцией---
        percentToFloat = new HashMap<>();
        for (int i=0;i<11;i++){
            percentToFloat.put(i*10+"%",i/10.f);
        }

        tTenementGenerationField = new JTextField("3");
        tTenementGenerationField.addActionListener(e -> {
            JTextField field = (JTextField)e.getSource();
            String text = field.getText();
            int val=-1;
            try {
                val = Integer.parseInt(text);
            } catch (NumberFormatException ex){
                field.setText(Integer.toString(habitat.gettTenemnt()));
                JOptionPane.showMessageDialog(f, "Введено не корректное значение");
                return;
            }
            if (val<=0){
                field.setText(Integer.toString(habitat.gettTenemnt()));
                JOptionPane.showMessageDialog(f, "Введено не корректное значение");
                return;
            }
            habitat.settTenemnt(val);
        });
        tPrivateGenerationField = new JTextField("1");
        tPrivateGenerationField.addActionListener(e -> {
            JTextField field = (JTextField)e.getSource();
            String text = field.getText();
            int val=-1;
            try {
                val = Integer.parseInt(text);
            } catch (NumberFormatException ex){
                field.setText(Integer.toString(habitat.gettPrivate()));
                JOptionPane.showMessageDialog(f, "Введено не корректное значение");
                return;
            }
            if (val<=0){
                field.setText(Integer.toString(habitat.gettPrivate()));
                JOptionPane.showMessageDialog(f, "Введено не корректное значение");
                return;
            }
            habitat.settPrivate(val);
        });
        tPrivateGenerationField.setPreferredSize(new Dimension(80,20));
        Vector<String> percents = new Vector<>(11);
        for (int i=0;i<11;i++){
            percents.add(i*10+"%");
        }
        pPrivateGeneration = new JComboBox<>(percents);
        pPrivateGeneration.setSelectedIndex(6);
        pPrivateGeneration.addItemListener(e -> {
            String value = (String)((JComboBox)e.getSource()).getSelectedItem();
            habitat.setpPrivate(percentToFloat.get(value));
        });
        pTenementGeneration = new JList<>(percents);
        pTenementGeneration.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pTenementGeneration.setSelectedIndex(8);
        pTenementGeneration.addListSelectionListener(e -> {
            String value = (String)((JList)e.getSource()).getSelectedValue();
            habitat.setpTenement(percentToFloat.get(value));
        });

        Box privateBox = Box.createVerticalBox();
        privateBox.add(new JLabel("Период генерации деревянного дома"));
        privateBox.add(tPrivateGenerationField);
        panelGenerationSettings.add(privateBox);

        Box tenementBox = Box.createVerticalBox();
        tenementBox.add(new JLabel("Период генерации капитального дома"));
        tenementBox.add(tTenementGenerationField);
        panelGenerationSettings.add(tenementBox);

        Box pPrivateBox = Box.createVerticalBox();
        pPrivateBox.add(new JLabel("Вероятность генерации деревянного дома"));
        pPrivateBox.add(pPrivateGeneration);
        panelGenerationSettings.add(pPrivateBox);

        Box pTenementBox = Box.createVerticalBox();
        pTenementBox.add(new JLabel("Вероятность генерации капитального дома"));
        pTenementBox.add(pTenementGeneration);
        panelGenerationSettings.add(pTenementBox);


        f.add(panelViewSettings, BorderLayout.WEST);
        f.add(panelGenerationSettings, BorderLayout.SOUTH);

        //---МЕНЮ---
            //---Меню генерации---
            menuBar = new JMenuBar();
            generationMenu = new JMenu("Генерация");
            startItem = new JMenuItem("Старт");
            startItem.setActionCommand(START_SIMULATION);
            startItem.addActionListener(this);
            stopItem = new JMenuItem("Стоп");
            stopItem.setActionCommand(STOP_SIMULATION);
            stopItem.addActionListener(this);
            generationMenu.add(startItem);
            generationMenu.add(stopItem);
            menuBar.add(generationMenu);

            //---Меню настроек---
            settingsMenu = new JMenu("Настройки");
            showTimeItem = new JCheckBoxMenuItem("Отображать время",true);
            showTimeItem.setActionCommand(SWITCH_SHOW_TIME);
            showTimeItem.addActionListener(this);
            showInfoItem = new JCheckBoxMenuItem("Отображать информацию");
            showInfoItem.addItemListener(e -> {
                setShowInformation(((JCheckBoxMenuItem) e.getSource()).isSelected());
            });
            settingsMenu.add(showTimeItem);
            settingsMenu.add(showInfoItem);
            menuBar.add(settingsMenu);

        //---Панель инструментов---
        toolBar = new JToolBar();
        startToolBarBtn = new JButton("Start");
        startToolBarBtn.setActionCommand(START_SIMULATION);
        startToolBarBtn.addActionListener(this);
        startToolBarBtn.setBackground(unpressedBtnColor);

        stopToolBarBtn = new JButton("Stop");
        stopToolBarBtn.setActionCommand(STOP_SIMULATION);
        stopToolBarBtn.addActionListener(this);
        stopToolBarBtn.setBackground(unpressedBtnColor);

        showTimeToolBarBtn = new JButton("ST");
        showTimeToolBarBtn.setActionCommand(SWITCH_SHOW_TIME);
        showTimeToolBarBtn.addActionListener(this);
        showTimeToolBarBtn.setBackground(pressedBtnColor);

        showInfoToolBarBtn = new JButton("SI");
        showInfoToolBarBtn.setActionCommand(SWITCH_SHOW_INFO);
        showInfoToolBarBtn.addActionListener(this);
        showInfoToolBarBtn.setBackground(pressedBtnColor);

        toolBar.add(startToolBarBtn);
        toolBar.add(stopToolBarBtn);
        toolBar.add(showTimeToolBarBtn);
        toolBar.add(showInfoToolBarBtn);
        f.add(toolBar,BorderLayout.NORTH);

        f.setJMenuBar(menuBar);
        f.setSize(1000, 600);
        f.setVisible(true);
        f.setFocusable(true);
    }

    private void setShowInformation(boolean showInformation) {
        showInfo = showInformation;
        showInformationCheckBox.setSelected(showInformation);
        showInfoItem.setSelected(showInformation);
        if (showInfo) showInfoToolBarBtn.setBackground(pressedBtnColor);
        else showInfoToolBarBtn.setBackground(unpressedBtnColor);
    }

    private void stopSimulation() {
        timer.stop();

        if (!showInfo || showInfoDialog()) {
            habitat.ClearList();
            stopSimulationButton.setEnabled(false);
            startSimulationButton.setEnabled(true);
            stopItem.setEnabled(false);
            startItem.setEnabled(true);
            startToolBarBtn.setEnabled(true);
            stopToolBarBtn.setEnabled(false);
            time = 0;
        } else timer.start();
    }

    private void startSimulation(){
        timer.start();
        world.removeAll();
        startSimulationButton.setEnabled(false);
        stopSimulationButton.setEnabled(true);
        startItem.setEnabled(false);
        stopItem.setEnabled(true);
        world.add(timeLabel);
        startToolBarBtn.setEnabled(false);
        stopToolBarBtn.setEnabled(true);
        f.repaint();
    }

    private void setShowTime(boolean show){
        timeLabel.setVisible(show);
        showTimeItem.setState(show);

        if (show) {
            showTimeSimulation.setSelected(true);
            showTimeToolBarBtn.setBackground(pressedBtnColor);
        }
        else {
            dontShowTimeSimulation.setSelected(true);
            showTimeToolBarBtn.setBackground(unpressedBtnColor);
        }
    }

    private boolean showInfoDialog(){
        HashMap<String, Integer> dataReport = habitat.getHousesReport();
        StringBuilder reportText = new StringBuilder();
        for (String key : dataReport.keySet()) {
            reportText.append(key + ": " + dataReport.get(key) + "\n");
        }
        ReportDialog dialog = new ReportDialog(f, reportText.toString());
        dialog.setVisible(true);
        return dialog.getResult();
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
            case SHOW_TIME:
                setShowTime(true);
                break;
            case DONTSHOW_TIME:
                setShowTime(false);
                break;
            case SWITCH_SHOW_TIME:
                setShowTime(!timeLabel.isVisible());
                break;
            case SWITCH_SHOW_INFO:
                setShowInformation(!showInfo);
                break;
        }
    }
}
