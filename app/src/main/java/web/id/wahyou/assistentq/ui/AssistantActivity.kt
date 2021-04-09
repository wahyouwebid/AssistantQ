package web.id.wahyou.assistentq.ui

import android.animation.Animator
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.ClipboardManager
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Animatable
import android.hardware.camera2.CameraManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper
import org.w3c.dom.Text
import web.id.wahyou.assistentq.R
import web.id.wahyou.assistentq.data.AssistantDatabase
import web.id.wahyou.assistentq.databinding.ActivityAssistentBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class AssistantActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAssistentBinding
    private lateinit var assistantViewModel: AssistantViewModel
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent
    private lateinit var keeper : String

    private var REQUESTCALL = 1
    private var SENDSMS = 2
    private var READSMS = 3
    private var SHAREFILE = 4
    private var SHARETEXTFILE = 5
    private var READCONTACT = 6
    private var CAPTUREPHOTO = 7

    private var REQUEST_CODE_SELECT_DOC : Int = 100
    private var REQUEST_ENABLE_BLUETOOTH : Int = 1000

    private var bluetoothAdapter : BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private lateinit var cameraManager: CameraManager
    private lateinit var clipboardManager: ClipboardManager

    private lateinit var cameraId : String
    private lateinit var ringtone : Ringtone

    private val logTts = "TTS"
    private val logSr = "SR"
    private val logKeeper = "keeper"

    private var imageIndex : Int = 0
    private lateinit var imageUri : Uri
    private lateinit var helper : OpenWeatherMapHelper

