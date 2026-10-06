package template;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import java.util.List;

/**
 * Janela secundária para visualizar as 7 colunas (Listas/Arrays) da mesa.
 */
public class JanelaColunas extends EngineFrame {

    private Main jogoPrincipal;

    public JanelaColunas(Main jogoPrincipal) {
        super(850, 600, "Monitor de Dados: 7 Montes", 60, true);
        this.jogoPrincipal = jogoPrincipal;
    }

    @Override
    public void create() { }

    @Override
    public void update(double delta) { 
        // Se a barra de espaço for pressionada, a Main altera a flag para false. Fecha-se a janela.
        if ( !jogoPrincipal.isModoDidatico() ) {
           // closeWindow();
        }
    }

    @Override
    public void draw() {
        clearBackground(WHITE);
        
        drawText("Estrutura das Colunas na Memória", 20, 20, 24, BLACK);
        
        List<List<Carta>> colunas = jogoPrincipal.getColunas();
        if (colunas == null) return;

        double cardW = 44; 
        double cardH = 60; 

        // Desenha as 7 colunas lado a lado
        for (int i = 0; i < 7; i++) {
            List<Carta> col = colunas.get(i);
            int startX = 20 + (i * 115);
            int y = 70;
            
            drawText("Lista " + i, startX, y, 16, DARKBLUE);
            y += 20;
            drawText("Itens: " + col.size(), startX, y, 14, GRAY);
            y += 20;

            // Fundo da lista
            drawRectangle(startX - 5, y - 5, cardW + 10, 480, LIGHTGRAY);

            // Desenha as cartas cascateando
            for (Carta c : col) {
                if (c.getImagemFrente() != null) {
                    drawImage(c.getImagemFrente(), new Rectangle(0, 0, 88, 120), new Rectangle(startX, y, cardW, cardH), WHITE);
                    drawRectangle(startX, y, cardW, cardH, BLACK);
                    
                    // Se estiver de costas no jogo, avisamos visualmente na ferramenta
                    if (!c.virada) {
                        drawText("[VERSO]", startX + 2, y + 25, 10, RED);
                    }
                }
                y += 20; 
            }
        }
    }
}