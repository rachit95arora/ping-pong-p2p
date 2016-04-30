import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
 
class LaunchGame{
    
    JFrame mainFrame = new JFrame("Play Pong");
    JTextField usernameTxt;
    JTextField portTxt;
    JTextField ballTxt;
    JInternalFrame loginFrame;
    Image background;
   
    public static void main(String[] args){
        LaunchGame ex1 = new LaunchGame();
        ex1.go();     
    }

    public void go(){     
        
        mainFrame.setSize(400,633);
        loginFrame = new JInternalFrame("Pong");
        loginFrame.setSize(400,633);
        JPanelWithBackground loginPanel;

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
        
         
        JTextArea textArea = new JTextArea(25,25);
        usernameTxt = new JTextField(25);
        portTxt = new JTextField(25);
        ballTxt = new JTextField(25);

        JLabel usernameLbl = new JLabel("Username: ");
        JLabel portLbl = new JLabel("Port           : ");
        JLabel ballLbl = new JLabel("Balls          : ");
        usernameLbl.setForeground(Color.WHITE);
        portLbl.setForeground(Color.WHITE);
        ballLbl.setForeground(Color.WHITE);
        

        JButton keyboardButton = new JButton("Play with Keyboard");
        JButton mouseButton = new JButton("Play with Mouse");
        keyboardButton.addActionListener(new keyboardButtonListener());
        mouseButton.addActionListener(new mouseButtonListener());
 
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
        loginFrame.setVisible(true);                    
        mainFrame.setVisible(true);      
    }
 
 
 
    public class keyboardButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent ev){
            //if username and password is good hide child window
            mainFrame.setVisible(false);
            
            int port = Integer.parseInt(portTxt.getText().trim());
            int balls = Integer.parseInt(ballTxt.getText().trim());
            String username = usernameTxt.getText().trim();

            PongWindow window = new PongWindow (true,username,port,balls);
            window.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
            window.setVisible (true);
        }
         
    }

    public class mouseButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent ev){
            //if username and password is good hide child window
            mainFrame.setVisible(false);
            
            int port = Integer.parseInt(portTxt.getText().trim());
            int balls = Integer.parseInt(ballTxt.getText().trim());
            String username = usernameTxt.getText().trim();

            PongWindow window = new PongWindow (false,username,port,balls);
            window.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
            window.setVisible (true);
        }
         
    }
}