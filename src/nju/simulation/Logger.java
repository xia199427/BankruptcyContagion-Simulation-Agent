package nju.simulation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Logger {
	private static String log_file = "D://multi_agent.out";
	private static FileWriter writer;
	private static boolean isClose = false;
	private static BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
	private static Runnable writeThread = new Runnable(){
		public void run(){
			while(true){
				try {
					if(isClose && queue.isEmpty()){
						writer.close();
						break;
					}
					String str = queue.take();
					writer.write(str);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
		}
	};
	
	public static void start() throws IOException{
		File file = new File(log_file);
		if(!file.exists())
			file.createNewFile();
		
		writer = new FileWriter(file, true);
		
		Thread thread = new Thread(writeThread);
		thread.start();
		log("============================================\r\n");
	}
	
	
	public static synchronized void log(String str){
		String msg = "<" + AgentsWorld.timestep + "> " + str;
		if(isClose)
			return;
		queue.offer(msg);
	}
	
	public static void stop(){
		isClose = true;
		if(queue.isEmpty())
			queue.add("\r\n");
	}

	public static void log_startSimulation(){
		String msg = "-------------Start Simulation-------------\r\n";
		log(msg);
	}
	
	public static void log_endSimulation(){
		String msg = "-------------End Simulation--------------\r\n";
		msg += "===========================================\r\n";
		log(msg);
	}
	
	public static void log_action(String agent_id, String action_msg){
		String msg = "\t[" + agent_id + "_ACTION] : " + action_msg + "\r\n";
		log(msg);
	}
	
	public static void log_bankruptEvent(String agent_id){
		String msg = "\t!!!" + agent_id + " becomes bankrupt!\r\n";
		log(msg);
	}
}
