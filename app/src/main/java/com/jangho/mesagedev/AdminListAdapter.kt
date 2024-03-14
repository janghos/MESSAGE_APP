package com.jangho.mesagedev

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class AdminListAdapter(private val contacts: List<LoginFragment.User>, activity: MainActivity) : RecyclerView.Adapter<AdminListAdapter.ContactViewHolder>() {

    val activityContext = activity
    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content: TextView = itemView.findViewById(R.id.tv_list_number)
        val toggle : ToggleButton = itemView.findViewById(R.id.tb_permission)
        val deleteBtn: TextView = itemView.findViewById(R.id.btn_delete)
        val tvAdmin : TextView = itemView.findViewById(R.id.tv_admin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_group_admin_list, parent, false)
        return ContactViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.content.text = contact.id
        if(contact.id == activityContext.getSaveString("id")) {
            holder.deleteBtn.visibility = View.GONE
            holder.toggle.visibility = View.GONE
            holder.tvAdmin.visibility = View.VISIBLE
        }
        holder.toggle.isChecked = contact.enable!!
        holder.toggle.setOnCheckedChangeListener { _, isChecked ->
            val contactId = contacts[position].id
            val query = activityContext.returnFirebase().getReference("users").orderByChild("id").equalTo(contactId)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val userRef = snapshot.ref
                        userRef.child("enable").setValue(isChecked)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException())
                }
            })
        }
        holder.deleteBtn.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(activityContext)
            alertDialogBuilder.setMessage("정말 삭제하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("확인") { dialog, _ ->
                    // 확인 버튼을 눌렀을 때 아이템을 삭제합니다.
                    val contactId = contacts[position].id
                    val query = activityContext.returnFirebase().getReference("users").orderByChild("id").equalTo(contactId)

                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (snapshot in dataSnapshot.children) {
                                snapshot.ref.removeValue()
                            }
                            holder.toggle.visibility = View.GONE
                            holder.content.visibility = View.GONE
                            holder.deleteBtn.visibility = View.GONE
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.e(TAG, "onCancelled", databaseError.toException())
                        }
                    })

                    dialog.dismiss()
                }
                .setNegativeButton("취소") { dialog, _ ->
                    dialog.dismiss()
                }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }
}