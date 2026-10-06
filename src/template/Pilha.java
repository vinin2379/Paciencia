package template;

import java.util.ArrayList;
import java.util.List;

/**
 * Estrutura de dados Pilha (Stack) usando conceito LIFO (Last In, First Out).
 */
public class Pilha<T> {
    
    private List<T> elementos = new ArrayList<>();

    // Adiciona no topo da pilha
    public void push( T item ) {
        elementos.add( item );
    }

    // Remove e retorna o item do topo da pilha
    public T pop() {
        if ( isEmpty() ) return null;
        return elementos.remove( elementos.size() - 1 );
    }

    // Apenas olha qual é o item do topo (sem remover)
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

    // Método didático para a demonstração visual na Main
    public List<T> getElementosParaVisualizacao() {
        return new ArrayList<>( elementos );
    }
}