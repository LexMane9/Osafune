package elements;

import com.alexnae.game.OCG;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import game.Parametros;

import java.util.ArrayList;


//Clase Carta. Contiene toda la información que necesita el juego para que las cartas funcionen correctamente.
//Extiende de DragAndDropActor, ya que es un objeto "arrastrable".
public class Carta extends DragAndDropActor {
    //Atributos de una carta
    public String nombre;
    public int poder;
    public int coste;
    public Zona zona;
    public ArrayList<Carta> array;
    boolean targetable;
    boolean aliado, hechizo;
    boolean enabled;
    boolean haAtacado;
    //Booleanos de las habilidades o keywords.
    boolean drenaje, arrasar, imbloqueable;

    //Sonidos que puede hacer una carta
    Sound errorSound, damageSound, invocarSound, fusionSound, barajarSound;

    //Constructor complejo de carta. Lo usamos cuando es necesario pasar muchos parámetros.
    public Carta(float x, float y, Stage s, int poder, boolean aliado, Zona zona, ArrayList<Carta> array) {
        super(x, y, s);
        //Necesitamos crear los sonidos en la carta para poder usarlos
        damageSound = Gdx.audio.newSound(Gdx.files.internal("sound/damage.wav"));
        errorSound = Gdx.audio.newSound(Gdx.files.internal("sound/error.wav"));
        invocarSound = Gdx.audio.newSound(Gdx.files.internal("sound/invocar.wav"));
        barajarSound = Gdx.audio.newSound(Gdx.files.internal("sound/barajar.wav"));
        fusionSound = Gdx.audio.newSound(Gdx.files.internal("sound/fusion.wav"));
        //Keywords en false
        drenaje = false;
        imbloqueable = false;
        arrasar = false;
        this.poder = poder;
        this.zona = zona;
        this.aliado = aliado;
        this.array = array;
        targetable = true;
        enabled = true;

        //Según el poder le asignamos una imagen u otra.
        if (poder == 1) {
            loadTexture("Monstruos/Level1.png");
        }
        if (poder == 2) {
            loadTexture("Monstruos/Level2.png");
        }
        if (poder == 3) {
            loadTexture("Monstruos/Level3.png");
        }
        if (poder == 4) {
            loadTexture("Monstruos/Level4.png");
        }
        if (poder == 5) {
            loadTexture("Monstruos/Level5.png");
        }
        if (poder == 6) {
            loadTexture("Monstruos/Level6.png");
        }
        if (poder == 7) {
            loadTexture("Monstruos/Level7.png");
        }
        if (poder == 8) {
            loadTexture("Monstruos/Level8.png");
        }
        if (poder == 9) {
            loadTexture("Monstruos/Level9.png");
        }
        if (poder >= 10) {
            loadTexture("Monstruos/Level10.png");
        }
        //Traemos la carta al frente y le damos tamaño.
        this.toFront();
        this.setSize(Parametros.anchoCarta, Parametros.altoCarta);
        this.setBoundaryRectangle();

    }

    //Constructor sin parámetros de carta. Sirve para cuando vamos a setear los parámetros después de crear la carta.
    public Carta(float x, float y, Stage s) {
        super(x, y, s);
        this.toFront();
        this.setSize(Parametros.anchoCarta, Parametros.altoCarta);
        this.setBoundaryRectangle();
        //Sonidos
        errorSound = Gdx.audio.newSound(Gdx.files.internal("sound/error.wav"));
        damageSound = Gdx.audio.newSound(Gdx.files.internal("sound/damage.wav"));
        invocarSound = Gdx.audio.newSound(Gdx.files.internal("sound/invocar.wav"));
        fusionSound = Gdx.audio.newSound(Gdx.files.internal("sound/fusion.wav"));
        barajarSound = Gdx.audio.newSound(Gdx.files.internal("sound/barajar.wav"));
        //Keywords en false
        drenaje = false;
        imbloqueable = false;
        arrasar = false;

    }

