package nju.agent;

import java.util.HashMap;
import java.util.Stack;

import nju.agent.msg.*;
import nju.simulation.AgentsWorld;
import nju.simulation.Logger;

public class FirmAgent {
	private String id;
	
	private BankruptEventResponseCom beRespCom;
	private AssetsCom assetsCom;
	private BankruptDetectCom brdCom;
	
	private boolean isBankruptcy = false;//是否破产
	private Stack<AbstractMessage> msg_queue = new Stack<AbstractMessage>();
	private HashMap<String,Component> register_map = new HashMap<String, Component>();
	/**
	 * 
	 * @param id，	agent的id作为识别依据
	 * @param u，	agent的破产阈值U	 
	 * @param aerfa	逆向影响的衰减系数α
	 * @param c，	agent的初始资产C
	 * @param e，	agent周期回复的最小值ε
	 * @param k，	agent回复速率指标k
	 */
	public FirmAgent(String id, double u, double aerfa, double c, double e, double k){
		this.id = id;
		
		beRespCom = new BankruptEventResponseCom(this, aerfa);
		assetsCom = new AssetsCom(this, c, e, k);
		brdCom = new BankruptDetectCom(this, u);
		
		register_map.put(beRespCom.getClass().getName(), beRespCom);
		register_map.put(assetsCom.getClass().getName(), assetsCom);
		register_map.put(brdCom.getClass().getName(), brdCom);
	}
	
	//仅在模拟初始时调用，设置破产传染源；
	public void setBankruptcy(){
		bankruptAction();
	}
	
	public String getID(){
		return this.id;
	}
	
	//给agent传递信息的接口
	public void informAgent(AbstractMessage message){
		msg_queue.push(message);
	}
	
	//自身破产后的行为——对周围agents 采取的动作。
	void bankruptAction(){
		Logger.log_bankruptEvent(this.getID());
		this.isBankruptcy = true;
		//记录
		AgentsWorld.bankruptNum++;
		//TODO  向周围的agent 发送破产信息。
		AgentRelations agentRelations = AgentRelations.getInstance();
		
		//往下游传递影响; 顺向
		FirmAgent[] downstreamAgents = agentRelations.getDownstreamAgents(this.id);
		int d_len = downstreamAgents.length;
		for(int i = 0 ; i < d_len ; i++){
			FirmAgent agent = downstreamAgents[i];
			if(!agent.isBankruptcy()){
				double value = agentRelations.getValue(this.id, agent.getID());
				BankruptcyMessage msg = new BankruptcyMessage(this, value, false);
				agent.informAgent(msg);
			}
		}
		
		//往上游传递影响； 逆向
		FirmAgent[] upstreamAgents = agentRelations.getUpstreamAgents(this.id);
		int u_len = upstreamAgents.length;
		for(int i = 0 ; i < u_len ; i++){
			FirmAgent agent = upstreamAgents[i];
			if(!agent.isBankruptcy()){
				double value = agentRelations.getValue(agent.getID(), this.id);
				BankruptcyMessage msg = new BankruptcyMessage(this, value, true);
				agent.informAgent(msg);
			}
		}
		
	}
	
	
	//思考， 每个时间周期调用的agent 处理/思考的行为。
	public void thinking(){
		//“破产事件响应”组件 发挥作用；
		while(!msg_queue.empty()){
			AbstractMessage message = msg_queue.remove(0);
			if(message instanceof BankruptcyMessage){
				this.beRespCom.handle((BankruptcyMessage)message);
			}
		}
		
		//检测agent是否破产；
		this.brdCom.execute();
		
		//如果未破产，则资产有一定的周期回复；
		if(!isBankruptcy()){
			this.assetsCom.autoRecovery();
		}
	}
	
	//获得组件
	Component getComponent(String key){
		return register_map.get(key);
	}
	
	public boolean isBankruptcy(){
		return isBankruptcy;
	}

}
