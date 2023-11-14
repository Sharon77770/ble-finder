package kr.co.hanbit.bletest

import android.Manifest
import android.R
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.get
import kr.co.hanbit.bletest.databinding.ActivityMainBinding
import java.util.*
import java.util.concurrent.BlockingDeque
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    val PERMISSIONS = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN)

    val REQUEST_PERMISSION_CODE = 1



    var btAdapter : BluetoothAdapter? = null
    var btManager : BluetoothManager? = null



    lateinit var binding: ActivityMainBinding



    var dvList = ArrayList<BluetoothDevice>()
    lateinit var dvListViewAdapter: BtDeviceListViewAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = BluetoothAdapter.getDefaultAdapter()

        if (!checkPermissions()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE)
        }


        if (!checkPermissions()) {
            showExitDialog("블루투스 권한이 없습니다",
            "앱에 블루투스 권한이 추가되어 있지 않습니다.\n설정에서 앱에 블루투스 권한을 추가해 주세요.")
        }
        else{
            run()
        }
    }





    fun run(){
        if(btAdapter == null){
            showExitDialog("블루투스를 지원하지 않는 기기입니다", "블루루투스 연결장치를 확인 할 수 없습니다." +
                    "\n해당기기가 블루투스 기능을 지원하는지 확인해주세요.")
        }
        else if(!btAdapter!!.isEnabled){
            showExitDialog("블루투스 기능이 비활성화 되어 있습니다.",
            "블루투스 기능을 활성화 한 뒤 다시 앱을 실행해주세요.")
        }
        else{
            dvListViewAdapter = BtDeviceListViewAdapter(this, dvList, btManager!!)
            binding.listView.adapter = dvListViewAdapter

            binding.listView.setOnItemClickListener { adapterView, view, i, l ->
                val dv = adapterView.getItemAtPosition(i) as BluetoothDevice
                val nextIntent = Intent(this, FindBtDvActivity::class.java)
                TargetData.btDevice = dv
                TargetData.targetIdx = i + 1
                startActivity(nextIntent)
            }

            showAllBtDv()

            binding.refreshBtn.setOnClickListener{
                showAllBtDv()
            }

            /*
            val tm = Timer()
            tm.schedule(object :TimerTask(){
                override fun run() {
                    runOnUiThread {
                        Log.d("my", "1")
                        showAllBtDv()
                    }
                }
            }, 0, 2000)
            */
        }
    }




    fun showAllBtDv(){
        dvList.clear()

        for(dv in btAdapter!!.bondedDevices){
            dvList.add(dv)
        }

        dvListViewAdapter.notifyDataSetChanged()
    }




    fun showExitDialog(title: String, msg: String) {
        val dlg = AlertDialog.Builder(this@MainActivity)
        dlg.setTitle(title) //제목

        dlg.setMessage(msg) // 메시지

        dlg.setPositiveButton(
            "앱 종료",
            DialogInterface.OnClickListener { dialog, which ->
                finishAffinity()
                System.runFinalization()
                System.exit(0)
            })

        dlg.show()
    }



    fun checkPermissions(): Boolean {
        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}