/**
 * @(#)BellScheduleGUI.java
 *
 *
 * @Comp Sci Club 2015-2016
 * @version 1.01 2016/2/12
 * Version 1.00 (Initial Release)
 * Version 1.01 (Adjusted for early dismissals)
 */
import java.util.*;
import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class BellScheduleGUI {

	/* TODO
	 *
	 * Create a for loop or algorithm
	 *
	 */

	public static int classNum; //number of class periods
	public static int classTime; //number of minutes per class
	public static int lunchPd; //period before which lunch occurs
	public static int lunchLength; //how long lunch lasts (in minutes)
	public static boolean SSEP; //is there SSEP?
	public static int SSEPLength; //number of minutes for SSEP
	public static int SSEPPd; //period before which SSEP occurs
	public static int schoolStart; //time that school starts (in minutes)
	public static int schoolEnd; //time that school ends (in minutes)
	public static int totalTime; //total length of school
	public static boolean assembly; //is there an assembly?
	public static int assemblyLength; //how long an assembly lasts if there is one
	public static int assemblyPd; //period before assembly
	private static final int passLength = 5; //time between classes
	private static int numPassPd; //number of passing periods
//	private static ArrayList<Integer> events; Obsolete (Replaced by ArrayList<Class> events)
//	private static ArrayList<Integer> schedMin; Obsolete (Replaced by ArrayList<Class> events)
//	private static ArrayList<String> labels; Obsolete (Replaced by ArrayList<Class> events)
	private static ArrayList<Class> events; //Darude [GREEN] Sandstorm//
	public static String SchName; //Name of schedule as in "schedule [SchName]"


    public static void main(String args[]) throws IOException
    {
    	//initialize values
    	//EVENTS
    	/*
    	 * The period lengths of class blocks assuming all events
    	 * are classes.
    	 */
//    	events = new ArrayList<Integer>(); //lengths in order of period(class)
 //   	schedMin = new ArrayList<Integer>();
 //   	labels=new ArrayList<String>();
		assembly = false;
 		ScanGUI window = new ScanGUI();


    }
    public static void secondary() throws IOException {
    	events=new ArrayList<Class>();

	//	scanInput();

    	totalTime=schoolEnd-schoolStart;
    	numPassPd=(classNum);
    	if(SSEP)
    		numPassPd++;
    	//assemblies may affect pass periods
    	int rTime=totalTime; //used to calculate remaining time
    	rTime-=numPassPd*passLength;
    	rTime-=classNum*classTime;
    	if(SSEP)
    		rTime-=SSEPLength;
    	else
    		rTime-=15;
    	if(assembly)
    		rTime-=assemblyLength;
    	lunchLength=rTime;
		//if you have multiple lunches, we don't care
		orderClasses();
//		scheduleMin();
//		labelMaker();
/*		for(int h = 0; h<schedMin.size();h++)
		{
			System.out.println(schedMin.get(h)+" "+labels.get(h));
		}
		System.out.println();*/
		PrintWriter file = new PrintWriter("Created_Schedule.txt");
		file.println("schedule " + SchName);
		for(int fruity=0;fruity<events.size();fruity++)
		{
			if(events.get(fruity).getStart()!=0)
			{
				file.print(convert(events.get(fruity).getStart())+" ");
				file.println("Start of "+events.get(fruity).getName());
			}
			if(events.get(fruity).getEnd()!=0)
			{
				file.print(convert(events.get(fruity).getEnd())+" ");
				file.println("End of "+events.get(fruity).getName());
			}

		}
		file.println("24 00 End of the day");
		file.close();
    }

    public static void orderClasses()
    {
		int count=schoolStart;
		int current=1; //current class that we are entering
    	while(current <= classNum)
    	{
    		String abrev="";
    		if(current-1==SSEPPd)
    		{
    			if(SSEP)
    			{
    				events.add(new Class("SSEP",count,count+SSEPLength));
    				count+=SSEPLength+5;
    			}

    		}
    		if(current-1== assemblyPd){
    			if(assembly){
    				events.add(new Class("Assembly",count,count+assemblyLength));
    				count+=assemblyLength;
    			}
    		}
    		if(current-1==lunchPd)
    		{
    			events.add(new Class("Lunch",0,count+lunchLength));
    			count+=lunchLength+5;
    		}
    		if(current==1)
    		{
    			abrev="st";

    		}
    		else if(current==2)
    		{
    			abrev="nd";
    		}
    		else
    		{
    			abrev="th";
    		}
    		if(current==1&&!SSEP){
    			events.add(new Class((current+abrev+" Per."),count,count+classTime+15));
    			count+=15;
    		}
    		else
    			events.add(new Class((current+abrev+" Per."),count,count+classTime));
    		count+=classTime+5;
    		current++;
    	}
    }
/*  public static void orderClasses()
    {
    	int current=0; //current class that we are entering
    	while(current < classNum)
    	{
    		if(current==SSEPPd)
    		{
    			events.add(SSEPLength);
    		}

    		if(current==lunchPd)
    		{
    			events.add(lunchLength);
    		}
    		events.add(classTime);
    		current++;
    	}
    	/*if(current==SSEPPd)
    	{
    		events.add(SSEPLength);
    	}
    		if(current==lunchPd)
    	{
    		events.add(lunchLength);
    	}
    }*/
/*    public static void scheduleMin()
    {
    	int time = schoolStart;
    	schedMin.add(time);
    	for(int h = 0;h<events.size();h++ )
    	{
    		time+=events.get(h);
    		schedMin.add(time);
    		time+=passLength;
    		schedMin.add(time);
    	}

    }*/

    public static void scanInput()
    {
    	SchName = JOptionPane.showInputDialog("Name of the schedule:");
    	classNum = Integer.valueOf(JOptionPane.showInputDialog("Number of class periods (W/O SSEP or Lunch):"));
    	classTime = Integer.valueOf(JOptionPane.showInputDialog("Number minutes in each class:"));
    	lunchPd = Integer.valueOf(JOptionPane.showInputDialog("Period right before lunch:"));

    	SSEPLength = Integer.valueOf(JOptionPane.showInputDialog("Length of SSEP (minutes):"));
    	SSEPPd = Integer.valueOf(JOptionPane.showInputDialog("Period right before SSEP:"));
    	/* Need to change to input format in future to hours and minutes */
    	schoolStart = Integer.valueOf(JOptionPane.showInputDialog("School start time (minutes):"));
    	schoolEnd = Integer.valueOf(JOptionPane.showInputDialog("School end time (minutes):"));
    	// add assembly options in the future
    	assembly = Boolean.valueOf(JOptionPane.showInputDialog("Is there an assembly? true or false (all lowercase)"));
    	if(assembly){ //Just to spite Sam
    		assemblyLength = Integer.valueOf(JOptionPane.showInputDialog("Length of Assembly (in minutes):"));
    		assemblyPd = Integer.valueOf(JOptionPane.showInputDialog("Period right before assembly:"));
    	}

    //	passLength = Integer.valueOf(JOptionPane.showInputDialog("Length of passing periods (minutes):"));
    	SSEP = !(SSEPLength==0); //Add option for this in future
    }
/*
    public static void labelMaker()
    {
    	boolean p=false;
    	int period=1;
    	for(int k=0;k<schedMin.size();k++)
    	{
    		if(!p)
    		{
    			labels.add("start of period "+period);
    			p=true;
    		}

    		if(p)
    		{
    			labels.add("end of period "+period);
    			p=false;
    			period++;
    		}
    	}
    }*/
    public static String convert(int n)
    {
    	int hours,min;
    	hours = n/60;
    	min = n%60;
     	return String.format("%02d %02d",hours,min);
    }
}

