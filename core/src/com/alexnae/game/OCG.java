package com.alexnae.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import elements.Carta;
import elements.Zona;
import game.Parametros;

import java.util.ArrayList;
import java.util.Collections;

//Clase principal. Contiene la inteligencia del oponente y todos los elementos del juego.
public class OCG extends Game implements Screen {


    //Declaramos los diferentes parametros del juego
    public static int vidaActual = 10;
    public static int vidaEnemigo = 10;
    public static Zona mazo;
    public static Zona zonaMano;
    static float volumen;
    //Creamos arraylists para almacenar los elementos carta y zona
    static ArrayList<Zona> zonasDeMonstruo;
    static ArrayList<Zona> zonasMonstruoEnemigas;
    static ArrayList<Carta> manoArray;
    static ArrayList<Carta> mazoArray;
    //Seteamos los cristales iniciales, el turno y declaramos las clases.
    static int cristalesJ = 1;
    static int cristalesOp = 0;
    //Declaramos los objetos de sonido
    static Sound barajarSound;
    //Archivo Skin que define el estilo de los botones y el texto. Es un json.
    public Skin skin;
    //Necesitamos un SpriteBatch para poder pintar encima de algo.
    public SpriteBatch batch;
    //Necesitamos un renderer para pintar por pantalla.
    public ShapeRenderer renderer;
    //Creamos stages para representar todas las diferentes pantallas del juego.
    //Main stage es la pantalla principal de la partida.
    public Stage mainStage;
    //UiStage es la pantalla de menu principal, settings la de opciones y gameover la de
    //fin de juego.
    public Stage uiStage, settingsStage, gameOverStage;
    //Creamos las diferentes zonas del juego.
    public Zona campo;
    public Zona retratoEnemigo;
    public Zona retratoJugador;
    public Zona campoOp;
    Array<Zona> zonas;
    ArrayList<Carta> manoOpArray;
    ArrayList<Carta> campoOpArray;
    ArrayList<Carta> campoArray;
    ArrayList<Carta> mazoOpArray;
    ArrayList<Carta> cartas;
    int turno = 1;
    int claseJugador, claseOponente;
    //Establecemos booleanos para controlar el flujo de pantallas
    Boolean menuMomento = true;
    Boolean musicaMomento = false;
    Boolean juegoMomento = false;
    Boolean jugadorActivo = true;
    Boolean gameOver = false;
    //Creamos tablas para alinear los objetos gráficos
    Table fondo, fondoM, root, uiTablaFondo, uiTablaFondo2, uiTablaFondo3;
    //Creamos Strings para mostrar texto acorde a la clase
    String clase1 = "Hemomante";
    String clase2 = "Pícaro";
    String clase3 = "Invocador";
    String claseRandom = "Aleatorio";
    //Creamos 2 imagenes para la pantalla de victoria y derrota
    Image fondoWin;
    Image fondoLose;
    //Creamos botones
    TextButton crearCarta;
    TextButton jugar;
    //Declaramos zonas de monstruos que se crearán mas adelante.
    Zona zona1, zona2, zona3, zona4, zona5;
    Zona zonaE1, zonaE2, zonaE3, zonaE4, zonaE5;
    //Creamos labels para mostrar texto por pantalla
    Label vmJugador, vmEnemigo;
    Label labelCentral;
    Carta ritualDeInvocacion;
    Sound ataqueSound;
    Sound invocarSound;
    Sound errorSound;
    Music musica;
    private Label uiLabel;

    //Método para robar carta. Coge la primera carta del array mazo y la mueve al array mano.
    public static void robarCarta() {
        Carta cartaARobar = mazoArray.get(0);
        //La carta a robar tiene que venir del mazo.
        if (cartaARobar.getZona() == mazo) {
            manoArray.add(cartaARobar);
            cartaARobar.setArray(manoArray);
            cartaARobar.setZona(zonaMano);
            cartaARobar.setEnabled(true);
            cartaARobar.setDraggable(true);
            cartaARobar.setPosition((float) (Parametros.anchoCarta * 1.1 * manoArray.size()), 0);
            cartaARobar.toFront();
            barajarSound.play(volumen);
            //Borra de mazo
            if (mazoArray.size() != 0) {
                mazoArray.remove(0);
                System.out.println("borro de mazo");
                Collections.shuffle(mazoArray);
            }
        } else {
            //Mensaje que muestra las cartas que quedan en cada array
            System.out.println(manoArray.toString() + " " + mazoArray.toString());
        }
        //Método que reordena las cartas de la mano
        reordenarMano();
    }

    //Método que reordena las cartas de la mano para evitar que sean tapadas o queden huecos.
    public static void reordenarMano() {
        int contador = 0;
        for (Carta cartaAOrdenar : manoArray) {
            cartaAOrdenar.setPosition((float) (Parametros.anchoCarta * 1.1 * contador), 0);
            cartaAOrdenar.toFront();
            contador++;
        }
    }

    //Método que comprueba si el enemigo tiene monstruos o no para poder atacar directamente.
    public static boolean puedoPegar() {

        for (Zona zonaEnemiga : zonasMonstruoEnemigas) {
            if (zonaEnemiga.getCarta() != null)
                return false;
        }
        return true;
    }

    //Métodos estáticos para pasar información a la clase carta.
    public static ArrayList<Zona> getZonasDeMonstruo() {
        return zonasDeMonstruo;
    }

    public static ArrayList<Zona> getZonasMonstruoEnemigas() {
        return zonasMonstruoEnemigas;
    }

    public static int getVidaActual() {
        return vidaActual;
    }

    public static void setVidaActual(int vidaActual) {
        OCG.vidaActual = vidaActual;
    }

    public static int getVidaEnemigo() {
        return vidaEnemigo;
    }

    public static void setVidaEnemigo(int vidaEnemigo) {
        OCG.vidaEnemigo = vidaEnemigo;
    }

    public static int getCristalesJ() {
        return cristalesJ;
    }

    public static void setCristalesJ(int cristalesNuevos) {
        cristalesJ = cristalesNuevos;
    }

