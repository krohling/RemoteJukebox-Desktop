import java.lang.Runnable;
import java.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.io.*;
import javax.microedition.rms.*;
import com.nextel.ui.*;

public class JukeBox extends MIDlet{
	public final static Font PROPORTIONAL_BOLD_SMALL = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL);
	public final static Font PROPORTIONAL_BOLD_MEDIUM = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
	public final static Font PROPORTIONAL_PLAIN_SMALL = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
	public final static Font PROPORTIONAL_PLAIN_MEDIUM = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
	public final static Font MONOSPACE_PLAIN_SMALL = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL);
	public final static Font MONOSPACE_ITALIC_SMALL = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_ITALIC, Font.SIZE_SMALL);
	public final static Font MONOSPACE_BOLD_MEDIUM = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_MEDIUM);

	private Display myDisplay;
	private MenuScreen myMenu;
	private PlayListScreen myPLScreen;
	private ServerScreen mySScreen;
	private ProgressScreen progressScreen;
	private SongDisplay songDisplay;
	private ControlScreen controlScreen;
	private Displayable Previous;
	
	private DataHandler Handler;
	private boolean connected, paused = false;
	private String Server, SongName = "";
	
	public JukeBox() {
		String tempList[] = { "No List Loaded" };
		myDisplay = Display.getDisplay(this);
		OHandset.setMIDlet(this);
		connected = false;
		System.out.println("1");
		myMenu = new MenuScreen(this);
		myPLScreen = new PlayListScreen(this, tempList);
		mySScreen = new ServerScreen(this);
		progressScreen = new ProgressScreen("", this);
		songDisplay = new SongDisplay(this);
		controlScreen = new ControlScreen(this);}
		
	protected void startApp() { 
		if (!paused) {
			SplashScreen splash = new SplashScreen(this, "Splash.png", 5000);
			myDisplay.setCurrent(splash);}
		else
			setPrevious(); }

	protected void pauseApp() {
		paused = true; }

	public void init() {
		RecordStore record;
		byte[] tempByte = null;

		setSScreen();

		try {
			Handler = new DataHandler(this, Server);
			new Thread(Handler).start();}
		catch(IOException e) {
			DisplayError("Unable To Open Data Listening Port");}

		if(RecordStore.listRecordStores() != null) {
			try {
				record = RecordStore.openRecordStore("serverinfo", false);
				tempByte = record.getRecord(1);
				record.closeRecordStore();
				mySScreen.setIPText(new String(tempByte)); }
			catch(RecordStoreFullException f) { }
			catch(RecordStoreException e) { } } }

	public void getPlayList() {
		setProgress("Retrieving PlayList...\n One Moment.");
		sendData("playlist\n"); }

	public void setMenu() {
		Previous = (Displayable) myMenu;
		myDisplay.setCurrent(myMenu);}
		
	public void setPLScreen() {
		Previous = (Displayable) myPLScreen;
		myDisplay.setCurrent(myPLScreen);}
		
	public void setSScreen() {
		Previous = (Displayable) mySScreen;
		myDisplay.setCurrent(mySScreen);}
		
	public void setSongDisplay() {
		Previous = (Displayable) songDisplay;
		myDisplay.setCurrent(songDisplay);}

	public void setControlScreen() {
		Previous = (Displayable) controlScreen;
		controlScreen.start();
		myDisplay.setCurrent(controlScreen);}

	public void setPrevious() {
		myDisplay.setCurrent(Previous);}
		
	public void setProgress(String message) {
		if(connected) {
			progressScreen.setMessage(message);
			myDisplay.setCurrent(progressScreen); } }

	protected void destroyApp(boolean unconditional) {
		closeConnection();
		System.out.println("sent closing"); }
		
	public void Close() {
		if(connected)
			closeConnection();
		notifyDestroyed();}
		
	public void initConnection(String serverName) throws IOException{
		mySScreen.setIPText(serverName);
		if (connected) {
			System.out.println("init: closing");
			closeConnection();}
		Server = "socket://" + serverName + ":7777";
		Handler = new DataHandler(this, serverName);
		System.gc();
		connected = true;}

	public void closeConnection() {
		sendData("exiting\n");
		try {
			connected = false;
			Handler.stop();}
		catch(IOException e) { } }

	public void sendData(String Data) {
		if (connected)
			System.out.println("Sending: " + Data);
			try {
				Handler.SendData(Data);}
			catch(IOException e) {
				DisplayError("ERROR SENDING DATA");} }
				
	public synchronized void updateSongList(String list) {
		String[] tempList = null;
		try {
			tempList = Split(list);
			if (tempList != null & tempList.length > 0)
				songDisplay.setSongList(tempList);}
		catch(Exception e) { } }
				
	public synchronized void updatePlayList(String list) {
		String[] temp = { "No List Available" };
		String[] tempList = null;
		System.out.println("list: " + list);
		if (list == "") {
			System.out.println("1");
			updatePLScreen(temp);
			setPLScreen();}
		else {
			System.out.println("2");
			try {
				tempList = Split(list);
				if (tempList != null & tempList.length > 0) {
					updatePLScreen(tempList);
					setPLScreen();}
				else {
					updatePLScreen(temp);
					setPLScreen();}
				System.out.println("3"); }
			catch(Exception e) {
				System.out.println("4");
				updatePLScreen(temp);
				setPLScreen();} } }
 
 	public String[] Split(String list) {
		int x, y, index, lastindex = 0;
		String temp;
		String tempList[] = null;

		for(x = 0, y = 0; x < list.length(); x++)
			if(list.charAt(x) == '\n'){
				index = x - 1;
				temp = list.substring(lastindex, index);
				if (temp.length() > 0)
					y++;
				else
					x = list.length();
				lastindex = index + 2;}
			
		tempList = new String[y];
		index = 0;
		lastindex = 0;
		
		for(x = 0, y = 0; x < list.length(); x++)
			if(list.charAt(x) == '\n'){
				if (x > 0)
					index = x - 1;
				temp = list.substring(lastindex, index);
				if (temp.length() > 0) {
					tempList[y] = temp;
					y++;}
				else
					x = list.length();
				lastindex = index + 2;}
		return tempList;}

	private void updatePLScreen(String[] listinfo) { 
		myPLScreen = new PlayListScreen(this, listinfo);
		System.gc();}

	public void saveServer(String serverName) {
		byte[] serverBytes = serverName.getBytes();
		RecordStore record = null;
		setSScreen();
		try {
			record = RecordStore.openRecordStore("serverinfo", true);
			record.setRecord(1, serverBytes, 0, serverBytes.length);}
		catch(RecordStoreFullException f) { }
		catch(RecordStoreException e) {
			try {
				record.addRecord(serverBytes, 0, serverBytes.length);}
			catch(RecordStoreException f) {
				DisplayError("ERROR SAVING SERVER DATA");} }
		try {
			record.closeRecordStore();}
		catch(Exception e) { } }
		
	public void DisplayError(String Message) {
		Alert temp = new Alert("ERROR", Message, null, AlertType.ERROR);
		temp.setTimeout(Alert.FOREVER);
		myDisplay.setCurrent(temp);
		System.gc();}
		
	public void notifySongTitle(String Title) {
		int endIndex = 0;
		for(int x = 0; x < Title.length(); x++)
			if (Title.charAt(x) == '\n') {
				 endIndex = x;
				 x = Title.length();}
		SongName = Title.substring(0, endIndex);
		controlScreen.setMessage("Playing: " + SongName);}

	public void notifySongStopped() {
		controlScreen.setMessage("Stopped: " + SongName);}

	public void notifyPlayListSelected(String text) {
		int endIndex = 0;
		for(int x = 0; x < text.length(); x++)
			if (text.charAt(x) == '\n') {
				 endIndex = x;
				 x = text.length();}
		myMenu.updatePlaylistDisplay(text.substring(0, endIndex));}

	public void changeMenuDisplay(String display) {
		myMenu.changeDisplay(display);}

	public void Reset() {
		String[] temp = { "No List Loaded" };
		String[] temp2 = { "SongList Not Loaded" };
		changeMenuDisplay("");
		songDisplay.setSongList(temp2);
		controlScreen.setMessage("");
		updatePLScreen(temp); } }

	
