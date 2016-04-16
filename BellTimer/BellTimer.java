/**
 * @(#)BellTimer.java
 *
 * belltimer application to schedule class bell schedule
 * Richard Schenke
 * version 1.0 2011/05/04
 * version 1.1 2011/05/09 implemented as applet, regular sched only
 * version 1.2 2011/05/10 application, data driven by BellSchedule.java
 *							renamed as BellTimer2
 * version 1.3 2011/05/12 expanded to 1024 x 768. Schedule change boxes
 *							are now data-driven.
 * version 1.4 2011/05/12 Remove flicker by using double buffer video
 * version 2.0 2011/05/27 Implemented with swing and JFrame
 * version 2.1 2011/08/26 Fixed "next day" problem.
 * version 2.2 2012/07/28 Combined into one file, BellSchedule eliminated,
 *							and created a .jar
 * version 2.3 2012/11/14 Eliminated advocacy schedule, fixed button names
 * version 3.0 2013/10/30 Showing official Attendance time
 *						  Removed numScheds from data file, added attendance times.
 *						  Added input checks for schedule data.
 *						  Made the cmd window close when the graphic frame is closed.
 *						  Calculate event start ms one time
*/


import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.border.Border;
import java.awt.event.*;

import java.io.*;


public class BellTimer extends JFrame
{

	private static final int WIDTH = 1024;
	private static final int HEIGHT = 768;

	//these are the buttons that can be clicked
	private JButton[] skeds;

	// these are data display fields
	private JLabel nowTxt, nowTime, remTxt, remTime, nxtTxt, nxtTime;

	// schedule and time data
	private final int MAX_SCHEDS = 20;
	private String message;
	private int counter=0;
	private Calendar now, nextEvent;
	private long nxtMillisec;
	private int schedNum, eventNum, numScheds;
    private SimpleDateFormat sdf;
    private DecimalFormat twoDig;
	private long diffH, diffM, diffS;
    private Schedule today;
//    private String eventText;
	private Schedule[] schedArray;
	// for official attendance taking time
	private Calendar ADAstart, ADAstop;
	private int ADAstartHr,ADAstartMin,ADAstopHr,ADAstopMin;
	private long ADAstartMillisec, ADAstopMillisec;
	private boolean ADAFlash; // toggles red and blue
	private boolean ADATime; // turns on blink during ADA reporting time


	public static void main(String args[]) throws IOException
	{

		BellTimer run = new BellTimer();
		run.addWindowListener(new WindowAdapter()
			{public void windowClosing(WindowEvent e)
				{System.exit(0);}});

	}



	public BellTimer() throws IOException
	{
		super("Bell Schedule Timer, v3.0");
		// create bell schedule structure
		buildSchedule();

		setSize(WIDTH,HEIGHT);
		buildPanel();

		// formats for displaying the time
		sdf = new SimpleDateFormat("hh:mm:ss"); // 12 hr format
		twoDig = new DecimalFormat("00"); // display leading zero in hh:mm:ss

		schedNum = 0; // default to regular schedule
		today = schedArray[schedNum];
        eventNum = 0;
		// set up timers (Calendar class objects)
		changeEvent();

        //... Create timer which calls action listener every second..
        //    Use full package qualification for javax.swing.Timer
        //    to avoid potential conflicts with java.util.Timer.
        javax.swing.Timer t = new javax.swing.Timer(1000, new ClockListener());
        t.start();

		setVisible(true);
	}

	public void buildSchedule() throws IOException
	{
		int lastEventTime = 0;
		// open the file
		String fName = "BellSchedules.txt";
		BufferedReader inStream = new BufferedReader(new FileReader(fName));
		// First line contains the 4 ints for ADA reporting times
		String inString = inStream.readLine();
 		Scanner in = new Scanner(inString);
 		String stuff;
 		stuff = in.next();
 		ADAstartHr = Integer.parseInt(stuff);
 		stuff = in.next();
 		ADAstartMin = Integer.parseInt(stuff);
 		stuff = in.next();
 		ADAstopHr = Integer.parseInt(stuff);
 		stuff = in.next();
 		ADAstopMin = Integer.parseInt(stuff);
 		System.out.println("ADA Time: starts at "+ADAstartHr+":"+ADAstartMin+", stops at "+ADAstopHr+":"+ADAstopMin);
		ADAinit();

		/* next line has the word "schedule" followed by the schedule name
		 * one line per event: hour, minute, event name
		 * (hour is in 0-23 format)
*/

		schedArray = new Schedule[MAX_SCHEDS];
		int iSched = 0;
		inString = inStream.readLine();
		while (inString != null)
		{
			if (inString.substring(0,8).equals("schedule"))
			{
				schedArray[iSched] = new Schedule(inString.substring(9));
				lastEventTime = 0;
			}
			inString = inStream.readLine();
			while (inString!=null && !inString.substring(0,8).equals("schedule"))
			{
				lastEventTime = schedArray[iSched].addEvent(inString,lastEventTime);
				inString = inStream.readLine();
			}
			iSched++;
		}
		inStream.close();
		numScheds = iSched;
	}

