package nju.agent;

import nju.agent.msg.BankruptcyMessage;

public class BankruptEventResponseCom extends Component{
	
	private final double aerfa;//逆向影响的衰减系数α
	
	/**
	 * 
	 * @param agent
	 * @param aerfa   逆向影响的衰减系数α
	 */
	BankruptEventResponseCom(FirmAgent agent, double aerfa){
		this.agent = agent;
		this.aerfa = aerfa;
	}
	
	public void handle(BankruptcyMessage msg){
		double diff_c;
		if(msg.isReverse()){
			//是逆向传来的信息，即信息时从下游agent传来
			diff_c =  msg.getValue() * this.aerfa;
		}else{
			//是顺向传来的信息，即从上游传来
			diff_c = msg.getValue();
		}
		
		AssetsCom assetsCom = (AssetsCom) agent.getComponent(AssetsCom.class.getName());
		assetsCom.minusCash(diff_c);
	}

}
