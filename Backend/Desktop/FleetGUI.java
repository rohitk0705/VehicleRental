package Backend.Desktop;

import Backend.Common.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class FleetGUI extends JFrame implements ActionListener {
    private FleetService service;
    private JTable table;
    private DefaultTableModel model;
    private JTextField idField, brandField, extraField, priceField, searchField;
    private JComboBox<String> typeCombo;
    private JButton addBtn, rentBtn, returnBtn, importBtn, exportBtn, clearBtn, exitBtn;
    private JLabel totalRevenueLabel;

    public FleetGUI(File dataFile){
        service = new FleetService(dataFile);
        initUI();
        refreshTable();
    }

    public FleetGUI(){
        this(new File("fleet.txt"));
    }

    private void initUI(){
        setTitle("Fleet Manager");
        setSize(950, 520);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new GridLayout(3,1));

        // Input Row
        JPanel inputs = new JPanel(new FlowLayout(FlowLayout.LEFT));

        typeCombo = new JComboBox<>(new String[]{"Car","Bike","Truck"});
        inputs.add(typeCombo);

        idField = new JTextField(8);
        inputs.add(new JLabel("ID"));
        inputs.add(idField);

        brandField = new JTextField(10);
        inputs.add(new JLabel("Brand"));
        inputs.add(brandField);

        extraField = new JTextField(8);
        inputs.add(new JLabel("Extra"));
        inputs.add(extraField);

        priceField = new JTextField(6);
        inputs.add(new JLabel("Price"));
        inputs.add(priceField);

        addBtn = new JButton("Add");
        addBtn.addActionListener(this);
        inputs.add(addBtn);

        rentBtn = new JButton("Rent");
        rentBtn.addActionListener(this);
        inputs.add(rentBtn);

        returnBtn = new JButton("Return");
        returnBtn.addActionListener(this);
        inputs.add(returnBtn);

        top.add(inputs);

        // Search Row
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));

        searchField = new JTextField(20);
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e){ filterTable(); }
        });
        searchPanel.add(searchField);

        importBtn = new JButton("Import CSV");
        importBtn.addActionListener(this);
        searchPanel.add(importBtn);

        JButton testDataBtn = new JButton("Load Test Data");
        testDataBtn.addActionListener(e -> {
            service.loadTestData();
            refreshTable();
            JOptionPane.showMessageDialog(this, "Test data loaded!");
        });
        searchPanel.add(testDataBtn);

        exportBtn = new JButton("Export CSV");
        exportBtn.addActionListener(this);
        searchPanel.add(exportBtn);

        clearBtn = new JButton("Clear Data");
        clearBtn.addActionListener(this);
        searchPanel.add(clearBtn);

        exitBtn = new JButton("Exit");
        exitBtn.addActionListener(this);
        searchPanel.add(exitBtn);

        top.add(searchPanel);

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        summaryPanel.add(new JLabel("Total Revenue:"));
        totalRevenueLabel = new JLabel();
        summaryPanel.add(totalRevenueLabel);
        top.add(summaryPanel);

        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"Type","ID","Brand","Rented","Extra"}, 0
        ){
            public boolean isCellEditable(int r, int c){ return false; }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void filterTable(){
        String q = searchField.getText().trim().toLowerCase();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);

        if (q.isEmpty()) sorter.setRowFilter(null);
        else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + q));

        table.setRowSorter(sorter);
    }

    private void refreshTable(){
        model.setRowCount(0);
        List<Vehicle> list = service.getFleet();

        for (Vehicle v : list) {
            model.addRow(new Object[]{
                v.getTypeName(),
                v.getId(),
                v.getBrand(),
                v.isRented() ? "Yes" : "No",
                v.getExtra()
            });
        }

        updateRevenueLabel();
    }

    private void updateRevenueLabel(){
        if (totalRevenueLabel == null) return;
        double revenue = service.getTotalRevenue();
        DecimalFormat df = new DecimalFormat("₹#,##0.00");
        totalRevenueLabel.setText(" " + df.format(revenue));
    }

    @Override
    public void actionPerformed(ActionEvent e){
        Object s = e.getSource();

        if (s == addBtn) doAdd();
        else if (s == rentBtn) doRent();
        else if (s == returnBtn) doReturn();
        else if (s == importBtn) doImport();
        else if (s == exportBtn) doExport();
        else if (s == clearBtn) doClear();
        else if (s == exitBtn) {
            if (JOptionPane.showConfirmDialog(this,"Exit app?","Confirm",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                System.exit(0);
        }
    }

    private void doAdd(){
        String type = (String) typeCombo.getSelectedItem();
        String id = idField.getText().trim();
        String brand = brandField.getText().trim();
        String extra = extraField.getText().trim();
        String priceStr = priceField.getText().trim();

        if (id.isEmpty() || brand.isEmpty() || extra.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Fill all fields","Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (service.existsId(id)) {
            JOptionPane.showMessageDialog(this,"ID exists","Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            switch (type) {
                case "Car":
                    service.addVehicle(new Car(id, brand, extra, price));
                    break;

                case "Bike":
                    service.addVehicle(new Bike(id, brand, extra, price));
                    break;

                case "Truck":
                    double load = Double.parseDouble(extra);
                    service.addVehicle(new Truck(id, brand, load, price));
                    break;
            }

            refreshTable();
            clearInputs();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,"Invalid number in Extra or Price",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearInputs(){
        idField.setText("");
        brandField.setText("");
        extraField.setText("");
        priceField.setText("");
        idField.requestFocusInWindow();
    }

    private void doRent(){
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this,"Select a vehicle","Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        String id = (String) model.getValueAt(modelRow, 1);

        if (JOptionPane.showConfirmDialog(this,"Rent " + id + "?","Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

            JOptionPane.showMessageDialog(this, service.rentVehicle(id));
            refreshTable();
        }
    }

    private void doReturn(){
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this,"Select a vehicle","Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        String id = (String) model.getValueAt(modelRow, 1);

        if (JOptionPane.showConfirmDialog(this,"Return " + id + "?","Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

            JOptionPane.showMessageDialog(this, service.returnVehicle(id));
            refreshTable();
        }
    }

    private void doImport(){
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();

            try {
                service.importCsv(f);
                refreshTable();
                JOptionPane.showMessageDialog(this,"Imported successfully");

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,"Import failed: " + ex.getMessage());
            }
        }
    }

    private void doExport(){
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();

            try {
                service.exportCsv(f);
                JOptionPane.showMessageDialog(this,"Exported successfully");

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,"Export failed: " + ex.getMessage());
            }
        }
    }

    private void doClear(){
        if (JOptionPane.showConfirmDialog(this,"Clear all fleet data?","Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

            service.clearData();
            refreshTable();
            JOptionPane.showMessageDialog(this,"Cleared");
        }
    }
}
