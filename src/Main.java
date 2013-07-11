import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Main implements ActionListener {
	JTextArea textArea;
	JFrame frame;
	ArrayList<String> puushes;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			final Main main = new Main();

			@Override
			public void run() {
				main.inigui();
			}
		});
	}

	public void inigui() {

		frame = new JFrame("Puush Saver");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(320, 174));

		textArea = new JTextArea(8, 27);
		textArea.setEditable(false);
		JScrollPane scrollpane = new JScrollPane(textArea);
		scrollpane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(scrollpane);

		JButton readPuushesButton = new JButton();
		readPuushesButton.addActionListener(this);
		readPuushesButton.setText("Read puushes from file");
		panel.add(readPuushesButton);

		JButton accessPuushesButton = new JButton();
		accessPuushesButton.addActionListener(this);
		accessPuushesButton.setText("Access puushes");

		panel.add(accessPuushesButton);

		frame.add(panel);
		frame.setResizable(false);
		frame.setLocation(100, 100);
		frame.pack();
		frame.setVisible(true);

		readPuushes();
	}

	private void readPuushes() {
		File f = new File("puush.txt");
		puushes = new ArrayList<>();
		int n = 0;
		try {
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String s;
			while ((s = br.readLine()) != null) {
				if (!s.startsWith("#") && !(s.trim().length() ==0)) {
					puushes.add(s);
					n++;
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			JOptionPane
					.showMessageDialog(
							frame,
							"<html><body><p style='width: 200px;'> Unable to find 'puush.txt' in directory, perhaps you moved the .jar file without also copying the puush.txt file? Exiting.");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame,
					"Unable to read file 'puush.txt', exiting.");
			System.exit(0);
		}
		textArea.append("Successfully read " + n + " puush links from file.\n");
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "Read puushes from file":
			readPuushes();
			break;
		case "Access puushes":
			new Thread(new Runnable() {
				@Override
				public void run() {
					accessPuushes();
				}
			}).start();
			break;
		default:
			break;
		}
	}

	private void accessPuushes() {
		String result = null;
		for (String puushUrl : puushes) {
			final String puushUrl2 = puushUrl;
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					textArea.append(puushUrl2 + ": ");
				}
			});
			try {
				URL url = new URL(puushUrl);
				URLConnection urlconn = url.openConnection();
				InputStream stream = urlconn.getInputStream();
				Scanner s = new Scanner(stream);
				while (s.hasNext()) {
					s.nextLine();
				}
				result = "Success";
				s.close();
			} catch (MalformedURLException e) {
				result = "Failed - Invalid URL";
				e.printStackTrace();
			} catch (IOException e) {
				result = "Failed - Unable to access URL";
				e.printStackTrace();
			} finally {
				final String result2 = result;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						textArea.append(result2 + "\n");
						textArea.setCaretPosition(textArea.getDocument()
								.getLength());

					}
				});
			}

		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				textArea.append("Finished" + "\n");
				textArea.setCaretPosition(textArea.getDocument().getLength());

			}
		});
	}

}