class MenuScreen extends OCompositeScreen{
	OTicker playlistDisplay;
	
	MenuScreen(final JukeBox Parent) {
		super("REMOTE JUKEBOX", Parent.PROPORTIONAL_BOLD_MEDIUM, 5);
		OPushButton tempButton;
		OSoftKey Exit;

		playlistDisplay = new OTicker("", 13, getWidth(), Parent.MONOSPACE_PLAIN_SMALL, 8);
		add(playlistDisplay, 2, 0, Graphics.HCENTER);

		tempButton = new OPushButton("PlayList", Parent.PROPORTIONAL_BOLD_SMALL, 5, 3, "PlayList");
		tempButton.setAction(new OCommandAction () {
			public void performAction() {
	  			Parent.setPLScreen(); } } );
 		add(tempButton, 2, 2, Graphics.HCENTER);
	  			
		tempButton = new OPushButton("Server", Parent.PROPORTIONAL_BOLD_SMALL, 5, 3, "Server");
		tempButton.setAction(new OCommandAction () {
			public void performAction() {
	  			Parent.setSScreen(); } } );
 		add(tempButton, 1, 3, Graphics.HCENTER);

		tempButton = new OPushButton("SongList", Parent.PROPORTIONAL_BOLD_SMALL, 5, 3, "SongList");
		tempButton.setAction(new OCommandAction () {
			public void performAction() {
	  			Parent.setSongDisplay(); } } );
 		add(tempButton, 3, 3, Graphics.HCENTER);

		tempButton = new OPushButton("Controls", Parent.PROPORTIONAL_BOLD_SMALL, 5, 3, "Controls");
		tempButton.setAction(new OCommandAction () {
			public void performAction() {
	  			Parent.setControlScreen(); } } );
 		add(tempButton, 2, 4, Graphics.HCENTER);
	  			
		Exit = new OSoftKey("Exit");
	 	Exit.setAction(new OCommandAction () {
			public void performAction() {
				Parent.Close();} } );
		addSoftKey(Exit, OSoftKey.LEFT);}

