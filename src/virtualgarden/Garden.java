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
import java.util.Random;

/**
 *
 * @author Michael
 */
public class Garden
{
    private static Garden instance;
    int width, height;
    ArrayList<Plant> plants;
    static ArrayList<Plant> plantsToAdd;
    static ArrayList<Plant> plantsToDestroy;
    
    public static Garden getInstance() {
        if (instance == null) {
            throw new RuntimeException("Must instantiate garden first with Init method");
        }
        return instance;
    }
    
    public static void Init(int width, int height, int seeds) {
        instance = new Garden(width, height, seeds);
        plantsToAdd = new ArrayList<Plant>();
        plantsToDestroy = new ArrayList<Plant>();
    }
    
    private Garden(int width, int height, int seeds)
    {
        this.width = width;
        this.height = height;
        plantNewSeeds(seeds);
    }
    
    public void grow(float amt) {
        for (Plant p : plants) {
            p.grow(amt);
        }
        for (Plant p : plantsToAdd) {
            plants.add(p);
        }
        plantsToAdd.clear();
        for (Plant p : plantsToDestroy) {
            plants.remove(p);
            p.detachColliders();
        }
        plantsToDestroy.clear();
    }
    
    public void notifyDeath(Plant p) {
        plantsToDestroy.add(p);
    }
    
    public void draw(Graphics g) {
        Drawer d = Drawer.getInstance();
        for (Plant p : plants) {
            p.addToDrawer(d);
        }
        d.draw(g);
    }
    
    private void plantNewSeeds(int numSeeds) {
        Random r = new Random();
        plants = new ArrayList<Plant>();
        for (int i = 0; i < numSeeds; i++) {
            Plant newPlant = new Plant(r.nextInt(width), r.nextInt(height), 
                    Plant.Chromosomes.generate());
            plants.add(newPlant);
        }
    }
    
    public void plantSeedsFromReproduction(ArrayList<Plant.Chromosomes> seeds, int x, int y, int spread) {
        Random r = new Random();
        
        for (Plant.Chromosomes seed : seeds) {
            int angle = (int)(r.nextFloat() * 2 * Math.PI);
            int distance = (int)(r.nextFloat() * spread);

            int newX = x + (int)(distance * Math.cos(distance));
            int newY = y + (int)(distance * Math.sin(distance));
            
            if (newX > 0 && newX < width && newY > 0 && newY < height)
                plantsToAdd.add(new Plant(newX, newY, seed));
        }
        
    }
    
    public Plant getRandomMate() {
        Random r = new Random();
        return plants.get(r.nextInt(plants.size()));
    }
}
