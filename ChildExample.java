import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
 
class ChildExample{
 
    JTextField usernameTxt;
    JTextField typeTxt;
    JTextField portTxt;
    JTextField inputTxt;
    JInternalFrame loginFrame;
    JCheckBox chinButton;
    JCheckBox glassesButton;
     
    public static void main(String[] args){
        ChildExample ex1 = new ChildExample();
        ex1.go();
                 
     
    }
    public void go(){
     
         
        JFrame mainFrame = new JFrame("Main");
        mainFrame.setSize(400,500);
        loginFrame = new JInternalFrame("Login");
        loginFrame.setSize(400,500);
        mainFrame.setDefaultCloseOperation(mainFrame.EXIT_ON_CLOSE);
        JDesktopPane mainPanel = new JDesktopPane();
        JPanel loginPanel = new JPanel();
         
        JTextArea textArea = new JTextArea(25,25);
        usernameTxt = new JTextField(25);   
        typeTxt = new JTextField(25);
        portTxt = new JTextField(25);
        inputTxt = new JTextField(25);

        JLabel usernameLbl = new JLabel("Username: ");
        JLabel typeLbl = new JLabel("Player       : ");
        JLabel portLbl = new JLabel("Port           : ");
        JLabel inputLbl = new JLabel("Play With : ");
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new loginButtonListener());
 
        mainPanel.add(textArea);
        mainPanel.add(loginFrame);
        loginPanel.add(usernameLbl);
        loginPanel.add(usernameTxt);
        loginPanel.add(typeLbl);
        loginPanel.add(typeTxt);
        loginPanel.add(portLbl);
        loginPanel.add(portTxt);
        loginPanel.add(inputLbl);
        loginPanel.add(inputTxt);
        loginPanel.add(loginButton);
         
        loginFrame.getContentPane().add(BorderLayout.CENTER,loginPanel);
        mainFrame.getContentPane().add(BorderLayout.CENTER,mainPanel);
 
        loginFrame.setVisible(true);                    
        mainFrame.setVisible(true);     
                         
 
    }
 
 
 
    public class loginButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent ev){
            //if username and password is good hide child window
            loginFrame.setVisible(false);
            
            int playerType = Integer.parseInt(typeTxt.getText().trim());
            int port = Integer.parseInt(portTxt.getText().trim());
            String inpType = inputTxt.getText().trim();
            String username = usernameTxt.getText().trim();
            boolean inp;

            if(inpType.equals("Keyboard") || inpType.equals("keyboard") || inpType.equals("KEYBOARD"))
                inp = true;
            else
                inp = false;

            // PongWindow window = new PongWindow (playerType,inp,port,username);
            // window.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
            // window.setVisible (true);
        }
         
    }
}