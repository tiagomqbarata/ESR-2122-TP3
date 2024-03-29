import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Streamer extends JFrame implements ActionListener {

    //GUI:
    //----------------
    JLabel label;

    //RTP variables:
    //----------------
    DatagramPacket senddp; //UDP packet containing the video frames (to send)A
    DatagramSocket RTPsocket; //socket to be used to send and receive UDP packet


    static List<InetAddress> IPAddrToSend; //Client IP address
    static String VideoFileName = "movie.Mjpeg"; //video file to request to the server

    //Video constants:
    //------------------
    int imagenb = 0; //image nb of the image currently transmitted
    VideoStream video; //VideoStream object used to access video frames
    static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video
    static int FRAME_PERIOD = 100; //Frame period of the video to stream, in ms
    static int VIDEO_LENGTH = 500; //length of the video in frames

    Timer sTimer; //timer used to send the images at the video frame rate
    byte[] sBuf; //buffer used to store the images to send to the client


    public Streamer(){
        //init Frame
        super("Streamer");



        System.out.println(VideoFileName);

        try {
            IPAddrToSend = new ArrayList<>();
            RTPsocket = new DatagramSocket(); //init RTP socket
            video = new VideoStream(VideoFileName); //init the VideoStream object:
            System.out.println("Servidor: vai enviar video da file " + VideoFileName);

        } catch (SocketException e) {
            System.out.println("Servidor: erro no socket: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Servidor: erro no video: " + e.getMessage());
        }

        //Handler to close the main window
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                //stop the timer and exit
                sTimer.stop();
                System.exit(0);
            }});

        //GUI:
        label = new JLabel("Send frame #        ", JLabel.CENTER);
        getContentPane().add(label, BorderLayout.CENTER);
    }

    public void startStream(){
        // init para a parte do servidor
        sTimer = new Timer(FRAME_PERIOD, this); //init Timer para servidor
        sTimer.setInitialDelay(0);
        sTimer.setCoalesce(true);
        sBuf = new byte[15000]; //allocate memory for the sending buffer
        sTimer.start();
    }

    public void stopStream(){
        sTimer.stop();
    }

    public void addIp(InetAddress ip){
        IPAddrToSend.add(ip);
    }

    public void remIp(InetAddress ip){
        IPAddrToSend.remove(ip);
    }

    public int getNumberIps(){
        return IPAddrToSend.size();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        //if the current image nb is less than the length of the video
        if (imagenb < VIDEO_LENGTH) {
            //update current imagenb
            imagenb++;

            try {
                //get next frame to send from the video, as well as its size
                int image_length = video.getnextframe(sBuf);

                //Builds an RTPpacket object containing the frame
                RTPpacket rtp_packet = new RTPpacket(MJPEG_TYPE, imagenb, imagenb*FRAME_PERIOD, sBuf, image_length);

                //get to total length of the full rtp packet to send
                int packet_length = rtp_packet.getlength();

                //retrieve the packet bitstream and store it in an array of bytes
                byte[] packet_bits = new byte[packet_length];
                rtp_packet.getpacket(packet_bits);

                //send the packet as a DatagramPacket over the UDP socket
                for(InetAddress ip : IPAddrToSend){
                    senddp = new DatagramPacket(packet_bits, packet_length, ip, ott.RTP_PORT);
                    RTPsocket.send(senddp);
                }

                System.out.println("Send frame #"+imagenb);
                //print the header bitstream
                rtp_packet.printheader();

                //update GUI
                label.setText("Send frame #" + imagenb);
            }
            catch(Exception ex) {
                System.out.println("Exception caught in actionPerformed: "+ex);
                System.exit(0);
            }
        }
        else {
            //if we have reached the end of the video file, stop the timer
            //sTimer.stop();
            imagenb=0;
            try {
                video = new VideoStream(VideoFileName); //init the VideoStream object:
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
