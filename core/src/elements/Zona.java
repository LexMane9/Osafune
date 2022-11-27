package elements;

import com.badlogic.gdx.scenes.scene2d.Stage;

//Clase Zona utilizada para crear todo lo que no son cartas en el juego.
public class Zona extends DropTargetActor {
    //Tienen una carta, pueden estar ocupadas o no y ser aliadas o no.
    public Carta carta;
    boolean ocupado;
    boolean aliado;

    //Constructor de la zona.
    public Zona(float x, float y, Stage s, float w, float h) {
        super(x, y, s, w, h);
    }

    //Métodos Heredados
    @Override
    public boolean isTargetable() {
        // TODO Auto-generated method stub
        return super.isTargetable();
    }

    @Override
    public void setTargetable(boolean t) {
        // TODO Auto-generated method stub
        super.setTargetable(t);
    }

    @Override
    public boolean isTouchable() {
        // TODO Auto-generated method stub
        return super.isTouchable();
    }

    //Getters y setters.
    public boolean isOcupado() {
        return ocupado;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public boolean isAliado() {
        return aliado;
    }

    public void setAliado(boolean aliado) {
        this.aliado = aliado;
    }

    public Carta getCarta() {
        return carta;
    }

    public void setCarta(Carta carta) {
        this.carta = carta;
    }
}
