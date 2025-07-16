package DAD;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class Main extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPanel;
    private List<FoodSystem.CartItem> cartItems = new ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Main window = new Main();
                window.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Main() {
        setTitle("Main Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        getContentPane().setLayout(new BorderLayout());

        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        JPanel navPanel = new JPanel();
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        navPanel.setPreferredSize(new Dimension(200, getHeight()));
        navPanel.setBackground(new Color(210, 180, 140)); 

        Font btnFont = new Font("Segoe UI", Font.BOLD, 14);
        Color textColor = Color.WHITE;
        Color bgColor = new Color(160, 82, 45); 

        JButton btnFood = new JButton("FOOD LIST");
        JButton btnCart = new JButton("VIEW CART");
        JButton btnPayment = new JButton("PAYMENT");
        JButton btnStatus = new JButton("TRACK ORDER");

        JButton[] buttons = {btnFood, btnCart, btnPayment, btnStatus};
        for (JButton btn : buttons) {
            btn.setFocusPainted(false);
            btn.setFont(btnFont);
            btn.setForeground(textColor);
            btn.setBackground(bgColor);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            btn.setPreferredSize(new Dimension(200, 70));
            navPanel.add(btn);
        }

        btnFood.addActionListener(e -> openFoodSystem());
        btnCart.addActionListener(e -> openCartPanel());
        btnPayment.addActionListener(e -> {
            double total = calculateTotal();
            openPaymentPanel(cartItems, total);
        });
        btnStatus.addActionListener(e -> openOrderStatusPanel());

        getContentPane().add(navPanel, BorderLayout.WEST);

        openFoodSystem();
    }

    public void openFoodSystem() {
        contentPanel.removeAll();
        FoodSystem foodPanel = new FoodSystem(cartItems);
        foodPanel.setOnViewCart(this::openCartPanel);
        contentPanel.add(foodPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void openCartPanel() {
        contentPanel.removeAll();
        contentPanel.add(new CartPanel(this, cartItems, this::openFoodSystem));
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    public void openPaymentPanel(List<FoodSystem.CartItem> cartItems, double total) {
        contentPanel.removeAll();
        contentPanel.add(new PaymentPanel(this, cartItems, total));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

   
    private void openOrderStatusPanel() {
        contentPanel.removeAll();
        contentPanel.add(new OrderStatusPanel()); 
        contentPanel.revalidate();
        contentPanel.repaint();
    }


    private double calculateTotal() {
        return cartItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }
}
