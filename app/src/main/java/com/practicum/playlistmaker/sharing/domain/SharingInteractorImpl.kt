package com.practicum.playlistmaker.sharing.domain



class SharingInteractorImpl(val externalNavigator: ExternalNavigator) : SharingInteractor {

    override fun shareApp(url: String) {
        externalNavigator.shareLink(getShareAppLink(url))
    }

    override fun openTerms(url: String) {
        externalNavigator.openLink(getTermsLink(url))
    }

    override fun openSupport(email: String) {
        externalNavigator.openEmail(getSupportEmailData(email))
    }

    private fun getShareAppLink(url: String): String {
        return url
    }

    private fun getSupportEmailData(email: String): String {
        return email
    }

    private fun getTermsLink(url: String): String {
        return url
    }
}