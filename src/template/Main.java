package template;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.core.utils.DrawingUtils;
import br.com.davidbuzatto.jsge.geom.Rectangle;
import br.com.davidbuzatto.jsge.image.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Paciência (Klondike) com modo didático (Painel de Monitoramento unificado).
 */
public class Main extends EngineFrame {

    
    private static final int LARGURA = 800;
    private static final int ALTURA = 650;
    private static final int CW = 88;
    private static final int CH = 120;
    private static final int GAP = 24;
    private static final int MARGEM = 20;
    private static final int TOPO_Y = 20;
    private static final int TAB_Y = 165;

    private enum Origem { NENHUMA, DESCARTE, COLUNA, FUNDACAO }

    
    private Image logo;
    private Image imgVersoGlobal;

    private Pilha<Carta> estoque;
    private Pilha<Carta> descarte;
    
    private List<List<Carta>> colunas;
    private List<List<Carta>> fundacoes;

    private List<Carta> arrastando;
    private Origem origem;
    private int origemIdx;
    private int origemPos;
    private int offX;
    private int offY;
    private int mx;
    private int my;

    private int movimentos;
    private double tempo;
    private boolean venceu;

    private boolean modoDidatico = false;

    public Main() {
        super( LARGURA, ALTURA, "Paciência", 60, true, false, false, false, false, false );
    }

    @Override
    public void create() {
        logo = DrawingUtils.createLogo();
        logo.resize( (int) ( logo.getWidth() * 0.1 ), (int) ( logo.getWidth() * 0.1 ) );
        setWindowIcon( logo );

        imgVersoGlobal = loadImage( "resources/images/verso.png" );
        if ( imgVersoGlobal != null ) {
            imgVersoGlobal.resize( CW, CH );
        }

        novoJogo();
    }

    @Override
    public void update( double delta ) {
        mx = getMouseX();
        my = getMouseY();

       
        if ( isKeyPressed( KEY_SPACE ) ) {
            modoDidatico = !modoDidatico;
            
            
            if ( modoDidatico ) {
                new Thread(() -> new JanelaMonitoramento(this)).start();
            }
        }

        if ( isKeyPressed( KEY_R ) ) { 
            novoJogo(); 
            return; 
        }
        if ( venceu ){
            return;
        }
        tempo += delta;

        if ( isMouseButtonPressed( MOUSE_BUTTON_LEFT ) ) {
            aoPressionar();
        }
        else if ( isMouseButtonReleased( MOUSE_BUTTON_LEFT ) ) {
            aoSoltar();
        }
        if ( isMouseButtonPressed( MOUSE_BUTTON_RIGHT ) ) {
            autoFundacao();
        }
    }

    

