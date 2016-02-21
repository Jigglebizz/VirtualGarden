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
import java.util.ArrayList;
import java.util.Random;

/**
 * Our plant class. Defines functionality, genetics, etc.
 * 
 * @author Michael Hawthorne
 */
public class Plant extends PlantPart implements Drawer.Drawable {
    Chromosomes chromosomes;            // Set of chromosomes
    public int x, y;                    // Position
    
    PlantPartRoots roots;               // Sub-part of plant. The roots.
    ArrayList<PlantPartFlower> flowers; // The flowers of the plant.
    
    /**
     * Plant ctor.
     * 
     * @param x X position.
     * @param y Y position.
     * @param chromosomes Set of Chromosomes to grow from.
     */
    public Plant(int x, int y, Chromosomes chromosomes) {
        super(x, y, chromosomes.all_plant);
        this.chromosomes = chromosomes;
        this.x = x;
        this.y = y;
        
        roots = new PlantPartRoots(x, y, chromosomes.roots);
        Collision c = Collision.getInstance();
        c.register(roots);
        
        flowers = new ArrayList<PlantPartFlower>();
        flowers.add(new PlantPartFlower(x, y, chromosomes.flower));
        
        try {
            if (c.isColliding(roots)) {
                die();
            }
        } catch (Collision.ColliderNotPopulatedException e) {
            System.err.println("You forgot to populate the collider\n"
                               + e.toString());
        }
    }
    
    /**
     * Kill yourself.
     */
    public void die() {
        Garden.getInstance().notifyDeath(this);
    }
    
    /**
     * Called by Garden when plant is destroyed.
     */
    public void detachColliders() {
        Collision c = Collision.getInstance();
        c.remove(roots);
    }
    
    /**
     * Returns Chromosomes.
     * 
     * @return Our Chromosome set.
     */
    public Chromosomes getChromosomes() {
        return chromosomes;
    }
    
    /**
     * Advance time, grow the plant.
     * 
     * @param amt Amount of time.
     */
    public void grow(float amt) {
        super.grow(amt, this);
        
        // Calculate how much energy we're producing
        float energy = roots.produceEnergy();
        for (PlantPartFlower f : flowers) {
            energy += f.produceEnergy();
        }
        
        // Grow each part in order that energy reaches them
        if (energy >= roots.consumeEnergy()) {
            roots.grow(amt, this);
            energy -= roots.consumeEnergy();
            
            for (PlantPartFlower f : flowers) {
                if (energy >= f.consumeEnergy()) {
                    f.grow(amt, this);
                    energy -= f.consumeEnergy();
                }
            }
        }
        if (energy < 0) {
            die();
        }
    }
    
    /**
     * Add this plant to the Drawer.
     * 
     * @param d The Drawer.
     */
    public void addToDrawer(Drawer d) {
        d.addToDrawList(roots, 0);
        d.addToDrawList(this, 1);
        for( PlantPartFlower f : flowers) {
            d.addToDrawList(f, 2);
        }
    }
    
    /**
     * Draw the plant itself. It's just a green circle on its own.
     * 
     * @param g Graphics object
     */
    @Override
    public void draw(Graphics g) {
        g.setColor(new Color(53, 196, 70));
        g.fillOval(x - 3, y - 3, 6, 6);
    }

    /**
     * Calculate how much energy we consume in a grow cycle.
     * 
     * @return The amount of energy we consume.
     */
    @Override
    protected float consumeEnergy() {
        float totalEnergy = 0;
        totalEnergy += roots.consumeEnergy();
        for (PlantPartFlower f : flowers) {
            totalEnergy += f.consumeEnergy();
        }
        return totalEnergy;
    }

    /**
     * Calculate how much energy we produce in a grow cycle.
     * 
     * @return The amount of energy we produce.
     */
    @Override
    protected float produceEnergy() {
        float totalEnergy = 0;
        totalEnergy += roots.produceEnergy();
        for (PlantPartFlower f : flowers) {
            totalEnergy += f.produceEnergy();
        }
        return totalEnergy;
    }
    
    /**
     * Our Plant's set of Chromosomes.
     */
    public static class Chromosomes {
        // Our plant will be dynamic. It will have a set of Chromosomes
        // each containing a set of genes. A Chromosome will control a component
        // of the plant. A gene will control the pieces of that component.

        // Each Chromosome is represented by a long
        long roots;         // Energy process
        long leaf;          // Light absorption
        long stem;          // Energy distribution
        long flower;        // Reproduction
        long all_plant;     // Variables that affect the whole plant, such as
                            // lifespan
        
        // Constants for genetics. Play with these to make things grow/mate
        // differently.
        private static final float CROSSOVER_RATE = 0.7f;
        private static final float MUTATION_RATE = 0.001f;
        
        /**
         * Generate a stochastic set of genes.
         * 
         * @return A new Chromosomes object, initialized randomly.
         */
        public static Chromosomes generate() {
            Chromosomes c = new Chromosomes();
            Random r = new Random();
            c.roots = r.nextLong();
            c.leaf = r.nextLong();
            c.stem = r.nextLong();
            c.flower = r.nextLong();
            c.all_plant = r.nextLong();
            return c;
        }
        
        /**
         * Mate a set of Chromosomes.
         * 
         * @param m Mother
         * @param f Father
         * @return A new Chromosomes object, initialized via mating.
         */
        public static Chromosomes mate(Chromosomes m, Chromosomes f) {
            Chromosomes newC = new Chromosomes();
            newC.roots = mateSingleChromosome(m.roots, f.roots);
            newC.leaf = mateSingleChromosome(m.leaf, f.leaf);
            newC.stem = mateSingleChromosome(m.stem, f.stem);
            newC.flower = mateSingleChromosome(m.flower, f.flower);
            newC.all_plant = mateSingleChromosome(m.all_plant, f.all_plant);
            
            return newC;
        }
        
        /**
         * Helper function for mate. Mates a single Chromosome.
         * 
         * This function works by iterating through each bit of the mother, 
         * and picking a random number. If that number is below the crossover 
         * threshold, we do a crossover between mother and father. A crossover 
         * is an exchange of genes. The bit of the father replace the remaining 
         * bits of the mother. 
         * 
         * If that number is also below the mutation threshold, we do a 
         * mutation. That is, we flip the bit at that position.
         * 
         * @param m Mother
         * @param f Father
         * @return A new, mated chromosome.
         */
        private static long mateSingleChromosome(long m, long f) {
            Random r = new Random();
            boolean hasCrossedOver = false;
            long crossover_mask = Long.MAX_VALUE;
            long mutation_mask = Long.highestOneBit(Long.MAX_VALUE);
            long newC = m;
            for (int i = 0; i < Long.SIZE; i++) {
                float chance = r.nextFloat();
                if (!hasCrossedOver && chance <= CROSSOVER_RATE) {
                    newC = newC & ~crossover_mask | f & crossover_mask;
                    hasCrossedOver = true;
                }
                if (chance < MUTATION_RATE) {
                    newC = (newC & ~mutation_mask) | ~(newC & mutation_mask);
                }
                
                crossover_mask &= ~mutation_mask;
                mutation_mask = (mutation_mask >> 1) & ~mutation_mask;
            }
            
            return newC;
        }
    }
}
