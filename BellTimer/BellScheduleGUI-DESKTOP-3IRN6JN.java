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

public class BellScheduleGUI {

	/* TODO
	 *
	 * Create a for loop or algorithm
	 *
	 */

	private static int classNum; //number of class periods
	private static int classTime; //number of minutes per class
	private static int lunchPd; //period before which lunch occurs
	private static int lunchLength; //how long lunch lasts (in minutes)
	private static boolean SSEP; //is there SSEP?
	private static int SSEPLength; //number of minutes for SSEP
	private static int SSEPPd; //period before which SSEP occurs
	private static int schoolStart; //time that school starts (in minutes)
	private static int schoolEnd; //time that school ends (in minutes)
	private static int totalTime; //total length of school
	private static boolean assembly; //is there an assembly?
	private static int assemblyLength; //how long an assembly lasts if there is one
	private static int assemblyPd; //period before assembly
	private static final int passLength = 5; //time between classes
	private static int numPassPd; //number of passing periods
//	private static ArrayList<Integer> events; Obsolete (Replaced by ArrayList<Class> events)
//	private static ArrayList<Integer> schedMin; Obsolete (Replaced by ArrayList<Class> events)
//	private static ArrayList<String> labels; Obsolete (Replaced by ArrayList<Class> events)
	private static ArrayList<Class> events; //Darude [GREEN] Sandstorm//


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
 		events=new ArrayList<Class>();

		scanInput();

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
		PrintWriter file = new PrintWriter("test.txt");
		file.println("schedule test");
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

class ScanGUI{

	private final int WIDTH = 450;
	private final int HEIGHT = 600;
	private JFrame guiFrame = new JFrame();
	private JPanel guiPanel = new JPanel();

	public ScanGUI(){

	}

	public static void main(String args[]) {
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
	}
}