    //Método fusionar.
    private static void fusionar(Carta carta1, Carta carta2) {
        if (!carta1.isHaAtacado() && carta2 != null) {
            int poderCarta1 = carta1.getPoder();
            int poderCarta2 = carta2.getPoder();
            carta2.setPoder(poderCarta1 + poderCarta2);
            Zona zonaY = carta1.getZona();
            //Limpiamos la zona de la carta 1 al fusionarla.
            if (zonaY != null) {
                zonaY.setOcupado(false);
                zonaY.carta.remove(carta1);
            }
            //Matamos la carta1
            carta1.die();
            //Evolucionamos la carta 2.
            carta2.morph(carta2.getPoder(), carta2);
            //Si la carta fusionada no tiene arrasar, se pone inactiva.
            if (!carta2.arrasar)
                carta2.setHaAtacado(true);
        }
    }

    //Método del combate.
    public static void combate(Carta carta1, Carta carta2) {
        //Comprobamos que la carta no haya atacado
        if (!carta1.isHaAtacado()) {
            //Comprobamos que la carta objetivo esta viva.
            if (carta1.getPoder() > carta2.getPoder() && (carta2.getPoder() > 0)) {
                int poderGanador = carta1.getPoder();
                int dmgPerdedor = carta2.getPoder();
                //Comprobamos si las cartas tienen drenaje
                checkDrenaje(carta1, carta2);
                //Quitamos poder igual al poder de la carta que ha muerto.
                carta1.setPoder(poderGanador - dmgPerdedor);
                //Limpiamos la zona de la carta que ha muerto.
                Zona zonaY = carta2.getZona();
                if (zonaY != null) {
                    zonaY.setOcupado(false);
                    zonaY.carta.remove(carta2);
                }
                //Matamos la carta que pierde
                carta2.die();
                //Actualizamos la carta ganadora.
                carta1.morph(carta1.getPoder(), carta1);
                //Desactivamos la carta si no tiene arrasar.
                if (!carta1.arrasar)
                    carta1.setHaAtacado(true);
            }
            //Lo mismo pero si la carta 2 gana a la carta 1 en poder
            if (carta1.getPoder() < carta2.getPoder() && (carta1.getPoder() > 0)) {
                int poderGanador = carta2.getPoder();
                int dmgPerdedor = carta1.getPoder();
                checkDrenaje(carta1, carta2);
                carta2.setPoder(poderGanador - dmgPerdedor);
                Zona zonaZ = carta1.getZona();
                if (zonaZ != null) {
                    zonaZ.setOcupado(false);
                    zonaZ.carta.remove(carta1);
                }
                carta1.die();
                carta2.morph(carta2.poder, carta2);
            }
            //Si ambas tienen el mismo poder,ambas se destruyen y liberan sus zonas.
            if (carta1.getPoder() == carta2.getPoder() && carta1.getPoder() > 0 && carta2.getPoder() > 0) {
                Zona zona1 = carta1.getZona();
                Zona zona2 = carta2.getZona();
                checkDrenaje(carta1, carta2);
                if (zona1 != null && zona2 != null) {
                    zona1.setOcupado(false);
                    zona2.setOcupado(false);
                }
                if (!carta1.arrasar)
                    carta1.setHaAtacado(true);
                carta1.remove(carta1);
                carta2.remove(carta2);
                carta1.die();
                carta2.die();

            }
        }
    }

    //Método que comprueba drenaje
    private static void checkDrenaje(Carta carta1, Carta carta2) {
        if (carta1.drenaje && carta1.isAliado()) {
            OCG.setVidaActual(OCG.getVidaActual() + carta1.poder);
        }
        if (carta2.drenaje && carta2.isAliado()) {
            OCG.setVidaActual(OCG.getVidaActual() + carta2.poder);
        }
        if (carta1.drenaje && !carta1.isAliado()) {
            OCG.setVidaEnemigo(OCG.getVidaEnemigo() + carta1.poder);
        }
        if (carta2.drenaje && !carta2.isAliado()) {
            OCG.setVidaEnemigo(OCG.getVidaEnemigo() + carta2.poder);
        }
        //Si la vida va a crecer a más de 10, se setea en 10.
        if (OCG.getVidaActual() > 10) {
            OCG.setVidaActual(10);
        }
        if (OCG.getVidaEnemigo() > 10) {
            OCG.setVidaEnemigo(10);
        }
    }


