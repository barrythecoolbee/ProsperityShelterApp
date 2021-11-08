package dam_a46565.prosperityshelterrr

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val mp : MediaPlayer = MediaPlayer.create(this, R.raw.naruto)

        val scaleDown1: Animation = AnimationUtils.loadAnimation(this, R.anim.scale_down1)
        val scaleDown2: Animation = AnimationUtils.loadAnimation(this, R.anim.scale_down2)

        val v1: View = findViewById(R.id.Circle)
        val v2: View = findViewById(R.id.Circle2)

        val interpolator = BounceInterpolator(0.2, 20.0)
        scaleDown1.setInterpolator(interpolator)
        scaleDown2.setInterpolator(interpolator)
        v1.startAnimation(scaleDown1)
        v2.startAnimation(scaleDown2)

        val introButton : Button = findViewById(R.id.IntroButton)
        introButton.setOnClickListener(View.OnClickListener {
            mp.start();
            val intent = Intent(this@IntroActivity, LoginActivity::class.java)
            startActivity(intent)
        })


    }
}