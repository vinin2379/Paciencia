package template;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import java.util.List;

/**
 * Janela secundária para visualizar os 4 organizadores (Fundações).
 */
public class JanelaFundacoes extends EngineFrame {

    private Main jogoPrincipal;

    public JanelaFundacoes(Main jogoPrincipal) {
        super(600, 450, "Monitor de Dados: Organizadores", 60, true);
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
        
        drawText("Estrutura dos Organizadores", 20, 20, 24, BLACK);
        

        List<List<Carta>> fundacoes = jogoPrincipal.getFundacoes();
        if (fundacoes == null) return;

        double cardW = 66; 
        double cardH = 90;

        // Desenha as 4 Pilhas
        for (int i = 0; i < 4; i++) {
            List<Carta> fund = fundacoes.get(i);
            int x = 40 + (i * 130);
            
            drawText("Pilha " + (i+1), x + 10, 90, 16, DARKBLUE);
            
            // Fundo visual (Tubo da pilha)
            drawRectangle(x - 5, 110, cardW + 10, 290, LIGHTGRAY);
            
            int cy = 300; // A base da pilha começa lá embaixo
            
            for (Carta c : fund) {
                if (c.getImagemFrente() != null) {
                    drawImage(c.getImagemFrente(), new Rectangle(0, 0, 88, 120), new Rectangle(x, cy, cardW, cardH), WHITE);
                    drawRectangle(x, cy, cardW, cardH, BLACK);
                }
                cy -= 15; // Sobe
            }
            
            // Seta mostrando o topo
            if (!fund.isEmpty()) {
                drawText("<- TOPO", x + cardW + 5, cy + 50, 12, RED);
            }
        }
    }
}