package Algorithm;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Michael Walah
 * @NPM 2014730019
 */
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toMap;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageProcessing {

    private static final int KERNEL_SIZE = 3;
    private static final Size BLUR_SIZE = new Size(3, 3);
    //Variable for integer value
    private int lowThresh = 0;
    private int lowThreshXRatio = 0;
    private int largestIndex;
    private int count_idx;
    //Variables for Matrix
    private Mat src; //Matrix for Image Source
    private Mat srcBlur = new Mat(); //Matrix for Blurring the Image Source
    private Mat detectedEdges = new Mat(); //Matrix for get Edge Detection Value
    private Mat dst = new Mat(); //Matrix for Destination Image in Canny Edge Detection
    private Mat drawing; //Matrix for Drawing Contours
    private Mat image_lab; //Matrix for Conversion Color
    private Mat samples; //Matrix for Reshape Image to 2D Matrix
    private Mat labels; //Matrix for Clustering Labels
    private Mat centers; //Matrix for Clustering Centers
    private Mat out; //Matrix that being used for operation AND in Masking
    private Mat out_2; //Matrix that being used for operation XOR in Masking
    //Variable for Scalar
    private Scalar color; //Variable that being used for image pixel value
    //Variable for List Contains Matrix of Point
    private List<MatOfPoint> contours; //Using for find and draw the contours
    private List<MatOfPoint> hullList; //Using for find and draw the contours
    private ArrayList<File> dt = new ArrayList<>(); //List that being used for load image data training
    private ArrayList<String> al; //List that contains values of the image
    //Variable for Array
    private double[][] intraTotalValue; //Contains intra cluster distance total value
    private double[] intraClusterDistanceValue; //Contains intra cluster distance value per cluster
    //Variable for Frame and Label
    private JFrame frame;
    private JFrame originalFrame;
    private JLabel imgLabel;
    //Variable for Map (Mapping)
    private Map<Integer, Double> dominantColor;
    //Random number generator
    private Random rng = new Random();

    public ImageProcessing() {
        String imagePath = "data-train/ManggaMentah/mangga-mentah-kurang10.jpg";
        src = Imgcodecs.imread(imagePath);

        if (src.empty()) {
            System.out.println("Empty image: " + imagePath);
            System.exit(0);
        }
        // Create and set up the window.
        originalFrame = new JFrame("Original Image");
        frame = new JFrame("Processing Image");

        originalFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set up the content pane.
        Image img_2 = HighGui.toBufferedImage(src);
        Image img = HighGui.toBufferedImage(src);

        addComponentsToPane(originalFrame.getContentPane(), img_2);
        addComponentsToPane(frame.getContentPane(), img);
        // Use the content pane's default BorderLayout. No need for
        // setLayout(new BorderLayout());
        // Display the window.
        originalFrame.pack();
        frame.pack();

        originalFrame.setLocationRelativeTo(null);
        frame.setLocationRelativeTo(null);

        originalFrame.setVisible(true);
        frame.setVisible(true);
        update();
    }

    private void addComponentsToPane(Container pane, Image img) {
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
        
        imgLabel = new JLabel(new ImageIcon(img));
        pane.add(imgLabel, BorderLayout.CENTER);
    }

    public void doCannyEdgeDetection() {
        dst = new Mat(); //reset objek dst
        //pre processing
        Mat greyImage = new Mat();
        Imgproc.cvtColor(src, greyImage, Imgproc.COLOR_BGR2GRAY);//convert gambar ke grayscale
        Imgproc.GaussianBlur(greyImage, srcBlur, BLUR_SIZE, 100);//apply gaussian blur

        lowThresh = 50;
        lowThreshXRatio = 50;
        //Imgproc.Canny(srcBlur, detectedEdges, lowThresh, lowThresh * RATIO, KERNEL_SIZE, false); //apply canny edge detection
        Imgproc.Canny(srcBlur, detectedEdges, lowThresh, lowThreshXRatio, KERNEL_SIZE, false);
    }

    public void doDilation() {
        Imgproc.dilate(detectedEdges, detectedEdges, new Mat(), new Point(-1, -1), 1); //apply dilate for filling gaps
    }

    public void drawContours() {
        contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(detectedEdges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        //find largest hull
        double maxArea = 0.0;
        MatOfPoint biggestHull = new MatOfPoint();
        largestIndex = 0;
        int count = 0;
        hullList = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            MatOfInt hull = new MatOfInt();
            Imgproc.convexHull(contour, hull);
            if (maxArea < hull.size().area()) {
                maxArea = hull.size().area();
                largestIndex = count;
                Point[] contourArray = contour.toArray();
                Point[] hullPoints = new Point[hull.rows()];
                List<Integer> hullContourIdxList = hull.toList();
                for (int i = 0; i < hullContourIdxList.size(); i++) {
                    hullPoints[i] = contourArray[hullContourIdxList.get(i)];
                }
                biggestHull = new MatOfPoint(hullPoints);
            }
            count = count + 1;
        }
        hullList.add(biggestHull);

        //draw contour
        drawing = Mat.zeros(detectedEdges.size(), CvType.CV_8UC3);
        color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
        Imgproc.drawContours(drawing, contours, largestIndex, color);
        Imgproc.drawContours(drawing, hullList, 0, color);
    }

    public Mat doMasking() {
        //create mask
        double h = drawing.size().height;
        double w = drawing.size().width;
        src.copyTo(dst, drawing);
        Mat mask = Mat.zeros((int) h + 2, (int) w + 2, CvType.CV_8U);
        Imgproc.floodFill(drawing, mask, new Point(0, 0), new Scalar(255, 255, 255));
        color = new Scalar(0, 0, 0);
        Imgproc.drawContours(drawing, contours, largestIndex, color);
        Imgproc.drawContours(drawing, hullList, 0, color);

        //apply masking
        out = new Mat();
        Core.bitwise_and(src, drawing, out);
        out_2 = new Mat();
        Core.bitwise_xor(src, out, out_2);
        return out_2;
    }

    public void doClustering() {
        //Create New Matrix to Convert BGR to CIE LAB
        image_lab = new Mat();

        Imgproc.cvtColor(out_2, image_lab, Imgproc.COLOR_BGR2Lab);
        //ambil pixel bukan hitam

        List<double[]> temp = new ArrayList<>();
        for (int i = 0; i < image_lab.rows(); i++) {
            for (int j = 0; j < image_lab.cols(); j++) {
                double[] value = image_lab.get(i, j);
                if (value[0] != 0.0 && value[1] != 128.0 && value[2] != 128.0) {
                    temp.add(value);
                }
            }
        }

        //Convert from ArrayList to Matrix
        Mat filtered_pixel = new Mat(1, temp.size(), CvType.CV_8UC3);
        for (int i = 0; i < temp.size(); i++) {
            filtered_pixel.put(0, i, temp.get(i));
        }

        //Reshape to 2D matrix
        samples = filtered_pixel.reshape(1, filtered_pixel.cols() * filtered_pixel.rows());

        Mat samples32f = new Mat();
        //samples.convertTo(samples32f, CvType.CV_32F, -127.0 / 128.0);
        samples.convertTo(samples32f, CvType.CV_32F);
        TermCriteria term = new TermCriteria(TermCriteria.COUNT, 100, 1);

        //Create Matrix to Put Labels
        labels = new Mat();
        //Create Matrix to Put Centers (Centroids)
        centers = new Mat();
        //Running K-Means Clustering Algorithm
        Core.kmeans(samples32f, 10, labels, term, 1, Core.KMEANS_PP_CENTERS, centers);

        //Print All Centers
        System.out.println("Centers: " + centers.dump());

        //Calculate Intra Cluster Distance
        System.out.println("==============================================");
        System.out.println("Intra Cluster Distance Value");

        //Get Each L, a, and b Value On One Cluster
//        System.out.println(samples.rows());
        intraTotalValue = new double[centers.rows()][2]; //[x][0] simppan total 'jarak euclidean' dari seluruh anggota cluster dengan centroid, x[0][1] simpan total anggota
        for (int i = 0; i < samples.rows(); i++) {
            double[] labCenterValueL = centers.get((int) (labels.get(i, 0))[0], 0);
            double[] labCenterValueA = centers.get((int) (labels.get(i, 0))[0], 1);
            double[] labCenterValueB = centers.get((int) (labels.get(i, 0))[0], 2);
//            System.out.println((samples.get(i,0))[0] +","+(samples.get(i,1))[0] +","+(samples.get(i,2))[0]);
//            System.out.println(labCenterValueL[0] +","+labCenterValueA[0] +","+labCenterValueB[0]);
            double distEuclid = Math.sqrt(Math.pow((samples.get(i, 0))[0] - labCenterValueL[0], 2)
                    + Math.pow((samples.get(i, 1))[0] - labCenterValueA[0], 2)
                    + Math.pow((samples.get(i, 2))[0] - labCenterValueB[0], 2));
//            System.out.println(distEuclid +","+(int) (labels.get(i, 0))[0]);
            intraTotalValue[(int) (labels.get(i, 0))[0]][0] = intraTotalValue[(int) (labels.get(i, 0))[0]][0] + distEuclid;
            intraTotalValue[(int) (labels.get(i, 0))[0]][1] = intraTotalValue[(int) (labels.get(i, 0))[0]][1] + 1;
        }
        System.out.println(intraTotalValue[0][0]);
        System.out.println(intraTotalValue[0][1]);

        System.out.println("Check intra cluster value");
        intraClusterDistanceValue = new double[centers.rows()];
        count_idx = 0;
        for (double[] tes : intraTotalValue) {
            intraClusterDistanceValue[count_idx] = tes[0] / tes[1];
            System.out.println(tes[0] / tes[1]);
            count_idx++;
        }
    }

    public void findDominantColor() {
        dominantColor = new HashMap<Integer, Double>();
        //Find Dominant Color
        System.out.println("==============================================");
        System.out.println("Dominant Colors");
        double intraClusterDistance2 = 0;
        double labClusterValue2 = 0;
        double score = 0;
        for (int i = 0; i < centers.rows(); i++) {
            intraClusterDistance2 = Math.pow((1 / intraClusterDistanceValue[i]), 0.8);
            labClusterValue2 = Math.pow((intraTotalValue[i][1] / samples.rows()), 0.2);
            score = intraClusterDistance2 + labClusterValue2;
            dominantColor.put(i, score);
        }
        Map<Integer, Double> sorted = dominantColor.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        //Print sortedmap
        for (Map.Entry<Integer, Double> en : sorted.entrySet()) {
            System.out.println("Key (Cluster) = " + en.getKey() + ", Value = " + en.getValue());
        }

        //Get 5 Dominant Color
        double[][] vector_image = new double[5][3];
        count_idx = 0;
        for (Map.Entry<Integer, Double> en : sorted.entrySet()) {
//            System.out.println(en.getKey());
            if (count_idx == 5) {
                break;
            }
            vector_image[count_idx][0] = (centers.get(en.getKey(), 0))[0];
            vector_image[count_idx][1] = (centers.get(en.getKey(), 1))[0];
            vector_image[count_idx][2] = (centers.get(en.getKey(), 2))[0];
            count_idx++;
        }

        System.out.println("==============================================");
        //Debug and Print the 5 Vector Image
        count_idx = 0;
        for (double[] tes : vector_image) {
            System.out.println("vektor: " + (count_idx + 1));
            System.out.println(tes[0]);
            System.out.println(tes[1]);
            System.out.println(tes[2]);
            System.out.println("============================");
            count_idx++;
        }
    }

    public ArrayList<String> loadDataTraining(File[] list) {
        al = new ArrayList<>();
        for (File x : list) {
            if (x.isDirectory()) {
                if (x.getAbsolutePath().contains("ManggaMatang") || x.getAbsolutePath().contains("ManggaMentah")) {
                    al.addAll(loadDataTraining(x.listFiles()));
                }
            } else {
                dt.add(x);
                String test = x.getAbsolutePath();
                String str = "";
                for (int i = 0; i < test.length(); i++) {
                    if (test.charAt(i) == '\\') {
                        str += "/";
                    } else {
                        str += test.charAt(i) + "";
                    }
                }
                al.add(str);
            }
        }
        return al;
    }
    
//    public ArrayList<Double[][]> trainData(){
//        for (int i = 0; i < al.size(); i++) {
//            
//        }
//        return null;
//    }

    private void update() {
        doCannyEdgeDetection();
        doDilation();
        drawContours();
        doMasking();
        doClustering();
        findDominantColor();
        
        //done
        Image img = HighGui.toBufferedImage(out_2);
        imgLabel.setIcon(new ImageIcon(img));
        frame.repaint();
    }
}