package template;

/**
 * Fila (FIFO) genérica implementada com lista encadeada.
 * 
 * Operações principais: enqueue (enfileirar) e dequeue (desenfileirar).
 * 
 * @param <T> Tipo dos elementos da fila.
 */
public class Fila<T> {

    private class No {
        T dado;
        No prox;
        No( T dado ) {
            this.dado = dado;
        }
    }

    private No inicio;
    private No fim;
    private int tamanho;

    /**
     * Insere um elemento no final da fila.
     */
    public void enqueue( T item ) {
        No novo = new No( item );
        if ( fim == null ) {
            inicio = novo;
        } else {
            fim.prox = novo;
        }
        fim = novo;
        tamanho++;
    }

    /**
     * Remove e retorna o elemento do início da fila.
     */
    public T dequeue() {
        if ( inicio == null ) {
            throw new IllegalStateException( "A fila está vazia!" );
        }
        T dado = inicio.dado;
        inicio = inicio.prox;
        if ( inicio == null ) {
            fim = null;
        }
        tamanho--;
        return dado;
    }

    /**
     * Retorna (sem remover) o elemento do início da fila.
     */
    public T peek() {
        if ( inicio == null ) {
            throw new IllegalStateException( "A fila está vazia!" );
        }
        return inicio.dado;
    }

    /**
     * Retorna (sem remover) o elemento do final da fila.
     */
    public T peekLast() {
        if ( fim == null ) {
            throw new IllegalStateException( "A fila está vazia!" );
        }
        return fim.dado;
    }

    public boolean isEmpty() {
        return tamanho == 0;
    }

    public int size() {
        return tamanho;
    }

    public void clear() {
        inicio = null;
        fim = null;
        tamanho = 0;
    }

}