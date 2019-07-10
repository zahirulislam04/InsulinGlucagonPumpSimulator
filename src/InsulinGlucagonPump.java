import javax.annotation.processing.RoundEnvironment;

public class InsulinGlucagonPump implements Runnable {

	private BGLGenerator BGL = new BGLGenerator();
	volatile private static int hbs_correction_factor=50;	//High blood sugar correction factor is 50 for person weight 65Kg
	volatile private static int hbs_insulin_dose;	//calculated insulin dose for high blood sugar
	volatile private static int CHO_factor=50;	//1 unit of insulin can dispose 60gm of carb
	volatile private static int cho_insuline_dose;
	volatile private static int carb_qty; //carb or meal intake in gm
	volatile private static int glucagon_dose;
	volatile private static int glucagon_sensitivity_factor=4; //GSF is 4mg for person weight 65kg 
	volatile private static int insulin_daily_dose_limit=40;	//daily limit is 40 unit
	volatile private int insulin_remaining_dose=40;	//daily limit is 40 unit.. it contains remaining units of insulin for a day
	volatile private static String msgWarning="";
	
	volatile private int currBGL,maxBGL,minBGL;
	volatile boolean insulinpushed = false;
	volatile boolean glucagonpushed = false;
	
	
	public void run() {
		int dose=0,iLimit=0;
		iLimit = getInsulinDailyDoseLimit();
		
		while(true) {
			currBGL=BGL.getCurrentBGL();
			maxBGL= BGL.getMaxSafeBGL();
			minBGL = BGL.getMinSafeBGL();

//			if(getInsulinDailyDoseLimit()-getInsulinRemainingDose()>0) {
				//High Blood Sugar detection and insulin pumping
//				System.out.println(BGL.getTargetBGL());
				if (currBGL>maxBGL) {
					//System.out.println(currBGL);
//					System.out.println(BGL.getTargetBGL());
					setInsulinPushed(false);
					if(!getInsulinPushed()) {
						dose=(currBGL-BGL.getTargetBGL())/getHBSCorrectionFactor();
						
//						if(getInsulinRemainingDose()-dose<0) {
//							setWarningMsg("Insufficent insulin quantity in insulin cartidge. Please refil the cartidge.");
//							System.out.println("low cartidge");
//						}
//						else {
							
							try {
								Thread.currentThread().sleep(3000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
							
//							if(iLimit-dose<0) {
//								System.out.println(iLimit);
								setHBSInsulinDose(dose);
								setInsulinRemainingDose(iLimit-dose);
								setInsulinPushed(true);
								BGL.setStartBGL(BGL.getTargetBGL());//
								BGL.setCurrentBGL(BGL.getTargetBGL());//	
//							}
//							else{
//								setWarningMsg("Low cartidge");
//							}
							
//						}						
					}
				}
				
				
				//Carbohydrate intake detection and push insulin to dispose carbs			
				if(getCarbQTY()!=0) {
					setInsulinPushed(false);
					if(!getInsulinPushed()) {
						dose= getCarbQTY()/getCHOFactor();	
//						if(getInsulinRemainingDose()-dose<0) {
//							
//						}
//						else {
							
							try {
								Thread.currentThread().sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 	
							
							setHBSInsulinDose(dose);
//							setInsulinRemainingDose(getInsulinRemainingDose()-dose);
							setInsulinPushed(true);
							setCarbQTY(0);
//						}							
					}
				}
				
				//Low Blood Sugar detection and Glucagon pumping
				if (currBGL<minBGL) {				
					setGlucagonPushed(false);
					System.out.println(BGL.getTargetBGL());
					if(!getGlucagonPushed()) {
						dose=(BGL.getTargetBGL()-currBGL)/getGlucagonSensitivityFactor();
//						if(getInsulinRemainingDose()-dose<0) {
//							
//						}
//						else {
							
//							setInsulinRemainingDose(getInsulinRemainingDose()-dose);
							
							try {
								Thread.currentThread().sleep(3000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
							
							setGlucagonDose(dose);
							setGlucagonPushed(true);
							BGL.setStartBGL(BGL.getTargetBGL());//
							BGL.setCurrentBGL(BGL.getTargetBGL());//
//						}						
					}
				}			
//			}					
		}		
	}
 
	public void setHBSCorrectionFactor(int factor) {
		hbs_correction_factor=factor;
	}
	
	public int getHBSCorrectionFactor() {
		return hbs_correction_factor;
	}
	
	public void setHBSInsulinDose(int dose) {
		hbs_insulin_dose=dose;
	}
	
	public int getHBSInsulinDose() {
		return hbs_insulin_dose;
	}
	
	public void setCHOInsulineDose(int dose) {
		cho_insuline_dose=dose;
	}
	
	public int getCHOInsulineDose() {
		return cho_insuline_dose;
	}
	
	public void setCHOFactor(int factor) {
		CHO_factor=factor;
	}
	
	public int getCHOFactor() {
		return CHO_factor;
	}
	
	public void setInsulinPushed(boolean pushed) {
		this.insulinpushed = pushed;
	}
	
	public boolean getInsulinPushed() {
		return insulinpushed;
	}
	
	public void setGlucagonPushed(boolean pushed) {
		this.glucagonpushed  = pushed;
	}
	
	public boolean getGlucagonPushed() {
		return glucagonpushed;
	}
	
	
	public void setCarbQTY(int qty) {
		carb_qty = qty;
	}
	
	public int getCarbQTY() {
		return carb_qty;
	}
	
	public void setGlucagonDose(int dose) {
		this.glucagon_dose = dose;
	}
	
	public int getGlucagonDose() {
		return glucagon_dose;
	}
	
	public void setGlucagonSensitivityFactor(int factor) {
		this.glucagon_sensitivity_factor  = factor;
	}
	
	public int getGlucagonSensitivityFactor() {
		return glucagon_sensitivity_factor;
	}
	
	public void setInsulinDailyDoseLimit(int dailydose) {
		this.insulin_daily_dose_limit=dailydose;
	}
	
	public int getInsulinDailyDoseLimit() {
		return this.insulin_daily_dose_limit;
	}
	
	public void setInsulinRemainingDose(int remainingdose) {
		this.insulin_remaining_dose=remainingdose;
	}
	
	public int getInsulinRemainingDose() {
		return this.insulin_remaining_dose;
	}
	
	public void setWarningMsg(String msg) {
		msgWarning = msg;
	}
	
	public String getWarningMsg() {
		return msgWarning;
	}
	
}
