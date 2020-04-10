package com.dtdweb.exfilemanager

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {

        private const val REQUEST_EXTERNAL_STORAGE: Int = 1001

        private const val REQUEST_OPEN_TREE: Int = 1002

        private val PERMISSIONS_STORAGE: Array<String> = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestExternalStoragePermission()

        exec.setOnClickListener {

            clearMessage()

            // Storage Access Framework APIを実行して外部ストレージのURIを取得する
            startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), REQUEST_OPEN_TREE)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_OPEN_TREE && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                // データ取得
                val docFile = DocumentFile.fromTreeUri(this, uri)!!
                setMessage("canRead = " + docFile.canRead())
                if (docFile.canRead()) {
                    setMessage("docFile.listFiles() = " + docFile.listFiles())
                    docFile.listFiles().forEach {
                        setMessage("fileName = " + it.name)
                        setMessage("filePath = " + it.uri.path)
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun requestExternalStoragePermission() {
        val perm = ContextCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (perm != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)
        }
    }

    private fun setMessage(mess: String) {
        message.text = message.text.toString() + mess + "\n\n"
    }

    private fun clearMessage() {
        message.text = ""
    }

}
