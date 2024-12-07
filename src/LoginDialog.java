import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginDialog extends JDialog {
    private String username;
    private String password;

    public LoginDialog(JFrame parent) {
        super(parent, "Login", true);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Username:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(usernameLabel, constraints);

        JTextField usernameField = new JTextField(15);
        constraints.gridx = 1;
        panel.add(usernameField, constraints);

        JLabel passwordLabel = new JLabel("Password:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(passwordLabel, constraints);

        JPasswordField passwordField = new JPasswordField(15);
        constraints.gridx = 1;
        panel.add(passwordField, constraints);

        JButton loginButton = new JButton("Login");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, constraints);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = usernameField.getText();
                password = new String(passwordField.getPassword());
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginDialog.this,
                            "Username and password must not be empty.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    dispose();
                }
            }
        });

        setContentPane(panel);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}