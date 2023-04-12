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
import com.pandacorp.taskui.presentation.utils.Constans

class ListAdapter(
    context: Context, languagesList: MutableList<ListItem>, private val preferenceKey: String
) : ArrayAdapter<ListItem>(context, 0, languagesList) {
    companion object {
        private const val TAG = SettingsActivity.TAG
    }
    
    private var onListItemClickListener: OnListItemClickListener? = null
    
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        }
        val listItem = getItem(position)!!
        
        val layout = view!!.findViewById<ConstraintLayout>(R.id.ListItemLayout)
        val textView = view.findViewById<TextView>(R.id.ListItemTextView)
        val cardView = view.findViewById<CardView>(R.id.ListItemCardView)
        val imageView = view.findViewById<ImageView>(R.id.ListItemImageView)
        
        layout.setOnClickListener { v ->
            onListItemClickListener?.onClick(v, listItem, position)
        }
        
        textView.text = listItem.title
        imageView.setImageDrawable(listItem.drawable)
        // make imageview rounded if key == theme
        if (preferenceKey == Constans.PreferencesKeys.themesKey) {
            cardView.radius = 80f
        }
        return view
    }
    
    fun setOnClickListener(onListItemClickListener: OnListItemClickListener) {
        this.onListItemClickListener = onListItemClickListener
    }
    
    
    fun interface OnListItemClickListener {
        fun onClick(view: View, listItem: ListItem, position: Int)
    }
}
    
