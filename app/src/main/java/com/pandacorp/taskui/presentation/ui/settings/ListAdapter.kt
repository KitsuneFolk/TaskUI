package com.pandacorp.taskui.presentation.ui.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.pandacorp.taskui.R
import com.pandacorp.taskui.presentation.utils.Constants

class ListAdapter(
    context: Context, languagesList: MutableList<ListItem>, private val preferenceKey: String
) : ArrayAdapter<ListItem>(context, 0, languagesList) {
    private var onListItemClickListener: OnListItemClickListener? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)!!
        val listItem = getItem(position)!!
        view.apply {
            findViewById<ImageView>(R.id.ListItemImageView).apply {
                setImageDrawable(listItem.drawable)
            }
            findViewById<ConstraintLayout>(R.id.ListItemLayout).apply {
                setOnClickListener {
                    onListItemClickListener?.onClick(listItem)
                }
            }
            findViewById<TextView>(R.id.ListItemTextView).apply {
                text = listItem.title
            }
            findViewById<CardView>(R.id.ListItemCardView).apply {
                if (preferenceKey == Constants.PreferenceKeys.themesKey) radius = 80f
            }
        }
        return view
    }

    fun setOnClickListener(onListItemClickListener: OnListItemClickListener) {
        this.onListItemClickListener = onListItemClickListener
    }

    fun interface OnListItemClickListener {
        fun onClick(listItem: ListItem)
    }
}
    
