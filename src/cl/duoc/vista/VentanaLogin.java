package cl.duoc.vista;

import cl.duoc.controlador.AuthControlador;

import javax.swing.*;
import java.awt.*;

public class VentanaLogin extends JFrame {
    private final AuthControlador auth = new AuthControlador();
    private final JTextField txtCorreo = new JTextField(20);
    private final JPasswordField txtPassword = new JPasswordField(20);
    private final JButton btnIngresar = new JButton("Ingresar");

    public VentanaLogin() {
        setTitle("Biblioteca Escolar - Iniciar sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JLabel titulo = new JLabel("Sistema de Gestión de Biblioteca Escolar", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centro.add(titulo, BorderLayout.NORTH);
        centro.add(Ui.campos(new String[]{"Correo:", "Contraseña:"},
                new JComponent[]{txtCorreo, txtPassword}), BorderLayout.CENTER);
        centro.add(btnIngresar, BorderLayout.SOUTH);
        setContentPane(centro);

        getRootPane().setDefaultButton(btnIngresar);   // Enter = Ingresar
        btnIngresar.addActionListener(e -> ingresar());
        pack();
        setLocationRelativeTo(null);
    }

    private void ingresar() {
        String correo = txtCorreo.getText();
        String password = new String(txtPassword.getPassword());
        btnIngresar.setEnabled(false);

        Tareas.ejecutar(() -> auth.iniciarSesion(correo, password), resultado -> {
            btnIngresar.setEnabled(true);
            if (resultado.isPresent()) {
                dispose();
                FabricaVentanas.crear(resultado.get()).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Correo o contraseña incorrectos.",
                        "Acceso denegado", JOptionPane.WARNING_MESSAGE);
                txtPassword.setText("");
            }
        }, ex -> {
            btnIngresar.setEnabled(true);
            Ui.error(this, ex);
        });
    }
}
