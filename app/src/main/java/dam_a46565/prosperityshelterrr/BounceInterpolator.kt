package dam_a46565.prosperityshelterrr

import android.view.animation.Interpolator

class BounceInterpolator : Interpolator {

    var myAmplitude : Double = 1.0
    var myFrequency : Double = 10.0

    constructor(amplitude: Double, frequency : Double){
        myAmplitude = amplitude
        myFrequency = frequency
    }

    override fun getInterpolation(time : Float): Float {
        return ((-1*Math.pow(Math.E, -time/myAmplitude)* Math.cos(myFrequency*time)+1).toFloat())
    }

}