    //Método que elimina o destruye una carta.
    public boolean remove(Carta carta) {
        //Limpia la zona de la carta.
        if (carta.getZona() != null) {
            System.out.println("La carta " + carta + " no tiene zona " + zona);
            carta.getZona().setOcupado(false);
            carta.getZona().setCarta(null);
        }
        //Matamos la carta de todas las formas posibles.
        carta.setPoder(0);
        carta.setEnabled(false);
        carta.die();
        return true;
    }

    //Método onDrop. Es lo que hace la carta al entrar en contacto con otro actor.
    @Override
    public void onDrop() {
        //Almacenamos de donde venimos y a donde vamos. El camino es largo.
        Zona zona = (Zona) getDropTarget();
        Zona zonaOrigen = this.getZona();
        //Si no es un hechizo, la carta es un monstruo.
        if (!this.hechizo) {
            //Comprobamos que el monstruo no esté en la mano.
            if (!(this.getZona().getName().equals("Mano Jugador"))) {
                if (zona != null && zona.getCarta() != null && hasDropTarget()) {
                    //Si la carta se superpone a un enemigo, estos combaten.
                    if (hasDropTarget() && (zona.isOcupado()) && (zona.getCarta().isTargetable()) && zona.getCarta().getPoder() >= 1 && !zona.getCarta().isAliado()) {
                        combate(this, zona.getCarta());
                        damageSound.play(OCG.getVolumen());
                        OCG.reordenarMano();
                    }
                    //Si la carta es aliada, se fusionan.
                    else if ((zona.getCarta().isTargetable()) && (zona.isOcupado()) && zona.getCarta().getPoder() >= 1 && zona.getCarta().isAliado() && zona.getCarta() != this) {
                        fusionar(this, zona.getCarta());
                        fusionSound.play(OCG.getVolumen());
                        OCG.reordenarMano();
                        //Si no se cumplen las condiciones, devolvemos la carta a la zona de la que venía.
                    } else {
                        moveToStart();
                        errorSound.play(OCG.getVolumen());
                    }
                }
                //Si la carta atacante apunta al retrato enemigo, le atacamos.
                else if (zona != null && zona.getName().equals("CARA")) {
                    //Si no se puede, error.
                    if (!imbloqueable && !OCG.puedoPegar()) {
                        moveToStart();
                        errorSound.play(OCG.getVolumen());
                    }
                    //Si se puede, le hacemos daño
                    else {
                        OCG.setVidaEnemigo(OCG.getVidaEnemigo() - this.poder);
                        this.setDraggable(false);
                        this.setHaAtacado(true);
                        damageSound.play(OCG.getVolumen());
                        OCG.reordenarMano();
                    }
                }
                //Si no se puede devolvemos la carta a su zona de origen.
                else {
                    moveToStart();
                    errorSound.play(OCG.getVolumen());
                }
            }
            //Si la carta es soltada en una zona aliada le asignamos esa zona.
            if (hasDropTarget() && !zona.isOcupado() && zona.getCarta() == null && (this.getZona().getName().equals("Mano Jugador"))) {
                if (zona.isAliado()) {
                    //La limpiamos de la zona de origen.
                    if (zonaOrigen != null) {
                        zonaOrigen.setCarta(null);
                        zonaOrigen.setOcupado(false);
                    }
                    //Movemos la carta a la zona nueva y se la asignamos.
                    moveToActor(zona);
                    zona.setCarta(this);
                    setAliado(true);
                    this.setZona(zona);
                    //Ocupamos la zona
                    zona.ocupado = true;
                    //Quitarla del array de la mano
                    borroDeMano(zonaOrigen);
                    //Desactivamos la carta.
                    setDraggable(false);
                    OCG.reordenarMano();
                } else {
                    //Si hay error
                    moveToStart();
                    errorSound.play(OCG.getVolumen());
                }

            } else {
                //Si hay error
                moveToStart();
                errorSound.play(OCG.getVolumen());
            }
        }
        //Si la carta es un hechizo:
        if (this.hechizo) {
            //Mientras la carta no sea "Cosecha de Sangre"
            if (zona != null && zona.getCarta() == null && zona.isAliado() && hasDropTarget() && !zona.isOcupado()
                    && !this.nombre.equals("Matar")) {
                //Método que utiliza el hechizo.
                usarHechizo();
            } else if (zona != null && zona.getCarta() != null && !zona.isAliado() && hasDropTarget() && zona.isOcupado()) {
                //Si la zona es enemiga y tiene un monstruo y usamos la cosecha de sangre, destruimos ese monstruo.
                if (this.nombre.equals("Matar")) {
                    //Restamos los cristales, destruimos el monstruo y limpiamos la zona. Tambien destruimos el hechizo.
                    if (OCG.getCristalesJ() >= 3) {
                        zona.carta.die();
                        zona.setCarta(null);
                        //Borramos de mano
                        borroDeMano(zonaOrigen);
                        OCG.setCristalesJ(OCG.getCristalesJ() - 3);
                        //Eliminamos el hechizo.
                        this.die();
                        this.zona.setCarta(null);
                        this.zona.setOcupado(false);
                        OCG.reordenarMano();
                    } else {
                        //Error
                        moveToStart();
                        errorSound.play(OCG.getVolumen());
                    }
                } else {
                    //Error
                    moveToStart();
                    errorSound.play(OCG.getVolumen());
                }
            } else {
                //Error
                moveToStart();
                errorSound.play(OCG.getVolumen());
            }
        } else {
            //Error
            moveToStart();
            errorSound.play(OCG.getVolumen());
        }
    }

