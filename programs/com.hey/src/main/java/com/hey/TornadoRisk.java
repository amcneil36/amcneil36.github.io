package main.java.com.hey;

public class TornadoRisk extends CityStats {

	@Override
	protected void updateData(Data data, String stateName) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) throws Exception {
		CityStats s = new TornadoRisk();
		s.processAllStates();

	}

}
