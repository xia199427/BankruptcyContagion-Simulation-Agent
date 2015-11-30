package nju.simulation;

import java.io.IOException;
import java.util.ArrayList;
import nju.agent.AgentRelations;
import nju.agent.FirmAgent;

public class AgentsWorldSimpleTest extends AgentsWorld{
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
		timestep = 0;
		while(turnbefore_bankruptNums != AgentsWorld.bankruptNum){
			turnbefore_bankruptNums = AgentsWorld.bankruptNum;
			timestep++;
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
		
		timestep = 0;
		
		System.out.println("一次模拟结束");
		
	}
	
	private ArrayList<Integer> selectDistinctNums(final int n, final int len){
		if(n == 0)
			return null;
		if(n == len){
			ArrayList<Integer> temp = new ArrayList<Integer>();
			for(int i = 0 ; i < len; i ++){
				temp.add(i);
			}
			
			return temp;
		}
		
		int count = n;
		ArrayList<Integer> list = new ArrayList<Integer>();
		while(count > 0){
			//初始化破产节点
			int d = (int) Math.floor( Math.random() * len );
			if(d == len)
				d = len-1;
			
			if(!list.contains(d)){
				list.add(d);
				count--;
			}
		}
		
		
		return list;
	}
	
	public void initBankruptcySource(final int n){
		int len = agents.length;
		if(n > len){
			throw new OutboundException();
		}
		
		if(n < len/2){
			ArrayList<Integer> indexes = this.selectDistinctNums(n, len);
			if(indexes == null)
				return;
			for(int i = 0; i < indexes.size() ;  i++){
				int index = indexes.get(i);
				agents[index].setBankruptcy();
			}
		}else{
			int exclude_nums = len - n;
			ArrayList<Integer> list = this.selectDistinctNums(exclude_nums, len);
			for(int i = 0 ; i < len ; i++){
				if(!list.contains(i)){
					agents[i].setBankruptcy();
				}
			}
		}
		
		AgentsWorld.bankruptNum = n;//与设置多少个破产传染源有关
	}
	
	
	public void simulate(){
		int init_B_num = 1;
		ExperimentData.clean();
		try {
			Logger.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int k = 0 ; k < 4 ; k++){
			final int counts = 100;
			double[] incres = new double[counts];
			double init_R_ratio = 0;
			
			for(int i = 0 ; i < counts ; i  ++){
				init();
				if(init_B_num > agents.length)
					break;
				
				Logger.log_startSimulation();
				
				initBankruptcySource(init_B_num);
				
				init_R_ratio = this.calBankruptRatio();
				
				startSimulation();
				
				Logger.log_endSimulation();
				
				double final_R_ratio = this.calBankruptRatio();
				double incre_R_ratio = final_R_ratio - init_R_ratio;
				
				incres[i] = incre_R_ratio;
			}
			//we have 1 init_R_ratio and 10 incre_R_ratio now
			double incre_avg = this.calAverage(incres);
			SimulationResult line = new SimulationResult(init_R_ratio, incre_avg);
			ExperimentData.addData(line);
			
			//初始破产agent数增加；
			init_B_num++;
		}
		
		Logger.stop();
		//用图显示结果。
		
		ExperimentData.showData();
		//plot(data);
	}
	
	
	
	private double calBankruptRatio(){
		int len = agents.length;
		double ratio = ((double) AgentsWorld.bankruptNum ) / len;
		return ratio;
	}
	
	private double calAverage(double[] data){
		double temp = 0 ;
		int len = data.length;
		for(int i = 0 ; i < len ; i++){
			temp += data[i];
		}
		
		return temp/len;
	}

}
