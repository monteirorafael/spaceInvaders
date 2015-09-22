package br.grupointegrado.SpaceInvaders;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.Vector;


/**
 * Created by Rafael on 03/08/2015.
 */
public class TelaJogo extends TelaBase {

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Stage palco;
    private Stage palcoInformações;
    private BitmapFont fonte;
    private Label lbPontuacao;
    private Label lbGameOver;
    private Image jogador;
    private Texture texturaJogador;
    private Texture texturaJogadorDireita;
    private Texture texturaJogadorEsquerda;
    private boolean indoDireita;
    private boolean indoEsquerda;
    private Boolean atirando;
    private Array<Image> tiros = new Array<Image>();
    private Texture texturaTiro;
    private Texture texturaMeteoro1;
    private Texture texturaMeteoro2;
    private Array<Image> meteoros1 = new Array<Image>();
    private Array<Image> meteoros2 = new Array<Image>();


    private Array<Texture> texturasExplosao = new Array<Texture>();
    private Array<Explosao> explosoes = new Array<Explosao>();

    private Sound somTiro;
    private Sound somExplosao;
    private Sound somGameOver;
    private Music musicaFundo;
    /**
     * Construtor padrão da tela de jogo
     * @param game Referência para a classe principal
     */
    public TelaJogo(MainGame game) {
        super(game);
    }

