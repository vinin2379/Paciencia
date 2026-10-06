package template;

import br.com.davidbuzatto.jsge.image.Image;

/**
 * Representa uma carta do baralho com suporte a imagens PNG.
 */
public class Carta {

    private static final String[] NOMES_NAIPES = { "Copas", "Ouros", "Paus", "Espadas" };
    private static final String[] NOMES_VALORES = {
        "", "Ás", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Valete", "Dama", "Rei"
    };

    final int naipe; // 0 e 1 vermelhos; 2 e 3 pretos
    final int valor; // 1 (A) até 13 (K)
    boolean virada;  // true = face para cima, false = costas

    private Image imagemFrente;
    private Image imagemVerso;

    public Carta( int naipe, int valor ) {
        this.naipe = naipe;
        this.valor = valor;
        this.virada = false;
    }

    public boolean vermelha() {
        return naipe < 2;
    }

    public Image getImagemFrente() {
        return imagemFrente;
    }

    public void setImagemFrente( Image imagemFrente ) {
        this.imagemFrente = imagemFrente;
    }

    public Image getImagemVerso() {
        return imagemVerso;
    }

    public void setImagemVerso( Image imagemVerso ) {
        this.imagemVerso = imagemVerso;
    }

    public String getCaminhoImagem() {
        return "resources/images/" + NOMES_VALORES[valor] + "_de_" + NOMES_NAIPES[naipe] + ".png";
    }
}