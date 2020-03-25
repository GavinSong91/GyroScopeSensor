package com.gavin.gyroscopesensor.gyroscope

import android.content.Context
import com.gavin.gyroscopesensor.AnimBean
import java.lang.ref.WeakReference

/**
 * Author:     gavinsong
 * Date:       2020/3/25
 * Description: 实现View对象，响应陀螺仪改变事件 达到控件随手机晃动的效果,
 *              注意传感器对手机电量的影响 在周期中注册、解除onResume onPause
 *----------------------------------------------------------------------------------
 */
class GyroScopeSensorHelper
    constructor(context: Context,var viewList:List<AnimBean>, var density: Float): ISensorListener {

    companion object {
        //默认0.02f在宽度填满屏幕的图片上，移动起来看着很舒服
        //FIXME 0.005
        private const val TRANSFORM_FACTOR = 0.02f
    }

    private var mTransformFactor = TRANSFORM_FACTOR
    private var mSensorListener: GyroScopeSensorListener? = null
    private var sensorDatas: List<SensorData>? = null//控件的各自移动位置数据

    init {
        val mContext: WeakReference<Context> = WeakReference(context)
        mSensorListener = GyroScopeSensorListener(mContext.get()!!)
        mSensorListener?.setSensorListener(this)
        initViewData()
    }

    fun initViewData(){
        //各个控件的X轴Y轴移动数据
        sensorDatas = arrayListOf(
            SensorData(0f, 0f), SensorData(0f, 0f), SensorData(0f, 0f),
            SensorData(0f, 0f), SensorData(0f, 0f), SensorData(0f, 0f),
            SensorData(0f, 0f), SensorData(0f, 0f), SensorData(0f, 0f),
            SensorData(0f, 0f)
        )
    }

    /**
     * 注册监听陀螺仪事件
     */
    fun start() {
        mSensorListener?.start()
    }

    /**
     * 监听陀螺仪事件耗电，因此在onPause里需要注销监听事件
     */
    fun stop() {
        mSensorListener?.stop()
    }


    /**
     * 设置移动的补偿变量，越高移动越快，标准参考[.TRANSFORM_FACTOR]
     */
    fun setTransformFactor(transformFactor: Float) {
        mTransformFactor = transformFactor
    }

    override fun onGyroScopeChange(horizontalShift: Float, verticalShift: Float) {

        if (viewList.isNotEmpty() && sensorDatas!!.isNotEmpty()){
            var coefficient = mTransformFactor * density//系数
            if (density == 2.0f) {//对分辨率低的系数调小点
                coefficient = mTransformFactor * 1.5f
            }
            for (i in viewList.indices){
                sensorDatas!![i].currentSensorX = horizontalShift
                sensorDatas!![i].currentSensorY = verticalShift
                viewList!![i].view.post {
                    viewList!![i].view.translationX = sensorDatas!![i].currentSensorX* coefficient * viewList!![i].transX
                    viewList!![i].view.translationY = sensorDatas!![i].currentSensorY* coefficient * viewList!![i].transY
                }
            }
        }
    }
}