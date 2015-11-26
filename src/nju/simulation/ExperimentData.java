package nju.simulation;

import java.util.ArrayList;

import MyProject.DotGraph;

import com.mathworks.toolbox.javabuilder.MWArray;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

public class ExperimentData {
	private static ArrayList<SimulationResult> results = new ArrayList<SimulationResult>();
	
	public static void addData(SimulationResult line){
		results.add(line);
	}
	
	public static void clean(){
		results.clear();
	}
	
	public static void showData(){
		SimulationResult[] data = new SimulationResult[results.size()];
		results.toArray(data);
		
		printArray(data);
		
		plot(data);
	}
	
	private static void plot(SimulationResult[] data){
		//get x,y 
		int len = data.length;
		double[] ax = new double[len];
		double[] ay = new double[len];
		
		for(int i = 0 ; i < len ; i++){
			SimulationResult temp = data[i];
			ax[i] = temp.init_B_ratio;
			ay[i] = temp.incre_B_ratio;
		}
		
		MWNumericArray x = new MWNumericArray(ax, MWClassID.DOUBLE);
		MWNumericArray y = new MWNumericArray(ay, MWClassID.DOUBLE);
		
		try {
			DotGraph figure = new DotGraph();
			figure.Plot(x,y);
			figure.waitForFigures();
			
			figure.dispose();
		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			MWArray.disposeArray(x);
			MWArray.disposeArray(y);
			System.out.println("Over!!!!!");
		}
	}
	
	private static void printArray(SimulationResult[] data){
		int len = data.length;
		for(int i = 0 ; i < len ; i++){
			SimulationResult turn = data[i];
			System.out.println(turn.init_B_ratio + " --> " + turn.incre_B_ratio);
		}
	}

}