	private void buildPanel()
	{


		Border blackline = BorderFactory.createLineBorder(Color.black);
		Border redline = BorderFactory.createLineBorder(Color.red);
		//this is the panel to which all objects will be added
		JPanel main = new JPanel();
										//use X.AXIS or Y.AXIS
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

		//these panels will be used to divide the screen
		JPanel top = new JPanel();
		JPanel bot = new JPanel();
		JPanel txt = new JPanel();
		JPanel tim = new JPanel();

		top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
		txt.setLayout(new GridLayout(3,0));
		tim.setLayout(new GridLayout(3,0));


		// keep the text layout as big as possible
		txt.setPreferredSize(new Dimension(600, 200));
		tim.setPreferredSize(new Dimension(400, 200));

	    Font font = new Font("sansserif", Font.PLAIN, 64);

		// instantiate Labels for time text and numbers
		nowTxt = new JLabel(" Current Time: ");
		remTxt = new JLabel(" Time Left: ");
		nxtTxt = new JLabel("Next event");
		nowTime = new JLabel();
		remTime = new JLabel("remTime");
		nxtTime = new JLabel("nxtTime");

		nowTime.setFont(font);
		remTime.setFont(font);
		nxtTime.setFont(font);
		nowTxt.setFont(font);
		remTxt.setFont(font);
		nxtTxt.setFont(font);
		nowTime.setBorder(blackline);
		remTime.setBorder(redline);
		nxtTime.setBorder(blackline);
		nowTxt.setBorder(blackline);
		remTxt.setBorder(redline);
		nxtTxt.setBorder(blackline);

		nowTime.setHorizontalAlignment(SwingConstants.TRAILING);
		remTime.setHorizontalAlignment(SwingConstants.TRAILING);
		nxtTime.setHorizontalAlignment(SwingConstants.TRAILING);

		// add the labels to the text panel, and time
		txt.add(nowTxt);
		txt.add(remTxt);
		txt.add(nxtTxt);
		tim.add(nowTime);
		tim.add(remTime);
		tim.add(nxtTime);

		// add the txt and tim panel to top
		top.add(txt);
		top.add(tim);

		//instantiate a new JButton for each kind of schedule
		skeds = new JButton[numScheds];
		for (int i=0; i<numScheds; i++)
		{
			skeds[i] = new JButton(schedArray[i].getSchedName());
			skeds[i].addActionListener(new SkedListener());
			String iVal = String.valueOf(i);
			skeds[i].setActionCommand(iVal);
			//add each button to the bottom panel
			bot.add(skeds[i]);
		}
		skeds[schedNum].setBackground(Color.yellow);
		//add all panels to the main panel
		main.add(top);
		main.add(bot);

		//add the main panel to the frame
		getContentPane().add(main);
	}




	private void calcTime()
	{
		// init now with current time and date
        now = Calendar.getInstance();
		long nowMillisec = now.getTimeInMillis();
		// has event occurred (diff<0)
		long diff = nxtMillisec-nowMillisec;
		while (diff<0)
		{
			eventNum++;
			if (eventNum>=today.getNumEvents()) // start the day over
			{
		        schedNum = 0;  // default to regular schedule
				eventNum=0;
				today = schedArray[schedNum];
				// make the selected button change color
				for(JButton jb:skeds)
					jb.setBackground(null);
				skeds[schedNum].setBackground(Color.yellow);
				ADAinit();
			}
			changeEvent();
			diff = nxtMillisec-nowMillisec;
		}
		diff = (long)Math.round(diff/1000.); // convert to seconds
		diffH = diff/(60*60);
		long rem = diff%(60*60);
		diffM = rem/60;
		diffS = rem%60;

		long start = ADAstartMillisec-nowMillisec;
		long stop  = ADAstopMillisec -nowMillisec;
		if (start<0&&stop>0) ADATime=true; else ADATime=false;
    }

	// switch to a new schedule or event because of midnight
	// or schedule selection
	private void changeEvent()
	{
        nextEvent = Calendar.getInstance(); // be sure it is today (rev 1.6)
        nextEvent.set(Calendar.HOUR_OF_DAY,today.getEvent(eventNum).getHour());
        nextEvent.set(Calendar.MINUTE,today.getEvent(eventNum).getMinute());
        nextEvent.set(Calendar.SECOND,0);
        nxtTxt.setText(today.getEvent(eventNum).getText());
		nxtMillisec = nextEvent.getTimeInMillis();
	}

