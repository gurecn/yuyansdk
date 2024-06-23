package com.yuyan.imemodule.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.VectorDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.LauncherModel
import com.yuyan.imemodule.callback.OnRecyclerItemClickListener
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.data.theme.ThemeManager.activeTheme
import com.yuyan.imemodule.entity.ClipBoardDataBean
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.utils.DevicesUtils.dip2px
import com.yuyan.imemodule.utils.DevicesUtils.px2dip
import com.yuyan.imemodule.utils.LogUtil.d
import splitties.views.dsl.core.margin

/**
 * 剪切板界面适配器
 */
class ClipBoardAdapter(context: Context, datas: MutableList<ClipBoardDataBean>) :
    RecyclerView.Adapter<ClipBoardAdapter.SymbolHolder>() {
    private var mDatas : MutableList<ClipBoardDataBean>
    private val mContext: Context
    private var textColor: Int
    private var mOnItemClickListener: OnRecyclerItemClickListener? = null
    fun setOnItemClickLitener(mOnItemClickLitener: OnRecyclerItemClickListener?) {
        mOnItemClickListener = mOnItemClickLitener
    }

    init {
        d("CandidatesAdapter", "CandidatesAdapter")
        mDatas = datas
        val theme = activeTheme
        textColor = theme.keyTextColor
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolHolder {
        val mContainer = LinearLayout(mContext)
        mContainer.gravity = Gravity.CENTER_VERTICAL
        val marginValue = dip2px(3)
        mContainer.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(marginValue*2, marginValue, marginValue*2, marginValue)
        }
        val paddingValue = dip2px(5)
        mContainer.setPadding(paddingValue, paddingValue, paddingValue ,paddingValue)
        mContainer.background = GradientDrawable().apply {
            setColor(activeTheme.keyBackgroundColor)
            setShape(GradientDrawable.RECTANGLE)
            setCornerRadius(ThemeManager.prefs.keyRadius.getValue().toFloat()) // 设置圆角半径
        }

        val viewContext = TextView(mContext)
        viewContext.id = R.id.clipboard_adapter_content
        viewContext.maxLines = 3
        viewContext.ellipsize = TextUtils.TruncateAt.END
        viewContext.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
            margin = marginValue
        }
        val viewRightBtn = ImageView(mContext)
        viewRightBtn.id = R.id.clipboard_adapter_delete
        viewRightBtn.isClickable = true
        viewRightBtn.setEnabled(true)
        viewRightBtn.setImageResource(R.drawable.ic_baseline_delete)
        viewRightBtn.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT, 0f).apply {
            margin = marginValue
        }
        mContainer.addView(viewContext)
        mContainer.addView(viewRightBtn)
        return SymbolHolder(mContainer)
    }

    override fun onBindViewHolder(holder: SymbolHolder, position: Int) {
        val dataBean = mDatas[position]
        holder.textView.text = dataBean.copyContent
        holder.textView.setOnClickListener { view: View? ->
            mOnItemClickListener!!.onItemClick(this@ClipBoardAdapter, view, position)
        }
        holder.deleteView.setOnClickListener{ _: View? ->
            LauncherModel.instance.mClipboardDao?.deleteClipboard(dataBean)
            mDatas.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    inner class SymbolHolder(view: LinearLayout) : RecyclerView.ViewHolder(view) {
        var textView: TextView
        var deleteView: ImageView
        init {
            textView = view.findViewById(R.id.clipboard_adapter_content)
            textView.setTextColor(textColor)
            textView.textSize = px2dip(EnvironmentSingleton.instance.candidateTextSize.toFloat()).toFloat()
            deleteView = view.findViewById(R.id.clipboard_adapter_delete)
            (deleteView.getDrawable() as VectorDrawable).setTint(textColor)
        }
    }
}
