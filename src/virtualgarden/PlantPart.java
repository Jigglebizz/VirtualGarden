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

/**
 * Defines a plant part.
 * 
 * @author Michael Hawthorne
 */
public abstract class PlantPart {
    protected long chromosome;
    protected float age;        // Used for calculating stages of growth.
    protected int parent_x;
    protected int parent_y;
    
    /**
     * Initializes a new PlantPart.
     * 
     * @param x X position.
     * @param y Y position.
     * @param chromosome Chromosome of the part.
     */
    public PlantPart(int x, int y, Long chromosome) {
        this.chromosome = chromosome;
        parent_x = x;
        parent_y = y;
        age = 0;
    }
    
    /**
     * Get parent Plant's X position.
     * 
     * @return X position on screen.
     */
    public int getParentX() {
        return parent_x;
    }
    
    /**
     * Get parent Plant's Y Position.
     * 
     * @return Y position on screen.
     */
    public int getParentY() {
        return parent_y;
    }
    
    /**
     * Gets a list of genes that an 'enzyme' can read in a chromosome. I ended
     * up not defining genes this way and may remove this.
     * 
     * @return A set of Genes.
     */
    //protected abstract ArrayList<Gene> getGenes();
    
    /**
     * Grow function. increases age.
     * 
     * @param amt Amount to grow.
     * @param parent The parent. Likely be needed by child classes.
     */
    protected void grow(float amt, Plant parent) {
        age += amt;
    }
    
    /**
     * Calculate consumed energy for grow cycle.
     * 
     * @return Amount of energy consumed.
     */
    protected abstract float consumeEnergy();
    
    /**
     * Calculate produced energy for grow cycle.
     * 
     * @return Amount of energy produced.
     */
    protected abstract float produceEnergy();
    
    
//    protected static abstract class Gene {
//        
//        private short phenome; // Packet-start, if you're into IT protocols
//        private short payload_size; // Size of the payload
//        
//        public Gene () {
//           this.phenome = definePhenome();
//           this.payload_size = definePayload();
//        }
//        
//        public short getPhenome() {
//            return phenome;
//        }
//        
//        public short getPayloadSize() {
//            return payload_size;
//        }
//        
//        public abstract void InterpretGene(long chromosome);   // What our gene does
//        public abstract short definePhenome();
//        public abstract short definePayload();
//    }
}