	public void updatePlaylistDisplay(String playlist) {
		playlistDisplay.setMessage("Current PlayList: " + playlist);}

	public void changeDisplay(String display) {
		playlistDisplay.setMessage(display); } }
		
class PlayListScreen extends OCompositeScreen{

	PlayListScreen(final JukeBox Parent, String[] tempList) {
		super("SELECT PLAYLIST", Parent.PROPORTIONAL_BOLD_MEDIUM, 2);
		final ODropDownList playList;
		OPushButton tempButton;
		OSoftKey Back;

		playList = new ODropDownList(tempList, Parent.PROPORTIONAL_PLAIN_SMALL);
		add(playList, 0, 1, Graphics.LEFT);
		add(new OLabel(" ", Parent.PROPORTIONAL_PLAIN_SMALL), 0, 2, Graphics.HCENTER);
		add(new OLabel(" ", Parent.PROPORTIONAL_PLAIN_SMALL), 0, 3, Graphics.HCENTER);
		tempButton = new OPushButton("Reload", Parent.PROPORTIONAL_BOLD_SMALL, 5, 3, "Reload");
		tempButton.setAction(new OCommandAction () {
			public void performAction() {
				Parent.getPlayList();} } );
		add(tempButton, 0, 4, Graphics.HCENTER);
		
		tempButton = new OPushButton("Select", Parent.PROPORTIONAL_BOLD_SMALL, 5, 3, "Select");
		tempButton.setAction(new OCommandAction () {
			public void performAction() {
					String temp = (String) playList.getSelectedValue();
					if (temp != "No List Loaded" & temp != "No List Available")
						Parent.sendData("load\n" + String.valueOf(playList.getSelectedIndex()) + "\n");} } );
		add(tempButton, 1, 4, Graphics.HCENTER);
		
		Back = new OSoftKey("Menu");
		Back.setAction(new OCommandAction() {
			public void performAction() {
				Parent.setMenu();} } );
		addSoftKey(Back, OSoftKey.LEFT); } }


