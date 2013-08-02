package adamatti;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class IOUtils {
	public static void waitEnter(String msg) throws Exception {
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		System.out.println(msg);
		br.readLine();
		// br.close();
	}

	public static void waitEnter() throws Exception {
		waitEnter("Press Enter to exit");
	}
}
