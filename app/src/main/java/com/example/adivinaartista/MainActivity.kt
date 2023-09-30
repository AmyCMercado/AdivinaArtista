package com.example.adivinaartista

import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.get
import com.airbnb.lottie.LottieAnimationView
import com.github.javafaker.Faker
import com.google.android.flexbox.FlexboxLayout
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var txtAsk: TextView
    private var respuesta:String = ""
    private lateinit var flexAlfabeto: FlexboxLayout
    private lateinit var flexResponse: FlexboxLayout
    private var indicesOcupados:ArrayList<Int> = arrayListOf()
    private var intentosPermitidos:Int = 0
    private var intentosHechos:Int = 0
    private lateinit var txtnumberattempts: TextView
    private var finalizado:Boolean = false
    private lateinit var lottieResult: LottieAnimationView
    private lateinit var lotieAnimThinking: LottieAnimationView
    private lateinit var txtResultMessage: TextView
    private lateinit var txtCorrectAnswer: TextView
    private lateinit var mediaPlayerperder: MediaPlayer
    private lateinit var mediaPlayerganar: MediaPlayer
    private lateinit var mediaPlayerclick: MediaPlayer
    private lateinit var mediaPlayerclicke: MediaPlayer
    private lateinit var reboot: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Mostrar el splash antes de que se le asigne a esta pantalla su recurso de diseño
        installSplashScreen()

        setContentView(R.layout.activity_main)


        //Widgets

        reboot = findViewById(R.id.reboot)
        txtAsk = findViewById(R.id.txtAsk)
        lotieAnimThinking = findViewById(R.id.animation_view_thik)
        flexResponse = findViewById(R.id.edit)
        flexAlfabeto = findViewById(R.id.flexboxLayout)
        txtnumberattempts= findViewById(R.id.txtnumberattempts)
        lottieResult = findViewById(R.id.animation_view_resultado)
        txtResultMessage = findViewById(R.id.txtResultMessage)
        txtCorrectAnswer = findViewById(R.id.txtCorrectAnswer)
        mediaPlayerperder=MediaPlayer.create(this,R.raw.perder)
        mediaPlayerganar=MediaPlayer.create(this,R.raw.ganador)
        mediaPlayerclick=MediaPlayer.create(this,R.raw.click)
        mediaPlayerclicke=MediaPlayer.create(this,R.raw.incorrecto)
        reboot= findViewById(R.id.reboot)

        //Generando palabra a adivinar

        respuesta = obtenerPalabraAleatoria().uppercase()
        //La cantidad de intentos permitidos se le dara el tamaño de caracteres + 2
        intentosPermitidos = respuesta.length + 2
        txtnumberattempts.text = "$intentosHechos/$intentosPermitidos"

        // Generando alfabeto que incluya las letras de la palabra a adivinar
        val alfabeto = generarAlfabeto(respuesta)

        //Desordenar el alfabeto generado para que el juego sea dinámico
        val alfabetoDesorden = desordenar(alfabeto)

        //Generando espacios donde se iran mostrando la respuesta
        mostrarEspacioRespuesta(respuesta.length, flexResponse)

        //Mostrar en la vista cada letra generada como boton para que se pueda seleccionar
        mostrarAlfabeto(alfabetoDesorden.uppercase(), flexAlfabeto)

        //Reiniciar el juego

        reboot.setOnClickListener {
            Reinicio()
        }

    }

    fun generarAlfabeto(semilla: String):String {
        val randomValues = List(5) { Random.nextInt(65, 90).toChar() }
        return "$semilla${randomValues.joinToString(separator = "")}"
    }

    fun desordenar(theWord: String):String {
        val theTempWord=theWord.toMutableList()
        for (item in 0..Random.nextInt(1,theTempWord.count()-1))
        {
            val indexA=Random.nextInt(theTempWord.count()-1)
            val indexB=Random.nextInt(theTempWord.count()-1)
            val temp=theTempWord[indexA]
            theTempWord[indexA]=theTempWord[indexB]
            theTempWord[indexB]=temp
        }
        return theTempWord.joinToString(separator = "")
    }

    fun obtenerPalabraAleatoria(): String {
        val faker = Faker()
        val palabra = faker.artist().name()
        return palabra.split(' ').get(0) //a veces devuelve nombres compuestos
    }

    fun mostrarEspacioRespuesta(cantidad:Int, vista:FlexboxLayout){
        for (letter in 1..cantidad) {
            val btnLetra = EditText(this)
            btnLetra.isEnabled = false
            val layoutParams = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(5, 5, 5, 5)
            btnLetra.layoutParams = layoutParams
            vista.addView(btnLetra)
        }
    }

    fun mostrarAlfabeto(alfabeto:String, vista:FlexboxLayout){
        for (letter in alfabeto) {
            val btnLetra = Button(this)
            btnLetra.text = letter.toString()
            btnLetra.textSize = 12f
            val layoutParams = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(5, 5, 5, 5)
            btnLetra.layoutParams = layoutParams
            vista.addView(btnLetra)
            btnLetra.setOnClickListener{
                clickLetra(it as Button)
            }
        }
    }

    fun clickLetra(btnClicked:Button) {
        if(!finalizado){
            //obtener el indice de la letra seleccionada inicialmente
            var starIndex = 0
            var resIndex = respuesta.indexOf(btnClicked.text.toString())
            //si el indice ya fue ocupado entonces no tomar en cuenta los indices hacia atras
            while(indicesOcupados.contains(resIndex)){
                starIndex = resIndex + 1
                resIndex = respuesta.indexOf(btnClicked.text.toString(), starIndex)

            }
            //si la respuesta contiene la letra seleccionada
            if(resIndex != -1){
                val flexRow = flexResponse.get(resIndex) as EditText
                flexRow.setText( respuesta.get(resIndex).toString())
                indicesOcupados.add(resIndex)
                mediaPlayerclick.start()
                btnClicked.setBackgroundColor(Color.GREEN)
                btnClicked.isEnabled = false
                btnClicked.setTextColor(Color.WHITE)


            }
            else{
                Toast.makeText(applicationContext, "Letra Incorrecta",
                    Toast.LENGTH_SHORT).show()
                mediaPlayerclicke.start()
                btnClicked.setBackgroundColor(Color.RED)
                btnClicked.isEnabled = false
                btnClicked.setTextColor(Color.WHITE)


            }
            intentosHechos++
            txtnumberattempts.text = "$intentosHechos/$intentosPermitidos"
            verificarResultado()
        }
    }

    fun verificarResultado(){
        if (intentosHechos == intentosPermitidos || indicesOcupados.size == respuesta.length){
            finalizado = true
            //si gana o perdio
            if (indicesOcupados.size == respuesta.length){
                lottieResult.setAnimation(R.raw.correcto)
                txtResultMessage.text = "Felicidades has ganado!!!"
                mediaPlayerganar.start()
            }
            else{
                lottieResult.setAnimation(R.raw.perdiendo)
                txtResultMessage.text = "Has perdido, inténtalo de nuevo"
                mediaPlayerperder.start()
            }
            txtCorrectAnswer.setText("La respuesta correcta es: $respuesta")
            //despues de configurar la vista ponerlas como visibles
            txtResultMessage.visibility = View.VISIBLE
            lottieResult.visibility = View.VISIBLE
            txtCorrectAnswer.visibility = View.VISIBLE
            //ocultar los que no se deben mostrar
            flexResponse.visibility = View.GONE
            txtnumberattempts.visibility = View.GONE
            flexAlfabeto.visibility = View.GONE

            txtAsk.visibility = View.GONE
            lotieAnimThinking.visibility = View.GONE
        }

    }
    fun Reinicio() {

        Log.d("MainActivity", "REINICIANDO JUEGO")
        respuesta = obtenerPalabraAleatoria().uppercase()
        intentosPermitidos = respuesta.length + 2
        intentosHechos = 0
        indicesOcupados.clear()
        finalizado = false
        flexResponse.removeAllViews()
        flexAlfabeto.removeAllViews()
        txtResultMessage.visibility = View.GONE
        lottieResult.visibility = View.GONE
        txtCorrectAnswer.visibility = View.GONE
        flexResponse.visibility = View.VISIBLE
        txtnumberattempts.visibility = View.VISIBLE
        flexAlfabeto.visibility = View.VISIBLE
        txtAsk.visibility = View.VISIBLE
        lotieAnimThinking.visibility = View.VISIBLE

        //Generarando alfabeto que incluya las letras de la palabra a adivinar
        val alfabeto = generarAlfabeto(respuesta)

        // Desordenando el alfabeto generado para que sea mas dinamica
        val alfabetoDesorden = desordenar(alfabeto)

        //Generarando espacios donde se mostaran la respuesta
        mostrarEspacioRespuesta(respuesta.length, flexResponse)

        //Mostrar en la vista cada letra en boton para seleccionar
        mostrarAlfabeto(alfabetoDesorden.uppercase(), flexAlfabeto)

        reboot.visibility = View.VISIBLE
    }


}