class Class
{
	private String name;
	private int start;
	private int end;

	public Class(String aname, int astart, int aend)
	{
		name=aname;
		start=astart;
		end=aend;
	}

	public String getName()
	{
		return name;
	}
	public int getStart() //will return 0 if there is no start time e.g. lunch
	{
		return start;
	}
	public int getEnd()
	{
		return end;
	}
}

class ScanGUI implements ActionListener, ItemListener{

	private final int WIDTH = 450;
	private final int HEIGHT = 225;
	private JFrame guiFrame = new JFrame("Schedule Generator");
	private JPanel guiPanel = new JPanel();
//	private JPanel footer = new JPanel();
	private JTextField name;
	private JTextField classNum;
	private JTextField classTime;
	private JTextField lunchPd;
	private JTextField SSEPLength;
	private JTextField SSEPPd;
	private JTextField schoolStart;
	private JTextField schoolEnd;
	private JTextField lunchLength;
	private JTextField assemblyLength;
	private JTextField assemblyPd;
	private JLabel descriptor,d1,d2,d3,d4,d5,d6,d7,d8,d9,d10,d11,dEnd;
	private JButton submit;
	private JCheckBox assembly;
	private String submitStr = "SUB";

	public ScanGUI(){
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		guiFrame.setSize(WIDTH,HEIGHT);

		guiFrame.add(guiPanel);
	//	guiFrame.add(footer);

		guiPanel.setLayout(new GridLayout(13,2));
		guiPanel.setLocation(0,0);
		guiPanel.setPreferredSize(new Dimension(WIDTH,(int)(HEIGHT*.8)));

	//	footer.setLocation(0,(int) (HEIGHT*.8));
	//	footer.setPreferredSize(new Dimension(WIDTH,(int)(HEIGHT*.2)));



		submit = new JButton("Submit");

		descriptor = new JLabel("Schedule Name:");
		d1 = new JLabel("Number of Classes:");
		d2 = new JLabel("Class length:");
		d3 = new JLabel("Period before lunch:");
		d4 = new JLabel("Length of lunch");
		d5 = new JLabel("SSEP length:");
		d6 = new JLabel("Period before SSEP:");
		d7 = new JLabel("Start School time:");
		d8 = new JLabel("End School time:");
		d9 = new JLabel("Is there an Assembly?");
		d10 = new JLabel("Length of Assembly");
		d11 = new JLabel("Period before Assembly");
		dEnd = new JLabel("");

		name = new JTextField(15);
		classNum = new JTextField(15);
		classTime= new JTextField(15);
		lunchPd = new JTextField(15);
		lunchLength = new JTextField(15);
		SSEPLength = new JTextField(15);
		SSEPPd = new JTextField(15);
		schoolStart = new JTextField("00:00",15);
		schoolEnd= new JTextField("00:00",15);
		assembly = new JCheckBox();
		assemblyLength = new JTextField(15);
		assemblyPd = new JTextField(15);

		assemblyLength.setEditable(false);
		assemblyPd.setEditable(false);

		assembly.setSelected(false);

		guiPanel.add(descriptor);
		guiPanel.add(name);
		guiPanel.add(d1);
		guiPanel.add(classNum);
		guiPanel.add(d2);
		guiPanel.add(classTime);
		guiPanel.add(d3);
		guiPanel.add(lunchPd);
		guiPanel.add(d4);
		guiPanel.add(lunchLength);
		guiPanel.add(d5);
		guiPanel.add(SSEPLength);
		guiPanel.add(d6);
		guiPanel.add(SSEPPd);
		guiPanel.add(d7);
		guiPanel.add(schoolStart);
		guiPanel.add(d8);
		guiPanel.add(schoolEnd);
		guiPanel.add(d9);
		guiPanel.add(assembly);
		guiPanel.add(d10);
		guiPanel.add(assemblyLength);
		guiPanel.add(d11);
		guiPanel.add(assemblyPd);

		guiPanel.add(dEnd);
		guiPanel.add(submit);
		//footer.add(submit);

		guiFrame.setVisible(true);
		//footer.setVisible(true);
		guiPanel.setVisible(true);
		//guiFrame.pack();

		submit.setActionCommand(submitStr);
		submit.addActionListener(this);

		assembly.addItemListener(this);
	}

