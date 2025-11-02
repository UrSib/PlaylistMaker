package com.practicum.playlistmaker.sharing.data

import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator


class ExternalNavigatorImpl: ExternalNavigator {
val application = Creator.provideApplication()

    override fun shareLink(link:String){
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        sharingIntent.setType("text/plain")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, link)
        val chooser = Intent.createChooser(sharingIntent, application.getString(R.string.share_app))
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(chooser)

    }
   override fun openLink(link:String){
        val agreementIntent = Intent(Intent.ACTION_VIEW)
        agreementIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        agreementIntent.setData(Uri.parse(link))
        application.startActivity(agreementIntent)
    }
    override fun openEmail(email: String){
        val uri = Uri.parse("mailto:${email}")
            .buildUpon()
            .appendQueryParameter("subject", application.getString(R.string.subject_support))
            .appendQueryParameter("body", application.getString(R.string.body_support))
            .build()
        val supportIntent = Intent(Intent.ACTION_SENDTO, uri)
        supportIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(supportIntent)
    }
}