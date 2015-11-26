package nju.simulation;

import java.util.ArrayList;

import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import nju.agent.AgentRelations;
import nju.agent.FirmAgent;

public class AgentsWorldSimpleTest extends AgentsWorld{
	private ArrayList<SimulationResult> simulationResults = new ArrayList<SimulationResult>();
	
	public static long INTERVAL = 0;
	
	private double[][] getEmptyRelations(int len){
		double[][] relations = new double[len][len];
		for(int i = 0 ; i < len ; i++){
			for( int j = 0 ; j < len ; j++){
				relations[i][j] = 0;
			}
		}
		
		return  relations;
	}

	@Override
	protected void initRelations() {
		// TODO Auto-generated method stub
		int len = agents.length;
		double[][] relations = getEmptyRelations(len);
		
		relations[0][1] = 30;
		relations[1][2] = 10;
		relations[1][3] = 15;
		relations[2][4] = 8;
		relations[3][4] = 5;
		
		AgentRelations.getInstance().initRelations(relations, agents);
	}

	@Override
	protected void initAgents() {
		// TODO Auto-generated method stub 可以从文件读取也可以硬编码
		double u = 1.5;
		double aerfa = 0.6;
		double e = 2;
		double k = 4;
		
		agents = new FirmAgent[5]; 
		agents[0] = new FirmAgent("Agent 0", u, aerfa, 28, e, k);
		agents[1] = new FirmAgent("Agent 1", u, aerfa, 24, e, k);
		agents[2] = new FirmAgent("Agent 2", u, aerfa, 6, e, k);
		agents[3] = new FirmAgent("Agent 3", u, aerfa, 8, e, k);
		agents[4] = new FirmAgent("Agent 4", u, aerfa, 10, e, k);
	}

	/**
	 * 时间周期方面的模拟可能不太准. 因为是先后执行的顺序，前面的agent传出的信息会被后面的agent在当前周期处理，而反之不然。
	 */
	@Override 
	public void startSimulation() {
		// TODO Auto-generated method stub
		
		int turnbefore_bankruptNums = 0;
		while(turnbefore_bankruptNums != AgentsWorld.bankruptNum){
			turnbefore_bankruptNums = AgentsWorld.bankruptNum;
			try {
				Thread.sleep(INTERVAL);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//运行模拟
			for(int i = 0 ; i < agents.length ; i++){
				FirmAgent agent = agents[i];
				if(!agent.isBankruptcy())
					agent.thinking();
			}
			
		}
		
		System.out.println("一次模拟结束");
		
	}
	
	public void initBankruptcySource(){
		//初始化破产节点
		int d = (int) Math.floor( Math.random() * 5 );
		if(d == 5)
			d = 4;
		agents[d].setBankruptcy();
		AgentsWorld.bankruptNum = 1;//与设置多少个破产传染源有关
	}
	
	
	public void simulate(){
		simulationResults.clear();
		
		for(int i = 0 ; i < 100 ; i  ++){
			init();
			initBankruptcySource();
			
			double init_R_ratio = this.calBankruptRatio();
			
			startSimulation();
			int turns = i+1;
			System.out.println("第"+ turns + "次模拟结束");
			double final_R_ratio = this.calBankruptRatio();
			
			SimulationResult result = new SimulationResult(init_R_ratio, final_R_ratio);
			simulationResults.add(result);
		}
		
		//用图显示结果。
		SimulationResult[] data = new SimulationResult[simulationResults.size()];
		simulationResults.toArray(data);
		
		printArray(data);
		
		//plot(data);
	}
	
	private void plot(SimulationResult[] data){
		//get x,y 
		int len = data.length;
		double[] ax = new double[len];
		double[] ay = new double[len];
		
		for(int i = 0 ; i < len ; i++){
			SimulationResult temp = data[i];
			ax[i] = temp.init_B_ratio;
			ay[i] = temp.final_B_ratio;
		}
		
		MWNumericArray x = new MWNumericArray(ax, MWClassID.DOUBLE);
		MWNumericArray y = new MWNumericArray(ay, MWClassID.DOUBLE);
	}
	
	private void printArray(SimulationResult[] data){
		int len = data.length;
		for(int i = 0 ; i < len ; i++){
			SimulationResult turn = data[i];
			System.out.println(turn.init_B_ratio + " --> " + turn.final_B_ratio);
		}
	}
	
	private double calBankruptRatio(){
		int len = agents.length;
		double ratio = ((double) AgentsWorld.bankruptNum ) / len;
		return ratio;
	}

}