    //Método que limpia del array mano la carta usada.
    private void borroDeMano(Zona zonaOrigen) {
        if (zonaOrigen.getName().equals("Mano Jugador")) {
            ArrayList<Carta> array = this.getArray();
            array.remove(this);
            OCG.reordenarMano();
        }
    }

    //Método que Se activa al soltar un hechizo en una zona aliada vacía.
    private void usarHechizo() {
        Zona zona = (Zona) getDropTarget();
        Zona zonaOrigen = this.getZona();
        int manaDisponible = OCG.getCristalesJ();
        int manaActual;
        //Switch según la carta usada comprobando su nombre
        switch (nombre) {
            //Se comprueba si se tiene mana suficiente y se utiliza la carta y despues se elimina.
            case "Ritual":
                if (manaDisponible > 0 && zona.isAliado()) {
                    manaActual = manaDisponible - 4;
                    if (manaActual < 0)
                        manaActual = 0;
                    this.zona.setCarta(morph(manaDisponible - manaActual, this));
                    invocarSound.play(OCG.getVolumen());
                    OCG.setCristalesJ(manaActual);
                    System.out.println(manaActual);
                    moveToActor(zona);
                    zona.setCarta(this);
                    this.setZona(zona);
                    zona.setOcupado(true);
                    borroDeMano(zonaOrigen);
                    OCG.reordenarMano();
                } else {
                    //Si no es exitoso hacemos que la carta no se asigne a la zona y vuelva a la mano.
                    zona.setCarta(null);
                    zona.setOcupado(false);
                    moveToStart();
                    errorSound.play(OCG.getVolumen());
                }
                break;

            case "Olla":
                OCG.robarCarta();
                OCG.robarCarta();
                this.die();
                zona.setCarta(null);
                zona.setOcupado(false);
                borroDeMano(zonaOrigen);
                OCG.reordenarMano();
                break;

            case "Pacto":
                if (manaDisponible >= 3 && zona.isAliado()) {
                    manaActual = manaDisponible - 3;
                    if (manaActual < 0) {
                        manaActual = 0;
                        zona.setCarta(null);
                        zona.setOcupado(false);
                        moveToStart();
                        errorSound.play(OCG.getVolumen());
                    }
                    OCG.robarCarta();
                    OCG.robarCarta();
                    zona.setCarta(null);
                    zona.setOcupado(false);
                    this.die();
                    OCG.setCristalesJ(manaActual);
                    borroDeMano(zonaOrigen);
                    OCG.reordenarMano();
                } else {
                    zona.setCarta(null);
                    zona.setOcupado(false);
                    moveToStart();
                    errorSound.play(OCG.getVolumen());
                }
                break;
            case "Sed":
                ArrayList<Zona> zonasAmigas = OCG.getZonasDeMonstruo();
                if (OCG.getCristalesJ() >= 2) {
                    for (Zona zonita : zonasAmigas) {
                        if (zonita.getCarta() != null && zonita.getCarta().isAliado()) {
                            zonita.getCarta().setDrenaje(true);
                            zonita.getCarta().setColor(Color.RED);
                        }
                    }
                    this.die();
                    zona.setCarta(null);
                    zona.setOcupado(false);
                    borroDeMano(zonaOrigen);
                    OCG.setCristalesJ(OCG.getCristalesJ() - 2);
                } else {
                    zona.setCarta(null);
                    zona.setOcupado(false);
                    moveToStart();
                    errorSound.play(OCG.getVolumen());
                }
                break;
            case "Hemo Ritual":
                if (manaDisponible >= 4 && zona.isAliado()) {
                    manaActual = manaDisponible - 4;
                    if (manaActual < 0) {
                        manaActual = 0;
                    }
                    this.zona.setCarta(morph(3, this));
                    invocarSound.play(OCG.getVolumen());
                    moveToActor(zona);
                    zona.setCarta(this);
                    this.setZona(zona);
                    zona.setOcupado(true);
                    this.setDrenaje(true);
                    this.setColor(Color.RED);
                    OCG.setCristalesJ(OCG.getCristalesJ() - 4);
                    borroDeMano(zonaOrigen);
                    OCG.reordenarMano();
                } else {
                    zona.setCarta(null);
                    zona.setOcupado(false);
                    moveToStart();
                    errorSound.play(OCG.getVolumen());
                }
                break;

            case "Picaro Ritual":
                if (manaDisponible >= 2 && zona.isAliado()) {
                    manaActual = manaDisponible - 2;
                    if (manaActual < 0) {
                        manaActual = 0;
                    }
                    this.zona.setCarta(morph(2, this));
                    invocarSound.play(OCG.getVolumen());
                    moveToActor(zona);
                    zona.setCarta(this);
                    this.setZona(zona);
                    zona.setOcupado(true);
                    this.setImbloqueable(true);
                    this.setColor(Color.LIGHT_GRAY);
                    OCG.setCristalesJ(OCG.getCristalesJ() - 2);
                    borroDeMano(zonaOrigen);
                    OCG.reordenarMano();
                } else {
                    zona.setCarta(null);
                    zona.setOcupado(false);
                    moveToStart();
                    errorSound.play(OCG.getVolumen());
                }
                break;

            case "Humildad":
                ArrayList<Zona> zonas = OCG.getZonasDeMonstruo();
                ArrayList<Zona> zonasenemigas = OCG.getZonasMonstruoEnemigas();
                if (OCG.getCristalesJ() >= 5) {
                    for (Zona zonita : zonas) {
                        if (zonita.getCarta() != null && zonita.getCarta().isAliado()) {
                            zonita.getCarta().die();
                            zonita.setCarta(null);
                            zonita.setOcupado(false);
                        }
                    }
                    for (Zona ze : zonasenemigas) {
                        if (ze.getCarta() != null && !ze.getCarta().isAliado()) {
                            ze.getCarta().die();
                            ze.setCarta(null);
                            ze.setOcupado(false);
                        }
                    }
                    this.die();
                    zona.setCarta(null);
                    zona.setOcupado(false);
                    borroDeMano(zonaOrigen);
                    OCG.setCristalesJ(OCG.getCristalesJ() - 2);
                    OCG.reordenarMano();
                } else {
                    zona.setCarta(null);
                    zona.setOcupado(false);
                    moveToStart();
                    errorSound.play(OCG.getVolumen());
                }
                break;

            case "Oro":
                if (manaDisponible >= 2) {
                    manaActual = manaDisponible - 2;
                    if (manaActual < 0) {
                        manaActual = 0;
                    }
                    OCG.setCristalesJ(OCG.getCristalesJ() - 2);
                    for (int i = 0; i < OCG.getCristalesOp(); i++) {
                        OCG.robarCarta();
                    }
                    this.die();
                    borroDeMano(zonaOrigen);
                    OCG.reordenarMano();
                } else {
                    zona.setCarta(null);
                    zona.setOcupado(false);
                    moveToStart();
                    errorSound.play(OCG.getVolumen());
                }
                break;
            case "Semilla":

                if (manaDisponible >= 1 && zona.isAliado()) {
                    manaActual = manaDisponible - 1;
                    if (manaActual < 0) {
                        manaActual = 0;
                    }
                    this.zona.setCarta(morph(1, this));
                    invocarSound.play(OCG.getVolumen());
                    moveToActor(zona);
                    zona.setCarta(this);
                    this.setZona(zona);
                    zona.setOcupado(true);
                    OCG.setCristalesJ(OCG.getCristalesJ() - 1);
                    borroDeMano(zonaOrigen);
                    OCG.robarCarta();
                    OCG.reordenarMano();
                } else {
                    zona.setCarta(null);
                    zona.setOcupado(false);
                    moveToStart();
                    errorSound.play(OCG.getVolumen());
                }
                break;

            case "Invo Ritual":

                if (manaDisponible >= 3 && zona.isAliado()) {
                    manaActual = manaDisponible - 3;
                    if (manaActual < 0) {
                        manaActual = 0;
                    }
                    this.zona.setCarta(morph(3, this));
                    invocarSound.play(OCG.getVolumen());
                    moveToActor(zona);
                    zona.setCarta(this);
                    this.setZona(zona);
                    zona.setOcupado(true);
                    this.setArrasar(true);
                    this.setHaAtacado(false);
                    this.setDraggable(true);
                    this.setColor(Color.LIME);
                    OCG.setCristalesJ(OCG.getCristalesJ() - 3);
                    borroDeMano(zonaOrigen);
                    OCG.reordenarMano();
                } else {
                    zona.setCarta(null);
                    zona.setOcupado(false);
                    moveToStart();
                    errorSound.play(OCG.getVolumen());
                }
                break;

            case "Furia":
                zonasAmigas = OCG.getZonasDeMonstruo();
                if (OCG.getCristalesJ() >= 3) {
                    for (Zona zonita : zonasAmigas) {
                        if (zonita.getCarta() != null && zonita.getCarta().isAliado()) {
                            //La carta también aumentaba el poder en 1 pero estaba demasiado fuerte.
                            //zonita.setCarta(zonita.getCarta().morph(zonita.getCarta().getPoder()+1,zonita.carta));
                            zonita.getCarta().setArrasar(true);
                            zonita.getCarta().setHaAtacado(false);
                            zonita.getCarta().setDraggable(true);
                            zonita.getCarta().setColor(Color.LIME);
                        }
                    }
                    this.die();
                    zona.setCarta(null);
                    zona.setOcupado(false);
                    borroDeMano(zonaOrigen);
                    OCG.setCristalesJ(OCG.getCristalesJ() - 3);
                } else {
                    zona.setCarta(null);
                    zona.setOcupado(false);
                    moveToStart();
                    errorSound.play(OCG.getVolumen());
                }
                break;

            case "Ira Invocador":
                zonasenemigas = OCG.getZonasMonstruoEnemigas();
                if (OCG.getCristalesJ() >= 3) {
                    for (Zona ze : zonasenemigas) {
                        if (ze.getCarta() != null && !ze.getCarta().isAliado()) {
                            ze.setCarta(ze.getCarta().morph(ze.getCarta().poder - 3, ze.carta));
                        }
                    }
                    this.die();
                    zona.setCarta(null);
                    zona.setOcupado(false);
                    borroDeMano(zonaOrigen);
                    OCG.setCristalesJ(OCG.getCristalesJ() - 3);
                    OCG.reordenarMano();
                } else {
                    zona.setCarta(null);
                    zona.setOcupado(false);
                    moveToStart();
                    errorSound.play(OCG.getVolumen());
                }
                break;
        }
    }

