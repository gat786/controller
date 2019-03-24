package `in`.webxstudio.batmobilecontroller

class SensorData(temperature:Float=0.toFloat(),humidity:Float=0.toFloat(),distance:Int=0){
    val baseUrl :String? = null
    var streamUrl:String? = null
        get() {return "$baseUrl+:8080/"}
}

class DirectionClass(direction: String? = null)

class Devices(val ip:String,val mac:String)