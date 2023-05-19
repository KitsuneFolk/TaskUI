package com.pandacorp.taskui.presentation.ui.settings.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pandacorp.taskui.R
import com.pandacorp.taskui.databinding.DialogListViewBinding
import com.pandacorp.taskui.presentation.ui.settings.ListAdapter
import com.pandacorp.taskui.presentation.ui.settings.ListItem
import com.pandacorp.taskui.presentation.utils.Constants

class DialogListView : CustomDialog() {

    private lateinit var binding: DialogListViewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = DialogListViewBinding.inflate(layoutInflater)

        val preferenceKey =
            requireArguments().getString(Constants.PreferenceKeys.preferenceBundleKey)

        binding.dialogListViewTitle.setText(
            when (preferenceKey) {
                Constants.PreferenceKeys.themesKey -> R.string.theme
                Constants.PreferenceKeys.languagesKey -> R.string.language
                else -> throw IllegalArgumentException("PreferenceKey = $preferenceKey")
            }
        )

        binding.dialogListViewCancel.setOnClickListener {
            dialog!!.cancel()
        }

        val itemsList: MutableList<ListItem> = when (preferenceKey) {
            Constants.PreferenceKeys.themesKey -> fillThemesList()
            Constants.PreferenceKeys.languagesKey -> fillLanguagesList()
            else -> throw IllegalArgumentException()

        }
        val adapter = ListAdapter(requireContext(), itemsList, preferenceKey).apply {
            setOnClickListener { _, listItem, _ ->
                sp.edit().putString(preferenceKey, listItem.value).apply()
                dialog!!.cancel()
                requireActivity().recreate()
            }
        }
        binding.dialogListViewListView.adapter = adapter

        return binding.root
    }

    private fun fillThemesList(): MutableList<ListItem> {
        val keysList = requireContext().resources.getStringArray(R.array.Themes_values)
        val titlesList = requireContext().resources.getStringArray(R.array.Themes)
        val drawablesList =
            requireContext().resources.obtainTypedArray(R.array.Themes_drawables)
        val themesList: MutableList<ListItem> = mutableListOf()
        repeat(keysList.size) { i ->
            themesList.add(
                ListItem(
                    keysList[i],
                    titlesList[i],
                    drawablesList.getDrawable(i)!!
                )
            )
        }
        drawablesList.recycle()
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
                    drawablesList.getDrawable(i)!!
                )
            )
        }
        drawablesList.recycle()
        return itemsList
    }

    companion object {
        fun newInstance(preferenceKey: String): DialogListView {
            val args = Bundle()
            args.putString(Constants.PreferenceKeys.preferenceBundleKey, preferenceKey)
            val dialog = DialogListView()
            dialog.arguments = args
            return dialog
        }
    }

}