class ServerScreen extends OCompositeScreen{
	OTextField serverField;
	
	ServerScreen(final JukeBox Parent) {
		super("Edit Server Info", Parent.PROPORTIONAL_BOLD_MEDIUM, 2);
		OPushButton tempButton;
		OSoftKey Back;

		serverField = new OTextField(15, Parent.PROPORTIONAL_PLAIN_MEDIUM, OTextDocument.NUMERIC);
		serverField.allow(".");
		add(serverField, 0, 0, Graphics.LEFT);
		add(new OLabel("\n", Parent.PROPORTIONAL_PLAIN_SMALL), 0, 1, Graphics.HCENTER);

		tempButton = new OPushButton("Connect", Parent.PROPORTIONAL_BOLD_SMALL, 5, 3, "Connect");
		tempButton.setAction(new OCommandAction () {
			public void performAction() {
				String temp = serverField.getText();
				Parent.saveServer(temp);
				try {
					Parent.Reset();
					Parent.initConnection(temp);
					Parent.getPlayList();}
				catch(IOException e) {
					Parent.DisplayError("NETWORK NOT AVAILABLE");} } } );
		add(tempButton, 0, 2, Graphics.HCENTER);

		tempButton = new OPushButton("New", Parent.PROPORTIONAL_BOLD_SMALL, 5, 3, "New");
		tempButton.setAction(new OCommandAction () {
			public void performAction() {
				setIPText(""); } } );
		add(tempButton, 1, 2, Graphics.HCENTER);

		Back = new OSoftKey("Menu");
	 	Back.setAction(new OCommandAction () {
			public void performAction() {
				 Parent.setMenu();} } );
		addSoftKey(Back, OSoftKey.LEFT);}
		
	public void setIPText(String text) {
		try {
			serverField.setText(text);}
		catch(Exception e) { } } }


class ProgressScreen extends OCompositeScreen {
	OLabel Message;
		
	ProgressScreen(String Text, final JukeBox Parent) {
		super( "One Moment...", Parent.PROPORTIONAL_BOLD_MEDIUM, 1 );
		OAnimation animation;
		OSoftKey Stop;

		String[] imageList = { "/Phone1.png", "/Phone2.png", "/Phone3.png", "/Phone4.png" };
		Stop = new OSoftKey("Stop");
	 	Stop.setAction(new OCommandAction () {
			public void performAction() {
				 Parent.setPrevious();} } );
		addSoftKey(Stop, OSoftKey.LEFT);
		try {
			animation = new OAnimation(imageList, 750);
			add(animation, 0, 0, Graphics.HCENTER );}
		catch(IOException e) { }
		Message = new OLabel(Text, OUILook.PLAIN_SMALL );
		add(Message, 0, 1, Graphics.LEFT); }

	public void setMessage(String text) {
		Message.setLabel(text); } }
			
class DataHandler implements Runnable{
		JukeBox Parent;
		StreamConnection SC;
		InputStreamReader in;
		OutputStreamWriter out;

		boolean listen;
		
		DataHandler(JukeBox Parent, String remoteIP) throws IOException{
			SetConnection(remoteIP);

			this.Parent = Parent;
			listen = true;}

		private void SetConnection(String remoteIP) {
			SC = (StreamConnection) Connector.open(remoteIP);
			in = new InputStreamReader(SC.openInputStream());
			out = new OutputStreamWriter(SC.openOutputStream());}
			
				
		public void run() {
			StringBuffer data;
			char c;
			int i;

			while(listen) {
				i = 0;
				try {
					while((i = in.read()) != -1) {
						c = (char) i;
						data.append(c);}
					
					CommandHandler(data.toString()); }
				catch(IOException e) {
					stop();
					Parent.DisplayError("CONNECTION TO SERVER LOST:\nDATA LISTENER"); } } }

		public void SendData(String data) throws IOException{
			out.write(data);}

