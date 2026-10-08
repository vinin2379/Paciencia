package template;

import java.util.ArrayList;
import java.util.List;

/**
 * Estrutura de dados Pilha (Stack) usando conceito LIFO (Last In, First Out).
 */
public class Pilha<T> {
    
    private List<T> elementos = new ArrayList<>();

    
    public void push( T item ) {
        elementos.add( item );
    }

    
    public T pop() {
        if ( isEmpty() ) return null;
        return elementos.remove( elementos.size() - 1 );
    }

    
    public T peek() {
        if ( isEmpty() ) return null;
        return elementos.get( elementos.size() - 1 );
    }

    public boolean isEmpty() {
        return elementos.isEmpty();
    }

    public int size() {
        return elementos.size();
    }

    
    public List<T> getElementosParaVisualizacao() {
        return new ArrayList<>( elementos );
    }
}