    @Override
    public void draw() {
        clearBackground( DARKGREEN );

        // estoque
        if ( !estoque.isEmpty() ) {
            desenharVerso( colX( 0 ), TOPO_Y );
        }
        else {
            desenharSlot( colX( 0 ), TOPO_Y, null );
            drawText( "Virar", colX( 0 ) + 22, TOPO_Y + CH / 2 - 8, 16, LIGHTGRAY );
        }
        drawText( "Estoque: " + estoque.size(), colX( 0 ), TOPO_Y + CH + 4, 14, WHITE );

        // descarte 
        boolean arrastandoDescarte = origem == Origem.DESCARTE && !arrastando.isEmpty();
        if ( !descarte.isEmpty() && !arrastandoDescarte ) {
             if (modoDidatico) {
                 desenharCarta( descarte.peek(), colX( 1 ), TOPO_Y );
             } else {
                 desenharSlot( colX( 1 ), TOPO_Y, null ); // Esconde a carta jogada!
             }
        } else {
             desenharSlot( colX( 1 ), TOPO_Y, null );
        }
        
        drawText( "Descarte: " + descarte.size(), colX( 1 ), TOPO_Y + CH + 4, 14, WHITE );

        // informações
        int s = (int) tempo;
        drawText( "Movs: " + movimentos, colX( 2 ) + 6, TOPO_Y + 10, 14, WHITE );
        drawText( String.format( "Tempo: %02d:%02d", s / 60, s % 60 ), colX( 2 ) + 6, TOPO_Y + 32, 14, WHITE );
        drawText( "R: novo jogo", colX( 2 ) + 6, TOPO_Y + 54, 14, WHITE );
       

        // fundações
        for ( int f = 0; f < 4; f++ ) {
            List<Carta> fund = fundacoes.get( f );
            boolean arrastandoDaqui = origem == Origem.FUNDACAO && origemIdx == f && !arrastando.isEmpty();
            int topo = fund.size() - ( arrastandoDaqui ? 2 : 1 );
            if ( topo >= 0 ) desenharCarta( fund.get( topo ), colX( 3 + f ), TOPO_Y );
            else desenharSlot( colX( 3 + f ), TOPO_Y, "A" );
        }

        // colunas
        for ( int c = 0; c < 7; c++ ) {
            List<Carta> col = colunas.get( c );
            int limite = col.size();
            if ( origem == Origem.COLUNA && origemIdx == c && !arrastando.isEmpty() ) limite = origemPos;
            if ( limite == 0 ) desenharSlot( colX( c ), TAB_Y, col.isEmpty() ? "K" : null );
            for ( int i = 0; i < limite; i++ ) desenharCarta( col.get( i ), colX( c ), cartaY( col, i ) );
        }

        for ( int k = 0; k < arrastando.size(); k++ ) {
            desenharCarta( arrastando.get( k ), mx - offX, my - offY + k * 26 );
        }

        if ( venceu ) {
            String t1 = "Você venceu!";
            String t2 = "Pressione R para jogar novamente";
            Rectangle r1 = measureTextBounds( t1, 40 );
            Rectangle r2 = measureTextBounds( t2, 18 );
            double cx = LARGURA / 2.0;
            double cy = 650 / 2.0;
            fillRectangle( cx - r2.width / 2 - 30, cy - 60, r2.width + 60, 120, BLACK );
            drawText( t1, cx - r1.width / 2, cy - 40, 40, WHITE );
            drawText( t2, cx - r2.width / 2, cy + 20, 18, WHITE );
        }
    }
    
    private void novoJogo() {
        estoque = new Pilha<>();
        descarte = new Pilha<>();
        colunas = new ArrayList<>();
        fundacoes = new ArrayList<>();

        for ( int i = 0; i < 7; i++ ) { 
            colunas.add( new ArrayList<>() ); 
        }
        for ( int i = 0; i < 4; i++ ) { 
            fundacoes.add( new ArrayList<>() ); 
        }

        List<Carta> todas = new ArrayList<>();
        for ( int n = 0; n < 4; n++ ) {
            for ( int v = 1; v <= 13; v++ ) {
                Carta c = new Carta( n, v );
                c.setImagemVerso( imgVersoGlobal );
                
                Image imgFrente = loadImage( c.getCaminhoImagem() );
                if ( imgFrente != null ) {
                    imgFrente.resize( CW, CH );
                    c.setImagemFrente( imgFrente );
                }
                todas.add( c );
            }
        }
        Collections.shuffle( todas );

        Pilha<Carta> baralho = new Pilha<>();
        for ( Carta c : todas ) {
            baralho.push( c );
        }

        for ( int i = 0; i < 7; i++ ) {
            for ( int j = i; j < 7; j++ ) {
                Carta c = baralho.pop();
                c.virada = ( i == j );
                colunas.get( j ).add( c );
            }
        }

        while ( !baralho.isEmpty() ) {
            estoque.push( baralho.pop() );
        }

        arrastando = new ArrayList<>();
        origem = Origem.NENHUMA;
        movimentos = 0;
        tempo = 0;
        venceu = false;
    }

    
    private void comprar() {
        if ( !estoque.isEmpty() ) {
            Carta c = estoque.pop();
            c.virada = true;
            descarte.push( c );
            movimentos++;
        } else if ( !descarte.isEmpty() ) {
            while ( !descarte.isEmpty() ) {
                Carta c = descarte.pop();
                c.virada = false;
                estoque.push( c );
            }
            movimentos++;
        }
    }

