package DAD;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import org.json.*;

public class UpdateMenu extends JPanel {
	private static final long serialVersionUID = 1L;
    private JTable foodTable, beverageTable;
    private DefaultTableModel foodModel, beverageModel;

    public UpdateMenu() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 239, 213));

        // ===== TOP PANEL WITH TITLE AND SEARCH =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 239, 213));
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Title (left)
        JLabel title = new JLabel("List Of Menu");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(title, BorderLayout.WEST);

        // Search (right)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 7));
        searchPanel.setBackground(new Color(255, 239, 213));
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField searchField = new JTextField(18);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ===== TABLE SETUP =====
        foodModel = new DefaultTableModel(new String[]{"ID", "Name", "Price (RM)", "Category"}, 0);
        beverageModel = new DefaultTableModel(new String[]{"ID", "Name", "Price (RM)", "Category"}, 0);

        foodTable = new JTable(foodModel);
        beverageTable = new JTable(beverageModel);
        foodTable.setRowHeight(30);
        beverageTable.setRowHeight(32);
        foodTable.getTableHeader().setBackground(new Color(210, 180, 140));
        beverageTable.getTableHeader().setBackground(new Color(210, 180, 140)); 
        foodTable.setBackground(Color.decode("#FFFAF0"));
        beverageTable.setBackground(Color.decode("#FFFAF0"));
        foodTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        beverageTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Food", new JScrollPane(foodTable));
        tabbedPane.addTab("Beverage", new JScrollPane(beverageTable));
        add(tabbedPane, BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(new Color(255, 248, 230));

        Color lightBrown = new Color(222, 184, 135);
        Dimension buttonSize = new Dimension(120, 35);

        JButton refreshBtn = new JButton("REFRESH");
        JButton addBtn = new JButton("ADD");
        JButton updateBtn = new JButton("UPDATE");
        JButton deleteBtn = new JButton("DELETE");

        for (JButton btn : new JButton[]{refreshBtn, addBtn, updateBtn, deleteBtn}) {
            btn.setBackground(lightBrown);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(buttonSize);
            btnPanel.add(btn);
        }

        add(btnPanel, BorderLayout.SOUTH);

        // ===== BUTTON ACTIONS =====
        refreshBtn.addActionListener(e -> loadMenuItems());
        addBtn.addActionListener(e -> showAddDialog());
        updateBtn.addActionListener(e -> showUpdateDialog(tabbedPane.getSelectedIndex()));
        deleteBtn.addActionListener(e -> deleteMenuItem(tabbedPane.getSelectedIndex()));

        // ===== SEARCH FILTER =====
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String keyword = searchField.getText().trim().toLowerCase();
                int selectedTab = tabbedPane.getSelectedIndex();
                JTable table = selectedTab == 0 ? foodTable : beverageTable;
                DefaultTableModel model = selectedTab == 0 ? foodModel : beverageModel;

                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
                table.setRowSorter(sorter);

                if (keyword.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + keyword, 1));
                }
            }
        });

        loadMenuItems();
    }

    private void loadMenuItems() {
        foodModel.setRowCount(0);
        beverageModel.setRowCount(0);
        try {
        	URL url = URI.create("http://localhost/api.php?action=get_menu").toURL();
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder jsonStr = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) jsonStr.append(line);
            reader.close();

            JSONArray items = new JSONArray(jsonStr.toString());
            for (int i = 0; i < items.length(); i++) {
                JSONObject obj = items.getJSONObject(i);
                Object[] row = {
                    obj.getInt("id"),
                    obj.getString("name"),
                    obj.getDouble("price"),
                    obj.getString("category")
                };

                if (obj.getString("category").equalsIgnoreCase("Beverage")) {
                    beverageModel.addRow(row);
                } else {
                    foodModel.addRow(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading menu: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JComboBox<String> categoryBox = new JComboBox<>(new String[]{"Food", "Beverage"});

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Price:")); panel.add(priceField);
        panel.add(new JLabel("Category:")); panel.add(categoryBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Menu Item", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                URL url = URI.create("http://localhost/api.php").toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject payload = new JSONObject();
                payload.put("action", "add_menu_item");
                payload.put("name", nameField.getText());
                payload.put("price", Double.parseDouble(priceField.getText()));
                payload.put("category", categoryBox.getSelectedItem().toString());

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(payload.toString().getBytes("utf-8"));
                }

                if (conn.getResponseCode() == 200) {
                    JOptionPane.showMessageDialog(this, "Item added.");
                    loadMenuItems();
                } else {
                    JOptionPane.showMessageDialog(this, "Add failed.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding: " + e.getMessage());
            }
        }
    }

    private void showUpdateDialog(int tabIndex) {
        JTable table = tabIndex == 0 ? foodTable : beverageTable;
        DefaultTableModel model = tabIndex == 0 ? foodModel : beverageModel;
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to update.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String name = model.getValueAt(row, 1).toString();
        String price = model.getValueAt(row, 2).toString();
        String category = model.getValueAt(row, 3).toString();

        JTextField nameField = new JTextField(name);
        JTextField priceField = new JTextField(price);
        JComboBox<String> catBox = new JComboBox<>(new String[]{"Food", "Beverage"});
        catBox.setSelectedItem(category);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Price:")); panel.add(priceField);
        panel.add(new JLabel("Category:")); panel.add(catBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Menu Item", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                URL url = URI.create("http://localhost/api.php").toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject payload = new JSONObject();
                payload.put("action", "update_menu_item");
                payload.put("id", id);
                payload.put("name", nameField.getText());
                payload.put("price", Double.parseDouble(priceField.getText()));
                payload.put("category", catBox.getSelectedItem().toString());

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(payload.toString().getBytes("utf-8"));
                }

                if (conn.getResponseCode() == 200) {
                    JOptionPane.showMessageDialog(this, "Item updated.");
                    loadMenuItems();
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating: " + e.getMessage());
            }
        }
    }

    private void deleteMenuItem(int tabIndex) {
        JTable table = tabIndex == 0 ? foodTable : beverageTable;
        DefaultTableModel model = tabIndex == 0 ? foodModel : beverageModel;
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete item ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                URL url = URI.create("http://localhost/api.php").toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject payload = new JSONObject();
                payload.put("action", "delete_menu_item");
                payload.put("id", id);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(payload.toString().getBytes("utf-8"));
                }

                if (conn.getResponseCode() == 200) {
                    JOptionPane.showMessageDialog(this, "Item deleted.");
                    loadMenuItems();
                } else {
                    JOptionPane.showMessageDialog(this, "Delete failed.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting: " + e.getMessage());
            }
        }
    }
}
