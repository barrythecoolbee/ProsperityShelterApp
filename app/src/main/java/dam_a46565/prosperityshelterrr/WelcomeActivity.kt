package dam_a46565.prosperityshelterrr

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        var mp : MediaPlayer = MediaPlayer.create(this, R.raw.naruto)

        var pawButton : Button = findViewById(R.id.WelcomeButton)
        pawButton.setOnClickListener(View.OnClickListener {
            mp.start();
            val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
            startActivity(intent)
        })
    }
}