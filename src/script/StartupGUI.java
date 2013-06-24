package script;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class StartupGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1537822869852505595L;
	private JPanel contentPane;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StartupGUI frame = new StartupGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public StartupGUI() {
		setTitle("Thock's Cooker");
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblChooseFoodTo = new JLabel("Type in name of food (e.g. Shark) but do not include the word \"raw\"");
		lblChooseFoodTo.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblChooseFoodTo.setBounds(27, 60, 385, 14);
		contentPane.add(lblChooseFoodTo);
		
		textField = new JTextField();
		textField.setBounds(164, 85, 86, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("Start");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CookingScript.FOOD_NAME = textField.getText().trim();
				dispose();
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnNewButton.setBounds(10, 212, 414, 39);
		contentPane.add(btnNewButton);
	}
}
