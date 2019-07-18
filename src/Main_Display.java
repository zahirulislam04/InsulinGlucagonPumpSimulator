import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import com.jgoodies.forms.layout.FormLayout;
import java.awt.Toolkit;
import com.sun.xml.internal.ws.wsdl.writer.document.Message;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import javafx.scene.control.ProgressBar;

import javax.swing.JLabel;
import java.awt.Panel;
import java.awt.SystemColor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.swing.UIManager;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.Insets;
import javax.swing.SwingConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JMenuBar;
import javax.swing.JMenu;

public class Main_Display {

	private JFrame frame;
	JLabel lblCarbohydrate;
	JTextArea txtMsgDisplay;
	JLabel lblClock;
	JProgressBar pgGlucagon;
	JProgressBar pgInsulin;
	JProgressBar pgBattery;
	JLabel lblOfBattery;
	JLabel lblOfInsulin;
	JLabel lblOfGlucagon;
	JPanel pnlChart;
	JTextField txtCurrBGL;
	JPanel pnlLeft;
	JLabel lblCurrentBgl;
	JLabel lblGlucagonQty;
	JLabel lblInsulinQty;
	private JButton btnInsulinRefil;
	private JButton btnGlucagonRefil;
	private JTextField txtBGLValue;
	private JLabel lblSetBgl;
	private JTextField textCarb;
	JButton btnApplyCarb;
	JButton btnRechargeBatery;
	JButton btnStart;
	JButton btnApplyBGL;
	private JButton btnStop;
	
	private static Main_Display window;
	
	private static BatteryProgress batery;
	private static Thread th_batery;
	private static Clock clk;
	private static Thread th_clock;
	private static BGL_Chart chart;
	private static Thread th_Chart;
	private static BGLGenerator BGL_Generator;
	private static Thread th_BGL;
	private static InsulinGlucagonPump insulin_pump;
	private static Thread th_insulin;
	private static InsulinUnitProgress insulin_progress;
	private static Thread th_insulin_progress;
	private static GlucagonUnitProgress glucagon_progress;
	private static Thread th_glucagon_progress;
	private static MessageAlert msg_alert;
	
	
	private static String thread_Status="initial";
	private static boolean batery_status=true;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new Main_Display();
					window.frame.setVisible(true);	
					
					msg_alert=new MessageAlert(); 
					
					batery = window.new BatteryProgress();
					th_batery = new Thread(batery);
//					th_batery.start();
					
					clk = window.new Clock();
					th_clock = new Thread(clk);
//					th_clock.start();
					
					BGL_Generator = new BGLGenerator();
					th_BGL = new Thread(BGL_Generator);					
//					th_BGL.start();
//					th_BGL.setName("BGL Thread");
					
					insulin_pump = new InsulinGlucagonPump();
					th_insulin = new Thread(insulin_pump);
//					th_insulin.start();
					
					insulin_progress = window.new InsulinUnitProgress();
					insulin_progress.setMaxInsulinUnit(300);
					insulin_progress.setCurrInsulinUnit(300);
					th_insulin_progress = new Thread(insulin_progress);
//					th_insulin_progress.start();
					
					glucagon_progress  = window.new GlucagonUnitProgress();
					glucagon_progress.setMaxGlucagonUnit(300);
					glucagon_progress.setCurrGlucagonUnit(300);
					th_glucagon_progress = new Thread(glucagon_progress);
//					th_glucagon_progress.start();
							
					
					chart = window.new BGL_Chart();
					th_Chart = new Thread(chart);
//					th_Chart.start();									
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
		
	
	public void clearMsgDisplay() {
		
		try {
			Thread.currentThread().sleep(10000);
		} catch (InterruptedException e) {		
			e.printStackTrace();
		}
		txtMsgDisplay.setText("");
	}
	
	
	public void setBateryStatus(boolean status) {		//status is either 1 (up) or 0 (down )
		batery_status = status;
	}
	
