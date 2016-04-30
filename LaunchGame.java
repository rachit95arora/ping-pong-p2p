import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
 
class LaunchGame{
    //Declare variables for the launch screen. The dialog box by the main frame will be titled Play Pong
    //Other variables such as those for textfields and internal frames are declared.
    JFrame mainFrame = new JFrame("Play Pong");
    JTextField usernameTxt;
    JTextField portTxt;
    JTextField ballTxt;
    JInternalFrame loginFrame;
    Image background;
   //The main method instantiates the class and calls the go() function
    public static void main(String[] args){
        LaunchGame ex1 = new LaunchGame();
        ex1.go();     
    }
    //The principal function to render the screen with the various components in order
    public void go(){     
        //Setting up the main frame and login frames with sizes and the name of the login frame
        mainFrame.setSize(400,633);
        loginFrame = new JInternalFrame("Pong");
        loginFrame.setSize(400,633);
        JPanelWithBackground loginPanel;
        //The background image is added if possible. Else a blank background login panel is initiated.
        try{
            mainFrame.getContentPane().add(new JPanelWithBackground("backnew.jpg"));
            loginPanel = new JPanelWithBackground("backMain.jpg");
        }
        catch(Exception e){
            System.out.println("Unable to set image");
            loginPanel = new JPanelWithBackground();
        }

        mainFrame.setDefaultCloseOperation(mainFrame.EXIT_ON_CLOSE);
        JDesktopPane mainPanel = new JDesktopPane();
        
        //Assigning new textfields with sizes for each of the declared ones
        JTextArea textArea = new JTextArea(25,25);
        usernameTxt = new JTextField(25);
        portTxt = new JTextField(25);
        ballTxt = new JTextField(25);
        //Assigning the labels to appear for the respective textboxes with text values and colors
        JLabel usernameLbl = new JLabel("Username: ");
        JLabel portLbl = new JLabel("Port           : ");
        JLabel ballLbl = new JLabel("Balls          : ");
        usernameLbl.setForeground(Color.WHITE);
        portLbl.setForeground(Color.WHITE);
        ballLbl.setForeground(Color.WHITE);
        
        //Declaring and initializing with the show text, the two buttons for options. The listener is specified too.
        JButton keyboardButton = new JButton("Play with Keyboard");
        JButton mouseButton = new JButton("Play with Mouse");
        keyboardButton.addActionListener(new keyboardButtonListener());
        mouseButton.addActionListener(new mouseButtonListener());
        //Adding the various components in order to the frame and login panel
        mainPanel.add(textArea);
        mainPanel.add(loginFrame);
        loginPanel.add(usernameLbl);
        loginPanel.add(usernameTxt);
        loginPanel.add(portLbl);
        loginPanel.add(portTxt);
        loginPanel.add(ballLbl);
        loginPanel.add(ballTxt);
        loginPanel.add(keyboardButton);
        loginPanel.add(mouseButton);
         
        loginFrame.getContentPane().add(BorderLayout.CENTER,loginPanel);
        mainFrame.getContentPane().add(BorderLayout.CENTER,mainPanel);
        
        mainFrame.setLocationRelativeTo(null);
        //The setVisible is invoked with true to complete rendition and allow view
        loginFrame.setVisible(true);                    
        mainFrame.setVisible(true);      
    }
 
 
    //The button listener class for the keyboard option
    public class keyboardButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent ev){
            //if username and password is good hide child window
            mainFrame.setVisible(false);
            //Format and convert to int the text areas for port and ball number
            int port = Integer.parseInt(portTxt.getText().trim());
            int balls = Integer.parseInt(ballTxt.getText().trim());
            String username = usernameTxt.getText().trim();
            //Start out a new PongWindow object with the just obtained parameters
            PongWindow window = new PongWindow (true,username,port,balls);
            window.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
            window.setVisible (true);
            //Finally putting this new window for pong into view
        }
         
    }
    //The button listener class for the mouse option
    public class mouseButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent ev){
            //if username and password is good hide child window
            mainFrame.setVisible(false);
            //Format and convert to int the text areas for port and ball number
            int port = Integer.parseInt(portTxt.getText().trim());
            int balls = Integer.parseInt(ballTxt.getText().trim());
            String username = usernameTxt.getText().trim();
            //Start out a new PongWindow object with the just obtained parameters
            PongWindow window = new PongWindow (false,username,port,balls);
            window.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
            window.setVisible (true);
            //Finally putting this new window for pong into view
        }
         
    }
}