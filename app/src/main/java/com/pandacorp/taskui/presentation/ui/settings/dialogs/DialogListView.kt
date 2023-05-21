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
    companion object {
        fun newInstance(preferenceKey: String): DialogListView {
            return DialogListView().apply {
                arguments = Bundle().apply {
                    putString(Constants.PreferenceKeys.preferenceBundleKey, preferenceKey)
                }
            }
        }
    }

    private val themesKeys by lazy {
        requireContext().resources.getStringArray(R.array.Themes_values)
    }
    private val themesTitles by lazy {
        requireContext().resources.getStringArray(R.array.Themes)
    }
    private val languagesKeys by lazy {
        requireContext().resources.getStringArray(R.array.Languages_values)
    }
    private val languagesTitles by lazy {
        requireContext().resources.getStringArray(R.array.Languages)
    }

    private var _binding: DialogListViewBinding? = null
    private val binding: DialogListViewBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = DialogListViewBinding.inflate(layoutInflater)

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
            else -> throw IllegalArgumentException("Unexpected value = $preferenceKey")
        }
        val adapter = ListAdapter(requireContext(), itemsList, preferenceKey).apply {
            setOnClickListener {
                if (it.value == sp.getString(preferenceKey, "")) return@setOnClickListener
                sp.edit().putString(preferenceKey, it.value).apply()
                dialog!!.cancel()
                requireActivity().recreate()
            }
        }
        binding.dialogListViewListView.adapter = adapter

        return binding.root
    }

    private fun fillThemesList(): MutableList<ListItem> {
        val drawablesList =
            requireContext().resources.obtainTypedArray(R.array.Themes_drawables)
        val themesList: MutableList<ListItem> = mutableListOf()
        repeat(themesKeys.size) { i ->
            themesList.add(
                ListItem(
                    themesKeys[i],
                    themesTitles[i],
                    drawablesList.getDrawable(i)!!
                )
            )
        }
        drawablesList.recycle()
        return themesList
    }

    private fun fillLanguagesList(): MutableList<ListItem> {
        val drawablesList =
            requireContext().resources.obtainTypedArray(R.array.Languages_drawables)
        val itemsList: MutableList<ListItem> = mutableListOf()
        repeat(languagesKeys.size) { i ->
            itemsList.add(
                ListItem(
                    languagesKeys[i],
                    languagesTitles[i],
                    drawablesList.getDrawable(i)!!
                )
            )
        }
        drawablesList.recycle()
        return itemsList
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}