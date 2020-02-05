package com.thul.biometriclibrary

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView


class BiometricDialogV23 : Dialog, View.OnClickListener {
    private var mcontext: Context? = null
    private var btnCancel: Button? = null
    private var imgLogo: ImageView? = null
    private var titleView: LinearLayout? = null
    private var itemTitle: TextView? = null
    private var screenvalue: String? = null
    private var titleBarBool: Boolean? = false
    private var itemDescription: TextView? = null
    private var itemSubtitle: TextView? = null
    private var itemStatus: TextView? = null
    private var biometricCallback: BiometricCallback? = null

    constructor(context: Context) : super(context) {
        mcontext = context.applicationContext
        setDialogView()
    }

    constructor(context: Context, biometricCallback: BiometricCallback?) : super(context) {
        mcontext = context.applicationContext
        this.biometricCallback = biometricCallback
        setDialogView()
    }

    constructor(context: Context, theme: Int) : super(context, theme) {}
    protected constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?) : super(context, cancelable, cancelListener) {}

    private fun setDialogView() {
        val bottomSheetView = layoutInflater.inflate(R.layout.fingerprint_layout_dialog, null)
        setCancelable(false)
        setContentView(bottomSheetView)
        btnCancel = findViewById(R.id.btn_cancel)
        btnCancel!!.setOnClickListener(this)
        imgLogo = findViewById(R.id.img_logo)
        titleView = findViewById(R.id.title_view)
        itemTitle = findViewById(R.id.item_title)
        itemStatus = findViewById(R.id.item_status)
        itemSubtitle = findViewById(R.id.item_subtitle)
        itemDescription = findViewById(R.id.item_description)
        updateLogo()
    }

    fun setTitle(title: String?) {
        itemTitle!!.text = title
    }

    fun setScreen(screen: String?) {
        screenvalue = screen
        if(screenvalue!!.equals(R.string.dialog_fullscreen)){
            getWindow()?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT)
        }else if(screenvalue!!.equals(R.string.dialog_bottom)){
            windowBottomPosition()
        }
    }

    fun updateStatus(status: String?) {
        itemStatus!!.text = status
    }

    fun setTitleBarText(titleBar: Boolean?) {
        titleBarBool = titleBar
        if(titleBarBool == false){
            imgLogo?.visibility = View.GONE
            titleView?.visibility = View.GONE
        }
    }

    fun setSubtitle(subtitle: String?) {
        itemSubtitle!!.text = subtitle
    }

    fun setDescription(description: String?) {
        itemDescription!!.text = description
    }

    fun setButtonText(negativeButtonText: String?) {
        btnCancel!!.text = negativeButtonText
    }

    private fun updateLogo() {
        try {
            val drawable = getContext().packageManager.getApplicationIcon(context!!.packageName)
            imgLogo!!.setImageDrawable(drawable)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(view: View) {
        dismiss()
        biometricCallback!!.onAuthenticationCancelled()
    }

    fun windowBottomPosition(){
        val window: Window? = window
        val param: WindowManager.LayoutParams = window!!.getAttributes()
        param.gravity = Gravity.BOTTOM
        window?.setAttributes(param)
        window?.setWindowAnimations(R.style.DialogAnimation)
    }
}