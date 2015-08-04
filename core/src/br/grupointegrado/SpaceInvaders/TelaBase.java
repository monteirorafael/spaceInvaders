package br.grupointegrado.SpaceInvaders;

import com.badlogic.gdx.Screen;

/**
 * Created by Rafael on 03/08/2015.
 */
public abstract class TelaBase implements Screen{

    protected MainGame game;

    public TelaBase(MainGame game){
        this.game = game;
    }

    @Override
    public void hide() {
        dispose();
    }
}
