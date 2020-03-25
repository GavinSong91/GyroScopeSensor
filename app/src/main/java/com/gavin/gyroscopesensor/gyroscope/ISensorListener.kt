package com.gavin.gyroscopesensor.gyroscope

/**
 * Author:     gavinsong
 * Date:       2020/3/25
 * Description:坐标参考http://www.cnblogs.com/octobershiner/archive/2011/11/06/2237880.html
 *----------------------------------------------------------------------------------
 */
interface ISensorListener {
    /**
     * @param horizontalShift 在X轴上的加速度，用于计算垂直位移
     * @param verticalShift   在Y轴上的加速度，用于计算水平位移
     */
    fun onGyroScopeChange(horizontalShift: Float, verticalShift: Float)
}