package DAD;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;

public class PaymentPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable table;
    private JLabel lblTotal;
    private JButton btnConfirm;
    private double grandTotal;
    private JFrame parentFrame;

    public PaymentPanel(JFrame parentFrame, List<FoodSystem.CartItem> cartItems, double total) {
        this.parentFrame = parentFrame;
        this.grandTotal = total;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(255, 239, 213));
        Font tableFont = new Font("Segoe UI", Font.PLAIN, 13);

        // Payment method dropdown
        JPanel paymentMethodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        paymentMethodPanel.setBackground(new Color(255, 239, 213));
        JLabel paymentLabel = new JLabel("Payment Method:");
        paymentLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        paymentLabel.setForeground(Color.BLACK);
        String[] paymentOptions = {"Cash", "Credit/Debit Card", "E-Wallet"};
        JComboBox<String> paymentDropdown = new JComboBox<>(paymentOptions);
        paymentDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        paymentDropdown.setPreferredSize(new Dimension(150, 25));
        paymentMethodPanel.add(paymentLabel);
        paymentMethodPanel.add(paymentDropdown);
        add(paymentMethodPanel, BorderLayout.NORTH);

        // Table
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Item", "Price (RM)", "Quantity", "Subtotal (RM)"}, 0);
        table = new JTable(model);
        table.setFont(tableFont);
        table.setRowHeight(24);
        table.setEnabled(false);
        table.setBackground(new Color(255, 250, 240));
        table.setForeground(Color.BLACK);
        table.getTableHeader().setBackground(new Color(210, 180, 140));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.decode("#FFFAF0"));
        add(scrollPane, BorderLayout.CENTER);

        for (FoodSystem.CartItem item : cartItems) {
            double subtotal = item.getPrice() * item.getQuantity();
            model.addRow(new Object[]{
                    item.getName(),
                    String.format("%.2f", item.getPrice()),
                    item.getQuantity(),
                    String.format("%.2f", subtotal)
            });
        }

        // Bottom
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(255, 239, 213));
        lblTotal = new JLabel("Total: RM " + String.format("%.2f", grandTotal));
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal.setForeground(Color.BLACK);
        bottomPanel.add(lblTotal, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(255, 239, 213));

        // Back
        JButton btnBack = createStyledButton("Back");
        btnBack.addActionListener(e -> {
            parentFrame.getContentPane().removeAll();
            parentFrame.getContentPane().add(new CartPanel(parentFrame, cartItems, () -> {
                parentFrame.getContentPane().removeAll();
                parentFrame.revalidate();
                parentFrame.repaint();
            }));
            parentFrame.revalidate();
            parentFrame.repaint();
        });
        buttonPanel.add(btnBack);

        // Confirm
        btnConfirm = createStyledButton("✔ Confirm Order");
        btnConfirm.addActionListener(e -> {
            String selectedMethod = (String) paymentDropdown.getSelectedItem();

            try {
                // STEP 1: Insert cart (order)
                JSONObject orderData = new JSONObject();
                orderData.put("action", "insert_cart");
                JSONArray itemsArray = new JSONArray();

                for (FoodSystem.CartItem item : cartItems) {
                    JSONObject obj = new JSONObject();
                    obj.put("item_id", item.getId()); // Make sure getId() returns menu_items.id
                    obj.put("quantity", item.getQuantity());
                    itemsArray.put(obj);
                }
                orderData.put("items", itemsArray);

                URL orderUrl = new URL("http://localhost/api.php");
                HttpURLConnection orderConn = (HttpURLConnection) orderUrl.openConnection();
                orderConn.setRequestMethod("POST");
                orderConn.setRequestProperty("Content-Type", "application/json");
                orderConn.setDoOutput(true);

                OutputStream orderOs = orderConn.getOutputStream();
                orderOs.write(orderData.toString().getBytes());
                orderOs.flush();
                orderOs.close();

                BufferedReader orderReader = new BufferedReader(new InputStreamReader(orderConn.getInputStream()));
                StringBuilder orderResponse = new StringBuilder();
                String orderLine;
                while ((orderLine = orderReader.readLine()) != null) {
                    orderResponse.append(orderLine);
                }
                orderReader.close();

                JSONObject orderResult = new JSONObject(orderResponse.toString());

                if (!orderResult.has("order_id")) {
                    JOptionPane.showMessageDialog(this, "❌ Failed to place order.");
                    return;
                }

                int orderId = orderResult.getInt("order_id");

                // STEP 2: Make payment
                JSONObject paymentData = new JSONObject();
                paymentData.put("action", "make_payment");
                paymentData.put("order_id", orderId);
                paymentData.put("payment_method", selectedMethod);
                paymentData.put("amount", grandTotal);

                URL payUrl = new URL("http://localhost/api.php");
                HttpURLConnection payConn = (HttpURLConnection) payUrl.openConnection();
                payConn.setRequestMethod("POST");
                payConn.setRequestProperty("Content-Type", "application/json");
                payConn.setDoOutput(true);

                OutputStream payOs = payConn.getOutputStream();
                payOs.write(paymentData.toString().getBytes());
                payOs.flush();
                payOs.close();

                int payResponse = payConn.getResponseCode();
                if (payResponse == HttpURLConnection.HTTP_OK) {
                    JOptionPane.showMessageDialog(this,
                            "✅ Order & Payment saved successfully.\nThank you!",
                            "Order Success", JOptionPane.INFORMATION_MESSAGE);
                    cartItems.clear();
                    if (parentFrame instanceof Main) {
                        ((Main) parentFrame).openFoodSystem();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Payment failed.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
            }
        });
        buttonPanel.add(btnConfirm);

        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(new Color(222, 184, 135));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(160, 35));
        return button;
    }
}