	public void scanText() throws IOException{
		BellScheduleGUI.SchName = name.getText();
		BellScheduleGUI.classNum = Integer.valueOf(classNum.getText());
		BellScheduleGUI.classTime = Integer.valueOf(classTime.getText());
		BellScheduleGUI.lunchPd = Integer.valueOf(lunchPd.getText());
		BellScheduleGUI.lunchLength = Integer.valueOf(lunchLength.getText());
		BellScheduleGUI.SSEPLength = Integer.valueOf(SSEPLength.getText());
		BellScheduleGUI.SSEPPd = Integer.valueOf(SSEPPd.getText());
		BellScheduleGUI.schoolStart = convertTime(schoolStart.getText());
		BellScheduleGUI.schoolEnd = convertTime(schoolEnd.getText());
		BellScheduleGUI.secondary();
	}

	public int convertTime(String time){
		int finalTime = 0;
		String[] times = time.split(":");
		finalTime+=Integer.valueOf(times[0])*60;
		return finalTime + Integer.valueOf(times[1]);
	}
	public void itemStateChanged(ItemEvent e){
		Object source = e.getItemSelectable();
		if(source == assembly){
			BellScheduleGUI.assembly = true;
			assemblyLength.setEditable(true);
			assemblyPd.setEditable(true);
			guiFrame.repaint();
		}
		if (e.getStateChange() == ItemEvent.DESELECTED){
			BellScheduleGUI.assembly = false;
			assemblyLength.setEditable(false);
			assemblyPd.setEditable(false);
			guiFrame.repaint();
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0)  {

		if(arg0.getActionCommand().equals(submitStr)) {


			try{
				scanText();
			} catch (IOException ex){
			}

			System.out.println("Submitted");

			guiFrame.setVisible(false);


		}
	}

/*	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				window.setTitle("Schedule Builder.");
				window.setResisable(true);
				window.setSize(WIDTH,HEIGHT);
				window.setLocationRelativeTo(null);
				window.setDefaultCloseOperation(JFrame.CLOSE_ON_EXIT);
				window.setVisible(true);
			}
		});
	}*/
}

