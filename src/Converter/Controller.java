package Controller;

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
import Model.ImageProcessing;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

public class Controller {

    private JTextArea logText;

    public void runnerForGUIFolder(int threshold, int cluster, int dominant, int k, int loopProgram, JTextArea logText, String path_test, JLabel statusText, String path_train) {
        //Create blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("Evaluation");

        //Data needs to be written (Object[])
        Map<String, Object[]> data = new TreeMap<>();
        data.put("Checker", new Object[]{"File Name", "Folder", "Prediction", "Time"});

        //Program start running
        logText.setText("Program Running");
        System.out.println("Program Running");
        this.logText = logText;

        //Load OpenCV Library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //Create Object from Class ImageProcessing
        ImageProcessing imgProc = new ImageProcessing(threshold, cluster, dominant);

        logText.setText(logText.getText() + "\n" + "Load Data Train");
        System.out.println("Load Data Train");
        //Load all train data with its classification (real)
        File[] files_train = new File(path_train).listFiles();
        ArrayList<String> allDataTrain = new ArrayList<>();
        allDataTrain = imgProc.loadDataTraining(files_train);
        String[][] trainData = new String[allDataTrain.size()][2];
        for (int i = 0; i < trainData.length; i++) {
            trainData[i][0] = allDataTrain.get(i);
            if (allDataTrain.get(i).contains("Matang")) {
                trainData[i][1] = "Matang";
            } else {
                trainData[i][1] = "Mentah";
            }
        }
        statusText.setText("Load Data Training Success");
        logText.setText(logText.getText() + "\n" + "Load Data Train Done");
        System.out.println("Load Data Train Done");

        logText.setText(logText.getText() + "\n" + "");
        System.out.println("");

        logText.setText(logText.getText() + "\n" + "Load Data Test");
        System.out.println("Load Data Test");
        //Load all test data with its classification (real)
        File[] files_test = new File(path_test).listFiles();
        ArrayList<String> allDataTest = new ArrayList<>();
        allDataTest = imgProc.loadDataTest(files_test);
        String[][] testData = new String[allDataTest.size()][2];
        for (int i = 0; i < testData.length; i++) {
            testData[i][0] = allDataTest.get(i);
            if (allDataTest.get(i).contains("Matang")) {

                testData[i][1] = "Matang";
            } else {
                testData[i][1] = "Mentah";
            }
        }
        statusText.setText("Load Data Test Success");
        logText.setText(logText.getText() + "\n" + "Load Data Test Done");
        System.out.println("Load Data Test Done");

        //create array list to store dominant color of all train data and test data
        ArrayList<double[][]> dominantColorTrain = new ArrayList<>();
        ArrayList<double[][]> dominantColorTest = new ArrayList<>();

        //Code to show all image in Data Test Folder
//        JFrame testDataFrame;
//        Mat[] imageTest = new Mat[allDataTest.size()];
//        for (int i = 0; i < testData.length; i++) {
//            String[] imageTestName = testData[i][0].split("/");
//            imageTest[i] = Imgcodecs.imread(allDataTest.get(i));
//            testDataFrame = new JFrame("Original Image: " + imageTestName[imageTestName.length - 1]);
//            testDataFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            Image originalImage = HighGui.toBufferedImage(imageTest[i]);
//            imgProc.addComponentsToPane(testDataFrame.getContentPane(), originalImage);
//            testDataFrame.pack();
//            testDataFrame.setLocationRelativeTo(null);
//            testDataFrame.setVisible(true);
//        }
//        logText.setText(logText.getText() + "\n" + "");
//        System.out.println("");
        logText.setText(logText.getText() + "\n" + "Do Processing On Data Train");
        System.out.println("Do Processing On Data Train");
        logText.setText(logText.getText() + "\n" + "Data Train Result:");
        System.out.println("Data Train Result:");
        //extract dominant color from train data
        for (int i = 0; i < trainData.length; i++) {
            dominantColorTrain.add(imgProc.extractFeature(trainData[i][0], 0));
            logText.setText(logText.getText() + "\n" + "Feature Extraction From Data Train " + (i + 1) + ", Done");
            System.out.println("Feature Extraction From Data Train " + (i + 1) + ", Done");
        }
        logText.setText(logText.getText() + "\n" + "Processing Data Train Success");
        statusText.setText("Processing Data Train Success");
        System.out.println("Processing Data Train Succcess");

        logText.setText(logText.getText() + "\n" + "");
        System.out.println("");

        //Record starting time program process
        long timeStart = 0;

        //Looping for every process for every one image test 
        for (int a = 0; a < loopProgram; a++) {
            timeStart = System.currentTimeMillis();

            System.out.println("Prediction Looping: " + (a + 1));
            logText.setText(logText.getText() + "\n" + "Do Processing On Data Test");
            System.out.println("Do Processing On Data Test");
            logText.setText(logText.getText() + "\n" + "Data Test Result:");
            System.out.println("Data Test Result:");

            for (int i = 0; i < testData.length; i++) {
                dominantColorTest.add(imgProc.extractFeature(testData[i][0], 0));
                logText.setText(logText.getText() + "\n" + "Feature Extraction From Data Test " + (i + 1) + ", Done");
                System.out.println("Feature Extraction From Data Test " + (i + 1) + ", Done");
            }

            logText.setText(logText.getText() + "\n" + "Processing Data Test Success");
            statusText.setText("Processing Data Test Success");
            System.out.println("Processing Data Test Succcess");

            logText.setText(logText.getText() + "\n" + "");
            System.out.println("");

            //classification
            logText.setText(logText.getText() + "\n" + "Do Classification");
            System.out.println("Do Classification");
            logText.setText(logText.getText() + "\n" + "Result:");
            System.out.println("Result:");
            statusText.setText("Classification");

            //Record end time program process
            long timeEnd = 0;
            //Process running time program
            long timeProgram = 0;
            long second = 0;

            for (int i = 0; i < dominantColorTest.size(); i++) {
                Map<Integer, Double> classificationRes = new HashMap<>();
                logText.setText(logText.getText() + "\n" + "Classification Computation Result For Test - " + (i + 1));
                System.out.println("Classification Computation Result For Test - " + (i + 1));
                for (int j = 0; j < dominantColorTrain.size(); j++) {
                    double res = imgProc.doClassification(dominantColorTest.get(i), dominantColorTrain.get(j));
                    classificationRes.put(j, res);
                }
                Map<Integer, Double> sortedRes = classificationRes.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .collect(Collectors.toMap(Map.Entry::getKey,
                                Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

                int limit = 0;
                int matang = 0;
                int mentah = 0;
                double totalNilaiMatang = 0.0;
                double totalNilaiMentah = 0.0;
                int index = 0;
                for (Map.Entry<Integer, Double> entry : sortedRes.entrySet()) {
                    if (limit >= sortedRes.size() - k) {
                        if (trainData[entry.getKey()][1].equalsIgnoreCase("Matang")) {
                            matang++;
                            totalNilaiMatang += entry.getValue();
                        } else {
                            mentah++;
                            totalNilaiMentah += entry.getValue();
                        }
                        index++;
                    }
                    limit++;
                }

                timeEnd = System.currentTimeMillis();
                timeProgram = timeEnd - timeStart;
                second = timeProgram / 1000;

                String[] imageName = testData[i][0].split("/");
                if (matang > mentah) {
                    logText.setText(logText.getText() + "\n" + "Image test for " + imageName[imageName.length - 1] + ", " + imageName[imageName.length - 2] + " : matang");
                    System.out.println("Image test for " + imageName[imageName.length - 1] + ", " + imageName[imageName.length - 2] + " : matang");
                    if (!data.containsKey(imageName[imageName.length - 1])) {
                        data.put(imageName[imageName.length - 1], new Object[]{imageName[imageName.length - 1], imageName[imageName.length - 2], "Matang", Long.toString(second)});
                    } else {
                        data.replace(imageName[imageName.length - 1], new Object[]{imageName[imageName.length - 1], imageName[imageName.length - 2], "Matang", Long.toString(second)});
                    }
                } else if (matang < mentah) {
                    logText.setText(logText.getText() + "\n" + "Image test for " + imageName[imageName.length - 1] + ", " + imageName[imageName.length - 2] + " : mentah");
                    System.out.println("Image test for " + imageName[imageName.length - 1] + ", " + imageName[imageName.length - 2] + " : mentah");
                    if (!data.containsKey(imageName[imageName.length - 1])) {
                        data.put(imageName[imageName.length - 1], new Object[]{imageName[imageName.length - 1], imageName[imageName.length - 2], "Mentah", Long.toString(second)});
                    } else {
                        data.replace(imageName[imageName.length - 1], new Object[]{imageName[imageName.length - 1], imageName[imageName.length - 2], "Mentah", Long.toString(second)});
                    }
                } else {
                    if (totalNilaiMatang > totalNilaiMentah) {
                        logText.setText(logText.getText() + "\n" + "Image test for " + imageName[imageName.length - 1] + ", " + imageName[imageName.length - 2] + " : matang");
                        System.out.println("Image test for " + imageName[imageName.length - 1] + ", " + imageName[imageName.length - 2] + " : matang");
                        if (!data.containsKey(imageName[imageName.length - 1])) {
                            data.put(imageName[imageName.length - 1], new Object[]{imageName[imageName.length - 1], imageName[imageName.length - 2], "Matang", Long.toString(second)});
                        } else {
                            data.replace(imageName[imageName.length - 1], new Object[]{imageName[imageName.length - 1], imageName[imageName.length - 2], "Matang", Long.toString(second)});
                        }
                    } else if (totalNilaiMatang < totalNilaiMentah) {
                        logText.setText(logText.getText() + "\n" + "Image test for " + imageName[imageName.length - 1] + ", " + imageName[imageName.length - 2] + " : mentah");
                        System.out.println("Image test for " + imageName[imageName.length - 1] + ", " + imageName[imageName.length - 2] + " : mentah");
                        if (!data.containsKey(imageName[imageName.length - 1])) {
                            data.put(imageName[imageName.length - 1], new Object[]{imageName[imageName.length - 1], imageName[imageName.length - 2], "Mentah", Long.toString(second)});
                        } else {
                            data.replace(imageName[imageName.length - 1], new Object[]{imageName[imageName.length - 1], imageName[imageName.length - 2], "Mentah", Long.toString(second)});
                        }
                    } else {
                        logText.setText(logText.getText() + "\n" + "Can't Classified");
                        System.out.println("Can't Classified"); //not possible
                    }
                }

                logText.setText(logText.getText() + "\n" + "Classification Success");
                System.out.println("Classification Success");
                logText.setText(logText.getText() + "\n" + "");
                System.out.println("");
                //Print the running time program on log UI and terminal
                logText.setText(logText.getText() + "\n" + "Running Time of Program " + second + " second");
                System.out.println("Running Time of Program " + second + " second");
                logText.setText(logText.getText() + "\n" + "");
                System.out.println("");
            }

            //Iterate over data and write to sheet
            Set<String> keyset = data.keySet();
            int rownum = 0;
            for (String key : keyset) {
                Row row = sheet.createRow(rownum++);
                Object[] objArr = data.get(key);
                int cellnum = 0;
                for (Object obj : objArr) {
                    Cell cell = row.createCell(cellnum++);
                    if (obj instanceof String) {
                        cell.setCellValue((String) obj);
                    } else if (obj instanceof Integer) {
                        cell.setCellValue((Integer) obj);
                    }
                }
            }
            try {
                //Write the workbook in file system
                FileOutputStream out = new FileOutputStream(new File("Classification Result " + a + ".xlsx"));
                workbook.write(out);
                out.close();
                logText.setText(logText.getText() + "\n" + "Classification Result " + a + ".xlsx Written Successfully on Disk");
                System.out.println("Classification Result " + a + ".xlsx Written Successfully on Disk");
            } catch (Exception e) {
                e.printStackTrace();
            }

            imgProc.src.release();
            imgProc.src = null;
            imgProc.srcBlur.release();
            imgProc.srcBlur = null;
            imgProc.dst.release();
            imgProc.dst = null;
            imgProc.detectedEdges.release();
            imgProc.detectedEdges = null;
            imgProc.greyImage.release();
            imgProc.greyImage = null;
            imgProc.hierarchy.release();
            imgProc.hierarchy = null;
            imgProc.mask.release();
            imgProc.mask = null;
            imgProc.drawing.release();
            imgProc.drawing = null;
            imgProc.image_lab.release();
            imgProc.image_lab = null;
            imgProc.out.release();
            imgProc.out = null;
            imgProc.out_2.release();
            imgProc.out_2 = null;
            imgProc.samples32f.release();
            imgProc.samples32f = null;
            imgProc.samples.release();
            imgProc.samples = null;
            imgProc.filtered_pixel.release();
            imgProc.filtered_pixel = null;
            imgProc.centers.release();
            imgProc.centers = null;
            imgProc.labels.release();
            imgProc.labels = null;
            System.gc();

            dominantColorTest = new ArrayList<>();
        }
        statusText.setText("Done");
        logText.setText(logText.getText() + "\n" + "");
        System.out.println("");
        logText.setText(logText.getText() + "\n" + "Program Done");
        System.out.println("Program Done");
    }

    //UI for demo program
    public void runnerForGUIImage(int threshold, int cluster, int dominant, int k, JTextArea logText, String path_test, JLabel statusText, String path_train) {
        //Program starting time
        long timeStart = System.currentTimeMillis();
        logText.setText("Program Running");
        System.out.println("Program Running");
        this.logText = logText;

        //Load OpenCV Library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //Create Object from Class ImageProcessing
        ImageProcessing imgProc = new ImageProcessing(threshold, cluster, dominant);

        logText.setText(logText.getText() + "\n" + "Load Data Train");
        System.out.println("Load Data Train");
        //Load all train data with its classification (real)
        File[] files_train = new File(path_train).listFiles();
        ArrayList<String> allDataTrain = new ArrayList<>();
        allDataTrain = imgProc.loadDataTraining(files_train);
        String[][] trainData = new String[allDataTrain.size()][2];
        for (int i = 0; i < trainData.length; i++) {
            trainData[i][0] = allDataTrain.get(i);
            if (allDataTrain.get(i).contains("Matang")) {
                trainData[i][1] = "Matang";
            } else {
                trainData[i][1] = "Mentah";
            }
        }
        statusText.setText("Load Data Training Success");
        logText.setText(logText.getText() + "\n" + "Load Data Train Done");
        System.out.println("Load Data Train Done");

        logText.setText(logText.getText() + "\n" + "");
        System.out.println("");

        logText.setText(logText.getText() + "\n" + "Load Data Test");
        System.out.println("Load Data Test");
        File files_test = new File(path_test);
        String imageTest = files_test.getAbsolutePath();
        System.out.println("Load Data Test Success" + "\n");
        logText.setText(logText.getText() + "\n" + "Load Data Test Success" + "\n");

        //create array list to store dominant color of all train data and test data
        ArrayList<double[][]> dominantColorTrain = new ArrayList<>();
        ArrayList<double[][]> dominantColorTest = new ArrayList<>();

        //Code to show all image in Data Test Folder
        Mat imgTestData = Imgcodecs.imread(imageTest);
        if (imgTestData.empty()) {
            System.out.println("Empty image: " + files_test);
            System.out.println("Can't find path");
            System.exit(0);
        }
        JFrame testDataFrame = new JFrame("Original Image");
        testDataFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Image originalImage = HighGui.toBufferedImage(imgTestData);
        imgProc.addComponentsToPane(testDataFrame.getContentPane(), originalImage);
        testDataFrame.pack();
        testDataFrame.setLocationRelativeTo(null);
        testDataFrame.setVisible(true);
        logText.setText(logText.getText() + "\n" + "");
        System.out.println("");

        //Image Processing for data train
        logText.setText(logText.getText() + "\n" + "Do Processing On Data Train");
        System.out.println("Do Processing On Data Train");
        logText.setText(logText.getText() + "\n" + "Data Train Result:");
        System.out.println("Data Train Result:");
        //extract dominant color from train data
        for (int i = 0; i < trainData.length; i++) {
            dominantColorTrain.add(imgProc.extractFeature(trainData[i][0], 0));
        }
        logText.setText(logText.getText() + "\n" + "Processing Data Train Success");
        statusText.setText("Processing Data Train Success");
        System.out.println("Processing Data Train Succcess");

        logText.setText(logText.getText() + "\n" + "");
        System.out.println("");

        //Image Processing for data test
        logText.setText(logText.getText() + "\n" + "Do Processing On Data Test");
        System.out.println("Do Processing On Data Test");
        logText.setText(logText.getText() + "\n" + "Data Test Result:");
        System.out.println("Data Test Result:");
        dominantColorTest.add(imgProc.extractFeature(imageTest, 1));
        logText.setText(logText.getText() + "\n" + "Processing Data Test Success");
        statusText.setText("Processing Data Test Success");
        System.out.println("Processing Data Test Succcess");

        logText.setText(logText.getText() + "\n" + "");
        System.out.println("");

        //classification
        logText.setText(logText.getText() + "\n" + "Do Classification");
        System.out.println("Do Classification");
        logText.setText(logText.getText() + "\n" + "Result:");
        System.out.println("Result:");
        statusText.setText("Classification");
        for (int i = 0; i < dominantColorTest.size(); i++) {
            Map<Integer, Double> classificationRes = new HashMap<>();
            logText.setText(logText.getText() + "\n" + "Classification computation result for test - " + i);
            System.out.println("Classification computation result for test - " + i);
            for (int j = 0; j < dominantColorTrain.size(); j++) {
                double res = imgProc.doClassification(dominantColorTest.get(i), dominantColorTrain.get(j));
                classificationRes.put(j, res);
            }
            Map<Integer, Double> sortedRes = classificationRes.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            int limit = 0;
            int matang = 0;
            int mentah = 0;
            double totalNilaiMatang = 0.0;
            double totalNilaiMentah = 0.0;
            Mat[] imgCandidateNearestNeighbor = new Mat[k];
            int index = 0;
            for (Map.Entry<Integer, Double> entry : sortedRes.entrySet()) {
                if (limit >= sortedRes.size() - k) {
                    imgCandidateNearestNeighbor[index] = Imgcodecs.imread(trainData[entry.getKey()][0]);//Variable use for take path of the nearest neighbor image
                    if (trainData[entry.getKey()][1].equalsIgnoreCase("Matang")) {
                        matang++;
                        totalNilaiMatang += entry.getValue();
                    } else {
                        mentah++;
                        totalNilaiMentah += entry.getValue();
                    }
                    index++;
                }
                limit++;
            }

            //Show classification result
            //substring is use for take the path of the image name,
            //ex: D:\Campus\Semester 12\Skripsi\Skripsi Sekarang\Program Skripsi\Klasifikasi Kematangan Buah Mangga Berdasarkan Warna\data-test\Matang\mangga-matang-test-1-rotate
            //it will only be mangga-matang-test-1-rotate
            String[] imageName = imageTest.split("/");
            if (matang > mentah) {
                logText.setText(logText.getText() + "\n" + "Image test for " + imageName[0].substring(133) + ", " + "Folder: " + imageName[0].substring(126, 132) + " : matang");
                System.out.println("Image test for " + imageName[0].substring(133) + ", " + "Folder: " + imageName[0].substring(126, 132) + " : matang");
            } else if (matang < mentah) {
                logText.setText(logText.getText() + "\n" + "Image test for " + imageName[0].substring(133) + ", " + "Folder: " + imageName[0].substring(126, 132) + " : mentah");
                System.out.println("Image test for " + imageName[0].substring(133) + ", " + "Folder: " + imageName[0].substring(126, 132) + " : mentah");
            } else {
                if (totalNilaiMatang > totalNilaiMentah) {
                    logText.setText(logText.getText() + "\n" + "Image test for " + imageName[0].substring(133) + ", " + "Folder: " + imageName[0].substring(126, 132) + " : matang");
                    System.out.println("Image test for " + imageName[0].substring(133) + ", " + "Folder: " + imageName[0].substring(126, 132) + " : matang");
                } else if (totalNilaiMatang < totalNilaiMentah) {
                    logText.setText(logText.getText() + "\n" + "Image test for " + imageName[0].substring(133) + ", " + "Folder: " + imageName[0].substring(126, 132) + " : mentah");
                    System.out.println("Image test for " + imageName[0].substring(133) + ", " + "Folder: " + imageName[0].substring(126, 132) + " : mentah");
                } else {
                    logText.setText(logText.getText() + "\n" + "Can't Classified");
                    System.out.println("Can't Classified"); //not possible
                }
            }

            //Code to show k Nearest Neighbor Image for 1 Image
            JFrame nearestNeighborFrame;
            for (int j = 0; j < k; j++) {
                // buat buffered image, load ke javafx
                nearestNeighborFrame = new JFrame("Nearest Neighbor Image - " + (j + 1));
                nearestNeighborFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                Image imgNearestNeighbor = HighGui.toBufferedImage(imgCandidateNearestNeighbor[j]);
                imgProc.addComponentsToPane(nearestNeighborFrame.getContentPane(), imgNearestNeighbor);
                nearestNeighborFrame.pack();
                nearestNeighborFrame.setLocationRelativeTo(null);
                nearestNeighborFrame.setVisible(true);
            }

            logText.setText(logText.getText() + "\n" + "Classification Success");
            System.out.println("Classification Success");
            logText.setText(logText.getText() + "\n" + "");
            System.out.println("");
            //Program end time
            long timeEnd = System.currentTimeMillis();
            long timeProgram = timeEnd - timeStart;
            //Calculate the program running time
            long second = timeProgram / 1000;
            //Print the program running time on UI and terminal
            logText.setText(logText.getText() + "\n" + "Running Time of Program " + second + " second");
            System.out.println("Running Time of Program " + second + " second");
        }
        statusText.setText("Done");
        logText.setText(logText.getText() + "\n" + "Program Done");
        System.out.println("Program Done");
        logText.setText(logText.getText() + "\n" + "");
        System.out.println("");
    }
}//end class