    public static int getCristalesOp() {
        return cristalesOp;
    }

    public static float getVolumen() {
        return volumen;
    }

    //Metodo create. Se lanza al ejecutar la aplicacion y carga todos los recursos e inicializa las variables a utilizar
    @Override
    public void create() {
        //Variables
        volumen = 0.1f;
        menuMomento = true;
        musicaMomento = false;
        juegoMomento = false;
        //Objetos
        batch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("purpleskin/purple.json"));
        Image fondoA = new Image(new Texture(Gdx.files.internal("fondoA.png")));
        Image fondoMenu = new Image(new Texture(Gdx.files.internal("fondoMenu.png")));
        Image fondoOpciones = new Image(new Texture(Gdx.files.internal("fondoMenu.png")));
        fondoWin = new Image(new Texture("coin.png"));
        fondoLose = new Image(new Texture("derrota.png"));
        renderer = new ShapeRenderer();
        mainStage = new Stage(new ScreenViewport());
        uiStage = new Stage();
        settingsStage = new Stage();
        gameOverStage = new Stage();
        uiTablaFondo3 = new Table();
        uiTablaFondo3.setFillParent(true);
        gameOverStage.addActor(uiTablaFondo3);
        manoArray = new ArrayList<Carta>();
        manoOpArray = new ArrayList<Carta>();
        zonas = new Array<Zona>();
        zonasDeMonstruo = new ArrayList<Zona>();
        zonasMonstruoEnemigas = new ArrayList<Zona>();
        cartas = new ArrayList<Carta>();
        campoOpArray = new ArrayList<Carta>();
        campoArray = new ArrayList<Carta>();
        mazoArray = new ArrayList<Carta>();
        mazoOpArray = new ArrayList<Carta>();

        fondo = new Table();
        fondo.setFillParent(true);
        fondo.add(fondoA);
        mainStage.addActor(fondo);
        uiTablaFondo = new Table();
        uiTablaFondo.setBackground(fondoMenu.getDrawable());
        uiTablaFondo.setFillParent(true);
        uiStage.addActor(uiTablaFondo);
        uiTablaFondo2 = new Table();
        uiTablaFondo2.setBackground(fondoOpciones.getDrawable());
        uiTablaFondo2.setFillParent(true);
        settingsStage.addActor(uiTablaFondo2);
        TextButton masVol = new TextButton("+ Volumen!", skin);
        masVol.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
                return false;
            musica.setVolume(volumen += 0.05f);

            System.out.println("volumen: " + volumen);
            invocarSound.play(volumen);


