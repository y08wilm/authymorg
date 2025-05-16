package io.github.y08wilm.authy;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class AppDelegate {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
        long startTime = System.currentTimeMillis();
		new Authy();
		Scanner scanner = new Scanner(System.in);
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String formattedMessage = dateFormat.format(date);
        double endTime = System.currentTimeMillis() - startTime;
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);
        System.out.println("[" + formattedMessage + "] [Server thread/INFO]: Done (" + df.format(endTime / 1000.0) + "s)! For help, type \"help\"");
		while (true) {
			String cmd = scanner.nextLine();
			if (cmd.equals("stop") || cmd.equals("kill")) {
				System.exit(-1);
			} else if (cmd.equals("help")) {
				System.out.println("the only command is \"kill\"");
			} else {
				System.out.println("unrecognized command \""+cmd+"\"");
			}
		}
	}

}
