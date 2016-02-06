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
 * Our garden. A singleton. Holds our plants and handles 
 * collection-safe creation and destruction.
 * 
 * @author Michael Hawthorne
 */
public class Garden
{
    private static Garden instance;             // Our singleton instance
    int width, height;
    ArrayList<Plant> plants;                    // Plants currently growing
    static ArrayList<Plant> plantsToAdd;        // Plants to be added on next cycle
    static ArrayList<Plant> plantsToDestroy;    // Plants to be destroyed on next cycle
    
    /**
     * Returns our Garden instance. Note that Init must be called before this.
     * 
     * @return Garden instance
     */
    public static Garden getInstance() {
        if (instance == null) {
            throw new RuntimeException("Must instantiate garden first with Init method");
        }
        return instance;
    }
    
    /**
     * Initializes our garden.
     * 
     * @param width Visual width.
     * @param height Visual height.
     * @param seeds Number of seeds to start with.
     */
    public static void Init(int width, int height, int seeds) {
        instance = new Garden(width, height, seeds);
    }
    
    /**
     * Garden ctor.
     * 
     * @param width Visual width.
     * @param height Visual height.
     * @param seeds Number of seeds to start with.
     */
    private Garden(int width, int height, int seeds)
    {
        this.width = width;
        this.height = height;
        plantsToAdd = new ArrayList<Plant>();
        plantsToDestroy = new ArrayList<Plant>();
        plantNewSeeds(seeds);
    }
    
    /**
     * A single cycle of our garden. Instructs plants to grow, and handles
     * creating/destroying plants afterward.
     * 
     * @param amt Time factor for growing.
     */
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
    
    /**
     * Notify garden to destroy plant at end of grow cycle. This is to make our
     * collections safe while we're iterating through the plants during the
     * grow phase.
     * 
     * @param p  The plant to destroy
     */
    public void notifyDeath(Plant p) {
        plantsToDestroy.add(p);
    }
    
    /**
     * Draws our plants.
     * 
     * @param g Graphics object
     */
    public void draw(Graphics g) {
        // Drawer allows us to draw our plants based on a depth value
        Drawer d = Drawer.getInstance();
        for (Plant p : plants) {
            p.addToDrawer(d);
        }
        d.draw(g);
    }
    
    /**
     * Clears the garden and plants all new seeds.
     * 
     * @param numSeeds The number of seeds.
     */
    private void plantNewSeeds(int numSeeds) {
        Random r = new Random();
        plants = new ArrayList<Plant>();
        for (int i = 0; i < numSeeds; i++) {
            Plant newPlant = new Plant(r.nextInt(width), r.nextInt(height), 
                    Plant.Chromosomes.generate());
            plantsToAdd.add(newPlant);
        }
    }
    
    /**
     * Plants a set of seeds around the parent.
     * 
     * If the seeds are planted outside the boundaries of the garden, they are
     * ignored/destroyed.
     * 
     * @param seeds A set of seeds to plant, determined from mating the parents.
     * @param x X position of parent.
     * @param y Y position of parent.
     * @param spread How wide the parent can spread seeds.
     */
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
    
    /**
     * Get a random mate from the garden.
     * 
     * @return A random plant from the garden.
     */
    public Plant getRandomMate() {
        Random r = new Random();
        return plants.get(r.nextInt(plants.size()));
    }
}
