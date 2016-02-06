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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Singleton class that handles registering objects that can collide with each
 * other. Only objects of a similar type may collide with each other. This would
 * not work for a video game, but it exactly what I'd like for this application.
 * 
 * @author Michael Hawthorne
 */
public class Collision {
    private static Collision instance;              // Dam son, a singleton.
    private HashMap<Class, ArrayList<Collider>> colliders; // List of colliders.
    
    /**
     * Get our Collision instance.
     * @return Collision instance.
     */
    public static Collision getInstance() {
        if (instance == null) {
            instance = new Collision();
        }
        return instance;
    }
    
    /**
     * Collision ctor.
     */
    private Collision() {
        colliders = new HashMap<Class, ArrayList<Collider>>();
    }
    
    /**
     * Register a new object with the Collision object.
     * 
     * @param c Collider to register.
     */
    public void register(Collider c) {
        ArrayList<Collider> colliderList;
        if (!colliders.containsKey(c.getClass())) {
            colliderList = new ArrayList<Collider>();
        } else {
            colliderList = colliders.get(c.getClass());
        }
        colliderList.add(c);
        colliders.put(c.getClass(), colliderList);
    }
    
    /**
     * Remove an object from the Collision object.
     * 
     * @param c The Collider to remove.
     */
    public void remove(Collider c) {
        ArrayList<Collider> cList = colliders.get(c.getClass());
        cList.remove(c);
        colliders.put(c.getClass(), cList);
    }
    
    /**
     * Generic method. We can implement object-specific interactions by enforcing
     * only certain types of objects to be passed in
     * @param <T>
     * @param c
     * @return
     * @throws virtualgarden.Collision.ColliderNotPopulatedException 
     */
    public <T extends Collider> boolean isColliding(T c) throws ColliderNotPopulatedException {
        ArrayList<T> cList = (ArrayList<T>)colliders.get(c.getClass());
        
        if (cList == null) {
            throw new ColliderNotPopulatedException();
        }
        
        for (T otherC : cList) {
            if (c != otherC && c.isColliding((T)otherC)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Specialized Exception for when we try to check if something is colliding
     * with an uninitialized list.
     */
    public static class ColliderNotPopulatedException extends Exception {
        
    }
    
    /**
     * Defines a Collider object.
     * @param <T> Should be the same as the object implementing this.
     */
    public interface Collider<T> {
        
        /**
         * Is this object colliding?
         * @param other The object to check against.
         * @return Are we colliding?
         */
        public abstract boolean isColliding(T other);
    }
}