    //Método que cambia la forma de la carta al dañarse o al fusionarse.
    public Carta morph(int poder, Carta carta) {
        //Limpiamos la imagen
        clearAnimation();
        //Reasignamos el poder al poder pasado por parámetro.
        this.setPoder(poder);
        //Si la carta tiene 0 o menos de poder muere.
        if (poder <= 0) {
            carta.remove();
        }
        if (poder == 1) {
            carta.loadTexture("Monstruos/Level1.png");
        }
        if (poder == 2) {
            carta.loadTexture("Monstruos/Level2.png");
        }
        if (poder == 3) {
            carta.loadTexture("Monstruos/Level3.png");
        }
        if (poder == 4) {
            carta.loadTexture("Monstruos/Level4.png");
        }
        if (poder == 5) {
            carta.loadTexture("Monstruos/Level5.png");
        }
        if (poder == 6) {
            carta.loadTexture("Monstruos/Level6.png");
        }
        if (poder == 7) {
            carta.loadTexture("Monstruos/Level7.png");
        }
        if (poder == 8) {
            carta.loadTexture("Monstruos/Level8.png");
        }
        if (poder == 9) {
            carta.loadTexture("Monstruos/Level9.png");
        }
        //Invocar al Lich.
        if (poder >= 10) {
            carta.loadTexture("Monstruos/Level10.png");
            //El Lich gana las 3 keywords y se pone poder 999.
            carta.arrasar = true;
            carta.drenaje = true;
            carta.imbloqueable = true;
            carta.poder = 999;
            fusionSound.play(OCG.getVolumen());
        }
        //Traemos al frente, damos tamaño y asignamos valores a los booleanos.
        this.toFront();
        this.setSize(Parametros.anchoCarta, Parametros.altoCarta);
        this.setBoundaryRectangle();
        carta.setHaAtacado(true);
        carta.setDraggable(false);
        //Si la carta de la que proviene el monstruo era un hechizo, deja de ser un hechizo.
        carta.setHechizo(false);
        carta.setEnabled(true);
        carta.setTargetable(true);
        //Si tiene arrasar, se pone activa.
        if (arrasar) {
            carta.haAtacado = false;
            carta.setDraggable(true);
        }
        //Retornamos la carta fusionada.
        return carta;
    }

