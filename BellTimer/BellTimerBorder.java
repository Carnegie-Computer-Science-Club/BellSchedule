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
*/


import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JLabel;
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
	private String message;
	private int counter=0;
	private Calendar now, nextEvent;
	private int schedNum, eventNum, numScheds;
    private SimpleDateFormat sdf;
    private DecimalFormat twoDig;
	private long diffH, diffM, diffS;
    private Schedule today;
    private String eventText;
	private Schedule[] schedArray;


	public static void main(String args[]) throws IOException
	{

		BellTimer run = new BellTimer();
	}


	public void buildSchedule() throws IOException
	{
		// open the file
		String fName = "BellSchedules.txt";
		BufferedReader inStream = new BufferedReader(new FileReader(fName));

		/* first line has one int, the number of schedules
		 * next line has the word "schedule" followed by the schedule name
		 * one line per event: hour, minute, event name
		 * (hour is in 0-23 format)
*/

		numScheds = Integer.parseInt(inStream.readLine());
		schedArray = new Schedule[numScheds];
		int iSched = 0;
		String inString = inStream.readLine();
		while (inString != null)
		{
			if (inString.substring(0,8).equals("schedule"))
			{
				schedArray[iSched] = new Schedule(inString.substring(9));
			}
			inString = inStream.readLine();
			while (inString!=null && !inString.substring(0,8).equals("schedule"))
			{
				schedArray[iSched].addEvent(inString);
				inString = inStream.readLine();
			}
			iSched++;
		}
		inStream.close();
		for (Schedule sch:schedArray)
			System.out.println(sch);
	}



	public BellTimer() throws IOException
	{
		super("Bell Schedule Timer, v2.3");
		// create bell schedule structure
		buildSchedule();

		setSize(WIDTH,HEIGHT);

		// formats for displaying the time
		sdf = new SimpleDateFormat("hh:mm:ss"); // 12 hr format
		twoDig = new DecimalFormat("00"); // display leading zero in hh:mm:ss

		// set up timers (Calendar class objects)
        nextEvent = Calendar.getInstance();
		schedNum = 0; // default to regular schedule
//        if (nextEvent.get(Calendar.DAY_OF_WEEK)==Calendar.WEDNESDAY)
//        	schedNum = 1; // use Advocacy for Wednesdays
		today = schedArray[schedNum];
        eventNum = 0;
		changeEvent(schedNum);
		Border blackline = BorderFactory.createLineBorder(Color.black);
		//this is the panel to which all objects will be added
		JPanel main = new JPanel();
										//use X.AXIS or Y.AXIS
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

		//these panels will be used to divide the screen
		JPanel top = new JPanel();
		JPanel bot = new JPanel();
		JPanel txt = new JPanel();
		JPanel tim = new JPanel();

		top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
		txt.setLayout(new GridLayout(3,0));
		tim.setLayout(new GridLayout(3,0));
		top.setBorder(blackline);
		tim.setBorder(blackline);
		// keep the text layout as big as possible
		txt.setPreferredSize(new Dimension(600, 200));
		tim.setPreferredSize(new Dimension(400, 200));

	    Font font = new Font("sansserif", Font.PLAIN, 64);

		// instantiate Labels for time text and numbers
		nowTxt = new JLabel(" Current Time: ");
		remTxt = new JLabel(" Time Left: ");
		nxtTxt = new JLabel(eventText);
		nowTime = new JLabel();
		remTime = new JLabel("remTime");
		nxtTime = new JLabel("nxtTime");

		nowTime.setFont(font);
		remTime.setFont(font);
		nxtTime.setFont(font);
		nowTxt.setFont(font);
		remTxt.setFont(font);
		nxtTxt.setFont(font);

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


		//add all panels to the main panel
		main.add(top);
		main.add(bot);

		//add the main panel to the frame
		getContentPane().add(main);

        //... Create timer which calls action listener every second..
        //    Use full package qualification for javax.swing.Timer
        //    to avoid potential conflicts with java.util.Timer.
        javax.swing.Timer t = new javax.swing.Timer(1000, new ClockListener());
        t.start();

		setVisible(true);
	}

	private void calcTime()
	{
		// init now with current time and date
        now = Calendar.getInstance();
		// has event occurred (diff<0)
		long diff = nextEvent.getTimeInMillis()-now.getTimeInMillis();
		while (diff<0)
		{
			eventNum++;
			if (eventNum>=today.getNumEvents()) // start the day over
			{
		        if (now.get(Calendar.DAY_OF_WEEK)==Calendar.WEDNESDAY)
		        	schedNum = 1; // use Advocacy for Wednesdays
		        else schedNum = 0;
				eventNum=0;
				today = schedArray[schedNum];
			}
			changeEvent(schedNum);
			nxtTxt.setText(eventText);
	        diff = nextEvent.getTimeInMillis()-now.getTimeInMillis();
		}
		diff = (long)Math.round(diff/1000.); // convert to seconds
		diffH = diff/(60*60);
		long rem = diff%(60*60);
		diffM = rem/60;
		diffS = rem%60;
    }

	// switch to a new schedule or event because of midnight
	// or schedule selection
	private void changeEvent(int sn)
	{
        nextEvent = Calendar.getInstance(); // be sure it is today (rev 1.6)
        nextEvent.set(Calendar.HOUR_OF_DAY,today.getEvent(eventNum).getHour());
        nextEvent.set(Calendar.MINUTE,today.getEvent(eventNum).getMinute());
        nextEvent.set(Calendar.SECOND,0);
        eventText = today.getEvent(eventNum).getText();
	}



    //////////////////// inner class ClockListener
 class ClockListener implements ActionListener
 {
    	public void actionPerformed(ActionEvent e)
    	{
			calcTime();
    		nowTime.setText(sdf.format(now.getTime()));
    		nxtTime.setText(sdf.format(nextEvent.getTime()));
    		remTime.setForeground(Color.red);
    		remTime.setText(twoDig.format(diffH)
					+ ":" + twoDig.format(diffM)
			 	 	+ ":" + twoDig.format(diffS) );
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
			changeEvent(schedNum);
			nxtTxt.setText(eventText);
			// make the selected button change color?setForeground(Color fg)

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
	}

	public void addEvent(String data)
	{
		events.add(new Event(data));
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
 		hour = Integer.parseInt(stuff);
 		stuff = in.next();
 		minute = Integer.parseInt(stuff);
 		eventName = in.nextLine();
	}

	public int getHour() {return hour; }
	public int getMinute() { return minute;}
	public String getText() { return eventName;}

	public String toString()
	{
		return "\nEvent time="+hour+":"+minute+" "+eventName;
	}
}
