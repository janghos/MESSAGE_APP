package com.jangho.mesagedev

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GroupListAdapter(private val items: List<String>, groupAddMode : Boolean) : RecyclerView.Adapter<GroupListAdapter.ViewHolder>() {


    interface OnGroupSelectedListener {
        fun onGroupSelected(groupList : String)
    }

    private var onGroupSelectedListener : OnGroupSelectedListener? = null

    private val addMode = groupAddMode
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.checkBox)
        val textView: TextView = view.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val checkedItems = mutableListOf<String>()
        holder.textView.text = items[position]
        // 체크박스 클릭 이벤트 처리 등을 여기에서 구현할 수 있습니다.
        if(!addMode){
            holder.checkBox.visibility = View.GONE
        }

        val item = items[position]
        holder.textView.text = item
        // 클릭 리스너를 설정합니다.

        holder.checkBox.setOnClickListener {
            // 리스너를 통해 선택된 그룹 이름 전달
            onGroupSelectedListener?.onGroupSelected(item.split(" / ")[0])
        }

    }

    override fun getItemCount(): Int = items.size

    fun setOnCheckListener(listener: OnGroupSelectedListener){
        onGroupSelectedListener = listener
    }
}