    private Carta removerUltimoDescarte() {
        return descarte.pop();
    }

    
    private int colX( int i ) { 
        return MARGEM + i * ( CW + GAP ); 
    }

    private double[] offsets( List<Carta> col ) {
        double abaixo = 12;
        double acima = 26;
        int nd = 0, nu = 0;
        for ( Carta c : col ) {
            if ( c.virada ) nu++;
            else nd++;
        }
        double disponivel = 650 - TAB_Y - CH - 15;
        double total = nd * abaixo + Math.max( 0, nu - 1 ) * acima;
        if ( total > disponivel && nu > 1 ) {
            acima = Math.max( 10, ( disponivel - nd * abaixo ) / ( nu - 1 ) );
        }
        return new double[] { abaixo, acima };
    }

    private double cartaY( List<Carta> col, int idx ) {
        double[] o = offsets( col );
        double y = TAB_Y;
        for ( int i = 0; i < idx; i++ ) {
            y += col.get( i ).virada ? o[1] : o[0];
        }
        return y;
    }

    private boolean dentro( double px, double py, double x, double y, double w, double h ) {
        return px >= x && px <= x + w && py >= y && py <= y + h;
    }

    private boolean podeFundacao( Carta c, int f ) {
        List<Carta> fund = fundacoes.get( f );
        if ( fund.isEmpty() ) return c.valor == 1;
        Carta topo = fund.get( fund.size() - 1 );
        return topo.naipe == c.naipe && c.valor == topo.valor + 1;
    }

    private boolean podeColuna( Carta c, int idx ) {
        List<Carta> col = colunas.get( idx );
        if ( col.isEmpty() ) return c.valor == 13;
        Carta topo = col.get( col.size() - 1 );
        return topo.virada && topo.vermelha() != c.vermelha() && topo.valor == c.valor + 1;
    }

    private void verificarVitoria() {
        for ( List<Carta> f : fundacoes ) {
            if ( f.size() < 13 ) return;
        }
        venceu = true;
    }

    private void removerOrigem() {
        switch ( origem ) {
            case DESCARTE: 
                removerUltimoDescarte(); 
                break;
            case COLUNA: 
                List<Carta> col = colunas.get( origemIdx );
                col.subList( origemPos, col.size() ).clear();
                if ( !col.isEmpty() ) col.get( col.size() - 1 ).virada = true;
                break;
            case FUNDACAO: 
                List<Carta> f = fundacoes.get( origemIdx );
                f.remove( f.size() - 1 );
                break;
            default: break;
        }
    }
    
    private void aoPressionar() {
        if ( dentro( mx, my, colX( 0 ), TOPO_Y, CW, CH ) ) {
            comprar();
            return;
        }
        if ( !descarte.isEmpty() && dentro( mx, my, colX( 1 ), TOPO_Y, CW, CH ) ) {
            arrastando.clear();
            arrastando.add( descarte.peek() );
            origem = Origem.DESCARTE;
            offX = mx - colX( 1 );
            offY = my - TOPO_Y;
            return;
        }
        for ( int f = 0; f < 4; f++ ) {
            List<Carta> fund = fundacoes.get( f );
            if ( !fund.isEmpty() && dentro( mx, my, colX( 3 + f ), TOPO_Y, CW, CH ) ) {
                arrastando.clear();
                arrastando.add( fund.get( fund.size() - 1 ) );
                origem = Origem.FUNDACAO;
                origemIdx = f;
                offX = mx - colX( 3 + f );
                offY = my - TOPO_Y;
                return;
            }
        }
        for ( int c = 0; c < 7; c++ ) {
            List<Carta> col = colunas.get( c );
            for ( int i = col.size() - 1; i >= 0; i-- ) {
                double cy = cartaY( col, i );
                if ( dentro( mx, my, colX( c ), cy, CW, CH ) ) {
                    if ( col.get( i ).virada ) {
                        arrastando.clear();
                        arrastando.addAll( col.subList( i, col.size() ) );
                        origem = Origem.COLUNA;
                        origemIdx = c;
                        origemPos = i;
                        offX = mx - colX( c );
                        offY = (int) ( my - cy );
                    }
                    return;
                }
            }
        }
    }

