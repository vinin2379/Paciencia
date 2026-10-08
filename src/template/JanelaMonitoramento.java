package template;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import java.util.List;

/**
 * Painel Único de Monitoramento das Estruturas de Dados.
 * 
 */
public class JanelaMonitoramento extends EngineFrame {

    private Main jogoPrincipal;

    public JanelaMonitoramento(Main jogoPrincipal) {
        
        super(1080, 850, "Painel de Controle", 60, true);
        this.jogoPrincipal = jogoPrincipal;
    }

    @Override
    public void create() { }

    @Override
    public void update(double delta) { 
        
        if ( !jogoPrincipal.isModoDidatico() ) {
            
        }
    }

    @Override
    public void draw() {
        clearBackground(WHITE);
        
        drawText("ESTRUTURAS DE DADOS", 20, 20, 28, DARKGREEN);
        drawLine(20, 60, 1060, 60, BLACK); 

       
        try {
            desenharPilhasEstoqueDescarte();
            desenharFundacoes();
            desenharColunas();
        } catch (Exception e) {
            
        }
    }

    
    private void desenharPilhasEstoqueDescarte() {
        int startY = 80;
        
        drawText("1. PILHAS HORIZONTAIS", 20, startY, 20, DARKBLUE);
        
        
        renderPilhaHorizontal(jogoPrincipal.getEstoque(), 20, startY + 50);
        
        
        renderPilhaHorizontal(jogoPrincipal.getDescarte(), 20, startY + 140);
    }

    private void renderPilhaHorizontal(Pilha<Carta> pilha, double x, double y) {
        
        drawRectangle(x, y, 580, 55, BLACK);
        
        List<Carta> itens = pilha.getElementosParaVisualizacao();
        if (itens.isEmpty()) {
            drawText("[ Pilha Vazia ]", x + 210, y + 23, 16, GRAY);
            return;
        }

        drawText("BASE", x + 5, y - 15, 12, GRAY); 
        double minCW = 35, minCH = 48, esp = 22; 

        for (int i = 0; i < itens.size(); i++) {
            Carta c = itens.get(i);
            double cx = x + 10 + (i * esp);
            double cy = y + 3;
            
            if (c.getImagemFrente() != null) {
                drawImage(c.getImagemFrente(), new Rectangle(0, 0, 88, 120), new Rectangle(cx, cy, minCW, minCH), WHITE);
                drawRectangle(cx, cy, minCW, minCH, BLACK);
            }
        }
        
        double fimX = x + 10 + ((itens.size() - 1) * esp) + minCW;
        drawText("<- TOPO", fimX + 5, y - 15, 14, RED);
    }

    
    private void desenharFundacoes() {
        
        int startX = 630;
        int startY = 80;
        
        drawText("2. PILHAS VERTICAIS", startX, startY, 20, DARKBLUE);
        
        List<List<Carta>> fundacoes = jogoPrincipal.getFundacoes();
        if (fundacoes == null) return;

        double cardW = 66, cardH = 90;

        for (int i = 0; i < 4; i++) {
            List<Carta> fund = fundacoes.get(i);
            
            int x = startX + (i * 105);
            
            drawText("Org. " + (i+1), x + 15, startY + 30, 16, BLACK);
            drawRectangle(x, startY + 50, cardW + 10, 240, LIGHTGRAY);
            
            int cy = startY + 190; 
            
            for (Carta c : fund) {
                if (c.getImagemFrente() != null) {
                    drawImage(c.getImagemFrente(), new Rectangle(0, 0, 88, 120), new Rectangle(x + 5, cy, cardW, cardH), WHITE);
                    drawRectangle(x + 5, cy, cardW, cardH, BLACK);
                }
                cy -= 15; 
            }
            
        }
    }

    
    private void desenharColunas() {
        int startY = 380;
        drawLine(20, startY - 15, 1060, startY - 15, LIGHTGRAY);
        drawText("3. LISTAS DINÂMICAS", 20, startY, 20, DARKBLUE);
        
        List<List<Carta>> colunas = jogoPrincipal.getColunas();
        if (colunas == null) return;

        double cardW = 55; 
        double cardH = 75; 

        for (int i = 0; i < 7; i++) {
            List<Carta> col = colunas.get(i);
            
            int startX = 30 + (i * 145);
            int y = startY + 30;
            
            drawText("Lista " + i, startX, y, 16, BLACK);
            y += 20;

            drawRectangle(startX - 5, y - 5, cardW + 10, 400, LIGHTGRAY);

            for (Carta c : col) {
                if (c.getImagemFrente() != null) {
                    drawImage(c.getImagemFrente(), new Rectangle(0, 0, 88, 120), new Rectangle(startX, y, cardW, cardH), WHITE);
                    drawRectangle(startX, y, cardW, cardH, BLACK);
                    
                    if (!c.virada) drawText("[VERSO]", startX + 5, y + 30, 10, RED);
                }
                y += 22; 
            }
        }
    }
}