	public boolean getBateryStatus() {		//status is either 1 (up) or 0 (down )
		return batery_status;
	}
	
	
	public void StartMachine() {
		if (getBateryStatus()) {
			if (thread_Status=="initial") {
				th_batery.start();
				th_clock.start();
				th_BGL.start();
				th_insulin.start();
				th_insulin_progress.start();
				th_glucagon_progress.start();			
				th_Chart.start();
				
				thread_Status="start";
				txtMsgDisplay.setText("Machine has started!!");
				
				int i=0;
				for (i=0;i<3;i++) {
					

						try {
							msg_alert.tone(1000,100);							
						} catch (LineUnavailableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}					
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}									
				}
								
			}
			else if (thread_Status=="stop") {
				//th_batery.resume();
				//th_clock.resume();
				th_BGL.resume();
				th_insulin.resume();
				th_insulin_progress.resume();
				th_glucagon_progress.resume();				
				th_Chart.resume();
				txtMsgDisplay.setText("Machine has resumed!!");
				thread_Status="resume";
				
				
				int i=0;
				for (i=0;i<3;i++) {
					

						try {
							msg_alert.tone(1000,100);	//restart
						} catch (LineUnavailableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}					
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}									
				}
					
				
			}
		}
		
	}
	
	public void StopMachine() {
		if (getBateryStatus()) {
			th_BGL.suspend();
			th_insulin.suspend();
			th_insulin_progress.suspend();
			th_glucagon_progress.suspend();			
			th_Chart.suspend();
			
			txtMsgDisplay.setText("Machine has stoped!!");
			thread_Status="stop";
			
			int i=0;
			for (i=0;i<3;i++) {
				

					try {
						msg_alert.tone(400,500,2); //use for low battery
					} catch (LineUnavailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}									
			}
				
			
		}
	}
	
	

	/**
	 * Create the application.
	 */
	public Main_Display() {
		initialize();		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 807, 506);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setLocationRelativeTo(null);
