package com.dtdweb.exfilemanager

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
    }

    private var manager: UsbManager? = null

    private val usbReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (ACTION_USB_PERMISSION == intent.action) {
                synchronized(this) {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        device?.apply {

                            setMessage("-----------------")

                            setMessage("device Path = " + this.deviceName)

                            setMessage("listFile = " + File(this.deviceName).listFiles())
                            File(this.deviceName).listFiles()?.forEach { rootFiles ->
                                setMessage("filePath = " + rootFiles.path)
                            }

                        }
                    } else {
                        setMessage("-----------------")
                        setMessage("permission error .")
                    }
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        manager = getSystemService(Context.USB_SERVICE) as UsbManager

        val permissionIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_USB_PERMISSION), 0)
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        registerReceiver(usbReceiver, filter)

        exec.setOnClickListener {

            clearMessage()

            val usbDevice = manager!!.deviceList.values.firstOrNull()
            if (usbDevice != null) {
                setMessage("device Found")

                setMessage("device Path = " + usbDevice.deviceName)
                setMessage("listFile = " + File(usbDevice.deviceName).listFiles())

                File(usbDevice.deviceName).listFiles()?.forEach { rootFiles ->
                    setMessage("filePath = " + rootFiles.path)
                }

                manager!!.requestPermission(usbDevice, permissionIntent)

            } else {
                setMessage("device Not Found")
            }
        }

    }

    private fun setMessage(mess: String) {
        message.text = message.text.toString() + mess + "\n\n"
    }

    private fun clearMessage() {
        message.text = ""
    }

}
