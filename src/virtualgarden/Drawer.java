/*
 * The MIT License
 *
 * Copyright 2016 Michael.
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
 * We need a priority based drawing system. This is a class to solve that.
 * 
 * @author Michael
 */
public class Drawer {
    private static Drawer instance;                 // We got a singleton on our hands
    HashMap<Integer, ArrayList<PlantPart>> parts;
    
    public static Drawer getInstance() {
        if (instance == null) {
            instance = new Drawer();
        }
        return instance;
    }
    
    private Drawer() {
        parts = new HashMap<Integer, ArrayList<PlantPart>>();
    }
    
    public void addToDrawList(PlantPart pp, int priority) {
        ArrayList<PlantPart> partsList;
        if (!parts.containsKey(priority)) {
            partsList =  new ArrayList<PlantPart>();
        } else {
            partsList = parts.get(priority);
        }
        partsList.add(pp);
        parts.put(priority, partsList);
    }
    
    public void draw(Graphics g) {
        int adding = 0;
        for (int i = 0; i < parts.size(); i++) {
            while (!parts.containsKey(i+adding)) {
                adding++;
            }
            ArrayList<PlantPart> partsList = parts.get(i+adding);
            for (PlantPart part : partsList) {
                part.draw(g);
            }
        }
        parts = new HashMap<Integer, ArrayList<PlantPart>>();
        System.gc();
    }
}
