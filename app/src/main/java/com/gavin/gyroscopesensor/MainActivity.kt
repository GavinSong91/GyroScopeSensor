package com.gavin.gyroscopesensor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gavin.gyroscopesensor.gyroscope.GyroScopeSensorHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var sensorHelper: GyroScopeSensorHelper?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewList()
    }

    private fun initViewList(){
        var viewList = arrayListOf<AnimBean>()
        viewList.add(AnimBean(view1, 20, 20))
        viewList.add(AnimBean(avatar4, 15, 15))
        viewList.add(AnimBean(avatar5, 30, 30))
        viewList.add(AnimBean(view2, 25, 25))
        viewList.add(AnimBean(avatar3, 50, 50))
        viewList.add(AnimBean(avatar1, 45, 45))
        viewList.add(AnimBean(avatar2, 35, 35))
        viewList.add(AnimBean(avatar6, 45, 45))
        viewList.add(AnimBean(view4, 15, 15))
        viewList.add(AnimBean(view3, 25, 25))
        sensorHelper = GyroScopeSensorHelper(this,viewList, 20f)
    }

    override fun onResume() {
        super.onResume()
        sensorHelper?.start()
    }

    override fun onPause() {
        super.onPause()
        sensorHelper?.stop()
    }
}
