package com.gavin.gyroscopesensor.gyroscope

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import java.lang.ref.WeakReference

/**
 * Author:     gavinsong
 * Date:       2020/3/25
 * Description:陀螺仪传感器监听
 *----------------------------------------------------------------------------------
 */
class GyroScopeSensorListener constructor(context: Context): SensorEventListener {

    private val mContext: WeakReference<Context> = WeakReference(context)
    private val sensorManager: SensorManager?
    private val sensor: Sensor?
    init {
        sensorManager = mContext.get()?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    }

    // 手机上一个位置时重力感应坐标
    private var sensorListener: ISensorListener? = null

    fun setSensorListener(sensorListener: ISensorListener) {
        this.sensorListener = sensorListener
    }

    fun start() {
        if (sensor != null){
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        if (sensor != null){
            sensorManager?.unregisterListener(this)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[SensorManager.DATA_X]
        val y = event.values[SensorManager.DATA_Y]

        //为什么要互换 x,y；是因为陀螺仪在y轴上的左右倾斜数据，最终会反应在图片在水平位置上的变化；同理x轴上下倾斜反应在垂直位置上
        //为调用端统一坐标，我们在这里就做了调换，调用端按照正常的坐标系使用即可
        if (sensorListener != null){
            sensorListener?.onGyroScopeChange(y, x)
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}