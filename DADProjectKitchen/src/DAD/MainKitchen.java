package DAD;

import javax.swing.*;
import java.awt.*;



public class MainKitchen extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                MainKitchen window = new MainKitchen();
                window.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public MainKitchen() {
        setTitle("Kitchen System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        getContentPane().setLayout(new BorderLayout());

        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        JPanel navPanel = new JPanel();
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        navPanel.setPreferredSize(new Dimension(200, getHeight()));
        navPanel.setBackground(new Color(210, 180, 140)); // Light brown

        Font btnFont = new Font("Segoe UI", Font.BOLD, 14);
        Color textColor = Color.WHITE;
        Color bgColor = new Color(160, 82, 45); // Darker brown

        JButton btnKitchen = new JButton("ORDER STATUS");
        JButton btnUpdateMenu = new JButton("LIST OF MENU");
  
        


        JButton[] buttons = {btnKitchen, btnUpdateMenu};
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

        btnKitchen.addActionListener(e -> openKitchenPanel());
        btnUpdateMenu.addActionListener(e -> openUpdateMenuPanel());


        getContentPane().add(navPanel, BorderLayout.WEST);
        

        openKitchenPanel(); 
    }

    private void openKitchenPanel() {
        contentPanel.removeAll();
        contentPanel.add(new KitchenPanel());
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void openUpdateMenuPanel() {
        contentPanel.removeAll();
        contentPanel.add(new UpdateMenu());
        contentPanel.revalidate();
        contentPanel.repaint();
    }

}
