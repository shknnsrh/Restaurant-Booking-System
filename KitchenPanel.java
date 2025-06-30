package DAD;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.Timer;
import org.json.*;

public class KitchenPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JTable orderTable;
    private DefaultTableModel tableModel;
    private java.util.List<OrderData> orderList = new ArrayList<>();

    public KitchenPanel() {
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Kitchen Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.setBackground(new Color(255, 239, 213));

        JButton refreshBtn = new JButton("Refresh Orders");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshBtn.addActionListener(e -> fetchOrders());
        headerPanel.add(refreshBtn, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"Order ID", "Items", "Status", "Change Status", "Action"};
        tableModel = new DefaultTableModel(columns, 0) {
        	private static final long serialVersionUID = 1L;
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4;
            }
        };

        orderTable = new JTable(tableModel);
        orderTable.setRowHeight(60);
        orderTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        orderTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        orderTable.setFillsViewportHeight(true);
        orderTable.setDefaultRenderer(Object.class, new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(orderTable);
        add(scrollPane, BorderLayout.CENTER);

        orderTable.getColumn("Change Status").setCellEditor(new StatusCellEditor());
        orderTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        orderTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));

        fetchOrders();

        new Timer(10000, e -> fetchOrders()).start();
    }

    private void fetchOrders() {
        tableModel.setRowCount(0);
        orderList.clear();
        try {
            URL url = URI.create("http://localhost/api.php?action=get_orders").toURL();
            Scanner sc = new Scanner(url.openStream());
            StringBuilder jsonStr = new StringBuilder();
            while (sc.hasNext()) {
                jsonStr.append(sc.nextLine());
            }
            sc.close();

            JSONArray orders = new JSONArray(jsonStr.toString());

            for (int i = 0; i < orders.length(); i++) {
                JSONObject order = orders.getJSONObject(i);
                int orderId = order.getInt("order_id");
                String status = order.getString("status");
                JSONArray items = order.getJSONArray("items");

                StringBuilder itemText = new StringBuilder();
                for (int j = 0; j < items.length(); j++) {
                    JSONObject item = items.getJSONObject(j);
                    itemText.append(item.getString("name"))
                            .append(" x")
                            .append(item.getInt("quantity"))
                            .append("\n");
                }

                OrderData od = new OrderData(orderId, itemText.toString().trim(), status);
                orderList.add(od);

                tableModel.addRow(new Object[]{
                        orderId,
                        "<html>" + itemText.toString().replaceAll("\n", "<br>") + "</html>",
                        status,
                        getNextStatusOption(status),
                        status.equalsIgnoreCase("Completed") ? "" : "Update"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching orders: " + e.getMessage());
        }
    }

    private String getNextStatusOption(String currentStatus) {
        switch (currentStatus.toLowerCase()) {
            case "pending":
                return "Preparing";
            case "preparing":
                return "Completed";
            case "completed":
                return "Completed";
            default:
                return currentStatus;
        }
    }

    private void updateOrderStatus(int rowIndex) {
        try {
            OrderData order = orderList.get(rowIndex);
            String newStatus = (String) orderTable.getValueAt(rowIndex, 3);

            if (newStatus == null || newStatus.equalsIgnoreCase(order.status)) {
                JOptionPane.showMessageDialog(this, "No status change detected.");
                return;
            }

            URL url = URI.create("http://localhost/api.php").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject payload = new JSONObject();
            payload.put("action", "update_order_status");
            payload.put("order_id", order.orderId);
            payload.put("new_status", newStatus);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Send to SocketServer
                try (Socket socket = new Socket("localhost", 12345);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                    JSONObject updateMsg = new JSONObject();
                    updateMsg.put("order_id", order.orderId);
                    updateMsg.put("status", newStatus);

                    JSONArray itemsArray = new JSONArray();
                    String[] lines = order.items.split("\n");
                    for (String line : lines) {
                        String[] parts = line.split(" x");
                        JSONObject item = new JSONObject();
                        item.put("name", parts[0].trim());
                        item.put("quantity", Integer.parseInt(parts[1].trim()));
                        itemsArray.put(item);
                    }
                    updateMsg.put("items", itemsArray);

                    out.println(updateMsg.toString());
                } catch (IOException ex) {
                    System.out.println("Failed to send socket update: " + ex.getMessage());
                }

                JOptionPane.showMessageDialog(this, "Order " + order.orderId + " updated to " + newStatus);
                fetchOrders();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update order. Response: " + responseCode);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating order: " + e.getMessage());
        }
    }


    class OrderData {
        int orderId;
        String items;
        String status;

        public OrderData(int orderId, String items, String status) {
            this.orderId = orderId;
            this.items = items;
            this.status = status;
        }
    }

    class StatusCellRenderer extends DefaultTableCellRenderer {
    	private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = table.getValueAt(row, 2).toString();

            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? new Color(245, 245, 245) : Color.WHITE);
            }

            if (column == 2) {
                switch (status.toLowerCase()) {
                    case "pending":
                        c.setBackground(new Color(255, 250, 200));
                        break;
                    case "preparing":
                        c.setBackground(new Color(200, 230, 255));
                        break;
                    case "completed":
                        c.setBackground(new Color(200, 255, 200));
                        break;
                    default:
                        c.setBackground(Color.white);
                }
            }

            return c;
        }
    }

    class StatusCellEditor extends DefaultCellEditor {
    	private static final long serialVersionUID = 1L;

        private JComboBox<String> comboBox;

        public StatusCellEditor() {
            super(new JComboBox<String>());
            comboBox = (JComboBox<String>) getComponent();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            comboBox.removeAllItems();
            comboBox.setEnabled(true);
            String currentStatus = table.getValueAt(row, 2).toString();

            switch (currentStatus.toLowerCase()) {
                case "pending":
                    comboBox.addItem("Preparing");
                    comboBox.addItem("Completed");
                    break;
                case "preparing":
                    comboBox.addItem("Completed");
                    break;
                case "completed":
                    comboBox.addItem("Completed");
                    comboBox.setEnabled(false);
                    break;
            }

            return comboBox;
        }

        @Override
        public Object getCellEditorValue() {
            return comboBox.getSelectedItem();
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
    	private static final long serialVersionUID = 1L;

        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
    	private static final long serialVersionUID = 1L;

        private JButton button;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> updateOrderStatus(row));
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.row = row;
            button.setText((value == null) ? "" : value.toString());
            return button;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Kitchen Dashboard");
            frame.getContentPane().setBackground(Color.decode("#FFE4C4"));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 500);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new KitchenPanel());
            frame.setVisible(true);
        });
    }
}
