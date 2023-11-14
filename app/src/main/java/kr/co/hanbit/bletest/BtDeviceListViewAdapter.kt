package kr.co.hanbit.bletest

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.res.ResourcesCompat
import kr.co.hanbit.bletest.databinding.ListViewItemBinding

class BtDeviceListViewAdapter (val context: Context, val dvList:ArrayList<BluetoothDevice>,
                               val btManager: BluetoothManager) : BaseAdapter() {

    lateinit var binding: ListViewItemBinding

    override fun getView(position: Int, covertView: View?, parent: ViewGroup?): View {
        binding = ListViewItemBinding.inflate(LayoutInflater.from(context))

        val dv = dvList[position]
        val gatt = dv.connectGatt(context, false, object : BluetoothGattCallback(){})

        binding.dvName.text = dv.name
        binding.macAd.text = dv.address

        if(btManager!!.getConnectionState(dv, BluetoothProfile.GATT_SERVER)
            == BluetoothProfile.STATE_DISCONNECTED) {
            binding.connState.text = "\uD83D\uDD34"
        }
        else{
            binding.connState.text = "\uD83D\uDFE2"
        }

        //gatt.close()

        return binding.root
    }
    override fun getItem(position: Int): Any {
        return dvList[position]
    }
    override fun getCount(): Int {
        return dvList.size
    }
    override fun getItemId(position: Int): Long {
        return 0
    }

}