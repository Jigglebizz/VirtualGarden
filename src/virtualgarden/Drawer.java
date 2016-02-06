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

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * We need a priority based drawing system. This is a class to solve that. It
 * is a singleton.
 * 
 * @author Michael Hawthorne
 */
public class Drawer {
    private static Drawer instance;           // We got a singleton on our hands
    HashMap<Integer, ArrayList<Drawable>> parts;
    
    /**
     * Get the Drawer instance.
     * 
     * @return The Drawer instance.
     */
    public static Drawer getInstance() {
        if (instance == null) {
            instance = new Drawer();
        }
        return instance;
    }
    
    /**
     * Initialize a new Drawer.
     */
    private Drawer() {
        parts = new HashMap<Integer, ArrayList<Drawable>>();
    }
    
    /**
     * Add a Drawable to the draw list, with an attached priority.
     * 
     * @param d Drawable to add
     * @param priority  Priority to draw it all. Lower = further in the back.
     */
    public void addToDrawList(Drawable d, int priority) {
        ArrayList<Drawable> partsList;
        if (!parts.containsKey(priority)) {
            partsList =  new ArrayList<Drawable>();
        } else {
            partsList = parts.get(priority);
        }
        partsList.add(d);
        parts.put(priority, partsList);
    }
    
    /**
     * Draw our Drawables in  priority order.
     * @param g 
     */
    public void draw(Graphics g) {
        int adding = 0;
        for (int i = 0; i < parts.size(); i++) {
            while (!parts.containsKey(i+adding)) {
                adding++;
            }
            ArrayList<Drawable> partsList = parts.get(i+adding);
            for (Drawable part : partsList) {
                part.draw(g);
            }
        }
        parts = new HashMap<Integer, ArrayList<Drawable>>();
        System.gc();
    }
    
    /**
     * Interface for defining an object that can be drawn by the Drawer.
     */
    public interface Drawable {
        /**
         * Draw the object.
         * @param g 
         */
        public abstract void draw(Graphics g);
    }
}