            return false;
        });
        TextButton menosVol = new TextButton("Volumen -", skin);
        menosVol.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
                return false;
            if (musica.getVolume() > 0.05f) {
                musica.setVolume(volumen -= 0.05f);
                invocarSound.play(volumen);
            } else {
                musica.setVolume(volumen = 0);
            }


            return false;
        });
        masVol.setSize(400, 100);
        menosVol.setSize(400, 100);
        masVol.setPosition(Gdx.graphics.getWidth() / 2 - masVol.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        menosVol.setPosition(Gdx.graphics.getWidth() / 2 - menosVol.getWidth() / 2, Gdx.graphics.getHeight() / 2 - menosVol.getHeight());
        TextButton volver = new TextButton("  Volver  ", skin);
        volver.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
                return false;
            menuMomento = true;
            musicaMomento = false;


            return false;
        });
        volver.setSize(400, 100);
        volver.setPosition(Gdx.graphics.getWidth() / 2 - menosVol.getWidth() / 2, Gdx.graphics.getHeight() / 2 - menosVol.getHeight() * 2);
        settingsStage.addActor(masVol);
        settingsStage.addActor(menosVol);
        settingsStage.addActor(volver);

        //Selectboxes para elegir las clases
        SelectBox<String> selectJugador = new SelectBox<>(skin);
        selectJugador.setItems("Jugador: "+clase1,"Jugador: "+ clase2+"   ","Jugador: "+ clase3,"Jugador: "+ claseRandom);
        selectJugador.setAlignment(Align.center);
        selectJugador.getList().setAlignment(Align.center);
        uiTablaFondo.add(selectJugador);
        SelectBox<String> selectOponente = new SelectBox<>(skin);
        selectOponente.setItems("Oponente: "+clase1, "Oponente: "+clase2+"    ", "Oponente: "+clase3, "Oponente: "+claseRandom);
        selectOponente.setAlignment(Align.center);
        selectOponente.getList().setAlignment(Align.center);
        uiTablaFondo.add(selectOponente);
        //El boton jugar del menú limpia la partida anterior e inicia una nueva
        jugar = new TextButton("  JUGAR  ", skin);
        jugar.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
                return false;

            //Eleccion de clase
            if (selectJugador.getSelected() == clase1)
                claseJugador = 1;
            if (selectJugador.getSelected() == clase2)
                claseJugador = 2;
            if (selectJugador.getSelected() == clase3)
                claseJugador = 3;
            if (selectJugador.getSelected() == claseRandom) {
                int random = (int) Math.floor(1 + Math.random() * 3);
                claseJugador = random;
            }

            if (selectOponente.getSelected() == clase1)
                claseOponente = 1;
            if (selectOponente.getSelected() == clase2)
                claseOponente = 2;
            if (selectOponente.getSelected() == clase3)
                claseOponente = 3;
            if (selectOponente.getSelected() == claseRandom) {
                int random = (int) Math.floor(1 + Math.random() * 3);
                claseOponente = random;
            }

            //Metodo que segun la clase asigna diferentes imagenes, mazos, etc.
            checkClases();

            //Desactiva el menu e inicia la pantalla de la partida
            menuMomento = false;
            musicaMomento = false;
            juegoMomento = true;
            barajarSound.play(volumen);
            //Metodo para debugear
            //uiStage.setDebugAll(true);


            return false;
        });
        jugar.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 100, Align.center);
        uiStage.addActor(jugar);

        //Botón que lleva a la pantalla de opciones
        TextButton opciones = new TextButton("  OPCIONES  ", skin);
        opciones.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
                return false;
            //Activa el menu de opciones y pausa el juego
            musicaMomento = true;
            juegoMomento = false;

            return false;
        });
        uiTablaFondo.row();
        opciones.setPosition(Gdx.graphics.getWidth() / 2 - opciones.getWidth() / 2, Gdx.graphics.getHeight() / 1.8f);
        uiTablaFondo.addActor(opciones);
        TextButton botonSalir = new TextButton("  SALIR  ",skin);
        botonSalir.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
                return false;
            this.dispose();
            Gdx.app.exit();
            return false;
        });
        botonSalir.setPosition(Gdx.graphics.getWidth() / 2 - botonSalir.getWidth() / 2, Gdx.graphics.getHeight() /6f);
        uiStage.addActor(botonSalir);
        root = new Table();
        root.setFillParent(true);
        mainStage.addActor(root);
        skin.get(Label.LabelStyle.class).font.getData().markupEnabled = true;
        root.row();
        labelCentral = new Label("Turno 1 de 10.", skin);
        root.add(labelCentral).right().expandX();

        //Metodos que crean las diversas zonas de juego y elementos graficos restantes.
        crearCampos();
        crearRetratos();
        crearLabelsVM();
        crearZMAliados();
        crearZonasMonstruoOp();

        //Nos aseguramos de que el fondo no tape nada.
        fondo.toBack();


        musica = Gdx.audio.newMusic(Gdx.files.internal("sound/paradise.mp3"));
        musica.setVolume(volumen);
        musica.setLooping(true);
        musica.play();
        invocarSound = Gdx.audio.newSound(Gdx.files.internal("sound/invocar.wav"));
        ataqueSound = Gdx.audio.newSound(Gdx.files.internal("sound/damage.wav"));
        barajarSound = Gdx.audio.newSound(Gdx.files.internal("sound/barajar.wav"));
        errorSound = Gdx.audio.newSound(Gdx.files.internal("sound/error.wav"));

        //Boton de pasar turno
        TextButton pasarTurno = new TextButton(" Pasar Turno ", skin);
        pasarTurno.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
                return false;
            pasarTurno();
            fondo.toBack();


            return false;
        });
        root.add(pasarTurno);
        uiLabel = new Label("", skin);
        uiLabel.setPosition(Gdx.graphics.getWidth() / 20, Gdx.graphics.getHeight() / 20);
        uiLabel.setSize(300f, 300);
        uiLabel.setColor(Color.WHITE);
        uiLabel.setFontScale(2);
        mainStage.addActor(uiLabel);
        fondo.toBack();
    }

    //Metodo que crea todos los objetos carta y los pone visualmente fuera de la pantalla
    private void crearMazos() {

        //El ritual de invocacion tiene su propio metodo para crearse, ver crearRitualInvocacion()
        ritualDeInvocacion = new Carta(99999, 99999, mainStage);
        crearRitualInvocacion(ritualDeInvocacion);
        Carta ritualDeInvocacion2 = new Carta(99999, 99999, mainStage);
        crearRitualInvocacion(ritualDeInvocacion2);
        Carta ritualDeInvocacion3 = new Carta(99999, 99999, mainStage);
        crearRitualInvocacion(ritualDeInvocacion3);
        Carta ritualDeInvocacion4 = new Carta(99999, 99999, mainStage);
        crearRitualInvocacion(ritualDeInvocacion4);
        Carta ritualDeInvocacion5 = new Carta(99999, 99999, mainStage);
        crearRitualInvocacion(ritualDeInvocacion5);
        Carta ritualDeInvocacion6 = new Carta(99999, 99999, mainStage);
        crearRitualInvocacion(ritualDeInvocacion6);
        Carta ritualDeInvocacion7 = new Carta(99999, 99999, mainStage);
        crearRitualInvocacion(ritualDeInvocacion7);
        Carta ritualDeInvocacion8 = new Carta(99999, 99999, mainStage);
        crearRitualInvocacion(ritualDeInvocacion8);
        Carta ritualDeInvocacion9 = new Carta(99999, 99999, mainStage);
        crearRitualInvocacion(ritualDeInvocacion9);
        Carta ritualDeInvocacion10 = new Carta(99999, 99999, mainStage);
        crearRitualInvocacion(ritualDeInvocacion10);

        //El resto de las cartas se crean aqui.
        //Se crean fuera de la pantalla, asignadas al mazo, tanto la zona visual como el array,
        //Y declarando que son aliados. Se ponen activados para poder actuar.
        //Se les setea un nombre que se leerá a la hora de usar el hechizo en el onDrop de Carta.
        //Se les setea un coste, una imagen y se dice si son hechizos y aliados.
        //Usamos el método darTamaño() para ajustar el tamaño de la carta.
        Carta potOfGreed = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        potOfGreed.setEnabled(true);
        potOfGreed.setNombre("Olla");
        potOfGreed.setCoste(0);
        potOfGreed.loadTexture("Spell/pot.png");
        potOfGreed.setHechizo(true);
        potOfGreed.setAliado(true);
        potOfGreed.setTargetable(false);
        darTamaño(potOfGreed);
        Carta matar = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        matar.setEnabled(true);
        matar.setNombre("Matar");
        matar.setCoste(3);
        matar.loadTexture("Spell/matar.png");
        matar.setHechizo(true);
        matar.setAliado(true);
        matar.setTargetable(false);
        darTamaño(matar);
        Carta matar2 = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        matar2.setEnabled(true);
        matar2.setNombre("Matar");
        matar2.setCoste(3);
        matar2.loadTexture("Spell/matar.png");
        matar2.setHechizo(true);
        matar2.setAliado(true);
        matar2.setTargetable(false);
        darTamaño(matar2);
        Carta matar3 = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        matar3.setEnabled(true);
        matar3.setNombre("Matar");
        matar3.setCoste(3);
        matar3.loadTexture("Spell/matar.png");
        matar3.setHechizo(true);
        matar3.setAliado(true);
        matar3.setTargetable(false);
        darTamaño(matar3);
        Carta sed = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        sed.setEnabled(true);
        sed.setNombre("Sed");
        sed.setCoste(3);
        sed.loadTexture("Spell/sed.png");
        sed.setHechizo(true);
        sed.setAliado(true);
        sed.setTargetable(false);
        darTamaño(sed);
        Carta sed2 = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        sed2.setEnabled(true);
        sed2.setNombre("Sed");
        sed2.setCoste(3);
        sed2.loadTexture("Spell/sed.png");
        sed2.setHechizo(true);
        sed2.setAliado(true);
        sed2.setTargetable(false);
        darTamaño(sed2);
        Carta ritualSangre = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        ritualSangre.setEnabled(true);
        ritualSangre.setNombre("Hemo Ritual");
        ritualSangre.setCoste(4);
        ritualSangre.loadTexture("Spell/ritualHemo.PNG");
        ritualSangre.setHechizo(true);
        ritualSangre.setAliado(true);
        ritualSangre.setTargetable(false);
        darTamaño(ritualSangre);
        Carta ritualSangre2 = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        ritualSangre2.setEnabled(true);
        ritualSangre2.setNombre("Hemo Ritual");
        ritualSangre2.setCoste(4);
        ritualSangre2.loadTexture("Spell/ritualHemo.PNG");
        ritualSangre2.setHechizo(true);
        ritualSangre2.setAliado(true);
        ritualSangre2.setTargetable(false);
        darTamaño(ritualSangre2);
        Carta pacto = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        pacto.setEnabled(true);
        pacto.setNombre("Pacto");
        pacto.setCoste(3);
        pacto.loadTexture("Spell/pacto.png");
        pacto.setHechizo(true);
        pacto.setAliado(true);
        pacto.setTargetable(false);
        darTamaño(pacto);
        Carta pacto2 = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        pacto2.setEnabled(true);
        pacto2.setNombre("Pacto");
        pacto2.setCoste(3);
        pacto2.loadTexture("Spell/pacto.png");
        pacto2.setHechizo(true);
        pacto2.setAliado(true);
        pacto2.setTargetable(false);
        darTamaño(pacto2);
        Carta ritualPicaro = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        ritualPicaro.setEnabled(true);
        ritualPicaro.setNombre("Picaro Ritual");
        ritualPicaro.setCoste(2);
        ritualPicaro.loadTexture("Spell/ritualdepicaro.png");
        ritualPicaro.setHechizo(true);
        ritualPicaro.setAliado(true);
        ritualPicaro.setTargetable(false);
        darTamaño(ritualPicaro);
        Carta ritualPicaro2 = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        ritualPicaro2.setEnabled(true);
        ritualPicaro2.setNombre("Picaro Ritual");
        ritualPicaro2.setCoste(2);
        ritualPicaro2.loadTexture("Spell/ritualdepicaro.png");
        ritualPicaro2.setHechizo(true);
        ritualPicaro2.setAliado(true);
        ritualPicaro2.setTargetable(false);
        darTamaño(ritualPicaro2);
        Carta oro = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        oro.setEnabled(true);
        oro.setNombre("Oro");
        oro.setCoste(2);
        oro.loadTexture("Spell/oro.png");
        oro.setHechizo(true);
        oro.setAliado(true);
        oro.setTargetable(false);
        darTamaño(oro);
        Carta oro2 = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        oro2.setEnabled(true);
        oro2.setNombre("Oro");
        oro2.setCoste(2);
        oro2.loadTexture("Spell/oro.png");
        oro2.setHechizo(true);
        oro2.setAliado(true);
        oro2.setTargetable(false);
        darTamaño(oro2);
        Carta humildad = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        humildad.setEnabled(true);
        humildad.setNombre("Humildad");
        humildad.setCoste(5);
        humildad.loadTexture("Spell/ira.png");
        humildad.setHechizo(true);
        humildad.setAliado(true);
        humildad.setTargetable(false);
        darTamaño(humildad);
        Carta humildad2 = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        humildad2.setEnabled(true);
        humildad2.setNombre("Humildad");
        humildad2.setCoste(5);
        humildad2.loadTexture("Spell/ira.png");
        humildad2.setHechizo(true);
        humildad2.setAliado(true);
        humildad2.setTargetable(false);
        darTamaño(humildad2);
        Carta semilla = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        semilla.setEnabled(true);
        semilla.setNombre("Semilla");
        semilla.setCoste(1);
        semilla.loadTexture("Spell/semilla.png");
        semilla.setHechizo(true);
        semilla.setAliado(true);
        semilla.setTargetable(false);
        darTamaño(semilla);
        Carta semilla2 = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        semilla2.setEnabled(true);
        semilla2.setNombre("Semilla");
        semilla2.setCoste(1);
        semilla2.loadTexture("Spell/semilla.png");
        semilla2.setHechizo(true);
        semilla2.setAliado(true);
        semilla2.setTargetable(false);
        darTamaño(semilla2);
        Carta devolve = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        devolve.setEnabled(true);
        devolve.setNombre("Ira Invocador");
        devolve.setCoste(3);
        devolve.loadTexture("Spell/irainvo.png");
        devolve.setHechizo(true);
        devolve.setAliado(true);
        devolve.setTargetable(false);
        darTamaño(devolve);
        Carta devolve2 = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        devolve2.setEnabled(true);
        devolve2.setNombre("Ira Invocador");
        devolve2.setCoste(3);
        devolve2.loadTexture("Spell/irainvo.png");
        devolve2.setHechizo(true);
        devolve2.setAliado(true);
        devolve2.setTargetable(false);
        darTamaño(devolve2);
        Carta ritualInvo = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        ritualInvo.setEnabled(true);
        ritualInvo.setNombre("Invo Ritual");
        ritualInvo.setCoste(3);
        ritualInvo.loadTexture("Spell/ritualinvo.png");
        ritualInvo.setHechizo(true);
        ritualInvo.setAliado(true);
        ritualInvo.setTargetable(false);
        darTamaño(ritualInvo);
        Carta ritualInvo2 = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        ritualInvo2.setEnabled(true);
        ritualInvo2.setNombre("Invo Ritual");
        ritualInvo2.setCoste(3);
        ritualInvo2.loadTexture("Spell/ritualinvo.png");
        ritualInvo2.setHechizo(true);
        ritualInvo2.setAliado(true);
        ritualInvo2.setTargetable(false);
        darTamaño(ritualInvo2);
        Carta furia = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        furia.setEnabled(true);
        furia.setNombre("Furia");
        furia.setCoste(3);
        furia.loadTexture("Spell/furia.png");
        furia.setHechizo(true);
        furia.setAliado(true);
        furia.setTargetable(false);
        darTamaño(furia);
        Carta furia2 = new Carta(99999, 99999, mainStage, 0, true, mazo, mazoArray);
        furia2.setEnabled(true);
        furia2.setNombre("Furia");
        furia2.setCoste(3);
        furia2.loadTexture("Spell/furia.png");
        furia2.setHechizo(true);
        furia2.setAliado(true);
        furia2.setTargetable(false);
        darTamaño(furia2);

        //Según la clase del jugador poblamos su mazo con unas cartas u otras.
        switch (claseJugador) {
            case 1:
                //Hemomante
                mazoArray.add(ritualDeInvocacion);
                mazoArray.add(ritualDeInvocacion2);
                mazoArray.add(ritualDeInvocacion3);
                mazoArray.add(ritualDeInvocacion4);
                mazoArray.add(ritualDeInvocacion5);
                mazoArray.add(ritualDeInvocacion6);
                mazoArray.add(ritualDeInvocacion7);
                mazoArray.add(ritualDeInvocacion8);
                mazoArray.add(ritualDeInvocacion9);
                mazoArray.add(ritualDeInvocacion10);
                mazoArray.add(potOfGreed);
                mazoArray.add(matar);
                mazoArray.add(matar2);
                mazoArray.add(matar3);
                mazoArray.add(sed);
                mazoArray.add(sed2);
                mazoArray.add(ritualSangre);
                mazoArray.add(ritualSangre2);
                mazoArray.add(pacto);
                mazoArray.add(pacto2);

                break;

            case 2:
                //Pícaro
                mazoArray.add(ritualDeInvocacion);
                mazoArray.add(ritualDeInvocacion2);
                mazoArray.add(ritualDeInvocacion3);
                mazoArray.add(ritualDeInvocacion4);
                mazoArray.add(ritualDeInvocacion5);
                mazoArray.add(ritualDeInvocacion6);
                mazoArray.add(ritualDeInvocacion7);
                mazoArray.add(ritualDeInvocacion8);
                mazoArray.add(ritualDeInvocacion9);
                mazoArray.add(ritualDeInvocacion10);
                mazoArray.add(potOfGreed);
                mazoArray.add(matar);
                mazoArray.add(matar2);
                mazoArray.add(matar3);
                mazoArray.add(ritualPicaro);
                mazoArray.add(ritualPicaro2);
                mazoArray.add(oro);
                mazoArray.add(oro2);
                mazoArray.add(humildad);
                mazoArray.add(humildad2);

                break;

            case 3:
                //Invocador
                mazoArray.add(ritualDeInvocacion);
                mazoArray.add(ritualDeInvocacion2);
                mazoArray.add(ritualDeInvocacion3);
                mazoArray.add(ritualDeInvocacion4);
                mazoArray.add(ritualDeInvocacion5);
                mazoArray.add(ritualDeInvocacion6);
                mazoArray.add(ritualDeInvocacion7);
                mazoArray.add(ritualDeInvocacion8);
                mazoArray.add(ritualDeInvocacion9);
                mazoArray.add(ritualDeInvocacion10);
                mazoArray.add(potOfGreed);
                mazoArray.add(matar);
                mazoArray.add(semilla);
                mazoArray.add(semilla2);
                mazoArray.add(devolve);
                mazoArray.add(devolve2);
                mazoArray.add(ritualInvo);
                mazoArray.add(ritualInvo2);
                mazoArray.add(furia);
                mazoArray.add(furia2);

                break;
        }
        //Barajamos el mazo.
        Collections.shuffle(mazoArray);

    }

    //Método que asigna tamaño a las cartas. También se asegura de que esten en el mazo.
    private void darTamaño(Carta cartaResize) {
        cartaResize.setSize(Parametros.anchoCarta, Parametros.altoCarta);
        cartaResize.setBoundaryRectangle();
        cartaResize.setArray(mazoArray);
        cartaResize.setZona(mazo);
        cartaResize.toFront();
    }


    //Método que hace que el oponente ataque con sus monstruos.

    //Método que crea los rituales de invocación
    private void crearRitualInvocacion(Carta ritualACrear) {
        ritualACrear.setEnabled(true);
        ritualACrear.setNombre("Ritual");
        ritualACrear.loadTexture("Spell/ritual.png");
        ritualACrear.setHechizo(true);
        ritualACrear.setAliado(true);
        ritualACrear.setTargetable(true);
        darTamaño(ritualACrear);
    }

    //Método que asigna Imagenes y mazos segun las clases
    private void checkClases() {
        switch (claseOponente) {
            case 1:
                campoOp.clearAnimation();
                campoOp.loadTexture("half.png");
                campoOp.setName("Campo Enemigo");
                campoOp.setBoundaryRectangle();
                campoOp.setTargetable(false);
                campoOp.setColor(Color.RED);

                for (Zona zona1 : zonasMonstruoEnemigas) {
                    zona1.clearAnimation();
                    zona1.loadTexture("dorsoRojo.png");
                    zona1.setSize(Parametros.anchoCarta + 24, Parametros.altoCarta + 24);
                    zona1.setBoundaryRectangle();
                    zona1.setAliado(false);
                }

                retratoEnemigo.clearAnimation();
                retratoEnemigo.loadTexture("hemo.png");
                retratoEnemigo.setSize(250, 250);
                retratoEnemigo.setBoundaryRectangle();
                retratoEnemigo.setAliado(false);
                retratoEnemigo.setOcupado(true);
                retratoEnemigo.setName("CARA");

                break;
            case 2:
                campoOp.clearAnimation();
                campoOp.loadTexture("half.png");
                campoOp.setName("Campo Enemigo");
                campoOp.setBoundaryRectangle();
                campoOp.setTargetable(false);
                campoOp.setColor(Color.BROWN);

                for (Zona zona1 : zonasMonstruoEnemigas) {
                    zona1.clearAnimation();
                    zona1.loadTexture("dorsoVerde.png");
                    zona1.setSize(Parametros.anchoCarta + 24, Parametros.altoCarta + 24);
                    zona1.setBoundaryRectangle();
                    zona1.setAliado(false);
                }

                retratoEnemigo.clearAnimation();
                retratoEnemigo.loadTexture("picaro.png");
                retratoEnemigo.setSize(250, 250);
                retratoEnemigo.setBoundaryRectangle();
                retratoEnemigo.setAliado(false);
                retratoEnemigo.setOcupado(true);
                retratoEnemigo.setName("CARA");

                break;
            case 3:
                campoOp.clearAnimation();
                campoOp.loadTexture("half.png");
                campoOp.setName("Campo Enemigo");
                campoOp.setBoundaryRectangle();
                campoOp.setTargetable(false);
                campoOp.setColor(Color.CYAN);

                for (Zona zona1 : zonasMonstruoEnemigas) {
                    zona1.clearAnimation();
                    zona1.loadTexture("dorsoAzul.png");
                    zona1.setSize(Parametros.anchoCarta + 24, Parametros.altoCarta + 24);
                    zona1.setBoundaryRectangle();
                    zona1.setAliado(false);
                }
                retratoEnemigo.clearAnimation();
                retratoEnemigo.loadTexture("invocador.png");
                retratoEnemigo.setSize(250, 250);
                retratoEnemigo.setBoundaryRectangle();
                retratoEnemigo.setAliado(false);
                retratoEnemigo.setOcupado(true);
                retratoEnemigo.setName("CARA");
                break;

        }
        switch (claseJugador) {
            case 1:
                campo.clearAnimation();
                campo.loadTexture("half.png");
                campo.setName("Campo Aliado");
                campo.setBoundaryRectangle();
                campo.setTargetable(false);
                campo.setColor(Color.RED);

                for (Zona zona1 : zonasDeMonstruo) {
                    zona1.clearAnimation();
                    zona1.loadTexture("dorsoRojo.png");
                    zona1.setSize(Parametros.anchoCarta + 24, Parametros.altoCarta + 24);
                    zona1.setBoundaryRectangle();
                    zona1.setAliado(true);

                }
                retratoJugador.clearAnimation();
                retratoJugador.loadTexture("hemo.png");
                retratoJugador.setSize(250, 250);
                retratoJugador.setBoundaryRectangle();
                retratoJugador.setAliado(false);
                retratoJugador.setOcupado(true);

                break;
            case 2:
                campo.clearAnimation();
                campo.loadTexture("half.png");
                campo.setName("Campo Aliado");
                campo.setBoundaryRectangle();
                campo.setTargetable(false);
                campo.setColor(Color.BROWN);

                for (Zona zona1 : zonasDeMonstruo) {
                    zona1.clearAnimation();
                    zona1.loadTexture("dorsoVerde.png");
                    zona1.setSize(Parametros.anchoCarta + 24, Parametros.altoCarta + 24);
                    zona1.setBoundaryRectangle();
                    zona1.setAliado(true);
                }

                retratoJugador.clearAnimation();
                retratoJugador.loadTexture("picaro.png");
                retratoJugador.setSize(250, 250);
                retratoJugador.setBoundaryRectangle();
                retratoJugador.setAliado(false);
                retratoJugador.setOcupado(true);
                break;
            case 3:
                campo.clearAnimation();
                campo.loadTexture("half.png");
                campo.setName("Campo Enemigo");
                campo.setBoundaryRectangle();
                campo.setTargetable(false);
                campo.setColor(Color.CYAN);

                for (Zona zona1 : zonasDeMonstruo) {
                    zona1.clearAnimation();
                    zona1.loadTexture("dorsoAzul.png");
                    zona1.setSize(Parametros.anchoCarta + 24, Parametros.altoCarta + 24);
                    zona1.setBoundaryRectangle();
                    zona1.setAliado(true);

                }
                retratoJugador.clearAnimation();
                retratoJugador.loadTexture("invocador.png");
                retratoJugador.setSize(250, 250);
                retratoJugador.setBoundaryRectangle();
                retratoJugador.setAliado(true);
                retratoJugador.setOcupado(true);
                break;

        }
        //Inicio de partida
        crearMazos();
        robarCarta();
        robarCarta();
        robarCarta();
    }

    //Método que crea las labels y las posiciona.
    private void crearLabelsVM() {
        vmJugador = new Label("Vida " + Parametros.vidaTotalMax + "\n Cristales: " + cristalesJ, skin);
        vmJugador.setBounds(Gdx.graphics.getWidth() - 350, 10, 300, 100);
        vmJugador.setAlignment(Align.right);
        vmJugador.toFront();
        mainStage.addActor(vmJugador);

        vmEnemigo = new Label("Vida " + Parametros.vidaTotalMax + "\n Cristales: " + cristalesOp, skin);
        vmEnemigo.setBounds(80, Gdx.graphics.getHeight() - 400, 300, 100);
        vmEnemigo.setAlignment(Align.center);
        vmEnemigo.toFront();
        mainStage.addActor(vmEnemigo);

    }

    //Método que pasa el turno. Usado tanto por oponente como jugador.
    private void pasarTurno() {
        turno += 1;
        labelCentral.setText("Turno " + ((turno + 1) / 2) + " de 10.");
        //Si es el turno de jugador
        if (turno % 2 != 0) {
            if (cristalesJ < 10) {
                cristalesJ = turno / 2 + 1;
                robarCarta();
            }
            //Refresca y activa los monstruos que ya estaban en el campo el turno anterior
            for (Zona i : zonasDeMonstruo) {
                if (i.getCarta() != null) {
                    i.getCarta().setDraggable(true);
                    i.getCarta().setHaAtacado(false);
                    i.getCarta().setEnabled(true);
                }
            }
        }
        //Turno del oponente
        if (turno % 2 == 0) {
            if (cristalesOp < 10)
                cristalesOp = turno / 2 + 1;
            for (Zona i : zonasMonstruoEnemigas) {
                if (i.getCarta() != null) {
                    i.getCarta().setHaAtacado(false);
                    i.getCarta().setEnabled(true);
                }
            }
        }
    }

    //Método que termina la partida y redirige a la gameover screen.
    private void terminarJuego() {
        gameOver = true;
        labelCentral.setText("La espada miente...");
    }

    //Método que pinta una pantalla u otra según el ganador.
    private void checkGanador() {
        if (vidaActual > vidaEnemigo) {
            uiTablaFondo3.setBackground(fondoWin.getDrawable());
        }
        if (vidaActual <= 0) {
            uiTablaFondo3.setBackground(fondoLose.getDrawable());
        }
        if (vidaActual <= vidaEnemigo) {
            uiTablaFondo3.setBackground(fondoLose.getDrawable());
        }
    }

    //Método que crea las zonas de monstruo enemigas.
    private void crearZonasMonstruoOp() {
        for (int j = 0; j < 5; j++) {

            zonaE1 = new Zona(Gdx.graphics.getWidth() / 5 + 150 + (Gdx.graphics.getWidth() / 7 * j), (Gdx.graphics.getHeight() / 1.8f), mainStage, j, j);
            zonaE1.setSize(Parametros.anchoCarta + 24, Parametros.altoCarta + 48);
            zonaE1.setBoundaryRectangle();
            zonaE1.setName("zonaE" + (j + 1));
            zonaE1.setAliado(false);
            zonasMonstruoEnemigas.add(zonaE1);
        }
    }

    //Método que crea las zonas de monstruo aliadas.
    private void crearZMAliados() {
        for (int i = 0; i < 5; i++) {

            zona1 = new Zona(Gdx.graphics.getWidth() / 5 + 150 + (Gdx.graphics.getWidth() / 7 * i), (Gdx.graphics.getHeight() / 7), mainStage, i, i);
            zona1.loadTexture("dorsoRojo.png");
            zona1.setSize(Parametros.anchoCarta + 24, Parametros.altoCarta + 48);
            zona1.setAliado(true);
            zona1.setBoundaryRectangle();
            zona1.setName("zona" + (i + 1));
            zonasDeMonstruo.add(zona1);
        }
    }

    //Método que crea las zonas de los retratos
    private void crearRetratos() {
        retratoEnemigo = new Zona(100, Gdx.graphics.getHeight() - 300, mainStage, 5f, 5f);
        retratoEnemigo.loadTexture("picaro.png");
        retratoEnemigo.setSize(250, 250);
        retratoEnemigo.setBoundaryRectangle();
        retratoEnemigo.setAliado(false);
        retratoEnemigo.setOcupado(true);

        retratoJugador = new Zona(100, Gdx.graphics.getHeight() / 2 - 250, mainStage, 5f, 5f);
        retratoJugador.loadTexture("hemo.png");
        retratoJugador.setSize(250, 250);
        retratoJugador.setBoundaryRectangle();
        retratoJugador.setOcupado(true);
        retratoJugador.setAliado(true);
        retratoJugador.setCarta(null);

        mainStage.addActor(retratoEnemigo);
        mainStage.addActor(retratoJugador);
        zonas.add(retratoEnemigo);
        zonas.add(retratoJugador);
    }

    //Método que crea los campos visualmente en el fondo. También crea la zona mano del jugador.
    private void crearCampos() {
        campoOp = new Zona(0, Gdx.graphics.getHeight() / 2, mainStage, Gdx.graphics.getWidth(), (Gdx.graphics.getHeight() / 2 - 300));
        campoOp.loadTexture("half.png");
        campo = new Zona(0, 0, mainStage, Gdx.graphics.getWidth(), (Gdx.graphics.getHeight() / 2));
        campo.loadTexture("half.png");
        campo.setName("Campo Aliado");
        campoOp.setName("Campo Enemigo");
        campoOp.setBoundaryRectangle();
        campo.setBoundaryRectangle();
        campo.setTargetable(false);
        campoOp.setTargetable(false);
        campo.toBack();
        campoOp.toBack();
        mainStage.addActor(campoOp);
        mainStage.addActor(campo);
        zonas.add(campoOp);
        zonas.add(campo);

        zonaMano = new Zona(50, 50, mainStage, Parametros.anchoCarta * 6, Parametros.altoCarta);
        zonaMano.setName("Mano Jugador");
        zonaMano.setTargetable(false);

    }

    //Ataque a una de nuestras criaturas.
    private void oponenteAtaca() {
        for (Zona i : zonasMonstruoEnemigas) {
            Carta cartaEnemiga = i.getCarta();
            if (cartaEnemiga != null) {
                for (Zona j : zonasDeMonstruo) {
                    Carta cartaAliada = j.getCarta();
                    if (cartaAliada != null) {
                        ataqueSound.play(volumen);
                        //Primero intentará hacer un intercambio favorable
                        if (!cartaEnemiga.isHaAtacado() && cartaEnemiga.getPoder()
                                >= cartaAliada.getPoder()) {
                            //Ataca
                            Carta.combate(cartaEnemiga, cartaAliada);
                            cartaEnemiga.setHaAtacado(true);
                            //Si no puede atacará a lo que pueda.
                        } else if (!cartaEnemiga.isHaAtacado()) {
                            Carta.combate(cartaEnemiga, cartaAliada);
                            cartaEnemiga.setHaAtacado(true);
                        }
                    }
                }
            }
        }
    }

    //Ataque directo al jugador
    private void oponenteAtacaDirecto() {
        for (Zona i : zonasMonstruoEnemigas) {
            Carta cartaEnemiga = i.getCarta();
            if (cartaEnemiga != null) {
                if (!cartaEnemiga.isHaAtacado() && (campoArray.isEmpty())) {
                    vidaActual = vidaActual - cartaEnemiga.getPoder();
                    cartaEnemiga.setHaAtacado(true);
                }
            }
        }
        ataqueSound.play(volumen);
    }

    //Método limpiador que desactiva las cartas muertas o usadas.
    private void matarMonstruos() {
        for (Carta i : cartas) {
            if (i.getPoder() <= 0) {
                i.setEnabled(false);
            }

        }
    }

    //Método que hace que el oponente invoque un monstruo acorde a sus cristales cada turno.
    private void oponenteInvoca() {

        //Busca una zona vacía
        Zona zonaVacia = null;
        for (Zona i : zonasMonstruoEnemigas) {
            if (i.getCarta() == null && zonaVacia == null) {
                zonaVacia = i;
            }
        }
        //Crea la carta
        if (zonaVacia != null) {
            Carta cartaEnemiga = new Carta(0, 999, mainStage, turno / 2,
                    false, zonaVacia, campoOpArray);
            cartaEnemiga.moveToActor(zonaVacia);
            zonaVacia.setCarta(cartaEnemiga);
            zonaVacia.setOcupado(true);
            zonaVacia.setAliado(false);
            cartaEnemiga.setZona(zonaVacia);
            cartaEnemiga.setDraggable(false);
            cartaEnemiga.setHaAtacado(true);
            cristalesOp = cristalesOp - turno / 2;
        }
    }

    //Método limpiador que desocupa las zonas para poder usar otra vez cartas en ella.
    private void desocuparZonas() {
        for (Zona i : zonasDeMonstruo) {
            if (i.carta != null && i.carta.getPoder() <= 0 && !i.carta.isHechizo()) {
                i.setOcupado(false);
                i.getCarta().remove();
                i.carta = null;
                i.setCarta(null);
            }
        }
        for (Zona i : zonasMonstruoEnemigas) {
            if (i.carta != null && i.carta.getPoder() <= 0) {
                i.setOcupado(false);
                i.getCarta().remove();
                i.carta = null;
            }
        }
    }

    //Método que actualiza las labels en el render
    private void actualizarInterfaz() {
        uiLabel.setSize(300, 200);
        uiLabel.setText("");
        vmJugador.setText("Vida: " + vidaActual + "\n Cristales: " + cristalesJ);
        vmEnemigo.setText("Vida Enemigo: " + vidaEnemigo + "\n" + "Cristales: " + cristalesOp);
    }

    //Logica de juego, metodo de juego, cada fps
    //Interacciones, comprobar ganador, bucle de juego empezar turno.
    @Override
    public void render() {
        //Limpiamos el buffer
        Gdx.gl.glClearColor(.9f, .9f, .9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //Actualizamos ui y limpiamos zonas.
        actualizarInterfaz();
        desocuparZonas();
        matarMonstruos();

        //Lógica de pantallas. En cada una se da el poder a un stage y solo se puede interactuar con éste.
        if (menuMomento) {
            uiStage.act();
            uiStage.draw();
            Gdx.input.setInputProcessor(uiStage);
        }
        if (musicaMomento) {
            settingsStage.act();
            settingsStage.draw();
            Gdx.input.setInputProcessor(settingsStage);
        }
        if (juegoMomento) {
            mainStage.act();
            mainStage.draw();
            Gdx.input.setInputProcessor(mainStage);
        }
        //IA del oponente. Invoca, ataca y después ataca directo y pasa turno.
        if (turno % 2 == 0) {
            System.out.println("turno OP " + turno);
            jugadorActivo = false;
            oponenteInvoca();
            oponenteAtaca();
            oponenteAtacaDirecto();
            pasarTurno();
        } else {
            //El resto del tiempo es nuestro turno.
            jugadorActivo = true;
        }
        //Si es nuestro turno la IA espera
        if (jugadorActivo) {
        }
        //Comprobante de si alguien muere o se acaban los 20 turnos.
        if (turno >= 21 || vidaActual <= 0 || vidaEnemigo <= 0) {
            terminarJuego();

        }
        //Si se termina el juego se va a la game over screen
        if (gameOver) {
            gameOverStage.act();
            gameOverStage.draw();
            checkGanador();
            Gdx.input.setInputProcessor(gameOverStage);

            //Botón volver a jugar, el cual reinicia los parametros de la partida y limpia la mesa.
            TextButton volver = new TextButton(" Volver a Jugar ", skin);
            volver.addListener((Event e) -> {
                if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
                    return false;
                menuMomento = true;
                musicaMomento = false;
                gameOver = false;
                juegoMomento = false;
                cristalesJ = 1;
                cristalesOp = 0;
                turno = 1;
                vidaActual = 10;
                vidaEnemigo = 10;

                //Limpiamos los arrays y zonas
                mazoOpArray.clear();
                mazoArray.clear();
                zonaMano.clear();
                manoOpArray.clear();
                campoArray.clear();
                campoOpArray.clear();

                //Desocupamos todo
                for (Zona zonasAliadas : zonasDeMonstruo) {
                    if (zonasAliadas.getCarta() != null) {
                        Carta carta = zonasAliadas.getCarta();
                        carta.setPoder(0);
                        carta.remove();
                    }

                }
                for (Zona zonasEnemigas : zonasMonstruoEnemigas) {
                    if (zonasEnemigas.getCarta() != null) {
                        Carta carta = zonasEnemigas.getCarta();
                        carta.setPoder(0);
                        carta.remove();
                        zonasEnemigas.setAliado(false);

                    }

                }
                for (Carta cartaEnMano : manoArray) {
                    if (cartaEnMano != null) {
                        cartaEnMano.setPoder(0);
                        cartaEnMano.remove();

                    }

                }
                manoArray.clear();
                return false;
            });
            volver.setSize(400, 100);
            volver.setPosition(Gdx.graphics.getWidth() / 2 - volver.getWidth() / 2, (Gdx.graphics.getHeight() / 2) - 450);
            gameOverStage.addActor(volver);

        }
    }

    //Métodos de LibGDX
    @Override
    public void resize(int width, int height) {
        mainStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        skin.dispose();
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub

    }

    @Override
    public void render(float delta) {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }
}