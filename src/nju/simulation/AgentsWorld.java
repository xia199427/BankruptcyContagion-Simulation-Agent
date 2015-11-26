package nju.simulation;

import nju.agent.FirmAgent;

public abstract class AgentsWorld {
	public static int bankruptNum = 0;
	
	protected FirmAgent[] agents;
	
	public void init(){
		bankruptNum = 0;
		initAgents();
		initRelations();
	}
	
	protected abstract void initRelations();
	protected abstract void initAgents();
	
	public abstract void startSimulation();

}
