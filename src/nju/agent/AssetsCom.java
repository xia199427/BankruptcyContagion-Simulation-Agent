package nju.agent;

import nju.simulation.Logger;


public class AssetsCom extends Component{
	private final double origin_c;
	private double c;//agent的初始资产C
	
	private final double e;//agent周期回复的最小值ε
	private final double k;//agent回复速率指标k
	/**
	 * 
	 * @param agent
	 * @param c   	  agent的资产值
	 * @param e		  agent周期回复的最小值ε
	 * @param k		  agent回复速率指标k
	 */
	AssetsCom(FirmAgent agent, double c, double e, double k){
		this.agent = agent;
		this.c = c;
		this.origin_c = c;
		
		this.e = e;
		this.k = k;
	}
	
	public void addCash(double cash){
		this.c += cash;
		Logger.log_action(agent.getID(), cash +" Cash Added, now we have: " + this.c);
	}
	
	public void minusCash(double cash){
		this.c -= cash;
		Logger.log_action(agent.getID(), cash + " substracted, now we have: " + this.c);
	}
	
	public double getCurrent_C(){
		return c;
	}
	//自动回复
	public void autoRecovery(){
		//只有c <= origin_c 时，才会自动回复。
		if(c > origin_c)
			return;
		double temp_incre = (origin_c - c) / k;
		if(temp_incre > e){
			this.c += temp_incre;
			Logger.log_action(agent.getID(), "auto recovery: " + temp_incre + ", now we have " + this.c);
		}else{
			this.c += e;
			Logger.log_action(agent.getID(), "auto recovery: " + e + ", now we hava " + this.c);
		}
		
	}
}
