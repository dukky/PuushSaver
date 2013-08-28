package im.duk.puushsaver;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.SwingUtilities;

import com.github.verbalexpressions.VerbalExpression;

public class PuushClipboardListener extends Thread {
	private VerbalExpression puushRegex;
	private Main ref;

	public PuushClipboardListener(Main ref) {
		this.puushRegex = new VerbalExpression().startOfLine().then("http").maybe("s").then("://").maybe("www.")
				.then("puu.sh/").add("[0-9A-Za-z.]+").endOfLine();
		this.ref = ref;
	}

	@Override
	public void run() {
		String s1 = getClipboard(), s2 = "";
		try {
			while (true) {
				s2 = getClipboard();
				if (!s2.equals(s1)) {
					if (puushRegex.testExact(s2)) {
						if (!ref.puushes.contains(s2)) {
							s1 = s2;
							System.out.println("New puush link detected " + s2);
							try {
								PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("puush.txt", true)));
								out.println();
								out.print(s2);
								out.close();
								final String s3 = s2;
								SwingUtilities.invokeLater(new Runnable() {
									@Override
									public void run() {
										ref.textArea.append("Added new puush link: " + s3 + "\n");
										ref.textArea.setCaretPosition(ref.textArea.getDocument().getLength());
									}
								});
								ref.readPuushes();
							} catch (IOException e) {

							}
						}
					}
				}
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {

		}
	}

	private static String getClipboard() {
		try {
			return Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
		} catch (HeadlessException | UnsupportedFlavorException | IOException e) {
			return "";
		}
	}
}