//    @Suppress("DEPRECATION")
//    private val imageDirectory = Environment.getExternalStorageState(Environment.DIRECTORY_PICTURES)


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.non_movable, R.anim.non_movable)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_assistent)

        val application = requireNotNull(this).application
        val dataSource = AssistantDatabase.getInstance(application).assistant()
        val viewModelFactory = AssistantViewModelFactory(dataSource, application)

        //viewModel
        assistantViewModel = ViewModelProvider(
            this,
            viewModelFactory
        ).get(AssistantViewModel::class.java)

        //Adapter
        val adapter = AssistantAdapter()
        binding.rvAssistant.adapter = adapter

        //observe
        assistantViewModel.messages.observe(this, {
            adapter.data = it
        })
        binding.lifecycleOwner = this


        if(savedInstanceState == null){
            binding.assistantLayout.visibility = View.INVISIBLE
            val viewTreeObserver : ViewTreeObserver = binding.assistantLayout.viewTreeObserver
            if(viewTreeObserver.isAlive){
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
                    override fun onGlobalLayout() {
                        circularRevealActivity()
                        binding.assistantLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }

                })
            }
        }

        //camera
        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            cameraId = cameraManager.cameraIdList[0]

        } catch (e: Exception){
            e.printStackTrace()
        }

        //clipboard
        clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        ringtone = RingtoneManager.getRingtone(
            applicationContext.applicationContext,
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        )

        helper = OpenWeatherMapHelper(getString(R.string.OPEN_WEATHER_MAP_API_KEY))

        textToSpeech = TextToSpeech(this) { status ->
            if(status == TextToSpeech.SUCCESS){
                val result = textToSpeech.setLanguage(Locale.ENGLISH)
                if(
                    result == TextToSpeech.LANG_MISSING_DATA||
                    result == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    Log.e(logTts, "Language Not Supported")
                } else {
                    Log.e(logTts, "Language Supported")
                }
            } else {
                Log.e(logTts, "Initialization failed")
            }
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, "true")
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                binding.tvStatus.text = "Waiting..."
            }

            override fun onBeginningOfSpeech() {
                binding.tvStatus.text = "Listening..."
            }

            override fun onRmsChanged(rmsdB: Float) {

            }

            override fun onBufferReceived(buffer: ByteArray?) {

            }

            override fun onEndOfSpeech() {
                binding.tvStatus.text = "Tekan Tombol"
            }

            override fun onError(error: Int) {

            }

            override fun onResults(results: Bundle?) {
                val data = results?.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION
                )

                if (data != null) {
                    keeper = data[0]
                    Log.d(logKeeper, keeper)

                    when {
                        keeper.contains("terima kasih") -> speak("Sama sama ini sudah menjadi tugas saya sebagai aplikasi")
                        keeper.contains("selamat datang") -> speak("untuk apa ?")
                        keeper.contains("makan apa ya hari ini") -> speak("Telor ceplok saja lah")
                        keeper.contains("besok libur harus ngapain") -> speak("Silahkan berolahraga, makan teratur, dan jangan lupa shalat")
                        keeper.contains("puasa kapan ya") -> speak("Puasa tanggal 13 hari selasa, jangan sampai gak puasa yah!")
                        keeper.contains("clear") -> assistantViewModel.onClear()
                        keeper.contains("Tanggal berapa sekarang") -> getDate()
                        keeper.contains("Jam berapa sekarang") -> getTime()
                        keeper.contains("Buka Gmail") -> openGmail()
                        keeper.contains("Buka Whatsapp") -> openWhatsApp()
                        keeper.contains("turn on bluetooth") -> turnOnBluetooth()
                        keeper.contains("turn off bluetooth") -> turnOffBluetooth()
                        keeper.contains("putar musik") -> playRingtone()
                        keeper.contains("stop musik") -> stopRingtone()
                        keeper.contains("halo") || keeper.contains("hi") -> speak("Halo, bisa saya bantu ?")
                        else -> speak("Ucapan tidak sesuai, Silahkan cobalagi")
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {

            }

            override fun onEvent(eventType: Int, params: Bundle?) {

            }

        })

        binding.imgVoice.setOnTouchListener { view, motionEvent ->
            when(motionEvent.action){
                MotionEvent.ACTION_UP -> {
                    speechRecognizer.stopListening()
                }

                MotionEvent.ACTION_DOWN -> {
                    textToSpeech.stop()
                    speechRecognizer.startListening(recognizerIntent)
                }
            }
            false
        }

        checkIfRecognizerAvailable()
    }

    private fun checkIfRecognizerAvailable() {
        if(SpeechRecognizer.isRecognitionAvailable(this)){
            Log.d(logSr, "yes")
        } else {
            Log.d(logSr, "not available")
        }
    }

    private fun speak(text: String) {
        textToSpeech.language = Locale.forLanguageTag("ID-id")
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        assistantViewModel.sendMessage(keeper, text)
    }

    private fun getDate(){
        val calendar = Calendar.getInstance()
        val formatedDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.time)
        val splitDate = formatedDate.split(",").toTypedArray()
        val date = splitDate[1].trim {it<= ' '}
        speak("Sekarang Adalah tanggal $date")
    }

    private fun getTime() {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("HH:mm:ss")
        val time : String = format.format(calendar.time)
        speak("Sekarang menunjukan waktu pukul $time")
    }

    private fun openWhatsApp(){
        val intent = packageManager.getLeanbackLaunchIntentForPackage("com.whatsapp")
        intent?.let { startActivity(it) }
    }

    private fun openGmail(){
        val intent = packageManager.getLeanbackLaunchIntentForPackage("com.google.android.gm")
        intent?.let { startActivity(it ) }
    }

    @SuppressLint("MissingPermission")
    private fun turnOnBluetooth(){
        if(!bluetoothAdapter.isEnabled){
            speak("Baik Bluetooth akan di aktifkan")
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH)
        } else {
            speak("Bluetooth sudah dalam keadaan aktif")
        }
    }

    @SuppressLint("MissingPermission")
    private fun turnOffBluetooth(){
        if(bluetoothAdapter.isEnabled){
            bluetoothAdapter.disable()
            speak("Baik Bluetooth akan dimakatikan")
        } else {
            speak("Bluetooth sudah dalam keadaan mati")
        }
    }

    private fun playRingtone(){
        speak("Playing Ringtone")
        ringtone.play()
    }

    private fun stopRingtone(){
        speak("Playing Ringtone")
        ringtone.stop()
    }

    private fun setAlarm(){

    }

    private fun circularRevealActivity() {
        val cx = binding.assistantLayout.right - getDips(44)
        val cy = binding.assistantLayout.bottom - getDips(44)

        val finalRadius = Math.max(
            binding.assistantLayout.width,
            binding.assistantLayout.height,

        )

        val circularReveal = ViewAnimationUtils.createCircularReveal(
            binding.assistantLayout,
            cx,
            cy,
            0f,
            finalRadius.toFloat()
        )

        circularReveal.duration = 500
        binding.assistantLayout.visibility = View.VISIBLE
        circularReveal.start()
    }

    private fun getDips(dps: Int): Int {
        val resources : Resources = resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dps.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val cx = binding.assistantLayout.width - getDips(44)
            val cy = binding.assistantLayout.height - getDips(44)
            val finalRadius = Math.max(
                binding.assistantLayout.width,
                binding.assistantLayout.height
            )
            val circularReveal = ViewAnimationUtils.createCircularReveal(
                binding.assistantLayout,
                cx,
                cy,
                finalRadius.toFloat(),0f
            )

            circularReveal.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    binding.assistantLayout.visibility = View.INVISIBLE
                    finish()
                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationRepeat(animation: Animator?) {

                }

            })

            circularReveal.duration = 500
            circularReveal.start()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
        speechRecognizer.cancel()
        speechRecognizer.destroy()
    }
}