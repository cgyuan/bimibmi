package com.cyuan.bimibimi.ui.download.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.model.DownloadTaskInfo

class DownloadedTaskAdapter :
    RecyclerView.Adapter<DownloadedTaskAdapter.DownloadedTaskViewHolder>() {

    private val mDownloadTasks = ArrayList<DownloadTaskInfo>()
    private lateinit var mContext: Context
    private var mOnItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadedTaskViewHolder {
        mContext = parent.context
        return DownloadedTaskViewHolder.createViewHolder(parent.context, parent)
    }

    override fun getItemCount() = mDownloadTasks.size

    fun refresh(tasks: MutableList<DownloadTaskInfo>?) {
        mDownloadTasks.clear()
        mDownloadTasks.addAll(tasks!!)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mOnItemClickListener = listener
    }

    override fun onBindViewHolder(holder: DownloadedTaskViewHolder, position: Int) {
        val taskInfo = mDownloadTasks[position]
        Glide.with(mContext).load(taskInfo.coverUrl).placeholder(R.drawable.ic_default_grey).into(holder.mImageView)

        holder.mTitleView.text = "【${taskInfo.title}】${taskInfo.episodeName}"
        holder.itemView.setOnClickListener {
            mOnItemClickListener?.onClick(taskInfo)
        }
    }

    interface OnItemClickListener {
        fun onClick(task: DownloadTaskInfo)
    }



    class DownloadedTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mImageView = itemView.findViewById<ImageView>(R.id.imageView)
        val mTitleView = itemView.findViewById<TextView>(R.id.title)

        companion object {
            fun createViewHolder(context: Context, parent: ViewGroup): DownloadedTaskViewHolder {
                val itemView =
                    LayoutInflater.from(context).inflate(R.layout.downloaded_task_item, parent, false)
                return DownloadedTaskViewHolder(itemView)
            }
        }
    }
}