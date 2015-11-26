package nju.agent;

import java.util.ArrayList;
import java.util.HashMap;

public class AgentRelations {
	private double[][] graphs;//这里的下标对应agent在agents列表里的index.
	private FirmAgent[] agents;//agents列表
	
	//记录agent id与index之间的对应关系
	private HashMap<String, Integer> id_table = new HashMap<String, Integer>();
	
	private static AgentRelations relations;
	private AgentRelations(){
		id_table.clear();
	}
	
	public static AgentRelations getInstance(){
		if(relations == null)
			relations = new AgentRelations();
		return relations;
	}
	
	/**
	 * 记录agent列表及其之间的关系，类功能做准备。
	 * @param agentsRelations
	 * @param agents
	 */
	public void initRelations(double[][] agentsRelations, FirmAgent[] agents){
		this.graphs = agentsRelations;
		
		for(int i = 0 ; i < agents.length ; i++){
			FirmAgent agent = agents[i];
			id_table.put(agent.getID(), i);
		}
		
		this.agents = agents;
	}
	
	
	public FirmAgent[] getUpstreamAgents(int index){
		int len = agents.length;
		ArrayList<FirmAgent> list = new ArrayList<FirmAgent>();
		for(int i = 0 ; i < len ; i++){
			if(graphs[i][index] > 0){
				list.add(agents[i]);
			}
		}
		
		FirmAgent[] results = new FirmAgent[list.size()];
		list.toArray(results);
		
		return results;
	}
	
	public FirmAgent[] getUpstreamAgents(String agent_id){
		int agent_index = id_table.get(agent_id);
		return getUpstreamAgents(agent_index);
	}
	
	public FirmAgent[] getDownstreamAgents(int index){
		int len = agents.length;
		ArrayList<FirmAgent> list = new ArrayList<FirmAgent>();
		
		for(int i = 0 ; i < len ; i++){
			if(graphs[index][i] > 0){
				list.add(agents[i]);
			}
		}
		
		FirmAgent[] results = new FirmAgent[list.size()];
		list.toArray(results);
		
		return results;
	}
	
	public FirmAgent[] getDownstreamAgents(String agent_id){
		int agent_index = id_table.get(agent_id);
		return getDownstreamAgents(agent_index);
	}
	
	
	public double getValue(int i , int j){
		return graphs[i][j];
	}
	
	public double getValue(String i_id, String j_id){
		int index_i = id_table.get(i_id);
		int index_j = id_table.get(j_id);
		
		return graphs[index_i][index_j];
	}

}
