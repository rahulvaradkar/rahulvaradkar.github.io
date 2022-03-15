package servlets;
import java.io.*;
import java.lang.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class BWLogger {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		
	public BWLogger(PrintStream so) {
		
		PrintStream myStream = new PrintStream(so) {
		@Override
		public void println(String x) {
			Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			
			super.println(sdf.format(timestamp)+ " " + x);
			}
		public void println(int x) {
			Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			
			super.println(sdf.format(timestamp)+ " " + x);
			}
		};
		System.setOut(myStream);
	}
}