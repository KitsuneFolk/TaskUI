package com.pandacorp.taskui.presentation.ui.settings.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.pandacorp.taskui.R
import com.pandacorp.taskui.databinding.DialogListViewBinding
import com.pandacorp.taskui.presentation.ui.settings.ListAdapter
import com.pandacorp.taskui.presentation.ui.settings.ListItem
import com.pandacorp.taskui.presentation.ui.settings.SettingsActivity
import com.pandacorp.taskui.presentation.utils.Constants

class DialogListView : CustomDialog() {
    
    private lateinit var binding: DialogListViewBinding
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        
        binding = DialogListViewBinding.inflate(layoutInflater)
        
        val preferenceKey =
            requireArguments().getString(Constants.PreferencesKeys.preferenceBundleKey)
        
        binding.dialogListViewTitle.setText(
                when (preferenceKey) {
                    Constants.PreferencesKeys.themesKey -> R.string.theme
                    Constants.PreferencesKeys.languagesKey -> R.string.language
                    else -> throw IllegalArgumentException("PreferenceKey = $preferenceKey")
                }
        )
        
        binding.dialogListViewCancel.setOnClickListener {
            dialog!!.cancel()
        }
        
        val itemsList: MutableList<ListItem> = when (preferenceKey) {
            Constants.PreferencesKeys.themesKey -> fillThemesList()
            Constants.PreferencesKeys.languagesKey -> fillLanguagesList()
            else -> throw IllegalArgumentException()
            
        }
        val adapter = ListAdapter(requireContext(), itemsList, preferenceKey)
        adapter.setOnClickListener { _, listItem, _ ->
            sp.edit().putString(preferenceKey, listItem.value).apply()
            dialog!!.cancel()
            requireActivity().setResult(AppCompatActivity.RESULT_OK)
            requireActivity().startActivity(Intent(context, SettingsActivity::class.java))
            requireActivity().finish()
            requireActivity().overridePendingTransition(0, 0)
        }
        binding.dialogListViewListView.adapter = adapter
        
        return binding.root
    }
    
    private fun fillThemesList(): MutableList<ListItem> {
        val keysList = requireContext().resources.getStringArray(R.array.Themes_values)
        val titlesList = requireContext().resources.getStringArray(R.array.Themes)
        val itemsList =
            requireContext().resources.obtainTypedArray(R.array.Themes_drawables)
        val themesList: MutableList<ListItem> = mutableListOf()
        repeat(keysList.size) { i ->
            themesList.add(
                    ListItem(
                            keysList[i],
                            titlesList[i],
                            itemsList.getDrawable(i)!!))
        }
        itemsList.recycle()
        return themesList
    }
    
    private fun fillLanguagesList(): MutableList<ListItem> {
        val keysList = requireContext().resources.getStringArray(R.array.Languages_values)
        val drawablesList =
            requireContext().resources.obtainTypedArray(R.array.Languages_drawables)
        val titlesList = requireContext().resources.getStringArray(R.array.Languages)
        val itemsList: MutableList<ListItem> = mutableListOf()
        repeat(keysList.size) { i ->
            itemsList.add(
                    ListItem(
                            keysList[i],
                            titlesList[i],
                            drawablesList.getDrawable(i)!!))
        }
        drawablesList.recycle()
        return itemsList
    }
    
    companion object {
        fun newInstance(preferenceKey: String): DialogListView {
            val args = Bundle()
            args.putString(Constants.PreferencesKeys.preferenceBundleKey, preferenceKey)
            val dialog = DialogListView()
            dialog.arguments = args
            return dialog
        }
    }
    
}