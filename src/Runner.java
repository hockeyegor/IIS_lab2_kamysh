import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;

public class Runner extends JFrame implements ActionListener {
    public static int MAX_ANSW_SIZE = 16;
    private String[] propertyNames = new String[0];
    private int taskSize = 0;
    private ClassesSet core;
    private DocumentBuilder docBuilder;

    private JLabel statusBar = new JLabel("Total properties: 0");
    private JLabel totalRulesBar = new JLabel("Total classes: 0");
    private JPanel centralPanel = new JPanel();
    private JPanel northPanel = new JPanel();
    private JButton select = new JButton("--- Define ---");
    private JCheckBox[] selButtons = new JCheckBox[MAX_ANSW_SIZE];

    private JFileChooser fileChooser = new JFileChooser();

    public Runner(String path) throws Exception {
        setTitle("lab №2. Kamysh E., 4 group");
        setSize(400, 400);
        setLocation(200, 200);

        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setValidating(false);
        f.setNamespaceAware(true);
        docBuilder = f.newDocumentBuilder();

        getData(path);
        select.addActionListener(this);

        JMenuBar menuBar = new JMenuBar();
        JMenu mainMenu = new JMenu("Menu");
        final JMenuItem itemReload = new JMenuItem("Load data...");
        final JMenuItem itemExit = new JMenuItem("Exit");

        mainMenu.add(itemReload);
        mainMenu.add(itemExit);
        menuBar.add(mainMenu);
        setJMenuBar(menuBar);

        itemExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        itemReload.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int ret = fileChooser.showOpenDialog(null);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File f = fileChooser.getSelectedFile();
                    try {
                        getData(f.getPath());
                        cleanCentralPanel();
                        select.setVisible(true);
                        select.setEnabled(true);
                        doSetView();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        northPanel.setLayout(new GridLayout(2, 1));
        northPanel.setBorder(BorderFactory.createEtchedBorder());
        northPanel.add(totalRulesBar);
        northPanel.add(statusBar);

        centralPanel.setLayout(new GridLayout(MAX_ANSW_SIZE + 1, 1));

        for (int i = 0; i < MAX_ANSW_SIZE; i++) {
            selButtons[i] = new JCheckBox();
            centralPanel.add(selButtons[i]);
        }

        JPanel b = new JPanel(new GridLayout(1, 3));
        b.add(new JPanel());
        b.add(select);
        b.add(new JPanel());

        centralPanel.add(b);
        add(northPanel, BorderLayout.NORTH);
        add(centralPanel, BorderLayout.CENTER);

        doSetView();
    }

    public static void main(String[] args) throws Exception {
        final String path = args.length == 0 ? null : args[0];
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                try {
                    JFrame me = new Runner(path);
                    me.setVisible(true);
                    me.setDefaultCloseOperation(EXIT_ON_CLOSE);
                } catch (Exception exc) {
                    System.err.println("Ошибка. " + exc.getMessage());
                    exc.printStackTrace();
                }
            }
        });
        int[] ch1 = new int[]{1, 1, 0, 0, 1, 0, 0};
        int[] ch2 = new int[]{0, 0, 1, 1, 1, 1, 0};
        int[] ch3 = new int[]{0, 0, 0, 0, 1, 1, 1};
    }

    public void actionPerformed(ActionEvent ae) {
        if (core != null) {
            BitSet bs = new BitSet(taskSize);
            for (int i = 0; i < taskSize; i++) {
                bs.set(i, selButtons[i].isSelected());
            }
            Vector n = new Vector("test", bs);
            Class result = core.belongs(n);
            JOptionPane.showMessageDialog(null, "Набор продуктов соответсвует кухне → " + result.getName() + "\n" +
                    result.mus);
        }
    }

    private void doSetView() {
        int i = 0;
        for (String category : propertyNames) {
            selButtons[i].setText(category);
            selButtons[i].setVisible(true);
            i++;
        }
        for (int j = i; j < MAX_ANSW_SIZE; j++) {
            selButtons[j].setVisible(false);
        }
    }

    private void cleanCentralPanel() {
        for (JCheckBox rb : selButtons) {
            rb.setVisible(false);
        }
        select.setVisible(false);
    }

    public void getData(String path) throws Exception {
        if (path == null)
            return;
        int classes = 0, properties = 0;
        String sName = "Лабораторная №2";
        Document d = docBuilder.parse(new File(path));
        NodeList props = d.getElementsByTagName("properties");
        Map<String, Integer> pMap = new HashMap<String, Integer>();
        Set<String> knownVectors = new HashSet<String>();
        ArrayList<String> pNames = new ArrayList<String>();
        if (props.getLength() > 0) {
            Element propsContainer = (Element) props.item(0);
            NodeList elProps = propsContainer.getElementsByTagName("property");
            for (int i = 0; i < elProps.getLength(); i++) {
                Element te = (Element) elProps.item(i);
                if (te.hasAttribute("name")) {
                    String pName = te.getAttribute("name");
                    pMap.put(pName, i);
                    pNames.add(pName);
                    properties++;
                }
            }
        }
        propertyNames = new String[properties];
        propertyNames = pNames.toArray(propertyNames);
        if (properties > 0) {
            NodeList eSets = d.getElementsByTagName("classSet");
            if (eSets.getLength() > 0) {
                Element classSet = (Element) eSets.item(0);
                NodeList eClasses = classSet.getElementsByTagName("class");
                classes = eClasses.getLength();
                Class[] classesArr = new Class[classes];
                for (int i = 0, si = eClasses.getLength(); i < si; i++) {
                    Element currClass = (Element) eClasses.item(i);
                    NodeList classVectors = currClass.getElementsByTagName("vector");
                    Vector[] classesVec = new Vector[classVectors.getLength()];
                    for (int j = 0, sj = classVectors.getLength(); j < sj; j++) {
                        Element currVect = (Element) classVectors.item(j);
                        NodeList aProperties = currVect.getElementsByTagName("property");
                        int[] bitprops = new int[properties];
                        for (int k = 0, sk = aProperties.getLength(); k < sk; k++) {
                            Element currProp = (Element) aProperties.item(k);
                            String propName = currProp.getAttribute("name");
                            if (propName != null && pMap.containsKey(propName)) {
                                bitprops[pMap.get(propName)] = 1;
                            }
                        }
                        String vName = currVect.hasAttribute("name") ? currVect.getAttribute("name") : "vector";
                        Vector tmpVect = new Vector(vName, arrToBitset(bitprops));
                        if (!knownVectors.contains(tmpVect.serialize())) {
                            classesVec[j] = tmpVect;
                            knownVectors.add(classesVec[j].serialize());
                        } else {
                            System.err.println("Duplicate vector found: " + vName);
                        }
                    }
                    String cName = currClass.hasAttribute("name") ? currClass.getAttribute("name") : "class";
                    classesArr[i] = new Class(cName, properties, classesVec);
                }
                sName = classSet.hasAttribute("name") ? classSet.getAttribute("name") : "classset";
                core = new ClassesSet(sName, classesArr, properties);
            }
        }
        taskSize = properties;
        totalRulesBar.setText(String.format("Total classes: %d", classes));
        statusBar.setText(String.format("Total properties: %d", properties));
        this.setTitle(sName);
    }

    private BitSet arrToBitset(int a[]) {
        BitSet res = new BitSet(a.length);
        int i = 0;
        for (int e : a) {
            res.set(i++, e > 0);
        }
        return res;
    }
}
