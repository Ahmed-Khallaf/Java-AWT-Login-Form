import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

class User {
    private final String username;
    private final String password;
    private final String role;

    User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}

class WelcomePage extends Frame {
    WelcomePage(User user) {
        super("Welcome");

        setLayout(new GridLayout(2, 1, 8, 8));
        add(new Label("Hello, " + user.getUsername(), Label.CENTER));
        add(new Label("Role: " + user.getRole(), Label.CENTER));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setSize(320, 160);
        setLocationRelativeTo(null);
    }
}

public class LoginForm extends Frame {
    private final List<User> users = new ArrayList<>();
    private int failedPasswordAttempts = 0;
    private static final int MAX_FAILED_PASSWORD_ATTEMPTS = 3;

    public LoginForm() {
        super("Login");
        loadUsers();

        setLayout(new GridBagLayout());
        Panel content = new Panel(new BorderLayout(10, 15));

        Label title = new Label("Welcome back", Label.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        content.add(title, BorderLayout.NORTH);

        Panel form = new Panel(new GridLayout(3, 2, 8, 8));

        form.add(new Label("Username:", Label.RIGHT));
        TextField username = new TextField(18);
        form.add(username);

        form.add(new Label("Password:", Label.RIGHT));
        TextField password = new TextField(18);
        password.setEchoChar('*');
        form.add(password);

        form.add(new Label("Role:", Label.RIGHT));
        Choice role = new Choice();
        role.add("Student");
        role.add("TA");
        role.add("Professor");
        form.add(role);

        content.add(form, BorderLayout.CENTER);

        Panel buttons = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        Button cancel = new Button("Cancel");
        Button login = new Button("Login");
        buttons.add(cancel);
        buttons.add(login);
        content.add(buttons, BorderLayout.SOUTH);

        add(content);

        login.addActionListener(e -> {
            String enteredUsername = username.getText().trim();
            String enteredPassword = password.getText();
            String selectedRole = role.getSelectedItem();

            if (enteredUsername.isEmpty() || enteredPassword.trim().isEmpty()) {
                showMessage("Please enter both username and password.");
                return;
            }

            User user = findUser(enteredUsername);
            if (user == null) {
                showMessage("Invalid username or password.");
                return;
            }

            if (!user.getPassword().equals(enteredPassword)) {
                failedPasswordAttempts++;
                int attemptsLeft = MAX_FAILED_PASSWORD_ATTEMPTS - failedPasswordAttempts;

                if (attemptsLeft <= 0) {
                    login.setEnabled(false);
                    showMessage("Too many incorrect password attempts. Login is disabled.");
                } else {
                    showMessage("Incorrect password. Attempts remaining: " + attemptsLeft);
                }
                return;
            }

            if (!user.getRole().equals(selectedRole)) {
                showMessage("The selected role does not match this account.");
                return;
            }

            failedPasswordAttempts = 0;
            showMessage("Login successful!");
            new WelcomePage(user).setVisible(true);
            dispose();
        });

        cancel.addActionListener(e -> dispose());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setSize(420, 340);
        setLocationRelativeTo(null);
    }

    private void showMessage(String message) {
        Dialog dialog = new Dialog(this, "Login Message", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.add(new Label(message, Label.CENTER), BorderLayout.CENTER);

        Button ok = new Button("OK");
        ok.addActionListener(e -> dialog.dispose());
        Panel buttonPanel = new Panel();
        buttonPanel.add(ok);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(330, 130);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void loadUsers() {
        users.add(new User("ahmed", "student123", "Student"));
        users.add(new User("eman", "ta12345", "TA"));
        users.add(new User("akrm", "professor123", "Professor"));
    }

    private User findUser(String name) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(name)) {
                return user;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
