package br.grupointegrado.SpaceInvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FillViewport;

/**
 * Created by Rafael on 14/09/2015.
 */
public class TelaMenu extends TelaBase {

    private OrthographicCamera camera;
    private Stage palco;
    private ImageTextButton btnIniciar;
    private Label lbTitulo;
    private Label lnPontuacao;

    private BitmapFont fonteTitulo;
    private BitmapFont fonteBotoes;

    private Texture texturaBotao; //textura normal do botao
    private Texture texturaBotaoPressionado; //textura do botao pressionado

    public TelaMenu(MainGame game) {
        super(game);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getHeight(), Gdx.graphics.getWidth());
        palco = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));

        Gdx.input.setInputProcessor(palco); //define o palco como processador de entradas;


        initFontes();
        initLabels();
    }

    private void atualizarLabels() {

        float x = camera.viewportWidth/ 2 - lbTitulo.getPrefWidth() /2;
        float y = camera.viewportHeight - 100;

        lbTitulo.setPosition(x,y);


    }

    private void initLabels() {
        Label.LabelStyle estilo = new  Label.LabelStyle();
        estilo.font = fonteTitulo;

        lbTitulo = new Label("Space Invaders", estilo);
        palco.addActor(lbTitulo);

    }

    private void initFontes() {
        FreeTypeFontGenerator gerador = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 48;
        params.color = new Color(.25f,.25f,.85f, 1); //tom de azul
        params.shadowOffsetX =2;
        params.shadowOffsetY =2;
        params.shadowColor = Color.BLACK;

        fonteTitulo = gerador.generateFont(params);


    }

    @Override
    public void render(float delta) {
        //Limpa tela
        Gdx.gl.glClearColor(1f, 1f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        atualizarLabels();

        palco.act(delta);
        palco.draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false,width,height);
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        palco.dispose();
        fonteTitulo.dispose();
    }
}
