package vn.vmg.lendingapp.facechecklib.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView
import vn.vmg.lendingapp.facechecklib.R

class CustomDialogFaceSuccess(
    context: Context,
    des:String?="",
    negativeButton:String?="",
    positiveButton:String?="",
    private val onClickListener: OnClickListener
) :
    Dialog(context) {
    private var des = des
    private var negativeButton = negativeButton
    private var positiveButton = positiveButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog_success)
        setCanceledOnTouchOutside(true)
      var btnNegative = findViewById<TextView>(R.id.btnNegative)
      var btnPositive = findViewById<TextView>(R.id.btnNegative)
      var tv = findViewById<TextView>(R.id.tv)
        tv.text = des
        if (negativeButton.isNullOrEmpty()){
            btnNegative.visibility = View.GONE
        } else{
            btnNegative.visibility = View.VISIBLE
            btnNegative.text = negativeButton
        }
        btnNegative.setOnClickListener {
            onClickListener.onClickNegativeButton(this)
        }
        btnPositive.text = positiveButton
        btnPositive.setOnClickListener {
            onClickListener.onClickPositiveButton(this)
        }

    }

    interface OnClickListener {
        fun onClickPositiveButton(dialog: CustomDialogFaceSuccess)
        fun onClickNegativeButton(dialog: CustomDialogFaceSuccess)
    }
}