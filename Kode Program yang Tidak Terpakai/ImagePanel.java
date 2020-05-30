package Algorithm;


import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Image;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Michael
 */
public class ImagePanel extends JPanel{
    private Image image;
    
    public ImagePanel() {
    }
    
    public void setImage(Image newImage){
        this.image = newImage;
    }
    
    public Image getImage(){
        return image;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this); //Draw Image to JPanel
    }
}
