package template;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import java.util.List;

/**
 * Painel Único de Monitoramento das Estruturas de Dados.
 * Largura ajustada para 1080px (para caber ao lado do jogo de 800px num monitor 1920px).
 */
public class JanelaMonitoramento extends EngineFrame {

    private Main jogoPrincipal;

    public JanelaMonitoramento(Main jogoPrincipal) {
        // Reduzido de 1200 para 1080 pixels de largura
        super(1080, 850, "Painel de Controle", 60, true);
        this.jogoPrincipal = jogoPrincipal;
    }

    @Override
    public void create() { }

    @Override
    public void update(double delta) { 
        // Se apertar espaço na Main, ela fecha essa janela automaticamente
        if ( !jogoPrincipal.isModoDidatico() ) {
            //closeWindow();
        }
    }

    @Override
    public void draw() {
        clearBackground(WHITE);
        
        drawText("ESTRUTURAS DE DADOS", 20, 20, 28, DARKGREEN);
        drawLine(20, 60, 1060, 60, BLACK); // Linha ajustada para nova largura

        // Usamos try/catch para evitar conflito se a lista mudar exato no milissegundo da renderização
        try {
            desenharPilhasEstoqueDescarte();
            desenharFundacoes();
            desenharColunas();
        } catch (Exception e) {
            // Ignora frame p/ não crashar
        }
    }

    // --- 1. SETOR: ESTOQUE E DESCARTE (Canto Superior Esquerdo) ---
    private void desenharPilhasEstoqueDescarte() {
        int startY = 80;
        
        drawText("1. PILHAS HORIZONTAIS", 20, startY, 20, DARKBLUE);
        
        //drawText("Estoque", 20, startY + 30, 16, BLACK);
        renderPilhaHorizontal(jogoPrincipal.getEstoque(), 20, startY + 50);
        
        //drawText("Descarte", 20, startY + 120, 16, BLACK);
        renderPilhaHorizontal(jogoPrincipal.getDescarte(), 20, startY + 140);
    }

    private void renderPilhaHorizontal(Pilha<Carta> pilha, double x, double y) {
        // Reduzi a caixa base de 600 para 580 para dar respiro
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

    // --- 2. SETOR: FUNDAÇÕES/ORGANIZADORES (Canto Superior Direito) ---
    private void desenharFundacoes() {
        // Puxado mais para a esquerda (de 680 para 630) para caber na tela
        int startX = 630;
        int startY = 80;
        
        drawText("2. PILHAS VERTICAIS", startX, startY, 20, DARKBLUE);
        
        List<List<Carta>> fundacoes = jogoPrincipal.getFundacoes();
        if (fundacoes == null) return;

        double cardW = 66, cardH = 90;

        for (int i = 0; i < 4; i++) {
            List<Carta> fund = fundacoes.get(i);
            // Reduzido o espaçamento entre elas de 125 para 105
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
            if (!fund.isEmpty()) drawText("<- TOPO", x + cardW + 10, cy + 50, 12, RED);
        }
    }

    // --- 3. SETOR: COLUNAS/MONTES (Parte Inferior da Janela) ---
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
            // Ajustado de 160 para 145 para que a última coluna não corte no canto direito
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