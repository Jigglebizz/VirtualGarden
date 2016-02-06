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

/**
 * The Roots of our plant. Mainly produces energy.
 * 
 * @author Michael Hawthorne
 */
public class PlantPartRoots extends PlantPart 
        implements Collision.Collider<PlantPartRoots>, Drawer.Drawable { 
    int depth;
    int width;
    float width_factor;
    boolean collided;       // We stop growing when we collide with other roots.
    
    /**
     * Initialize a new PlantPartRoots.
     * 
     * @param parent_x Parent's X position.
     * @param parent_y Parent's Y position.
     * @param chromosome Chromosomes of these roots.
     */
    public PlantPartRoots(int parent_x, int parent_y, Long chromosome) {
        super(parent_x, parent_y, chromosome);
        depth = (int)(chromosome >> 60) & 0xF;
        width = (int)Math.pow(((chromosome >> 56) & 0xF), 2);
        collided = false;
    }

    /**
     * Execute a grow cycle. Handles collision.
     * 
     * @param amt Amount to grow
     * @param parent Parent of roots.
     */
    @Override
    protected void grow( float amt, Plant parent) {
        super.grow(amt, parent);
        if (!collided) {
            width_factor += amt;
            // Collision code
            Collision collision = Collision.getInstance();
            try {
                if ( collision.isColliding((Collision.Collider) this)) {
                    collided = true;
                }
            }
            catch (Collision.ColliderNotPopulatedException e) {
                System.err.println("You forgot to populate the collider");
            }
        }
    }

    /**
     * Draw the roots.
     * 
     * @param g Graphics object.
     */
    @Override
    public void draw(Graphics g) {
        float depthScale = 1 - ((float)depth / 16f);
        Color c = new Color((int)(128 * depthScale), (int)(93 * depthScale), (int)(18 * depthScale));
        g.setColor(c);
        
        g.fillOval((int) (parent_x - getRadius()), (int) (parent_y - getRadius()), 
                   (int)getRadius() * 2, (int)getRadius() * 2);
    }

    /**
     * Calculate consumed energy.
     * 
     * @return Energy consumed.
     */
    @Override
    protected float consumeEnergy() {
        return depth * width * age;
    }

    /**
     * Calculate produced energy.
     * @return Energy produced.
     */
    @Override
    protected float produceEnergy() {
        return depth * getRadius();
    }
    
    /**
     * Get the radius of the roots.
     * 
     * @return Radius of the roots.
     */
    public float getRadius() {
        return width_factor * width / 2;
    }
    
    /**
     * Collision detection function.
     * 
     * @param other Other Roots to collide with.
     * @return Whether or not we are colliding.
     */
    @Override
    public boolean isColliding(PlantPartRoots other) {
        float distance = (float)Math.sqrt(Math.pow(parent_x - other.getParentX(), 2) + 
                                          Math.pow(parent_y - other.getParentY(), 2));
        float size = getRadius() + other.getRadius();
        return size > distance;
    }
}
