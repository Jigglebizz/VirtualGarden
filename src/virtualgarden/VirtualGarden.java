/*
 * The MIT License
 *
 * Copyright 2016 Michael Hawthorne.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package virtualgarden;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This is a virtual garden. It is an automata and a visual representation
 * of genetic algorithms at work
 * 
 * @author Michael Hawthorne
 */
public class VirtualGarden {
    private static final int WIDTH = 1900;
    private static final int HEIGHT = 900;
    static Garden garden;
    
    /**
     * Main function
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final DrawPanel panel = new DrawPanel();
        JFrame application = new JFrame();
        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        application.add(panel);
        application.setSize(WIDTH, HEIGHT);
        application.setVisible(true);
        
        Garden.Init(WIDTH, HEIGHT, 65);
        garden = Garden.getInstance();
        
        
        new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            garden.grow(0.001f);
                            panel.repaint();
                        }
                    }
                }).start();
    }
    
    /**
     * Some Java JPanel stuff. 
     * I don't really want to think too hard about Swing.
     */
    private static class DrawPanel extends JPanel {
        
        /**
         * ctor
         */
        public DrawPanel() {
            super();
            setBackground(new Color(54, 19, 8));
        }
        
        /**
         * Draws our garden
         * @param g 
         */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            garden.draw(g);
        }
    }
}
