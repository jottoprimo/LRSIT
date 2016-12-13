import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Vector;

public class Application implements Runnable, ActionListener {
    private final String START_SIMULATION="start simulation";
    private final String STOP_SIMULATION="stop simulation";
    private final String SHOW_TIME="show time";
    private final String DONTSHOW_TIME="-show time";
    private final String SWITCH_SHOW_TIME="switch time";
    private final String SWITCH_SHOW_INFO="switch info";

    private boolean showInfo = true;

    private HashMap<String, Float> percentToFloat;


    final Color unpressedBtnColor = new Color(0xEEEEEE);
    final Color pressedBtnColor = new Color(0xFFFFFF);

    static Habitat habitat;
    int time = 0;

    private HashMap<String, Image> images = new HashMap<>();
    private HashMap<House, ImagePanel> houses = new HashMap<>();

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
    private boolean sleepPrivate = false;
    private boolean sleepTenement = false;


    public Application() {
    }

    public static void main(String[] args) throws IOException {
        habitat = new Habitat(1000,300);
        SwingUtilities.invokeLater (new Application());
        habitat.start();
    }

    public void redraw(){
        SwingUtilities.invokeLater(() ->{
        for (House house_ : houses.keySet()) {
            houses.get(house_).setLocation(house_.getX(), house_.getY());
        }
         f.repaint();
        });
    }

    public void addHouse(House house){
        ImagePanel imagePanel = new ImagePanel();
        imagePanel.setLayout(null);
        imagePanel.setSize(30, 30);
        imagePanel.setLocation(house.getX(), house.getY());
        try {
            Image image = images.get(house.getImagePath());
            if (image == null) {
                image = ImageIO.read(new File(house.getImagePath()));
                images.put(house.getImagePath(), image);
            }
            imagePanel.setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        world.add(imagePanel);
        houses.put(house, imagePanel);

        //Singleton.getInstance().getImages().add(imagePanel);
        f.repaint();
    }

    public void run() {

            f = new JFrame("Main");
            world = new JPanel();
            JPanel panelViewSettings = new JPanel();
            panelViewSettings.setLayout(new BoxLayout(panelViewSettings, BoxLayout.Y_AXIS));
            JPanel panelGenerationSettings = new JPanel();
            panelGenerationSettings.setLayout(new FlowLayout(FlowLayout.LEFT));

            //---Среда---
            world.setMinimumSize(new Dimension(1000, 300));
            world.setPreferredSize(new Dimension(1000, 300));

            world.setLayout(null);
            timeLabel = new JLabel("Время: ");
            timeLabel.setLocation(0, 0);
            timeLabel.setSize(100, 20);
            timeLabel.setBorder(new BevelBorder(1));
            world.add(timeLabel);


        habitat.setCreteHouseListener(
                new IHabitatListener() {
                    @Override
                    public void onCreateHouse(House house) {
                        addHouse(house);
                    }

                    @Override
                    public void onRedraw() {
                        redraw();
                    }
                }
        );

            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
                    if (e.getKeyCode() == 84) {
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
            showTimeSimulation = new JRadioButton("Показывать время", true);
            showTimeSimulation.setActionCommand(SHOW_TIME);
            showTimeSimulation.addActionListener(this);
            dontShowTimeSimulation = new JRadioButton("Не показывать время");
            dontShowTimeSimulation.setActionCommand(DONTSHOW_TIME);
            dontShowTimeSimulation.addActionListener(this);
            showInformationCheckBox = new JCheckBox("Выводить информацию", showInfo);
            showInformationCheckBox.addItemListener(e -> {
                setShowInformation(((JCheckBox) e.getSource()).isSelected());
            });
            showTimeSimulationGroup.add(showTimeSimulation);
            showTimeSimulationGroup.add(dontShowTimeSimulation);
            Vector<Integer> properties = new Vector<>(10);
            for (int i=1;i<11;i++){
                properties.add(i);
            }
            JComboBox priorityPrivateSet = new JComboBox(properties);
            priorityPrivateSet.setSelectedIndex(4);
        priorityPrivateSet.addItemListener(e -> {
            Integer value = (Integer) ((JComboBox) e.getSource()).getSelectedItem();
            habitat.setPrivatePriority(value);
        });
            JComboBox priorityTenementSet = new JComboBox(properties);
        priorityTenementSet.setSelectedIndex(4);
        priorityTenementSet.addItemListener(e -> {
            Integer value = (Integer) ((JComboBox) e.getSource()).getSelectedItem();
            habitat.setTenementPriority(value);
        });
            JButton sleepPrivateButton= new JButton("ост. частные");
            JButton sleepTenementButton= new JButton("ост. капитальные");
            sleepPrivateButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (sleepPrivate){
                        habitat.wakeUpPrivate();
                    } else {
                        habitat.sleepPrivate();
                    }
                    sleepPrivate = !sleepPrivate;
                }
            });

            sleepTenementButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (sleepTenement){
                        habitat.wakeUpTenement();
                    } else {
                        habitat.sleepTenement();
                    }
                    sleepTenement = !sleepTenement;
                }
            });


            panelViewSettings.add(startSimulationButton);
            panelViewSettings.add(stopSimulationButton);
            panelViewSettings.add(showInformationCheckBox);
            panelViewSettings.add(showTimeSimulation);
            panelViewSettings.add(dontShowTimeSimulation);
        panelViewSettings.add(priorityPrivateSet);
        panelViewSettings.add(priorityTenementSet);
        panelViewSettings.add(sleepPrivateButton);
        panelViewSettings.add(sleepTenementButton);

            //---Панель управления симуляцией---
            percentToFloat = new HashMap<>();
            for (int i = 0; i < 11; i++) {
                percentToFloat.put(i * 10 + "%", i / 10.f);
            }

            tTenementGenerationField = new JTextField("3");
            tTenementGenerationField.addActionListener(e -> {
                JTextField field = (JTextField) e.getSource();
                String text = field.getText();
                int val = -1;
                try {
                    val = Integer.parseInt(text);
                } catch (NumberFormatException ex) {
                    field.setText(Integer.toString(habitat.gettTenemnt()));
                    JOptionPane.showMessageDialog(f, "Введено не корректное значение");
                    return;
                }
                if (val <= 0) {
                    field.setText(Integer.toString(habitat.gettTenemnt()));
                    JOptionPane.showMessageDialog(f, "Введено не корректное значение");
                    return;
                }
                habitat.settTenemnt(val);
            });
            tPrivateGenerationField = new JTextField("1");
            tPrivateGenerationField.addActionListener(e -> {
                JTextField field = (JTextField) e.getSource();
                String text = field.getText();
                int val = -1;
                try {
                    val = Integer.parseInt(text);
                } catch (NumberFormatException ex) {
                    field.setText(Integer.toString(habitat.gettPrivate()));
                    JOptionPane.showMessageDialog(f, "Введено не корректное значение");
                    return;
                }
                if (val <= 0) {
                    field.setText(Integer.toString(habitat.gettPrivate()));
                    JOptionPane.showMessageDialog(f, "Введено не корректное значение");
                    return;
                }
                habitat.settPrivate(val);
            });
            tPrivateGenerationField.setPreferredSize(new Dimension(80, 20));
            Vector<String> percents = new Vector<>(11);
            for (int i = 0; i < 11; i++) {
                percents.add(i * 10 + "%");
            }
            pPrivateGeneration = new JComboBox<>(percents);
            pPrivateGeneration.setSelectedIndex(6);
            pPrivateGeneration.addItemListener(e -> {
                String value = (String) ((JComboBox) e.getSource()).getSelectedItem();
                habitat.setpPrivate(percentToFloat.get(value));
            });
            pTenementGeneration = new JList<>(percents);
            pTenementGeneration.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            pTenementGeneration.setSelectedIndex(8);
            pTenementGeneration.addListSelectionListener(e -> {
                String value = (String) ((JList) e.getSource()).getSelectedValue();
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
            showTimeItem = new JCheckBoxMenuItem("Отображать время", true);
            showTimeItem.setActionCommand(SWITCH_SHOW_TIME);
            showTimeItem.addActionListener(this);
            showInfoItem = new JCheckBoxMenuItem("Отображать информацию");
            showInfoItem.addItemListener(e -> {
                setShowInformation(((JCheckBoxMenuItem) e.getSource()).isSelected());
            });
            JMenuItem saveProp = new JMenuItem("Сохранить настройки");
        saveProp.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showDialog(null,"Save");

            String file = fileChooser.getSelectedFile().getAbsolutePath();
            Properties prop = habitat.getProperties();
            try {
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream out = new ObjectOutputStream(fos);
                out.writeObject(prop);
                out.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

            JMenuItem loadProp = new JMenuItem("Загрузить настройки");
        loadProp.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showDialog(null,"Open");
            String file = fileChooser.getSelectedFile().getAbsolutePath();
            Properties prop = null;
            try {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fis);
                prop = (Properties) in.readObject();
                in.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
            tTenementGenerationField.setText(Integer.toString(prop.gettTenemnt()));
            tPrivateGenerationField.setText(Integer.toString(prop.gettPrivate()));
            pTenementGeneration.setSelectedIndex((int)(prop.getpTenement()*10));
            pPrivateGeneration.setSelectedIndex((int)(prop.getpPrivate()*10));
            habitat.setProperties(prop);
        });

        JMenuItem saveObjs = new JMenuItem("Сохранить объекты");
        saveObjs.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showDialog(null,"Save");

            String file = fileChooser.getSelectedFile().getAbsolutePath();
            try {
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream out = new ObjectOutputStream(fos);
                out.writeObject(Singleton.getInstance().getHouses());
                out.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        JMenuItem loadObjs = new JMenuItem("Загрузить объекты");
        loadObjs.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showDialog(null,"Open");
            String file = fileChooser.getSelectedFile().getAbsolutePath();
            ArrayList<House> objects= null;
            try {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fis);
                objects = (ArrayList<House>) in.readObject();
                in.close();
                synchronized (Singleton.getInstance()) {
                    Singleton.getInstance().setHouses(objects);
                    world.removeAll();
                    for (House house: objects){
                        addHouse(house);
                    }
                }
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
            redraw();
        });

            settingsMenu.add(showTimeItem);
            settingsMenu.add(showInfoItem);
        settingsMenu.add(saveProp);
        settingsMenu.add(loadProp);
        settingsMenu.add(saveObjs);
        settingsMenu.add(loadObjs);
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
            f.add(toolBar, BorderLayout.NORTH);

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
        habitat.sleepTenement();
        habitat.sleepPrivate();
        if (!showInfo || showInfoDialog()) {
            habitat.ClearList();
            stopSimulationButton.setEnabled(false);
            startSimulationButton.setEnabled(true);
            stopItem.setEnabled(false);
            startItem.setEnabled(true);
            startToolBarBtn.setEnabled(true);
            stopToolBarBtn.setEnabled(false);
            time = 0;
        } else {
            timer.start();

            habitat.wakeUpTenement();
            habitat.wakeUpPrivate();
        }
    }

    private void startSimulation(){
        timer.start();
        habitat.wakeUpTenement();
        habitat.wakeUpPrivate();
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
        synchronized (Thread.currentThread()) {
            if (show) {
                showTimeSimulation.setSelected(true);
                showTimeToolBarBtn.setBackground(pressedBtnColor);
            } else {
                dontShowTimeSimulation.setSelected(true);
                showTimeToolBarBtn.setBackground(unpressedBtnColor);
            }
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