    //Carta muere.
    private void die() {
        this.remove();
    }

    //Métodos heredados.
    @Override
    public DropTargetActor getDropTarget() {
        return super.getDropTarget();
    }

    public boolean isTargetable() {
        return targetable;
    }

    public void setTargetable(boolean t) {
        targetable = t;
    }

    @Override
    public void moveToActor(BaseActor other) {
        super.moveToActor(other);
    }

    @Override
    public void act(float delta) {
        // TODO Auto-generated method stub
        if (this.getEnabled()) {
            super.act(delta);
        }
    }

    //Getters setters y comprobadores de booleanos.
    public boolean isHechizo() {
        return hechizo;
    }

    public void setHechizo(boolean hechizo) {
        this.hechizo = hechizo;
    }

    public ArrayList<Carta> getArray() {
        return array;
    }

    public void setArray(ArrayList<Carta> array) {
        this.array = array;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPoder() {
        return poder;
    }

    public void setPoder(int poder) {
        this.poder = poder;
    }

    public boolean isAliado() {
        return aliado;
    }

    public void setAliado(boolean aliado) {
        this.aliado = aliado;
    }

    public void setCoste(int coste) {
        this.coste = coste;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public void setDrenaje(boolean drenaje) {
        this.drenaje = drenaje;
    }

    public void setArrasar(boolean arrasar) {
        this.arrasar = arrasar;
    }

    public void setImbloqueable(boolean imbloqueable) {
        this.imbloqueable = imbloqueable;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isHaAtacado() {
        return haAtacado;
    }

    public void setHaAtacado(boolean haAtacado) {
        this.haAtacado = haAtacado;
    }
}