    /**
     * Chamado quando a tela é exibida
     */
    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        palco = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));
        palcoInformações = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));

        initSons();
        initTexturas();
        initFonte();
        initInformacoes();
        initJogador();
    }

    private void initSons() {
        somTiro = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.mp3"));
        somExplosao = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.mp3"));
        somGameOver = Gdx.audio.newSound(Gdx.files.internal("sounds/gameover.mp3"));
        musicaFundo = Gdx.audio.newMusic(Gdx.files.internal("sounds/background.mp3"));
        musicaFundo.setLooping(true);


    }

    private void initTexturas() {

        texturaTiro = new Texture("sprites/shot.png");
        texturaMeteoro1 = new Texture("sprites/enemie-1.png");
        texturaMeteoro2 = new Texture("sprites/enemie-2.png");

        for (int i = 1; i<=17 ; i++){
            Texture text = new Texture("sprites/explosion-"+ i +".png");
            texturasExplosao.add(text);
        }
    }

    /**
         * Instancia os objetos do jogador e adiciona no palco.
          */
    private void initJogador() {
        texturaJogador = new Texture("sprites/player.png");
        texturaJogadorDireita = new Texture("sprites/player-right.png");
        texturaJogadorEsquerda = new Texture("sprites/player-left.png");

        jogador = new Image(texturaJogador);
        float x = camera.viewportWidth/2 - jogador.getWidth()/2 ;
        float y = 15;
        jogador.setPosition(x, y);
        palco.addActor(jogador);
    }


    /**
      * Instancia as informações escritas na tela.
      */
    private void initInformacoes() {

        Label.LabelStyle lbEstilo = new Label.LabelStyle();
        lbEstilo.font = fonte;
        lbEstilo.fontColor = Color.WHITE;

        lbPontuacao = new Label("0 pontos", lbEstilo);
        palcoInformações.addActor(lbPontuacao);

        lbGameOver = new Label("Game Over" ,lbEstilo);
        lbGameOver.setVisible(false);
        palco.addActor(lbGameOver);


    }

    /**
     * Chamado a todo quadro de atualização do jogo FPS (Frame ou quadros por segundo)
     * @param delta Tempo entre um quadro e outro em segundos
     */
    @Override
    public void render(float delta) {
        //Limpa tela
        Gdx.gl.glClearColor(.15f, .15f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        lbPontuacao.setPosition(10, camera.viewportHeight -lbPontuacao.getPrefHeight() -20); // X Y   viewportHeight-20 significa que ira seta na posição a 20 pixel abaixo da altura maxima da tela
        lbPontuacao.setText(pontuacao + " Pontos");

        lbGameOver.setPosition(camera.viewportWidth / 2 - lbGameOver.getWidth() / 2, camera.viewportHeight / 2);
        lbGameOver.setVisible(gameOver == true);

        atualizarExplosoes(delta);

        if (gameOver == false){
            if (!musicaFundo.isPlaying())
                musicaFundo.play();
            capturaTeclas();
            atualizarJogador(delta);
            atualizaTiro(delta);
            atualizarMeteoros(delta);
            detectarColisoes(meteoros1,5);
            detectarColisoes(meteoros2,15);
        }else{
            if (musicaFundo.isPlaying())
                musicaFundo.stop();
            reiniciarJogo();
        }




        //Atualiza a situação do palco
        palco.act(delta); // avisa que rodou um novo quadro
        //desenha o palco na tela
        palco.draw();

        //desenha o palco de informações
        palcoInformações.act(delta);
        palcoInformações.draw();
    }

    /**
     * verifica se o usuario pressionou enter para iniciar o jogo
     */
    private void reiniciarJogo() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
            Preferences preferencias = Gdx.app.getPreferences("SpaceInvaders");
            int pontuacaoMaxima = preferencias.getInteger("pontuacao_maxima", 0);
            // verifico se minha nova pontuação é maior que a pontuação maxima
            if (pontuacao > pontuacaoMaxima){
                preferencias.putInteger("pontuacao_maxima", pontuacao);
                preferencias.flush();
            }
            game.setScreen(new TelaMenu(game));
        }

    }

    private void atualizarExplosoes(float delta) {
        for(Explosao explosao : explosoes){
            //verifica se a explosao chegou ao fim
            if(explosao.getEstagio() >= 16){
                //chegou ao fim
                explosoes.removeValue(explosao, true); //remove a explosão do array
                explosao.getAtor().remove();
            }
            //ainda nao chegou ao fim
            else{
                explosao.atualizar(delta);
            }
        }
    }

    private Rectangle recJogador = new Rectangle();
    private Rectangle recTiro = new Rectangle();
    private Rectangle recMeteoro = new Rectangle();
    private int pontuacao = 0;
    private boolean gameOver = false;

    private void detectarColisoes(Array<Image> meteoros, int valePonto) {
        recJogador.set(jogador.getX(),jogador.getY(),jogador.getWidth(),jogador.getHeight());

        for (Image meteoro : meteoros){
            recMeteoro.set(meteoro.getX(),meteoro.getY(),meteoro.getWidth(),meteoro.getHeight());
            //detecta colisoes com os tiros
            for (Image tiro : tiros){
                recTiro.set(tiro.getX(),tiro.getY(),tiro.getWidth(),tiro.getHeight());

                if (recMeteoro.overlaps(recTiro)) {
                    //aqui ocorre uma colisão do tiro com o meteoro1;
                    pontuacao += valePonto;
                    tiro.remove(); //remove do palco;
                    tiros.removeValue(tiro, true); //remove da lista;
                    meteoro.remove(); // remove do palco;
                    meteoros.removeValue(meteoro, true); // remove da lista
                    criarExplosao(meteoro.getX() + meteoro.getWidth() /2,meteoro.getY() + meteoro.getHeight() /2);

                }
            }

            //detecta colisao com o player
            if (recJogador.overlaps(recMeteoro)){
                //ocorre colisao de jogador com meteoro1
                gameOver =true;
                somGameOver.play();


            }
            //

        }

    }

    /**
     * Cria explosao na posicao X e Y
     * @param x
     * @param y
     */
    private void criarExplosao(float x, float y) {
        Image ator = new Image(texturasExplosao.get(0));
        ator.setPosition(x - ator.getWidth() /2, y - ator.getHeight() /2);
        palco.addActor(ator);

        Explosao explosao = new Explosao(ator, texturasExplosao);
        explosoes.add(explosao);

        somExplosao.play();

    }

    private void atualizarMeteoros(float delta) {
        int qtdMeteoros = meteoros1.size + meteoros2.size; // retorna a quantidade de meteoros criados

        if (qtdMeteoros < 10){
            int tipo = MathUtils.random(1, 4); // retorna 1 ou 2 aleatoreamente
            if (tipo == 1) {
                //cria meteoro 1
                Image meteoro = new Image(texturaMeteoro1);
                float x = MathUtils.random(0, camera.viewportWidth - meteoro.getWidth());
                float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
                meteoro.setPosition(x, y);
                meteoros1.add(meteoro);
                palco.addActor(meteoro);
            } else if (tipo == 2 ) {
                //cria meteoro 2
                Image meteoro = new Image(texturaMeteoro2);
                float x = MathUtils.random(0, camera.viewportWidth - meteoro.getWidth());
                float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
                meteoro.setPosition(x, y);
                meteoros2.add(meteoro);
                palco.addActor(meteoro);
            }

    }

        float velocidade1 = 100; // 200 pixels por segundo
        for(Image meteoro : meteoros1){
            //Movimenta o tiro em direção ao topo
            float x = meteoro.getX();
            float y = meteoro.getY() - velocidade1 * delta;
            meteoro.setPosition(x, y); // atualiza a posição do meteoro;
            if(meteoro.getY() + meteoro.getHeight() < 0){
                meteoro.remove();//remove do palco;
                meteoros1.removeValue(meteoro, true);//remove da lista;
                pontuacao -= 30;
            }
        }

        float velocidade2 = 150; // 200 pixels por segundo
        for(Image meteoro : meteoros2){
            //Movimenta o tiro em direção ao topo
            float x = meteoro.getX();
            float y = meteoro.getY() - velocidade2 * delta;
            meteoro.setPosition(x, y); // atualiza a posição do meteoro;
            if(meteoro.getY() + meteoro.getHeight() < 0){
                meteoro.remove();//remove do palco;
                meteoros2.removeValue(meteoro, true);//remove da lista;
                pontuacao -= 60;
            }
        }

    }

    private final float MIN_INTERVALO_TIROS = 0.4f;//minimo de tempo entre os tiros
    private float intervaloTiros = 0;//tempo acumulado entre os tiros


    private void atualizaTiro(float delta) {
        intervaloTiros = intervaloTiros + delta; //acumula o tempo percorrido
        //cria um novo tiro se necessário
        if(atirando){
            //verifica se o tempo minimo foi atingido
            if (intervaloTiros >= MIN_INTERVALO_TIROS) {
                Image tiro = new Image(texturaTiro);
                float x = jogador.getX() + jogador.getImageWidth() / 2 - tiro.getWidth()/2;
                float y = jogador.getY() + jogador.getHeight();
                tiro.setPosition(x, y);
                tiros.add(tiro);
                palco.addActor(tiro);
                intervaloTiros = 0;
                somTiro.play();


            }
        }
        float velocidade = 200; // velocidade de movimentação do tiro
        //percorre todos os tiros existentes
        for(Image tiro : tiros){
            //Movimenta o tiro em direção ao topo
            float x = tiro.getX();
            float y = tiro.getY() + velocidade * delta;
            tiro.setPosition(x, y);
            //remove os tiros que sairam da tela
            if(tiro.getY() > camera.viewportHeight){
                tiros.removeValue(tiro, true);//remove da lista
                tiro.remove();//remove do palco
            }
        }
    }
    /**
     * Instancia os objetos de fonte.
     */
    private void initFonte() {

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.color = Color.WHITE;
        param.size = 24;
        param.shadowOffsetX =2;
        param.shadowOffsetY =2;
        param.shadowColor = Color.BLUE;


        fonte = generator.generateFont(param);
        generator.dispose();

    }


    /**
     * Atualiza a posição do jogador
     * @param delta Tempo entre um quadro e outro ( em segundos)
     */
    private void atualizarJogador(float delta) {
        float velocidade = 200; //velocidade de movimento do jogador
        if (indoDireita){
            if(jogador.getX() < camera.viewportWidth - jogador.getWidth()){
                float x = jogador.getX() + velocidade * delta;
                float y = jogador.getY();
                jogador.setPosition(x,y);

            }
        }
        if (indoEsquerda){
            if(jogador.getX() > 0){
                float x = jogador.getX() - velocidade * delta;
                float y = jogador.getY();
                jogador.setPosition(x,y);
            }
        }


        if (indoDireita){
            //troca imagem direita
            jogador.setDrawable(new SpriteDrawable(new Sprite(texturaJogadorDireita)));
        }else if (indoEsquerda){
            //trocar imagem esquerda
            jogador.setDrawable(new SpriteDrawable(new Sprite(texturaJogadorEsquerda)));
        }else{
            //trocar imagem centro
            jogador.setDrawable(new SpriteDrawable(new Sprite(texturaJogador)));
        }

    }

    /**
     * Verifica se as teclas estão pressionadas
     */
    private void capturaTeclas() {
        indoDireita = false;
        indoEsquerda = false;
        atirando = false;



        if(Gdx.input.isKeyPressed(Input.Keys.LEFT ) || clicouEsquerda()){
            indoEsquerda = true;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT ) || clicouDireita()){
            indoDireita = true;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
                Gdx.app.getType() == Application.ApplicationType.Android){
            atirando = true;
        }

    }

    private boolean clicouDireita() {

        if (Gdx.input.isTouched()){
            Vector3 posicao = new Vector3();
            //captura clique/toque na janela do windows
            posicao.x = Gdx.input.getX();
            posicao.y = Gdx.input.getY();
            //converter para uma coordenada do jogo
            posicao = camera.unproject(posicao);
            float meio = camera.viewportWidth /2;
            if (posicao.x < meio){
                return  true;
            }

        }
        return false;
    }

    private boolean clicouEsquerda() {

        if (Gdx.input.isTouched()){
            Vector3 posicao = new Vector3();
            //captura clique/toque na janela do windows
            posicao.x = Gdx.input.getX();
            posicao.y = Gdx.input.getY();
            //converter para uma coordenada do jogo
            posicao = camera.unproject(posicao);
            float meio = camera.viewportWidth /2;
            if (posicao.x < meio){
                return  true;
            }

        }
        return false;
    }


    /**
     * É chamado sempre que há uma alteração no tamanho da tela
     * @param width novo valor de largura da tela
     * @param height novo valor de altura da tela
     */
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false,width,height);
        camera.update();
    }


    /**
     * É chamado sempre que o jogo for minimizado
     */
    @Override
    public void pause() {

    }


    /**
     * É chamado sempre que o jogo voltar para o primeiro plano
     */
    @Override
    public void resume() {

    }


    /**
     * É chamado quando a tela for destruida
     */
    @Override
    public void dispose() {
        batch.dispose();
        palco.dispose();
        fonte.dispose();
        palcoInformações.dispose();
        texturaJogador.dispose();
        texturaJogadorDireita.dispose();
        texturaJogadorEsquerda.dispose();
        texturaTiro.dispose();
        texturaMeteoro1.dispose();
        texturaMeteoro2.dispose();
        for(Texture text : texturasExplosao){
            text.dispose();
        }
        somGameOver.dispose();
        somExplosao.dispose();
        somTiro.dispose();


    }
}
