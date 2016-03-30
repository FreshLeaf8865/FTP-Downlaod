import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
 
//import static org.junit.Assert.assertTrue;
 
public class FTPFunction {
    
     // Creating FTP Client instance
    FTPClient ftp = null;
     
    // Constructor to connect to the FTP Server
    public FTPFunction(String host, int port, String username, String password) throws Exception{
        
        ftp = new FTPClient();
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        int reply;
        ftp.connect(host,port);
        System.out.println("FTP URL is:"+ftp.getDefaultPort());
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new Exception("Exception in connecting to FTP Server");
        }
        ftp.login(username, password);
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();        
    }    
 
    // Method to upload the File on the FTP Server
    public void uploadFTPFile(String localFileFullName, String fileName, String hostDir)
            throws Exception
            {
                try {
                InputStream input = new FileInputStream(new File(localFileFullName));
 
                this.ftp.storeFile(hostDir + fileName, input);
                }
                catch(Exception e){
 
                }
            }
    
    // Download the FTP File from the FTP Server
    public void downloadFTPFile(String source, String destination) {
        try (FileOutputStream fos = new FileOutputStream(destination)) {
            this.ftp.retrieveFile(source, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // list the files in a specified directory on the FTP
    public boolean listFTPFiles(String directory, String fileName) throws IOException {
        // lists files and directories in the current working directory
        boolean verificationFilename = false;        
        FTPFile[] files = ftp.listFiles(directory);
        for (FTPFile file : files) {
            String details = file.getName();                
            System.out.println(details);            
            if(details.equals(fileName))
            {
                System.out.println("Correct Filename");
                verificationFilename=details.equals(fileName);
//                assertTrue("Verification Failed: The filename is not updated at the CDN end.",details.equals(fileName));                
            }
        }  
        
         return verificationFilename;
    }
    
    // Disconnect the connection to FTP
    public void disconnect(){
        if (this.ftp.isConnected()) {
            try {
                this.ftp.logout();
                this.ftp.disconnect();
            } catch (IOException f) {
                // do nothing as file is already saved to server
            }
        }
    }
    
    // Main method to invoke the above methods
    public static void main(String[] args) {
        try {
            FTPFunction ftpobj = new FTPFunction("gydev.com", 21, "ftp-test@gydev.com", "testing1234");
//            ftpobj.uploadFTPFile("C:\\Users\\shruti\\Shruti.txt", "Shruti.txt", "/");
            ftpobj.downloadFTPFile("appointments-confirmed.txt", "C:\\work\\appointments-confirmed.txt");
            System.out.println("FTP File downloaded successfully");
            ftpobj.disconnect();
            
            //replace
            BufferedReader file = new BufferedReader(new FileReader("C:\\work\\appointments-confirmed.txt"));
            FileOutputStream os = new FileOutputStream("C:\\work\\appointments-confirmed-sql.txt");
            String line;
            String format = "UPDATE appt SET appt_confirmed = '%s',Confirm='Y', Appt_Confirmed_log='9' WHERE appt_key = '%s'";
            String output = "";

            while ((line = file.readLine()) != null) {
            	Date dNow = new Date();
                SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
                String date_time = ft.format(dNow);

            	String newline = String.format(format, date_time, line);
            	output += newline + System.lineSeparator();
            }
            
            os.write(output.getBytes());

            file.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}