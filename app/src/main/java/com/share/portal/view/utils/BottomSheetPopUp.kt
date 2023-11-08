package com.share.portal.view.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.share.portal.databinding.BottomsheetWarningBinding


class BottomSheetPopUp(context: Context): BottomSheetDialogFragment() {
  private var title: String = context.getString(com.share.portal.R.string.warning_general_title)
  private var content: CharSequence = context.getString(com.share.portal.R.string.warning_general_content)
  private var img: Int = com.share.portal.R.drawable.ic_folder
  private var onDismiss: (() -> Unit)? = null

  fun addOnDismissListener(callback: () -> Unit) {
    onDismiss = callback
  }

  override fun getTheme(): Int = android.R.style.Theme_Light_Panel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View = BottomsheetWarningBinding.inflate(inflater).root

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setStyle(DialogFragment.STYLE_NO_FRAME, com.share.portal.R.style.bottom_sheet_dialog)
    loadArguments()
    setupView(view)
  }

  private fun loadArguments() {
    arguments?.let {
      img = it.getInt(DIALOG_IMG, 0)
      title = it.getString(DIALOG_TITLE, "")
      content = it.getCharSequence(DIALOG_DESC, "")
    }
  }

  private fun setupView(view: View) {
    BottomsheetWarningBinding.bind(view).run {
      val dialogFrag = view.parent as View
      dialogFrag.layoutParams = CoordinatorLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
      )

      ivLogo.setImageResource(img)
      tvTitle.text = title
      tvContent.text = content
    }
  }

  override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)
    onDismiss?.invoke()
  }

  companion object {
    private const val DIALOG_TAG = "POPUP"
    private const val DIALOG_IMG = "DIALOG_IMG"
    private const val DIALOG_TITLE = "DIALOG_TITLE"
    private const val DIALOG_DESC = "DIALOG_DESC"

    fun newDialog(
      fragmentManager: FragmentManager,
      context: Context,
      @DrawableRes image: Int,
      title: String,
      content: CharSequence,
      onDismiss: (() -> Unit)? = null
    ) {
      val dialog = BottomSheetPopUp(context)
      val bundle = Bundle().apply {
        putInt(DIALOG_IMG, image)
        putString(DIALOG_TITLE, title)
        putCharSequence(DIALOG_DESC, content)
      }
      dialog.arguments = bundle
      dialog.addOnDismissListener { onDismiss?.invoke() }

      fragmentManager.findFragmentByTag(DIALOG_TAG)?.let {
        (it as BottomSheetDialogFragment).dismiss()
      }

      dialog.show(fragmentManager, DIALOG_TAG)
    }
  }
}