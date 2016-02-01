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

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 *
 * @author Michael
 */
public class PlantPartFlower extends PlantPart {
    private final float hue;          // Hue of the flower from 0-1
    private final int seed_size;      // Size of seeds
    private final int seed_num;       // Number of seeds
    private final int seed_dispersal; // How far seeds spread when dropped
    
    private final int num_petals;
    private final int petal_size;
    private final float decline_age;
    private float flower_age;
    private int calculated_petal_size;
    
    private boolean spooged;
    
    public PlantPartFlower(int x, int y, Long chromosome) {
        super(x, y, chromosome);
        hue = ((int)(chromosome >> 56) & 0xFF) / 256f;
        seed_size = (int)((chromosome >> 52) & 0xF);
        seed_num  = (int)((chromosome >> 48) & 0x3);
        seed_dispersal = (int)((chromosome >> 44) & 0xF);
        
        num_petals = 3 + ((int)(chromosome >> 42) & 0x3);
        petal_size = 10 + (((int)(chromosome >> 38) & 0xF) * 2);
        decline_age = 0.5f + 3f * ((int)((chromosome >> 35) & 0x7) / 7f);
        
        flower_age = 0;
        calculated_petal_size = 0;
        spooged = false;
    }
    
    public void generateSeeds(Garden g, Plant parent) {
        Plant mate = g.getRandomMate();
        
        ArrayList<Plant.Chromosomes> seeds = new ArrayList<Plant.Chromosomes>();
        for (int i = 0; i < seed_num; i++) {
            seeds.add(Plant.Chromosomes.mate(
                                    parent.getChromosomes(), 
                                    mate.getChromosomes()));
        }
        g.plantSeedsFromReproduction(seeds, parent_x, parent_y, seed_dispersal * 56);
    }
    
    @Override
    protected void grow(float amt, Plant parent) {
        super.grow(amt, parent);
        flower_age += amt;
        
        if (flower_age <= decline_age) {
            calculated_petal_size = (int)(petal_size * (flower_age / decline_age));
        }
        else if (flower_age > decline_age) {
            if (!spooged) {
                generateSeeds(Garden.getInstance(), parent);
                spooged = true;
            }
            calculated_petal_size = (int)((1 - ((flower_age / decline_age) - 1)) * petal_size);
        }
        if (flower_age >= decline_age * 2) {
            calculated_petal_size = 0;
            parent.die();
        }
    }

    @Override
    protected ArrayList<Gene> getGenes() {
        ArrayList<Gene> genes = new ArrayList<Gene>();
        return genes;
    }

    @Override
    protected void draw(Graphics g) {
        for (int i = 0; i < num_petals; i++) {
            drawPetal(g, i);
        }
    }
    
    private void drawPetal(Graphics g, int i) {
        double angle = ((float)i / (float)num_petals) * 2 * Math.PI;
        double petal_angle = (2 * Math.PI) / (num_petals * 0.9f);
        
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];
        
        xPoints[0] = parent_x;
        xPoints[1] = (int)(parent_x + (calculated_petal_size * Math.cos(angle - petal_angle)));
        xPoints[2] = (int)(parent_x + (calculated_petal_size * Math.cos(angle + petal_angle)));
        
        yPoints[0] = parent_y;
        yPoints[1] = (int)(parent_y + (calculated_petal_size * Math.sin(angle - petal_angle)));
        yPoints[2] = (int)(parent_y + (calculated_petal_size * Math.sin(angle + petal_angle)));
        
        g.setColor(Color.getHSBColor(hue, 0.7f, 1));
        g.fillPolygon(xPoints, yPoints, 3);
        g.setColor(Color.getHSBColor(hue, 0.7f, 0.7f));
        g.drawPolyline(xPoints, yPoints, 3);
    }

    @Override
    protected float consumeEnergy() {
        return seed_size * seed_num * seed_dispersal;
    }

    @Override
    protected float produceEnergy() {
        return 0f;
    }
}
