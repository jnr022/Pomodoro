import javax.swing.*;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Pomodoro {

	static class OnePomodoro{
		private int onePom = 1500000;
		
		public int getOnePom() {
			return onePom;
		}
		public void decreaseOnePomByOneSec() {
			this.onePom -= 1000;
		}
	}
	
	static OnePomodoro onePomodoro;
	static boolean isStarted = false;
	static Timer timer;
	static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyy/MM/dd HH:mm:ss");
	static LocalDateTime now;
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Pomodoro");
		JPanel panelOfTimer = new JPanel();
		panelOfTimer.setBounds(270, 30, 100, 60);
		panelOfTimer.setBackground(Color.PINK);
		
		JLabel labelOfTimer = new JLabel("25:00");
		labelOfTimer.setFont(new Font("Serif", Font.PLAIN, 38));
		labelOfTimer.setForeground(Color.WHITE );
		panelOfTimer.add(labelOfTimer);
		
		JLabel titleOfNamePomodoro = new JLabel("Geben Sie einen Namen für Pomodoro ein:");
		titleOfNamePomodoro.setBounds(15, 120, 280, 40);
		
		JTextField nameOfPomodoro = new JTextField();
		nameOfPomodoro.setBounds(15, 155, 278, 30);
		
		JLabel warningToUser = new JLabel("Geben Sie den Pomodoronamen ein und starten Sie.");
		warningToUser.setBounds(15, 270, 350, 100);
		JLabel warningToUser2 = new JLabel("Nach Ablauf wird Ihr Pomodoro gespeichert.");
		warningToUser2.setBounds(15, 286, 350, 100);
		
		JButton button = new JButton("Start");
		button.setBounds(15, 210, 100, 35);
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				warningToUser.setText("Ihr Pomodoro " + nameOfPomodoro.getText());
				warningToUser2.setText("wird nach dem Beenden gespeichert.");
				if(!isStarted) {
					isStarted= true;
					if(onePomodoro == null) {
						onePomodoro = new OnePomodoro();
					}
					File fileOfProgress = new File("pomodoro.txt");
					try {
						if(fileOfProgress.createNewFile()) {
							System.out.println("Datei wurde erstellt: "+ fileOfProgress.getName());
						} else {
							System.out.println("Datei existiert bereits.");
						}
					} catch(Exception e1) {
						System.out.println("Fehler ist aufgetreten.");
						e1.printStackTrace();
					}
					timer = new Timer(1000, new ActionListener() {
						String timerText = "";
						
						@Override
						public void actionPerformed(ActionEvent e) {
							onePomodoro.decreaseOnePomByOneSec();
							if(onePomodoro.getOnePom()<=0) {
								System.out.println("Pomodoro erfolgreich beendet");
								now = LocalDateTime.now();								
								FileWriter writer = null;								
								try {
									writer= new FileWriter(fileOfProgress, true);
								} catch (IOException e1) {
								  
									e1.printStackTrace();
								}
								try {
									writer.append(nameOfPomodoro.getText()+" -> "+ dtf.format(now) + "wurde ein Pomodoro von 25 Minuten beendet.\n");
									warningToUser.setText("Pomodoro wurde gespeichert.");
									warningToUser2.setText("");
									writer.close();
								} catch (IOException e1) { 
									e1.printStackTrace();
								}
								timer.stop();
								isStarted = false;
								onePomodoro = new OnePomodoro();
							}
							int minute = (onePomodoro.getOnePom()/1000) /60;
							int second = (onePomodoro.getOnePom()/1000) % 60;			
							if(minute<10 && second<10) {
								timerText= "0"+minute+":0"+second;
							} else if ( minute <10 && !(second<10)) {
								timerText = "0"+minute+":"+second;
							} else if (second<10) {
								timerText= minute+":0"+second;
							} else timerText = minute+":"+second;
							labelOfTimer.setText(timerText);
						}
					});
					timer.start();
				}	
			}
		});
		
		JButton button2 = new JButton("Stopp");
		button2.setBounds(130, 210, 100, 35);
		button2.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				if(onePomodoro==null) return;
				timer.stop();
				isStarted=false;
				if(onePomodoro.getOnePom()!=(new OnePomodoro().getOnePom())) {
					warningToUser.setText("Pomodoro wurde angehalten.");
					warningToUser2.setText("Anhalten von Pomodoro wird nicht empfohlen.");
				} else {
					warningToUser.setText("Pomodoro kann nicht vor dem Start angehalten werden.");
					warningToUser2.setText("");
				}	
			}
		});	
		JButton button3 = new JButton("Abbrechen");
		button3.setBounds(245, 210, 100, 35);
		button3.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {	
				if(onePomodoro==null) return;
				timer.stop();
				isStarted=false;
				onePomodoro= new OnePomodoro();
				String timerText= "";
				int minute = (onePomodoro.getOnePom()/1000) /60;
				int second = (onePomodoro.getOnePom()/1000) % 60;
				if(minute<10 && second<10) {
					timerText= "0"+minute+":0"+second;
				} else if ( minute <10 && !(second<10)) {
					timerText = "0"+minute+":"+second;
				} else if (second<10) {
					timerText= minute+":0"+second;
				} else timerText = minute+":"+second;
				labelOfTimer.setText(timerText);
				warningToUser.setText("Pomodoro wurde abgebrochen");
				warningToUser2.setText("");
			}
		});
		
		JButton progress = new JButton(" Verlauf");
		progress.setBounds(15, 30, 105, 40);
		progress.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new java.io.File("pomodoro.txt"));
				} catch (IOException e1) {
					warningToUser.setText("Datei wurde noch nicht erstellt.");
					warningToUser2.setText("");
					e1.printStackTrace();
				}
			}
		});
		
		frame.add(nameOfPomodoro);
		frame.add(titleOfNamePomodoro);
		frame.add(progress);
		frame.add(warningToUser);
		frame.add(warningToUser2);
		frame.add(button3);
		frame.add(button2);
		frame.add(panelOfTimer);
		frame.add(button);
		frame.setLayout(null);
		frame.setSize(400,400);
		frame.setVisible(true);
	}
}