//		frame.setUndecorated(true);
		
		lblClock = new JLabel("");
		lblClock.setForeground(new Color(165, 42, 42));
		lblClock.setFont(new Font("Tahoma", Font.BOLD, 40));
		lblClock.setBounds(303, 15, 185, 69);
		frame.getContentPane().add(lblClock);
		
		pgGlucagon = new JProgressBar();
		pgGlucagon.setForeground(UIManager.getColor("ToolBar.darkShadow"));
		pgGlucagon.setMaximum(300);
		pgGlucagon.setBounds(45, 172, 87, 30);
		pgGlucagon.setBackground(Color.WHITE);
		pgGlucagon.setPreferredSize(new Dimension(50, 14));
		//frame.getContentPane().add(pgGlucagon);
		
		pgInsulin = new JProgressBar();
		pgInsulin.setMaximum(300);
		pgInsulin.setBounds(45, 227, 87, 30);
		pgInsulin.setBackground(Color.WHITE);
		pgInsulin.setPreferredSize(new Dimension(50, 14));
		pgInsulin.setForeground(UIManager.getColor("ToolBar.darkShadow"));
		//frame.getContentPane().add(pgInsulin);
		
		pgBattery = new JProgressBar();
		pgBattery.setBackground(Color.WHITE);
		pgBattery.setPreferredSize(new Dimension(50, 14));
		pgBattery.setForeground(Color.DARK_GRAY);
		pgBattery.setBounds(21, 11, 50, 14);
		frame.getContentPane().add(pgBattery);
		
		lblOfBattery = new JLabel("%");
		lblOfBattery.setHorizontalAlignment(SwingConstants.RIGHT);
		lblOfBattery.setBounds(21, 25, 50, 14);
		frame.getContentPane().add(lblOfBattery);
		
		lblOfInsulin = new JLabel("300");
		lblOfInsulin.setHorizontalAlignment(SwingConstants.CENTER);
		lblOfInsulin.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblOfInsulin.setBounds(139, 227, 31, 26);
		//frame.getContentPane().add(lblOfGlucagon);
		
		pnlChart = new JPanel();
		pnlChart.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlChart.setBounds(206, 120, 577, 336);
		frame.getContentPane().add(pnlChart);
		
		txtCurrBGL = new JTextField();
		txtCurrBGL.setHorizontalAlignment(SwingConstants.CENTER);
		txtCurrBGL.setEditable(false);
		txtCurrBGL.setBounds(45, 278, 87, 26);
		txtCurrBGL.setFont(new Font("Tahoma", Font.PLAIN, 16));
		//frame.getContentPane().add(txtCurrBGL);
		txtCurrBGL.setColumns(10);
		
		lblCurrentBgl = new JLabel("Current BGL");
		lblCurrentBgl.setBounds(45, 255, 103, 23);
		lblCurrentBgl.setFont(new Font("Tahoma", Font.BOLD, 14));
		//frame.getContentPane().add(lblCurrentBgl);
		
		pnlLeft = new JPanel();
		pnlLeft.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlLeft.setBounds(21, 120, 175, 336);
		pnlLeft.setLayout(null);
		//pnlLeft.setLayout(new Absolu());
		pnlLeft.add(pgGlucagon);
		pnlLeft.add(pgInsulin);
		pnlLeft.add(lblOfInsulin);
		pnlLeft.add(lblCurrentBgl);
		pnlLeft.add(txtCurrBGL);
		frame.getContentPane().add(pnlLeft);
		
		lblGlucagonQty = new JLabel("Glucagon Units");
		lblGlucagonQty.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblGlucagonQty.setBounds(45, 144, 103, 28);
		pnlLeft.add(lblGlucagonQty);
		
		lblInsulinQty = new JLabel("Insulin Units");
		lblInsulinQty.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblInsulinQty.setBounds(45, 205, 101, 23);
		pnlLeft.add(lblInsulinQty);
		
		txtMsgDisplay = new JTextArea();
		txtMsgDisplay.setBorder(new LineBorder(UIManager.getColor("ToolBar.dockingForeground")));
		txtMsgDisplay.setForeground(Color.RED);
		txtMsgDisplay.setFont(new Font("Monospaced", Font.BOLD, 14));
		txtMsgDisplay.setLineWrap(true);
		txtMsgDisplay.setBounds(4, 3, 166, 130);
		pnlLeft.add(txtMsgDisplay);
		//frame.getContentPane().add(lblOfInsulin);
		
		lblOfGlucagon = new JLabel("300");
		lblOfGlucagon.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblOfGlucagon.setHorizontalAlignment(SwingConstants.CENTER);
		lblOfGlucagon.setBounds(137, 172, 33, 30);
		pnlLeft.add(lblOfGlucagon);
		
		btnInsulinRefil = new JButton("");
		btnInsulinRefil.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				th_insulin_progress.suspend();		
				//insulin_pump.setHBSInsulinDose(0);
				insulin_progress.setCurrInsulinUnit(300);
				th_insulin_progress = new Thread(insulin_progress);
				th_insulin_progress.start();
				
				txtMsgDisplay.setText("Insulin cartidge has recharged.");
			}
		});
		btnInsulinRefil.setActionCommand("");
		btnInsulinRefil.setMargin(new Insets(0, 0, 0, 0));
		btnInsulinRefil.setIcon(new ImageIcon("D:\\HIS\\SS 19\\SCS\\Project\\Application\\img\\rechrg.png"));
		btnInsulinRefil.setBounds(9, 227, 31, 30);
		pnlLeft.add(btnInsulinRefil);
		
		btnGlucagonRefil = new JButton("");
		btnGlucagonRefil.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				th_glucagon_progress.suspend();		
				insulin_pump.setGlucagonDose(0);
				glucagon_progress.setCurrGlucagonUnit(300);
				th_glucagon_progress = new Thread(glucagon_progress);
				th_glucagon_progress.start();
				
				txtMsgDisplay.setText("Glucagon cartidge has recharged.");
			}
		});
		btnGlucagonRefil.setMargin(new Insets(0, 0, 0, 0));
		btnGlucagonRefil.setIcon(new ImageIcon("D:\\HIS\\SS 19\\SCS\\Project\\Application\\img\\rechrg.png"));
		btnGlucagonRefil.setBounds(9, 172, 31, 30);
		pnlLeft.add(btnGlucagonRefil);
		
		btnApplyBGL = new JButton("");
		btnApplyBGL.setMargin(new Insets(0, 0, 0, 0));
		btnApplyBGL.setIcon(new ImageIcon("D:\\HIS\\SS 19\\SCS\\Project\\Application\\img\\apply.png"));
		btnApplyBGL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int val=0;
				
				try{
					val = Integer.parseInt(txtBGLValue.getText());
				}catch (NumberFormatException ex) {
				    val=-999;
				}
				
				if (val!=-999) {
					textCarb.setText("");
					if(thread_Status=="start" || thread_Status=="resume") {
						th_BGL.suspend();						
						BGL_Generator.setStartBGL(val);
						th_BGL = new Thread(BGL_Generator);		
						th_BGL.start();	
						
						
						
					}
						
				}
				else {
					txtMsgDisplay.setText("Invalid Blood Glucose Input.");
					txtBGLValue.setText("");
				}											
			}
		});
		btnApplyBGL.setBounds(752, 15, 31, 30);
		frame.getContentPane().add(btnApplyBGL);
		
		btnRechargeBatery = new JButton("");
		btnRechargeBatery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRechargeBatery.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("deprecation")
			@Override
			public void mouseClicked(MouseEvent arg0) {				
				th_batery.suspend();				
				th_batery = new Thread(batery);
				th_batery.start();
				
				setBateryStatus(true);
				batery.setAlert(false);
				txtMsgDisplay.setText("Batery has recharged.");
				
			}
		});
		btnRechargeBatery.setMargin(new Insets(0, 0, 0, 0));
		btnRechargeBatery.setMinimumSize(new Dimension(30, 25));
		btnRechargeBatery.setMaximumSize(new Dimension(30, 25));
		btnRechargeBatery.setIcon(new ImageIcon("D:\\HIS\\SS 19\\SCS\\Project\\Application\\img\\rechrg.png"));
		btnRechargeBatery.setBounds(81, 11, 31, 30);
		frame.getContentPane().add(btnRechargeBatery);
		
		txtBGLValue = new JTextField();
		txtBGLValue.setFont(new Font("Tahoma", Font.BOLD, 14));
		txtBGLValue.setBounds(698, 15, 50, 30);
		frame.getContentPane().add(txtBGLValue);
		txtBGLValue.setColumns(10);
		
		lblSetBgl = new JLabel("Set Blood Glucose");
		lblSetBgl.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblSetBgl.setBounds(561, 18, 137, 27);
		frame.getContentPane().add(lblSetBgl);
		
		lblCarbohydrate = new JLabel("Carbohydrate (gm)");
		lblCarbohydrate.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblCarbohydrate.setBounds(561, 59, 137, 27);
		frame.getContentPane().add(lblCarbohydrate);
		
		textCarb = new JTextField();
		textCarb.setFont(new Font("Tahoma", Font.BOLD, 14));
		textCarb.setColumns(10);
		textCarb.setBounds(698, 56, 50, 30);
		frame.getContentPane().add(textCarb);
		
		btnApplyCarb = new JButton("");
		btnApplyCarb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int val = 0;
				try {
					val = Integer.parseInt(textCarb.getText());
				}
				catch(NumberFormatException ex) {
					val=-999;
				}
				
				if (val!=-999) {
					txtBGLValue.setText("");
					if(thread_Status=="start" || thread_Status=="resume") {						
						//System.out.println(val);
						if (val<0) 
							txtMsgDisplay.setText("Incorrect carbohydrate inserted. Value must be greater than zero(0).");				
						else {
							th_insulin.suspend();					
							insulin_pump.setCarbQTY(val);
							th_insulin = new Thread(insulin_pump);
							th_insulin.start();
							
						}
					}		
				}
				else {
					txtMsgDisplay.setText("Invalid Carbohydrate Input.");
					textCarb.setText("");
				}
						
			}
		});
		btnApplyCarb.setIcon(new ImageIcon("D:\\HIS\\SS 19\\SCS\\Project\\Application\\img\\apply.png"));
		btnApplyCarb.setMargin(new Insets(0, 0, 0, 0));
		btnApplyCarb.setBounds(752, 56, 31, 30);
		frame.getContentPane().add(btnApplyCarb);
		
		btnStart = new JButton("");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				StartMachine();
			}
		});
		btnStart.setIcon(new ImageIcon("D:\\HIS\\SS 19\\SCS\\Project\\Application\\img\\start_button.png"));
		btnStart.setBounds(21, 52, 50, 42);
		frame.getContentPane().add(btnStart);
		
		btnStop = new JButton("");
		btnStop.setIcon(new ImageIcon("D:\\HIS\\SS 19\\SCS\\Project\\Application\\img\\stop_button.png"));
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StopMachine();
			}
		});
		btnStop.setBounds(81, 52, 50, 42);
		frame.getContentPane().add(btnStop);
		
		
	}

	
	//Battery Progress bar thread class
	class BatteryProgress implements Runnable{


		boolean alert=false;
		
		public void run() {
			for(int i=100;i>=0;i--) {
				pgBattery.setValue(i);
				lblOfBattery.setText(i + "%");
				if (i<30) {
					txtMsgDisplay.setText("Batery is low. Please recharge it as soon as possible to avoid machine shut down.");
					
					int j=0;
					if(!getAlert()) {
						for (j=0;j<3;j++) {						
							try {
								msg_alert.tone(400,500,2); //use for low battery
								setAlert(true);
							} catch (LineUnavailableException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}					
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}									
						}
					}					
				}
				
				if(i==0) {
					StopMachine();					
					txtMsgDisplay.setText("Batery charge is empty!!! Recharge it to start the machine.");					
					setBateryStatus(false);
				}
				
				try {
					java.lang.Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}	
		
		public void setAlert(boolean alert) {
			this.alert=alert;
		}
		public boolean getAlert() {
			return this.alert;
		}
		
	}
	
	//Insulin Unit Progress bar thread class
	class InsulinUnitProgress implements Runnable{
		private int maxInsulineUnit;
		private int currentInsulinUnit;
		private InsulinGlucagonPump insulin = new InsulinGlucagonPump(); 
		private int qtyInsulinPushed=0;
		
		
		public void run() {
			while(true) {
				qtyInsulinPushed = insulin.getHBSInsulinDose();
				setCurrInsulinUnit(currentInsulinUnit-qtyInsulinPushed);
				insulin.setHBSInsulinDose(0);
				pgInsulin.setValue(getCurrInsulinUnit());					
				lblOfInsulin.setText(Integer.toString(currentInsulinUnit));
				if (qtyInsulinPushed>0) {
					txtMsgDisplay.setText(Integer.toString(qtyInsulinPushed) + " unit of Insulin pushed");
					
					/*Sound alert*/
					
					try {
						msg_alert.tone(1000,100);							
					} catch (LineUnavailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}									
											
					/*alert end*/
					

				}
				try {
					java.lang.Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}				
		}
		
		private void setMaxInsulinUnit(int max) {
			this.maxInsulineUnit = max;
		}
		
		private int getMaxInsulinUnit() {
			return maxInsulineUnit;
		}
		
		private void setCurrInsulinUnit(int curr) {
			this.currentInsulinUnit = curr;
		}
		
		private int getCurrInsulinUnit() {
			return currentInsulinUnit;
		}					
	}
	
	
	//Glucagon Unit progress bar
	class GlucagonUnitProgress implements Runnable{
		private int maxGlucagonUnit;
		private int currentGlucagonUnit;
		private InsulinGlucagonPump insulin = new InsulinGlucagonPump(); 
		private int qtyGlucagonPushed=0;
		
		public void run() {
			while(true) {
				qtyGlucagonPushed = insulin.getGlucagonDose();
				setCurrGlucagonUnit(currentGlucagonUnit-qtyGlucagonPushed);
				insulin.setGlucagonDose(0);
				pgGlucagon.setValue(getCurrGlucagonUnit());					
				lblOfGlucagon.setText(Integer.toString(currentGlucagonUnit));
				if (qtyGlucagonPushed>0) {
					txtMsgDisplay.setText(Integer.toString(qtyGlucagonPushed) + " unit of Glucagon pushed");
					
					/*Sound alert*/
					
					try {
						msg_alert.tone(1000,100);							
					} catch (LineUnavailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}									
											
					/*alert end*/
				}
				try {
					java.lang.Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}				
		}
		
		private void setMaxGlucagonUnit(int max) {
			this.maxGlucagonUnit = max;
		}
		
		private int getMaxGlucagonUnit() {
			return maxGlucagonUnit;
		}
		
		private void setCurrGlucagonUnit(int curr) {
			this.currentGlucagonUnit = curr;
		}
		
		private int getCurrGlucagonUnit() {
			return currentGlucagonUnit;
		}					
	}
	
	
	//Clock thread class
	class Clock implements Runnable{
					
		@Override
		public void run() {			
			while(true) {
				SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss");  
				Date date = new Date(System.currentTimeMillis());  			
				lblClock.setText(formatter.format(date));
			}
						
		}
		
	}
	
	//Chart to show blood glucose level over time
	//Used JFree chart
	class BGL_Chart extends JPanel implements Runnable{

		TimeSeries ts = new TimeSeries("data", Second.class);		
		private Random randGen = new Random();
		private int max_RandNum = 130, min_RandNum=127; //Range: maximum 140 and minimum 72 mg/dl
		private int curr_Glucose_Level;
		private BGLGenerator BGL = new BGLGenerator();
		
		
		public BGL_Chart() {
	    	TimeSeriesCollection dataset = new TimeSeriesCollection(ts);
			
	        JFreeChart chart = ChartFactory.createTimeSeriesChart(
	            "Blood Glucose",
	            "Time",
	            "Glucose Level (mg/dl)",
	            dataset,
	            false,
	            true,
	            false
	        );
	        
	        final XYPlot plot = chart.getXYPlot();
	        ValueAxis axis = plot.getDomainAxis();
	        axis.setAutoRange(true);
	        axis.setFixedAutoRange(60000.0);
	           
	        ChartPanel label = new ChartPanel(chart);
		    
		    label.setPreferredSize(new java.awt.Dimension(570, 300));
		    label.setSize(new java.awt.Dimension(570, 300));
		    add(label); //add chart into chart panel	

	        pnlChart.add(label); //adding chart panel into JPanel
		    pnlChart.revalidate();
	     	pnlChart.repaint();
	     	
	    }	
		
	    
	    public void run() {  	
	    	final TimeSeries series = new TimeSeries( "Random Data" );
            Second current = new Second();
            BGL.setStartBGL(100);
	    	while(true) {
//            	int num = GenerateRandNumb(max_RandNum, min_RandNum);
	    		int num = BGL.getCurrentBGL();
                txtCurrBGL.setText(Integer.toString(num));
                series.add(current, (double) num);
                ts.addAndOrUpdate(series);                
                current =(Second)current.next();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }
        }
	    
	    
	    int GenerateRandNumb(int max,int min) {	    		    	
	    	return randGen.nextInt((max - min) + 1) + min;
	    }
	}	
}




