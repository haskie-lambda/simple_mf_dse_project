package org.Team107.MF.functionalbasics;

/**
 *
 * @author Fabian Schneider
 */
public class Tuple<A, B> {
    private A _first;
    private B _second;
    
    public Tuple(A a, B b){
        this._first = a;
        this._second = b;
    }
    
    public A first() {
        return _first;
    }
    
    public B second() {
        return _second;
    }
    

    @Override
    public String toString() {
        return "(" + _second + ", " + _first + ")";
    }


    
}
