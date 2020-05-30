package Algorithm;


import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Michael
 */
public class Clustering {
    private Mat labels;
    private Mat centers;
    private Mat image_lab;
    private Mat src;
    private Mat samples;
    private MatToBufImg converter;
    private ImagePanel imagePanel;
    private JFrame frame;
    
    public Clustering(){
        String imagePath = "mangga-foto-rev2-dv/ManggaMentah/kondisi-kurang/mangga-mentah-kurang10.jpg";
        src = Imgcodecs.imread(imagePath);
        if (src.empty()) {
            System.out.println("Empty image: " + imagePath);
            System.exit(0);
        }
        
        // Create and set up the window.
        frame = new JFrame("Original Image");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        imagePanel = new ImagePanel();
        frame.setContentPane(imagePanel);
    }
    
    public void doClustering(){
        //Convert Mat to Java's BufferedImage
        converter = new MatToBufImg();
        
        //Create New Matrix to Convert BGR to CIE LAB
        image_lab = new Mat();
        //Convert BGR Image to LAB Image
        Imgproc.cvtColor(src, image_lab, Imgproc.COLOR_BGR2Lab);
        
        //CLUSTERING
        //Reshape to 2D Matrix
        samples = image_lab.reshape(1, image_lab.cols() * image_lab.rows());
        
        Mat samples32f = new Mat();
        samples.convertTo(samples32f, CvType.CV_32F, -127.0 / 128.0);
        TermCriteria term = new TermCriteria(TermCriteria.COUNT,100,1);
        
        //Create Matrix to Put Labels
        labels = new Mat();
        //Create Matrix to Put Centers (Centroids)
        centers = new Mat();
        //Running K-Means Clustering Algorithm
        Core.kmeans(samples32f, 10, labels, term, 1, Core.KMEANS_PP_CENTERS, centers);
        //Print All Labels
        //System.out.print("labels: "+labels.dump());
        //Print All Centers
        System.out.println("centers: "+centers.dump());
        
        System.out.println("image " + src.channels() + " channels " + src.cols() + " columns and " + src.rows() + " rows");
        //Output : Image contain 3 channels 601 columns and 488 rows
        
        //salt(image, 10001); //Put 10001 'salt' dots on the image (Try Salting Effect)
        
        converter.setMatrix(src, ".jpg");
        BufferedImage img = converter.getBufferedImage();
        //add the JPG image to JFrame
        imagePanel.setImage(img);
        //saltedImagePanel.repaint(); //ask the system to repaint the (updated GUI)
        //saltedImagePanel.setBackground(Color.black);
        
        frame.setSize(img.getWidth(), img.getHeight());
        
        //frame.add(new JLabel(icn_img));
        frame.setLocationRelativeTo(null); //center GUI
        //frame.revalidate();
        frame.setVisible(true);
    }
}
