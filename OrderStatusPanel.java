package DAD;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import org.json.*;

public class OrderStatusPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JPanel orderListPanel;

    public OrderStatusPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 255));

        JLabel titleLabel = new JLabel("Order Status Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(20, 20, 10, 0));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(255, 239, 213));
        topPanel.add(titleLabel, BorderLayout.WEST);
      

        orderListPanel = new JPanel();
        orderListPanel.setLayout(new BoxLayout(orderListPanel, BoxLayout.Y_AXIS));
        orderListPanel.setBackground(new Color(255, 239, 213));

        JScrollPane scrollPane = new JScrollPane(orderListPanel);
        scrollPane.setBorder(null);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        fetchAndDisplayOrders();
        startSocketListener();
    }

    private void fetchAndDisplayOrders() {
        orderListPanel.removeAll();
        try {
        	URI uri = URI.create("http://localhost/api.php?action=get_orders");
        	URL url = uri.toURL();

            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder jsonStr = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) jsonStr.append(line);
            reader.close();

            JSONArray orders = new JSONArray(jsonStr.toString());

            for (int i = 0; i < orders.length(); i++) {
                JSONObject obj = orders.getJSONObject(i);
                addOrderCard(obj);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to fetch orders: " + e.getMessage());
        }
        revalidate();
        repaint();
    }

    private void addOrderCard(JSONObject obj) {
        try {
            int orderId = obj.getInt("order_id");
            String status = obj.getString("status");
            JSONArray itemsArray = obj.getJSONArray("items");

            StringBuilder itemsText = new StringBuilder("<html>");
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject item = itemsArray.getJSONObject(i);
                itemsText.append(item.getString("name"))
                         .append(" x").append(item.getInt("quantity"))
                         .append("<br>");
            }
            itemsText.append("</html>");

            JPanel card = new JPanel(new BorderLayout(10, 10));
            card.setBackground(Color.WHITE);
            card.setBorder(new CompoundBorder(
                new EmptyBorder(10, 15, 10, 15),
                new MatteBorder(0, 0, 1, 0, new Color(220, 220, 220))
            ));

            JLabel idLabel = new JLabel("Order #" + orderId);
            idLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

            JLabel itemLabel = new JLabel(itemsText.toString());
            itemLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            JLabel statusLabel = new JLabel("Status: " + status);
            statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            statusLabel.setForeground(getStatusColor(status));

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setBackground(Color.WHITE);
            textPanel.add(idLabel);
            textPanel.add(Box.createVerticalStrut(5));
            textPanel.add(itemLabel);
            textPanel.add(Box.createVerticalStrut(5));
            textPanel.add(statusLabel);

            card.add(textPanel, BorderLayout.CENTER);
            orderListPanel.add(card);

        } catch (JSONException e) {
            System.err.println("Invalid order JSON: " + obj.toString());
        }
    }

    private void startSocketListener() {
        new Thread(() -> {
            try (Socket socket = new Socket("localhost", 12345);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                System.out.println("Connected to socket server.");
                String line;
                while ((line = reader.readLine()) != null) {
                    final String jsonStr = line.trim();
                    if (!jsonStr.isEmpty()) {
                        System.out.println("Received: " + jsonStr);
                        SwingUtilities.invokeLater(() -> {
                            try {
                                JSONObject obj = new JSONObject(jsonStr);
                                addOrderCard(obj);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }

            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        "Socket connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
                e.printStackTrace();
            }
        }).start();
    }

    private Color getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "pending": return new Color(255, 200, 0);
            case "preparing": return new Color(30, 144, 255);
            case "completed": return new Color(0, 128, 0);
            default: return Color.GRAY;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Order Status Panel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new OrderStatusPanel());
            frame.setVisible(true);
        });
    }
}
