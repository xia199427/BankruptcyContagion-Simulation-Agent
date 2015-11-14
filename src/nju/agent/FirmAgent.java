package nju.agent;

import java.util.HashMap;
import java.util.Stack;

import nju.agent.msg.*;

public class FirmAgent {
	private String id;
	
	private BankruptEventResponseCom beRespCom;
	private AssetsCom assetsCom;
	private BankruptDetectCom brdCom;
	
	private boolean isBankruptcy = false;//是否破产
	Stack<AbstractMessage> msg_queue = new Stack<AbstractMessage>();
	HashMap<String,Component> register_map = new HashMap<String, Component>();
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
	public void bankruptAction(){
		this.isBankruptcy = true;
		//TODO  向周围的agent 发送破产信息。
		
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
	
	boolean isBankruptcy(){
		return isBankruptcy;
	}

}
