package DAD;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.net.URI;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.Scanner;

public class FoodSystem extends JPanel {
    private static final long serialVersionUID = 1L;
    private Map<Integer, FoodItem> foodItems = new HashMap<>();
    private List<CartItem> cartItems;
    private Runnable onViewCart;

    public FoodSystem(List<CartItem> cartItems) {
        this.cartItems = cartItems;

        setLayout(new BorderLayout());
        setBackground(Color.decode("#FFEFD5"));
        JLabel title = new JLabel("Select Your Items");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JPanel foodPanel = createStyledPanel("Food Menu");
        JPanel beveragePanel = createStyledPanel("Beverage Menu");
        foodPanel.setBackground(Color.decode("#FFFAF0"));
        beveragePanel.setBackground(Color.decode("#FFFAF0"));
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Food", new JScrollPane(foodPanel));
        tabbedPane.addTab("Beverage", new JScrollPane(beveragePanel));
        add(tabbedPane, BorderLayout.CENTER);

        try {
            URL url = URI.create("http://localhost/api.php?action=get_menu").toURL();
            Scanner sc = new Scanner(url.openStream());
            StringBuilder jsonStr = new StringBuilder();
            while (sc.hasNext()) {
                jsonStr.append(sc.nextLine());
            }
            sc.close();

            JSONArray jsonArr = new JSONArray(jsonStr.toString());
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject obj = jsonArr.getJSONObject(i);
                int id = obj.getInt("id");
                String name = obj.getString("name");
                double price = obj.getDouble("price");
                String category = obj.optString("category", "Food").toLowerCase();

                JCheckBox checkbox = new JCheckBox(name + " - RM " + price);
                checkbox.setBackground(Color.decode("#FFFAF0"));
                JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
                row.add(checkbox);
                row.add(new JLabel("Qty:"));
                row.add(qtySpinner);
                row.setBackground(Color.decode("#FFFAF0"));

                foodItems.put(id, new FoodItem(id, name, price, checkbox, qtySpinner));

                if (category.contains("beverage")) {
                    beveragePanel.add(row);
                } else {
                    foodPanel.add(row);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading menu: " + e.getMessage());
        }

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.decode("#FFEFD5"));
        
        Color lightBrown = new Color(222, 184, 135);
        Dimension buttonSize = new Dimension(120, 35);
        JButton addToCartButton = new JButton("Add to Cart");
        JButton viewCartButton = new JButton("\uD83D\uDED2 View Cart");
      
        for (JButton btn : new JButton[]{addToCartButton, viewCartButton}) {
            btn.setBackground(lightBrown);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(buttonSize);
            buttonPanel.add(btn);
        }
        addToCartButton.addActionListener(e -> addToCart());
        viewCartButton.addActionListener(e -> {
            
            if (onViewCart != null) onViewCart.run();
        });

        buttonPanel.add(addToCartButton);
        buttonPanel.add(viewCartButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.decode("#FFFAF0"));
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.add(label);
        panel.add(Box.createVerticalStrut(10));
        return panel;
    }

    private void addToCart() {
        cartItems.clear();
        for (FoodItem item : foodItems.values()) {
            if (item.checkbox.isSelected()) {
                int qty = (int) item.spinner.getValue();
                cartItems.add(new CartItem(item.id, item.name, item.price, qty));
            }
        }
        if (!cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Items added to cart. View cart to confirm.");
        } else {
            JOptionPane.showMessageDialog(this, "No items selected.");
        }
    }

    public void setOnViewCart(Runnable onViewCart) {
        this.onViewCart = onViewCart;
    }

    public static class CartItem {
        private int id;
        private String name;
        private double price;
        private int quantity;

        public CartItem(int id, String name, double price, int qty) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = qty;
        }

        // Getter methods
        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        // Setter methods (optional)
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

    private class FoodItem {
        int id;
        String name;
        double price;
        JCheckBox checkbox;
        JSpinner spinner;

        FoodItem(int id, String name, double price, JCheckBox cb, JSpinner spinner) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.checkbox = cb;
            this.spinner = spinner;
        }
    }
}
