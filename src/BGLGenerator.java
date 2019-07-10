import java.util.Random;

public class BGLGenerator implements Runnable{

	private Random randGen = new Random();
	
	volatile private static int startBGL=100;
	private static int BGLOffset=1;
	volatile private static int targetBGL=100;
	volatile private static int maxSafeBGL=112; //safe range of blood glucose level is 70 to 130 mg/dl
	volatile private static int minSafeBGL=90; //safe range of blood glucose level is 70 to 130 mg/dl
	volatile private static int currBGL=100;
	
	
	
	@Override
	public void run() {				
		//GenerateRandomBGL();
		while(true) {
			int max,min;
			max = getStartBGL()+getBGLOffset();
			min = getStartBGL()-getBGLOffset();;
			
			int r_BGL = randGen.nextInt((max - min) + 1) + min;
			
			setCurrentBGL(r_BGL);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
		}
				
	}
	
	
	private void GenerateRandomBGL() {	    		    
		int max,min;
		max = getStartBGL()+getBGLOffset();
		min = getStartBGL();
		
		int r_BGL = randGen.nextInt((max - min) + 1) + min;
		
		setCurrentBGL(currBGL);
    }

	public void setCurrentBGL(int currBGL) {
		this.currBGL = currBGL;
	}
	
	public int getCurrentBGL() {
		return currBGL;
	}
	
	public void setStartBGL(int startBGL) {
		this.startBGL = startBGL;
	}
	
	public int getStartBGL() {
		return startBGL;
	}
	
	public void setBGLOffset(int BGLoffset) {
		this.BGLOffset = BGLoffset;
	}
	
	public int getBGLOffset() {
		return BGLOffset;
	}
	
	public void setTargetBGL(int target) {
		this.targetBGL = target;
	}
	
	public int getTargetBGL() {
		return targetBGL;
	}
	
	public void setMaxSafeBGL(int maxSafeBGL) {
		this.maxSafeBGL = maxSafeBGL;
	}
	
	public int getMaxSafeBGL() {
		return maxSafeBGL;
	}
	
	public void setMinSafeBGL(int minSafeBGL) {
		this.minSafeBGL = minSafeBGL;
	}
	
	public int getMinSafeBGL() {
		return minSafeBGL;
	}
	
}