    private void aoSoltar() {
        if ( arrastando.isEmpty() ) return;
        boolean ok = false;
        Carta primeira = arrastando.get( 0 );

        if ( arrastando.size() == 1 && my < TAB_Y - 10 ) {
            for ( int f = 0; f < 4; f++ ) {
                if ( mx >= colX( 3 + f ) - GAP / 2 && mx <= colX( 3 + f ) + CW + GAP / 2 && podeFundacao( primeira, f ) ) {
                    removerOrigem();
                    fundacoes.get( f ).add( primeira );
                    ok = true;
                    break;
                }
            }
        }
        if ( !ok && my >= TAB_Y - 10 ) {
            for ( int c = 0; c < 7; c++ ) {
                if ( mx >= colX( c ) - GAP / 2 && mx <= colX( c ) + CW + GAP / 2 ) {
                    if ( !( origem == Origem.COLUNA && origemIdx == c ) && podeColuna( primeira, c ) ) {
                        removerOrigem();
                        colunas.get( c ).addAll( arrastando );
                        ok = true;
                    }
                    break;
                }
            }
        }
        if ( ok ) {
            movimentos++;
            verificarVitoria();
        }
        arrastando.clear();
        origem = Origem.NENHUMA;
    }

    private void autoFundacao() {
        Carta carta = null;
        Origem o = Origem.NENHUMA;
        int idx = -1;

        if ( !descarte.isEmpty() && dentro( mx, my, colX( 1 ), TOPO_Y, CW, CH ) ) {
            carta = descarte.peek();
            o = Origem.DESCARTE;
        } else {
            for ( int c = 0; c < 7; c++ ) {
                List<Carta> col = colunas.get( c );
                if ( !col.isEmpty() && dentro( mx, my, colX( c ), cartaY( col, col.size() - 1 ), CW, CH ) ) {
                    carta = col.get( col.size() - 1 );
                    o = Origem.COLUNA;
                    idx = c;
                    break;
                }
            }
        }
        if ( carta == null || !carta.virada ) {
            return;
        }

        for ( int f = 0; f < 4; f++ ) {
            if ( podeFundacao( carta, f ) ) {
                origem = o;
                origemIdx = idx;
                if ( o == Origem.COLUNA ) origemPos = colunas.get( idx ).size() - 1;
                removerOrigem();
                origem = Origem.NENHUMA;
                fundacoes.get( f ).add( carta );
                movimentos++;
                verificarVitoria();
                return;
            }
        }
    }
    
    // desenho 
    private void desenharVerso( double x, double y ) {
        if ( imgVersoGlobal != null ) drawImage( imgVersoGlobal, x, y );
        else {
            fillRectangle( x, y, CW, CH, WHITE );
            fillRectangle( x + 5, y + 5, CW - 10, CH - 10, BLUE );
        }
    }

    private void desenharCarta( Carta c, double x, double y ) {
        if ( !c.virada ) {
            if ( c.getImagemVerso() != null ) drawImage( c.getImagemVerso(), x, y );
            else desenharVerso( x, y );
            return;
        }
        if ( c.getImagemFrente() != null ) {
            drawImage( c.getImagemFrente(), x, y );
        }
    }

    private void desenharSlot( double x, double y, String rotulo ) {
        drawRectangle( x, y, CW, CH, LIGHTGRAY );
        if ( rotulo != null ) {
            Rectangle r = measureTextBounds( rotulo, 30 );
            drawText( rotulo, x + CW / 2.0 - r.width / 2, y + CH / 2.0 - r.height / 2, 30, LIGHTGRAY );
        }
    }

    // Getters para a janela de monitoramento ler os dados
    public Pilha<Carta> getEstoque() { return estoque; }
    public Pilha<Carta> getDescarte() { return descarte; }
    public List<List<Carta>> getColunas() { return colunas; }
    public List<List<Carta>> getFundacoes() { return fundacoes; }
    public boolean isModoDidatico() { return modoDidatico; }

    public static void main( String[] args ) {
        new Main();
    }
}