package aaa.ccc.gluttonoussnakegame

import android.app.Service
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private val handle = Handler()
    var snackSpeed = 300L
    private var level: Int by Delegates.observable(1) { _, _, newValue ->
        tv_lv.text = "Lv: $newValue"
    }

    private lateinit var mVibrator: Vibrator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        initListener()


        mVibrator = getSystemService(Service.VIBRATOR_SERVICE) as Vibrator

        tv_lv.text = "Lv: 1"
    }

    private val game = object : Runnable {
        override fun run() {
            gameView.refreshView()

            if (gameView.gameStatus != GameStatus.PLAY) {
                Log.e("QQQ", "你輸惹！")
                handle.removeCallbacksAndMessages(null)
                return
            }

            when (gameView.foodCount) {
                in 0 until 5 -> {
                    snackSpeed = 300
                    level = 1
                }
                in 5 until 10 -> {
                    snackSpeed = 250
                    level = 2
                }
                in 10 until 15 -> {
                    snackSpeed = 200
                    level = 3
                }
                in 15 until 20 -> {
                    snackSpeed = 150
                    level = 4
                }
                in 20 until 25 -> {
                    snackSpeed = 130
                    level = 5
                }
                in 25 until 30 -> {
                    snackSpeed = 100
                    level = 6
                }
                in 30 until 35 -> {
                    snackSpeed = 80
                    level = 7
                }
                in 35 until 40 -> {
                    snackSpeed = 60
                    level = 8
                }
                in 40 until 50 -> {
                    snackSpeed = 40
                    level = 9
                }
                in 50 until 100 -> {
                    snackSpeed = 30
                    level = 10
                }
            }

            handle.postDelayed(this, snackSpeed)
        }
    }

    private fun initListener() {

        up.setOnClickListener {
            if (gameView.control == Control.DOWN) {
                return@setOnClickListener
            }
            gameView.control = Control.UP
//            gameView.refreshView()

            mVibrator.vibrate(100L)
        }
        down.setOnClickListener {
            if (gameView.control == Control.UP) {
                return@setOnClickListener
            }
            gameView.control = Control.DOWN
//            gameView.refreshView()

            mVibrator.vibrate(100L)

        }

        left.setOnClickListener {
            if (gameView.control == Control.RIGHT) {
                return@setOnClickListener
            }
            gameView.control = Control.LEFT
//            gameView.refreshView()

            mVibrator.vibrate(100L)

        }
        right.setOnClickListener {
            if (gameView.control == Control.LEFT) {
                return@setOnClickListener
            }
            gameView.control = Control.RIGHT
//            gameView.refreshView()

            mVibrator.vibrate(100L)

        }

        start.setOnClickListener {
            gameView.gameReset()
            handle.removeCallbacksAndMessages(null)
            handle.post(game)
            mVibrator.vibrate(100L)

        }

        reset.setOnClickListener {
            handle.removeCallbacksAndMessages(null)
            gameView.gameReset()
            mVibrator.vibrate(100L)

        }

    }


    override fun onDestroy() {
        super.onDestroy()
        handle.removeCallbacksAndMessages(null)
    }

}
