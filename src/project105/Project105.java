
package project105;

import java.io.File;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.applet.*;
import java.awt.event.*;

class Clock
{
	@SuppressWarnings("unused")
	public static void main(String args[]) throws MalformedURLException
	{
		Alarm c=new Alarm();
	}
}

class Alarm extends Frame implements ActionListener, Runnable
{
	private static final long serialVersionUID = 1L;
	Calendar time,ac;
	Choice ap=new Choice();
	TextField t=new TextField(10),ch[]=new TextField[2];
	Button select=new Button("Select Tone"),go=new Button("Set"),check=new Button("Check"),snooze=new Button("Snooze"),Default=new Button("Default Tone");
	URL tone;
	Thread alarm;
	volatile boolean stopit=false;
	private FileDialog f;
	private String song;
	private AudioClip chac;
	
	Alarm() throws MalformedURLException
	{
		super("Alarm Clock");
		setSize(300,250);
		setVisible(true);
		this.setResizable(false);
		Toolkit tool=Toolkit.getDefaultToolkit();
		Dimension d=tool.getScreenSize();
		this.setLocation(d.width/2-150, d.height/2-125);
		setLayout(null);
		snooze.setBounds(160,150,70,40);
		snooze.addActionListener(this);
		addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent w){ dispose(); System.exit(0); } } );
		File fil=new File("VTV.au");
		tone = fil.toURI().toURL();
		chac=null;
		chac=Applet.newAudioClip(tone);
		
		for(int i=0;i<2;i++)
		{
			ch[i]=new TextField(3);
			ch[i].addFocusListener( new FocusAdapter() { public void focusGained(FocusEvent f){ if(ch[0].isFocusOwner()){ ch[0].selectAll(); }else{ ch[1].selectAll(); } } } );
		}
		
		ch[0].setText("0");
		add(ch[0]);
		ch[0].setBounds(50, 50, 40, 25);
		
		ch[1].setText("00");
		add(ch[1]);
		ch[1].setBounds(120, 50, 40, 25);
		
		ap.add("AM");
		ap.add("PM");
		add(ap);
		ap.setBounds(190, 50, 50, 20);
		
		add(go);
		go.setBounds(50,150,70,40);
		go.addActionListener(this);
		
		select.setBounds(50, 80, 100, 25);
		add(select);
		select.addActionListener(this);
		
		add(check);
		check.setBounds(160,80,70,25);
		check.addActionListener(this);
		
		add(Default);
		Default.addActionListener(this);
		Default.setBounds(180, 115, 90, 25);
		
		add(t);
		t.setText("Default");
		t.setEditable(false);
		t.setBounds(30, 115, 140, 25);

		ch[0].requestFocus();
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		
		String s=e.getActionCommand();
		if(s.equals("Select Tone"))
		{
			chac.stop();
			check.setLabel("Check");
			boolean sel=false;
			try
			{
				f = new FileDialog(new Frame(),"Select the Alarm Tone",FileDialog.LOAD);
				while(!sel)
				{
					f.setVisible(true);
					if(f.getFile()!=null)
					{
						song = f.getFile();
						if(song.substring(song.length()-3).equalsIgnoreCase(".au"))
						{
							tone = new URL("file:///"+f.getDirectory()+f.getFile());
							chac=null;
							chac=Applet.newAudioClip(tone);	
							sel=true;
							t.setText(f.getFile());
						}
						else if(!song.equals("Default"))
						{
							check.setLabel("Check");
							Error err=new Error("Invalid File");
							err.setVisible(true);
						}
					}
					else
					{
						sel=true;
					}
				}
			}
			catch(Exception ex)
			{
			}
		}
		else if(s.equals("Check"))
		{
			check.setLabel("Fine");
			chac.loop();
		}
		else if(s.equals("Fine"))
		{
			check.setLabel("Check");
			chac.stop();
		}
		else if(s.equals("Snooze"))
		{
			chac.stop();
			snooze.setLabel("Snoozing...");
			check.setLabel("Check");
			Default.setEnabled(true);
			select.setEnabled(true);
			check.setEnabled(true);
		}
		else if(s.equals("Stop"))
		{
			stopit=true;
			chac.stop();
			try 
			{
				alarm.interrupt();
				alarm.join();
			}
			catch (InterruptedException e1) 
			{
			}
			alarm=null;
			go.setLabel("Set");
			remove(snooze);
			ch[0].setEditable(true);
			ch[1].setEditable(true);
			ap.setEnabled(true);
			check.setLabel("Check");
		}
		else if(s.equals("Default Tone"))
		{
			try
			{
				if(!t.getText().equals("Default"))
				{
					t.setText("Default");
					f=null;
					File fil=new File("VTV.au");
					tone = fil.toURI().toURL();
					chac.stop();
					check.setLabel("Check");
					chac=null;
					chac=Applet.newAudioClip(tone);
				}
			}
			catch(Exception w)
			{
			}
		}
		else if(s.equals("Set"))
		{
			stopit=false;
			song=t.getText();
			int h=-1,m=-1;
			try
			{
				h=Integer.parseInt(ch[0].getText());
				m=Integer.parseInt(ch[1].getText());
				throw new Exception("exc");
			}
			catch(Exception exc)
			{
				if(h==-1)
				{
					ch[0].setText("0");
				}
				if(m==-1)
				{
					ch[1].setText("00");
				}
				if(h>11)
					ch[0].setText("0");
				if(m>59)
					ch[1].setText("00");
			}
			ac=Calendar.getInstance();
			if(ap.getSelectedIndex()==1)
				h+=12;
			ac.set(ac.get(Calendar.YEAR), ac.get(Calendar.MONTH), ac.get(Calendar.DATE), h, m);
			alarm=new Thread(this,"Alarm");
			alarm.start();
			go.setLabel("Stop");
			ch[0].setEditable(false);
			ch[1].setEditable(false);
			ap.setEnabled(false);
			check.setLabel("Check");
			chac.stop();
		}
		else if(s.equals("Snoozing..."))
		{
			chac.loop();
			snooze.setLabel("Snooze");
			check.setLabel("Check");
			Default.setEnabled(false);
			select.setEnabled(false);
			check.setEnabled(false);
		}
	}
	
	public void run() 
	{
		try
		{
			int h= Integer.parseInt(ch[0].getText()), m=Integer.parseInt(ch[1].getText()), am_pm=ap.getSelectedIndex();
			while(!stopit)
			{
				time=Calendar.getInstance();
				if(time.get(Calendar.HOUR)==h&&time.get(Calendar.MINUTE)==m&&time.get(Calendar.AM_PM)==am_pm)
				{
					chac.loop();
					check.setLabel("Check");
					add(snooze);
					snooze.requestFocus();
					Default.setEnabled(false);
					select.setEnabled(false);
					check.setEnabled(false);
					Thread.sleep(120000);
					go.requestFocus();
					Default.setEnabled(true);
					select.setEnabled(true);
					check.setEnabled(true);
					snooze.setLabel("Snooze");
					if(check.getLabel().equals("Check"))
						chac.stop();
					if(m<50)
						ch[1].setText(""+(m+10));
					else
					{
						ch[1].setText(""+(m%10));
						if(h!=11)
							ch[0].setText(""+(h+1));
						else
						{
							ch[0].setText("0");
							ap.select((ap.getSelectedIndex()+1)%2);
						}
					}
					h= Integer.parseInt(ch[0].getText());
					m=Integer.parseInt(ch[1].getText());
					am_pm=ap.getSelectedIndex();
					remove(snooze);
					ac=Calendar.getInstance();
					if(ap.getSelectedIndex()==1)
						ac.set(ac.get(Calendar.YEAR), ac.get(Calendar.MONTH), ac.get(Calendar.DATE), h+12, m);
					else
						ac.set(ac.get(Calendar.YEAR), ac.get(Calendar.MONTH), ac.get(Calendar.DATE), h, m);
				}
				else
				{
					long mil1=time.getTimeInMillis();
					long mil2=ac.getTimeInMillis();
					mil2-=mil1;
					if(mil2<0)
					{
						mil2+=86400000;
					}
					Thread.sleep(mil2-(time.get(Calendar.SECOND)*1000));
				}
			}
		}
		catch(InterruptedException ex)
		{
			Default.setEnabled(true);
			select.setEnabled(true);
			check.setEnabled(true);
		}
		catch(Exception ex)
		{			
		}
	}
	
	class Error extends Dialog implements ActionListener
	{
		private static final long serialVersionUID = 1L;
		public Error(String title) 
		{
			super(new Frame(), title, true);
			setSize(300,100);
			Button ok=new Button("OK");
			ok.addActionListener(this);
			add(ok);
			addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent w){ dispose(); } } );
		}
		public void actionPerformed(ActionEvent e) 
		{
			dispose();
		}
		
	}
	
	
}