		private void CommandHandler(String data) {
			String command, arg;
			int argStart = 0, count = 0;

			for(count = 0; count < data.length(); count++)
				if(data.charAt(count) == '\n') {
					argStart = count + 1;
					count = data.length();}

			command = data.substring(0, argStart - 2);
			arg = data.substring(argStart, data.length());
			System.out.println(data);
			if(command.equals("playlist"))
				Parent.updatePlayList(arg);
			else if(command.equals("songtitle")) {
				Parent.notifySongTitle(arg);
				OHandset.beep();}
			else if(command.equals("songlist"))
				Parent.updateSongList(arg);
			else if(command.equals("stopped"))
				Parent.notifySongStopped();
			else if(command.equals("confirmplaylist")) {
				Parent.notifyPlayListSelected(arg);
				OHandset.beep(); }
			else if(command.equals("exiting")){
				Parent.closeConnection();
				Parent.Reset();
				Parent.changeMenuDisplay("Server Disconnected!");
				Parent.DisplayError("Server Disconnected.");}
			else if(command.equals("error")) {
				String[] temp;
				temp = Parent.Split(arg);
				Parent.DisplayError(temp[0]); } }

		public synchronized void stop() {
			try {
				in.close();
				out.close();
				SC.close();}
			catch(IOException e) { }
			listen = false;} }
			
class SongDisplay extends OCompositeScreen {
	OScrollList songList;
	OTicker ticker;

	int listLength = 0;
	
	SongDisplay(final JukeBox Parent) {
		super( "Song List", Parent.PROPORTIONAL_BOLD_MEDIUM, 1 );
		OSoftKey tempKey;

		String[] tempList = { "SongList Not Loaded" } ;
		ticker = new OTicker("", 20, getWidth(), Parent.MONOSPACE_PLAIN_SMALL, 16);

		songList = new OScrollList(100, 50, Parent.MONOSPACE_ITALIC_SMALL, Parent.MONOSPACE_PLAIN_SMALL);
		songList.populate(tempList);
		songList.setSelectionAction(new Runnable() {
			public void run() {
				setMessage((String) songList.getSelectedValue());} } );

		resetMessage();
		tempKey = new OSoftKey("Menu");
		tempKey.setAction(new OCommandAction() {
			public void performAction() {
				Parent.setMenu();} } );
		addSoftKey(tempKey, OSoftKey.LEFT);
				
		tempKey = new OSoftKey("Select");
		tempKey.setAction(new OCommandAction() {
			public void performAction() {
				if(listLength > 0) {
					System.out.println("sent playtrack");
					Parent.sendData("playtrack" + "\n" + String.valueOf(songList.getSelectedIndex()) + "\n");} } } );
		addSoftKey(tempKey, OSoftKey.RIGHT);

		add(ticker, 0, 0, Graphics.HCENTER);
		add(songList, 0, 1, Graphics.HCENTER);
		resetMessage();}

	public void resetMessage() {
		try {
			songList.setSelectedIndex(0);}
		catch(Exception e) { } }

	public void setMessage(String text) {
		ticker.setMessage(text);}

	public String getMessage() {
		return ticker.getMessage();}
		
	public void setSongList(String[] text) {
		System.out.println("setsonglist");
		listLength = text.length;
		songList.populate(text);
		resetMessage();} }

class OTicker extends OComponent implements OThread {
	private boolean keepTicking = false;
	private String message;
	private int messageLength, Width, Height, textPosition = 0;
	private Font font;
	private Runnable runScrolling;

	OTicker(String text, int Height, int Width, Font font, final int speed) {
		this.font = font;
		textPosition = getX();
		this.Height = Height;
		this.Width = Width;

		runScrolling = new Runnable() {
			public void run() {
				textPosition = getWidth();
				while(getContinue()) {
					repaint();
					textPosition = textPosition - speed;
					if (textPosition < -1 * messageLength)
						textPosition = getWidth(); 
					try {
						Thread.sleep(300);}
					catch (InterruptedException e) { } } } };

		setMessage(text);}

	public void start() {
		setContinue(true);
		new Thread(runScrolling).start();
		System.gc();}

	public void stop() {
		setContinue(false); }

