package com.cyuan.bimibimi.ui.download.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.core.utils.FileUtils
import com.cyuan.bimibimi.model.DownloadTaskInfo

class DownloadingTaskAdapter :
    RecyclerView.Adapter<DownloadingTaskAdapter.DownloadingTaskViewHolder>() {

    private val mDownloadTasks = ArrayList<DownloadTaskInfo>()
    private lateinit var mContext: Context
    private var mOnItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadingTaskViewHolder {
        mContext = parent.context
        return DownloadingTaskViewHolder.createViewHolder(parent.context, parent)
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

    override fun onBindViewHolder(holder: DownloadingTaskViewHolder, position: Int) {
        val taskInfo = mDownloadTasks[position]
        Glide.with(mContext).load(taskInfo.coverUrl).placeholder(R.drawable.ic_default_grey).into(holder.mImageView)
        var received = 0L
        var total = 0L
        if (taskInfo.receiveSize != "0") {
            received = taskInfo.receiveSize.toLong()
        }

        if (taskInfo.totalSize.isNotEmpty() && taskInfo.totalSize != "0") {
            total = taskInfo.totalSize.toLong()
        }
        val progress = (received * 1.0f / total * 1.0f * 100).toInt()
        holder.mProgressBar.progress = progress

        val fileSize: String = FileUtils.convertFileSize(total)
        val receivedSize: String = FileUtils.convertFileSize(received)

        var speed = "0"

        if (taskInfo.speed.isNotEmpty()) {
            speed = taskInfo.speed + "/s"
        }
        holder.mTitleView.text = taskInfo.title
        holder.mSpeedView.text = ""
        when (taskInfo.taskStatus) {
            -1 -> {
                holder.mStatusView.text = "正在连接资源，请稍等"
            }
            1 -> {
                holder.mStatusView.text = "正在下载 ${progress}%"
                holder.mSpeedView.text = speed
            }
            0, 4 -> {
                holder.mStatusView.text = "已暂停，点击开始"
            }
            2 -> {
                holder.mStatusView.text = "下载完成"
            }
            3 -> {
                holder.mStatusView.text = "下载失败, 点击重试"
            }
        }

        holder.itemView.setOnClickListener {
            mOnItemClickListener?.onClick(taskInfo)
        }
    }

    interface OnItemClickListener {
        fun onClick(task: DownloadTaskInfo)
    }



    class DownloadingTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mImageView = itemView.findViewById<ImageView>(R.id.imageView)
        val mTitleView = itemView.findViewById<TextView>(R.id.title)
        val mStatusView = itemView.findViewById<TextView>(R.id.status)
        val mSpeedView: TextView = itemView.findViewById(R.id.speed)
        val mProgressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)

        companion object {
            fun createViewHolder(context: Context, parent: ViewGroup): DownloadingTaskViewHolder {
                val itemView =
                    LayoutInflater.from(context).inflate(R.layout.download_task_item, parent, false)
                return DownloadingTaskViewHolder(itemView)
            }
        }
    }
}