	// Set the start and stop time for official state attendance reporting
	// run this at program start and at start of a new day
	private void ADAinit()
	{
		ADAstart = Calendar.getInstance();
		ADAstart.set(Calendar.HOUR_OF_DAY,ADAstartHr);
		ADAstart.set(Calendar.MINUTE,ADAstartMin);
		ADAstart.set(Calendar.SECOND,0);
		ADAstartMillisec = ADAstart.getTimeInMillis();
		ADAstop  = Calendar.getInstance();
		ADAstop.set(Calendar.HOUR_OF_DAY,ADAstopHr);
		ADAstop.set(Calendar.MINUTE,ADAstopMin);
		ADAstop.set(Calendar.SECOND,0);
		ADAstopMillisec  = ADAstop.getTimeInMillis();
	}



    //////////////////// inner class ClockListener
 class ClockListener implements ActionListener
 {
    	public void actionPerformed(ActionEvent e)
    	{
			calcTime();
    		nowTime.setText(sdf.format(now.getTime()));
    		nxtTime.setText(sdf.format(nextEvent.getTime()));
    		if (ADATime&&ADAFlash)
    		{
    			remTime.setForeground(Color.blue);
    			remTime.setText("ADA time");
    		}
    		else
    		{
    			remTime.setForeground(Color.red);
	    		remTime.setText(twoDig.format(diffH)
						+ ":" + twoDig.format(diffM)
				 	 	+ ":" + twoDig.format(diffS) );
    		}
    		ADAFlash = !ADAFlash;
/*
    		if (ADAFlash&&ADATime)
    			remTime.setForeground(Color.blue);
    		else
    			remTime.setForeground(Color.red);
    		ADAFlash = !ADAFlash;
    		remTime.setText(twoDig.format(diffH)
					+ ":" + twoDig.format(diffM)
			 	 	+ ":" + twoDig.format(diffS) );
*/
    		repaint();
    	}
 }

    //////////////////// inner class skedListener
 class SkedListener implements ActionListener
 {
    	public void actionPerformed(ActionEvent e)
    	{
 			message = e.getActionCommand();

			schedNum = Integer.parseInt(e.getActionCommand());
			today = schedArray[schedNum];
			eventNum = 0;
			changeEvent();
			// reset all the buttons to white
			for(JButton jb:skeds)
				jb.setBackground(null);
			// make the selected button change color
			skeds[schedNum].setBackground(Color.yellow);
    		repaint();
    	}
 }

} // end of BellTimer class

/**
 * @(#)Schedule Class
 *
 * a schedule is a list of events that happen throughout
 * a particular type of day.
 *
 * Richard Schenke
 * version 2.2 2012 July 26
 */
class Schedule
{
	private ArrayList<Event> events;
	private String schedName;

	public Schedule(String sName)
	{
		events = new ArrayList<Event>();
		schedName = sName;
		System.out.print(this);
	}

	public int addEvent(String data, int last)
	{
		Event ev = new Event(data);
		int nextTime = ev.getHour()*60+ev.getMinute();
		if (nextTime>last)
			events.add(ev);
		else
		{
			System.out.println("\n   ERROR events not in order.");
			System.exit(3);
			nextTime = 0;
		}
		return nextTime;
	}

	public Event getEvent(int i){ return this.events.get(i); }

	public String getSchedName() { return schedName; }

	public int getNumEvents() { return events.size(); }
	public String toString()
	{
		String schedString = "\n"+schedName +" Schedule";
		for (Event e:events)
			schedString += e.toString();
		return schedString;
	}
}

/**
 * @(#) Event class
 *
 * an event is a time when a change occurs, such as start or
 * end of a class period, and a label.
 *
 * Richard Schenke
 * version 2.2 2012 July 26
 */
class Event
{
	private int hour, minute;
	private String eventName;

 /* parse the line - first int is hour, second int is minute,
 * remainder of the line is the event name
 */
	public Event(String line)
	{
 		String stuff;
 		Scanner in = new Scanner(line);
 		stuff = in.next();
 		hour = isInteger(stuff);
 		// TODO validate hour 0<=h<=24
 		stuff = in.next();
 		minute = isInteger(stuff);
 		// TODO validate minute 0<=min<=60
 		eventName = in.nextLine();
 		System.out.print(this);
	}

	public int getHour() {return hour; }
	public int getMinute() { return minute;}
	public String getText() { return eventName;}

	public String toString()
	{
		return "\nEvent time="+hour+":"+minute+" "+eventName;
	}

	private int isInteger(String str)
	{
		try {      	return Integer.parseInt(str); 	}
		catch (NumberFormatException nfe)
 		{
 			System.out.println("\n   ERROR    "+str+" is not an integer");
 			System.exit(2);
	        return -1;
 		}
    }
}
