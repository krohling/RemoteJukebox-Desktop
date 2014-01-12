![RemoteJukebox](https://raw2.github.com/krohling/RemoteJukebox-Mobile/master/readme-files/img1.png)

The Remote JukeBox Application is actually a set of two applications that run cooperatively.  The server runs on a PC with an internet connection, and it functions as a standard MP3 player except that it receives commands from the client, allowing not only standard play functions but also playlist selection and random track selection. The client application runs on a J2ME-enabled device such as a cell phone, PDA, ect. and provides the interface allowing remote control of the server.  This User Guide is divided into two parts.  The first describing the operation of the server application and the second describing the client.


### 1.1	Anatomy Of The Server Application

![Server](https://raw2.github.com/krohling/RemoteJukebox-Mobile/master/readme-files/img2.png)

 A. Play Button - Begins playing of current track

B. Back Skip Button - Skips to the previous track

C. Forward Skip Button - Skips to the next track or if at the end of the list, restarts from the beginning.

D. Stop Button - Stops the currently playing track

E. Connection Indicator - Indicates wether or not a phone is connected, also clicking allows blocking/unblocking of client commands.

![RemoteJukebox](https://raw2.github.com/krohling/RemoteJukebox-Mobile/master/readme-files/phone-connected.png) Animated means a phone is connected.

![RemoteJukebox](https://raw2.github.com/krohling/RemoteJukebox-Mobile/master/readme-files/phone-disconnected.png) Not animated no phone is connected.

F.   Configure Local IP -Allows user to change the IP address the server listens on if multiple addresses exist on a machine

G.  Change Shared PlayList Directory -Allows user to change the directory containing the PlayLists it will share with the client.

H.	Load A PlayList -Load A PlayList

I.	Create A PlayList -Opens the Create PlayList window

J.	Current PlayList Display -Displays the current PlayList.  Double-Clicking on a song will play that song. 


### 1.2	Create PlayList Screen

![Server](https://raw2.github.com/krohling/RemoteJukebox-Mobile/master/readme-files/img3.png)

A.	Current Files -Displays the list of files included in the current PlayList.

B.	New PlayList -Clears the current list to create a new one.

C.	Edit PlayList -Opens an existing PlayList for editing.

D.	Save PlayList -Save current list.

E.	Close Window -Close this window.

F.	Add Button -Adds selected file to PlayList.

G.	Remove Button -Removes selected file from PlayList.

H.	Drive Selection Box -Select drive.

I.	Directory Selection Box -Select directory.

J.	File Selection Box -Select file, Double-Click or hit Add button to add to PlayList.


### Anatomy Of The Client Application

#### Menu Screen

![Server](https://raw2.github.com/krohling/RemoteJukebox-Mobile/master/readme-files/img4.png)

Main navigational screen.  Select from PlayList Screen, Server Screen, SongList Screen, Controls Screen or Exit.  Also, scrolling text at the top of the screen displays the currently selected PlayList.

#### PlayList Screen

![Server](https://raw2.github.com/krohling/RemoteJukebox-Mobile/master/readme-files/img5.png)

Allows user to select a PlayList.  These are downloaded from the server which are stored in the PlayList Directory.  Reload refreshes the information, Select selects the PlayList, choose this option when ready to load a PlayList, you will hear a beep to notify you that the list has been loaded by the server.

#### Server Screen

![Server](https://raw2.github.com/krohling/RemoteJukebox-Mobile/master/readme-files/img6.png)

Provides the interface necessary to Connect to the server.  Once connected you may change the IP address to connect to a different server however you must select Connect to initialize the connection.  Selecting New simply clears the text field to allow input of a new address.

#### SongList Screen

![Server](https://raw2.github.com/krohling/RemoteJukebox-Mobile/master/readme-files/img7.png)

This screen allows random access to all of the songs in the selected PlayList.  This information is downloaded when the PlayList is selected from the PlayList Screen.  To play a song scroll to it and select Select from the bottom right.

#### Controls Screen

![Server](https://raw2.github.com/krohling/RemoteJukebox-Mobile/master/readme-files/img8.png)

The Controls Screen provides a more intuitive interface.  The buttons allow access to the play, back, forward and stop functions and the scrolling text at the top of the screen displays the currently playing song/status.

UP- Play (Also 2 on keypad)

LEFT – Back Skip (Also 4 on keypad)

RIGHT – Forward Skip (Also 6 on keypad)

DOWN – Stop (Also 8 on keypad)

### 2.2 Help Running The Client Application

*Directions for  Motorola J2ME enabled phones (i85s, i90c, i80s, i50sx)

After installing the Remote JukeBox application go to       | Menu > Java Apps > JukeBox | and select Run.  You will see the initial splash screen display for a few seconds before going to the Server Screen.  Enter your computer’s IP Address (for help see: How to find server IP address) and select Connect.  The phone will connect to the server computer and download the PlayLists contained in your shared PlayList directory and display the PlayList screen.  From this point you are connected to the server and are free to beginning using the application.

#### How to find the server IP address (Windows 95 & 98):

The computer you plan to use as the server must be connected to the internet before you can proceed.

![Server](https://raw2.github.com/krohling/RemoteJukebox-Mobile/master/readme-files/img9.png)

#### 1. Select Run from the Start Menu.

![Server](https://raw2.github.com/krohling/RemoteJukebox-Mobile/master/readme-files/img10.png)

#### 2. Type “winipcfg” and select OK.

![Server](https://raw2.github.com/krohling/RemoteJukebox-Mobile/master/readme-files/img11.png)

#### 3. Select the appropriate Adapter.

![Server](https://raw2.github.com/krohling/RemoteJukebox-Mobile/master/readme-files/img12.png)

#### 4. This is your IP Address.
