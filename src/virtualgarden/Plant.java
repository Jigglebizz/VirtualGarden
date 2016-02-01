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
 *
 * @author Michael
 */
public class Plant extends PlantPart {
    Chromosomes chromosomes;
    public int x, y;
    
    PlantPartRoots roots;
    ArrayList<PlantPartFlower> flowers;
    
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
    }
    
    public void die() {
        Garden.getInstance().notifyDeath(this);
    }
    
    public void detachColliders() {
        Collision c = Collision.getInstance();
        c.remove(roots);
    }
    
    public Chromosomes getChromosomes() {
        return chromosomes;
    }
    
    public void drawRoots(Graphics g) {
        roots.draw(g);
    }
    
    public void grow(float amt) {
        super.grow(amt, this);
        roots.grow(amt, this);
        for (PlantPartFlower f : flowers) {
            f.grow(amt, this);
        }
    }
    
    public void addToDrawer(Drawer d) {
        d.addToDrawList(roots, 0);
        d.addToDrawList(this, 1);
        for( PlantPartFlower f : flowers) {
            d.addToDrawList(f, 2);
        }
    }
    
    @Override
    public void draw(Graphics g) {
        g.setColor(new Color(53, 196, 70));
        g.fillOval(x - 3, y - 3, 6, 6);
    }

    @Override
    protected float consumeEnergy() {
        float totalEnergy = 0;
        totalEnergy += roots.consumeEnergy();
        for (PlantPartFlower f : flowers) {
            totalEnergy += f.consumeEnergy();
        }
        return totalEnergy;
    }

    @Override
    protected float produceEnergy() {
        float totalEnergy = 0;
        totalEnergy += roots.produceEnergy();
        for (PlantPartFlower f : flowers) {
            totalEnergy += f.produceEnergy();
        }
        return totalEnergy;
    }

    @Override
    protected ArrayList<Gene> getGenes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static class Chromosomes {
        // Our plant will be dynamic. It will have a set of Chromosomes
        // each containing a set of genes. A Chromosome will control a component
        // of the plant. A gene will control the pieces of that component.

        // Each Chromosome is represented by a long
        // Each Gene is represented by 4 bits
        long roots;         // Energy process
        long leaf;          // Light absorption
        long stem;          // Energy distribution
        long flower;        // Reproduction
        long all_plant;     // Variables that affect the whole plant, such as
                            // lifespan
        
        private static final float crossover_rate = 0.7f;
        private static final float mutation_rate = 0.001f;
        
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
        
        public static Chromosomes mate(Chromosomes m, Chromosomes f) {
            Chromosomes newC = new Chromosomes();
            newC.roots = mateSingleChromosome(m.roots, f.roots);
            newC.leaf = mateSingleChromosome(m.leaf, f.leaf);
            newC.stem = mateSingleChromosome(m.stem, f.stem);
            newC.flower = mateSingleChromosome(m.flower, f.flower);
            newC.all_plant = mateSingleChromosome(m.all_plant, f.all_plant);
            
            return newC;
        }
        
        private static long mateSingleChromosome(long m, long f) {
            Random r = new Random();
            boolean hasCrossedOver = false;
            long crossover_mask = Long.MAX_VALUE;
            long mutation_mask = Long.highestOneBit(Long.MAX_VALUE);
            long newC = m;
            for (int i = 0; i < Long.SIZE; i++) {
                float chance = r.nextFloat();
                if (!hasCrossedOver && chance <= crossover_rate) {
                    newC = newC & ~crossover_mask | f & crossover_mask;
                    hasCrossedOver = true;
                }
                if (chance < mutation_rate) {
                    newC = (newC & ~mutation_mask) | ~(newC & mutation_mask);
                }
                
                crossover_mask &= ~mutation_mask;
                mutation_mask = (mutation_mask >> 1) & ~mutation_mask;
            }
            
            return newC;
        }
    }
}
