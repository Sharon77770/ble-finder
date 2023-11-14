package kr.co.hanbit.bletest

import android.app.Application
import android.bluetooth.*
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kr.co.hanbit.bletest.databinding.ActivityFindBtDvBinding
import java.util.*
import kotlin.math.roundToInt

class FindBtDvActivity : AppCompatActivity() {



    lateinit var binding: ActivityFindBtDvBinding



    var btAdapter : BluetoothAdapter? = null
    var btManager : BluetoothManager? = null



    lateinit var targetDv: BluetoothDevice
    lateinit var targetDvGatt: BluetoothGatt
    var currDis: Double = 0.0



    val tm = Timer()



    val kalmanFilter = KalmanFilter()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindBtDvBinding.inflate(layoutInflater)
        setContentView(binding.root)


        btManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = BluetoothAdapter.getDefaultAdapter()


        targetInit()
        raiderAnimation()
    }


    override fun finish() {
        tm.cancel()
        tm.purge()
        targetDvGatt.close()
        super.finish()
    }



    fun rssi2Dis(rssi: Int) : Double{
        return (((Math.pow(10.0,((-10-rssi)/20.0)) * 10.0) / 3.0) * 10.0).roundToInt() / 10.0
    }




    fun targetInit(){
        targetDv = TargetData.btDevice

        binding.targetDvName.text = targetDv.name

        targetDvGatt = targetDv.connectGatt(this, false, object:BluetoothGattCallback(){
            override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
                val predRssi = kalmanFilter.getPredictedRssi(rssi)
                currDis = rssi2Dis(predRssi)
            }
        })

        tm.schedule(object : TimerTask(){
            override fun run() {
                targetDvGatt.readRemoteRssi()
                if(btManager!!.getConnectionState(targetDv, BluetoothProfile.GATT_SERVER) ==
                                            BluetoothProfile.STATE_DISCONNECTED){

                    runOnUiThread {
                        showExitDialog("블루투스 연결 끊김", "해당 기기와 연결이 끊어졌습니다.")
                    }
                    tm.cancel()
                }
                runOnUiThread{
                    binding.distanceText.text = currDis.toString() + "M"
                }
            }
        }, 0, 500)
    }




    fun raiderAnimation(){
        val tm1 = Timer()
        val tm2 = Timer()
        val tm3 = Timer()
        val tm4 = Timer()

        tm1.schedule(object: TimerTask() {
            override fun run() {
                runOnUiThread{
                    binding.imageView5.visibility = View.GONE
                    binding.imageView6.visibility = View.GONE
                    binding.imageView7.visibility = View.GONE
                }
            }
        }, 0, 3200)

        tm2.schedule(object: TimerTask() {
            override fun run() {
                runOnUiThread {
                    binding.imageView5.visibility = View.VISIBLE
                }
            }
        }, 800, 3200)

        tm3.schedule(object: TimerTask() {
            override fun run() {
                runOnUiThread {
                    binding.imageView6.visibility = View.VISIBLE
                }
            }
        }, 1600, 3200)

        tm4.schedule(object: TimerTask() {
            override fun run() {
                runOnUiThread {
                    binding.imageView7.visibility = View.VISIBLE
                }
            }
        }, 2400, 3200)
    }



    fun showExitDialog(title: String, msg: String) {
        val dlg = AlertDialog.Builder(this@FindBtDvActivity)
        dlg.setTitle(title) //제목

        dlg.setMessage(msg) // 메시지

        dlg.setPositiveButton(
            "뒤로가기",
            DialogInterface.OnClickListener { dialog, which -> //토스트 메시지
                finish()
            })

        dlg.show()
    }

}