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
import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Michael
 */
public class PlantPartRoots extends PlantPart implements Collider<PlantPartRoots> { 
    int depth;
    int width;
    float width_factor;
    boolean collided;
    
    public PlantPartRoots(int parent_x, int parent_y, Long chromosome) {
        super(parent_x, parent_y, chromosome);
        depth = (int)(chromosome >> 60) & 0xF;
        width = (int)Math.pow(((chromosome >> 56) & 0xF), 2);
        collided = false;
    }

    @Override
    protected ArrayList<Gene> getGenes() {
        ArrayList<Gene> genes = new ArrayList<Gene>();
        return genes;
    }

    @Override
    protected void grow( float amt, Plant parent) {
        super.grow(amt, parent);
        if (!collided) {
            width_factor += amt;
            // Collision code
            Collision collision = Collision.getInstance();
            try {
                if ( collision.isColliding((Collider) this)) {
                    collided = true;
                }
            }
            catch (Collision.ColliderNotPopulatedException e) {
                System.err.println("You forgot to populate the collider");
            }
        }
    }

    @Override
    protected void draw(Graphics g) {
        float depthScale = 1 - ((float)depth / 16f);
        Color c = new Color((int)(128 * depthScale), (int)(93 * depthScale), (int)(18 * depthScale));
        g.setColor(c);
        
        g.fillOval((int) (parent_x - getRadius()), (int) (parent_y - getRadius()), 
                   (int)getRadius() * 2, (int)getRadius() * 2);
    }

    @Override
    protected float consumeEnergy() {
        return depth * width * age;
    }

    @Override
    protected float produceEnergy() {
        return depth * getRadius();
    }
    
    public float getRadius() {
        return width_factor * width / 2;
    }
    
    @Override
    public boolean isColliding(PlantPartRoots other) {
        float distance = (float)Math.sqrt(Math.pow(parent_x - other.getParentX(), 2) + 
                                          Math.pow(parent_y - other.getParentY(), 2));
        float size = getRadius() + other.getRadius();
        return size > distance;
    }
}
