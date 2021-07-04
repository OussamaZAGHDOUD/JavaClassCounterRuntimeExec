package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {

	static void doJob() throws IOException {
		System.out.println("hello job im the thread : "+Thread.currentThread().getName());
		Runtime runtime = Runtime.getRuntime();

		// put the Path of your java jar and the Jar file here to be executed  ( JavaClassCalculator in this case )
		final Process process = runtime.exec(
				"C:\\Program Files\\Java\\jdk1.8.0_271\\bin\\java -jar C:\\Users\\OUSSEMA\\Desktop\\Nouveaudossier\\javaClassCalculator2.jar",
				null);

		new Thread() {
			@Override
			public void run() {
				try {
					System.out.println("thread 1 = " + Thread.currentThread().getName());

					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line = "";
					try {
						while ((line = reader.readLine()) != null) {
							System.out.println(line);
						}
					} finally {
						reader.close();
					}
				} catch (Exception ee) {
				}
			}
		}.start();

		new Thread() {
			@Override
			public void run() {
				try {
					System.out.println("thread 2 = " + Thread.currentThread().getName());
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
					String line = "";
					try {
						while ((line = reader.readLine()) != null) {
							System.out.println(line);
						}
						
					} finally {
						reader.close();
					}
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		}.start();

		try {
			process.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			System.out.println("***inside the last try block ***");
			Thread.sleep(2000);
			System.out.println(Thread.currentThread().getName());
			System.out.println("fin");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		process.destroy();
		System.out.println("Fin du prog");
	}

	public static void main(String[] args) {
		new Thread(() -> {
			try {
				doJob();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		new Thread(() -> {
			try {
				doJob();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
	}
}
