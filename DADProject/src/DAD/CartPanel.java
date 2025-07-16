package DAD;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class CartPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JFrame parentFrame;

    public CartPanel(JFrame parentFrame, List<FoodSystem.CartItem> cartItems, Runnable onCancel) {
        this.parentFrame = parentFrame;
        setBackground(new Color(255, 239, 213));
        render(cartItems, onCancel, updatedList -> {
            removeAll();
            revalidate();
            repaint();
            add(new CartPanel(parentFrame, updatedList, onCancel));
        });
    }

    private void render(List<FoodSystem.CartItem> cartItems, Runnable onCancel, Consumer<List<FoodSystem.CartItem>> onItemRemoved) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Your Cart Summary");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(new Color(255, 250, 240));

        final double[] total = {0};
        for (FoodSystem.CartItem item : cartItems) {
            double subtotal = item.getPrice() * item.getQuantity();
            total[0] += subtotal;

            JPanel itemCard = new JPanel(new BorderLayout(10, 10));
            itemCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            itemCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            itemCard.setBackground(new Color(245, 245, 245));

            JLabel itemLabel = new JLabel(item.getName() + " x" + item.getQuantity());
            itemLabel.setFont(new Font("Arial", Font.PLAIN, 14));

            JLabel priceLabel = new JLabel("RM " + String.format("%.2f", subtotal));
            priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
            priceLabel.setHorizontalAlignment(SwingConstants.RIGHT);

            JButton removeBtn = new JButton("Remove");
            removeBtn.setFont(new Font("Arial", Font.PLAIN, 12));
            removeBtn.setBackground(new Color(231, 76, 60));
            removeBtn.setForeground(Color.WHITE);
            removeBtn.setFocusPainted(false);
            removeBtn.addActionListener(e -> {
                cartItems.remove(item);
                onItemRemoved.accept(cartItems);
            });

            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            rightPanel.setOpaque(false);
            rightPanel.add(priceLabel);
            rightPanel.add(removeBtn);

            itemCard.add(itemLabel, BorderLayout.WEST);
            itemCard.add(rightPanel, BorderLayout.EAST);

            itemsPanel.add(itemCard);
            itemsPanel.add(Box.createVerticalStrut(5));
        }

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setPreferredSize(new Dimension(400, 250));
        add(scrollPane, BorderLayout.CENTER);

        JLabel totalLabel = new JLabel("Total: RM " + String.format("%.2f", total[0]));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(52, 152, 219));
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(totalLabel, BorderLayout.SOUTH);

        JButton confirm = new JButton("Confirm Order");
        confirm.setBackground(new Color(46, 204, 113));
        confirm.setForeground(Color.WHITE);
        confirm.setFocusPainted(false);
        confirm.setFont(new Font("Arial", Font.BOLD, 14));

        JButton cancel = new JButton("Cancel");
        cancel.setBackground(new Color(231, 76, 60));
        cancel.setForeground(Color.WHITE);
        cancel.setFocusPainted(false);
        cancel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(255, 239, 213));
        buttonPanel.add(confirm);
        buttonPanel.add(cancel);
        add(buttonPanel, BorderLayout.PAGE_END);

        confirm.addActionListener(e -> {
            if (cartItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Your cart is empty!");
                return;
            }

            // Navigate directly to PaymentPanel without sending to kitchen
            JOptionPane.showMessageDialog(this, "Order confirmed.");

            if (parentFrame instanceof Main) {
                ((Main) parentFrame).openPaymentPanel(cartItems, total[0]);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Cannot navigate to PaymentPanel - unexpected parent frame.",
                        "Navigation Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancel.addActionListener(e -> {
            int confirmCancel = JOptionPane.showConfirmDialog(this, "Cancel order?", "Warning", JOptionPane.YES_NO_OPTION);
            if (confirmCancel == JOptionPane.YES_OPTION) {
                cartItems.clear();
                onCancel.run();
            }
        });
    }
}