	private synchronized void setContinue(boolean value) {
		keepTicking = value; }

	private synchronized boolean getContinue() {
		return keepTicking;}

	public void paint(Graphics g) {
		g.setColor( OUILook.BACKGROUND_COLOR );
		g.fillRect( getX(), getY(), getWidth(), getHeight() );
		g.setFont(font);
		g.setColor(OUILook.TEXT_COLOR);
		g.drawString(getMessage(), textPosition, getY(), Graphics.TOP|Graphics.LEFT);}

	public int getHeight() {
		return Height;}

	public int getWidth() {
		return Width;}

	public synchronized String getMessage() {
		return message;}
		
	public synchronized void setMessage(String text) {
		message = text;
		messageLength = font.stringWidth(message);
		textPosition = getWidth();} }


class ControlScreen extends Canvas implements CommandListener{
	private JukeBox Parent;
	private boolean keepTicking = false;
	private String message;
	private int messageLength, Width, Height, textPosition = 0;
	private Runnable runScrolling;
	private Image controls;
	private Command Back;

	ControlScreen(JukeBox Parent) {
		this.Parent = Parent;
		setMessage("2 = PLAY, 4 = BACK, 6 = FORWARD, 8 = STOP");
		Back = new Command("Menu", Command.OK, 1);
		addCommand(new Command("", Command.OK, 1));
		addCommand(Back);
		setCommandListener(this);

		try {
			controls = Image.createImage("/controls.png");}
		catch(IOException e) { }

		runScrolling = new Runnable() {
			public void run() {
				textPosition = getWidth();
				while(getContinue()) {
					repaint();
					textPosition = textPosition - 8;
					if (textPosition < -1 * messageLength)
						textPosition = getWidth();
					try {
						Thread.sleep(300);}
					catch (InterruptedException e) { } } } }; }

	public void commandAction(Command c, Displayable d) {
		if(c.equals(Back)) {
			Parent.setMenu();
			stop();} }

	public void start() {
		setContinue(true);
		new Thread(runScrolling).start();
		System.gc();}

	public void stop() {
		setContinue(false);}


	public void resetMessage() {
		setMessage("2 = PLAY, 4 = BACK, 6 = FORWARD, 8 = STOP"); }

	public synchronized void setMessage(String text) {
		message = text;
		messageLength = Parent.MONOSPACE_BOLD_MEDIUM.stringWidth(message);
		textPosition = getWidth();}

	private synchronized void setContinue(boolean value) {
		keepTicking = value;}

	private synchronized boolean getContinue() {
		return keepTicking;}

	public void paint(Graphics g) {
		g.drawImage(controls, getWidth()/2, 0, Graphics.HCENTER | Graphics.TOP);
		g.setFont(Parent.MONOSPACE_BOLD_MEDIUM);
		g.drawString(message, textPosition, 0, Graphics.TOP | Graphics.LEFT);}

	public void keyPressed(int keyCode) {
		System.out.println(keyCode);
		if (keyCode == -10 || keyCode == Canvas.KEY_NUM2)
			Parent.sendData("play\n");
		else if(keyCode == -12 || keyCode == Canvas.KEY_NUM6)
			Parent.sendData("forward\n");
		else if(keyCode == -11 || keyCode == Canvas.KEY_NUM8)
			Parent.sendData("stop\n");
		else if(keyCode == -13 || keyCode == Canvas.KEY_NUM4)
			Parent.sendData("back\n");} }

class SplashScreen extends Canvas{
	Image splashImage;

	SplashScreen(final JukeBox Parent, String ImageName, final int WaitTime) {
		Runnable terminate;
		try {
			splashImage = Image.createImage("/" + ImageName);}
		catch(IOException e) { }
		terminate = new Runnable() {
			public void run() {
				try {
					Thread.sleep(WaitTime);}
				catch(InterruptedException e) { }
				Parent.init();} };
		new Thread(terminate).start();}
		
	public void paint(Graphics g) {
		g.drawImage(splashImage, getWidth()/2, 0, Graphics.HCENTER | Graphics.TOP);} }