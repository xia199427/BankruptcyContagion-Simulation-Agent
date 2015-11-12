package nju.agent;


public class BankruptDetectCom extends Component{
	private final double U;//破产阈值U
	
	
	BankruptDetectCom(FirmAgent agent, double u){
		this.agent = agent;
		this.U = u;
	}
	
	public void execute(){
		AssetsCom assetsCom = (AssetsCom) agent.getComponent(AssetsCom.class.getName());
		double cur_c = assetsCom.getCurrent_C();
		if(cur_c < U){
			//判定agent 破产；
			agent.bankruptAction();
		}
		//agent未破产则不做任何动作。
	}

}
