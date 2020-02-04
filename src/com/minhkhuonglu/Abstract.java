package com.minhkhuonglu;

import java.util.logging.*;

/**
 * Class for the edge and vertex object to extends from
 * @author minhkhuonglu
 * @since 13
 * @version 1.0
 */
abstract class Abstract implements Interface{
    /**
     * store a name for an object
     */
    private String name;

    /**
     * Constructor for Edge
     */
    Abstract() {
        Logger.getLogger("New object created");
    }

    /**
     * getID of an object
     * @return ID of an object
     */
    public abstract int getID();

    /**
     * getName of an object
     * @return its name
     */
    public String getName(){
        return this.name;
    }

    /**
     * print out the object
     */
    public abstract void printMe();
}
