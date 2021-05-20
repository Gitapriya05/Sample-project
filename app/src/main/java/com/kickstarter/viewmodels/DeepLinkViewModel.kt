package com.kickstarter.viewmodels

import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Pair
import com.kickstarter.libs.ActivityViewModel
import com.kickstarter.libs.CurrentUserType
import com.kickstarter.libs.Environment
import com.kickstarter.libs.RefTag
import com.kickstarter.libs.rx.transformers.Transformers
import com.kickstarter.libs.utils.ObjectUtils
import com.kickstarter.libs.utils.Secrets
import com.kickstarter.libs.utils.UrlUtils.appendRefTag
import com.kickstarter.libs.utils.UrlUtils.refTag
import com.kickstarter.libs.utils.extensions.isEmailSubdomain
import com.kickstarter.models.User
import com.kickstarter.services.ApiClientType
import com.kickstarter.services.KSUri
import com.kickstarter.ui.activities.DeepLinkActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import rx.Notification
import rx.Observable
import rx.schedulers.Schedulers
import rx.subjects.BehaviorSubject
import java.io.IOException

interface DeepLinkViewModel {
    interface Outputs {
        /** Emits when we should start an external browser because we don't want to deep link.  */
        fun startBrowser(): Observable<String>

        /** Emits when we should start the [com.kickstarter.ui.activities.DiscoveryActivity].  */
        fun startDiscoveryActivity(): Observable<Void>

        /** Emits when we should start the [com.kickstarter.ui.activities.ProjectActivity].  */
        fun startProjectActivity(): Observable<Uri>

        /** Emits when we should start the [com.kickstarter.ui.activities.ProjectActivity] with pledge sheet expanded.  */
        fun startProjectActivityForCheckout(): Observable<Uri>

        /** Emits when we should finish the current activity  */
        fun finishDeeplinkActivity(): Observable<Void>
    }

    class ViewModel(environment: Environment) :
        ActivityViewModel<DeepLinkActivity?>(environment), Outputs {

        private val startBrowser = BehaviorSubject.create<String>()
        private val uriFromRedirect = BehaviorSubject.create<Uri>()
        private val startDiscoveryActivity = BehaviorSubject.create<Void>()
        private val startProjectActivity = BehaviorSubject.create<Uri>()
        private val startProjectActivityWithCheckout = BehaviorSubject.create<Uri>()
        private val updateUserPreferences = BehaviorSubject.create<Boolean>()
        private val finishDeeplinkActivity = BehaviorSubject.create<Void?>()
        val outputs: Outputs = this

        init {
            val apiClientType = environment.apiClient()
            val currentUser = environment.currentUser()
            val networkingClient = environment.networkingClient()

            val uriFromIntent = intent()
                .map { obj: Intent -> obj.data }
                .ofType(Uri::class.java)
                .filter { ObjectUtils.isNotNull(it) }
                .map { requireNotNull(it) }
                .distinctUntilChanged()

            uriFromIntent
                .filter { lastPathSegmentIsProjects(it) }
                .compose(Transformers.ignoreValues())
                .compose(bindToLifecycle())
                .subscribe {
                    startDiscoveryActivity.onNext(it)
                }

            uriFromIntent
                .filter { isProjectUrl(it) }
                .filter { !isCheckoutUri(it) }
                .filter { !isPreviewProjectUrl(it) }
                .map { appendRefTagIfNone(it) }
                .compose(bindToLifecycle())
                .subscribe {
                    startProjectActivity.onNext(it)
                }

            uriFromIntent
                .filter { KSUri.isSettingsUrl(it) }
                .compose(bindToLifecycle())
                .subscribe {
                    updateUserPreferences.onNext(KSUri.isSettingsUrl(it))
                }

            currentUser.observable()
                .filter { ObjectUtils.isNotNull(it) }
                .compose(Transformers.combineLatestPair(updateUserPreferences))
                .switchMap { it: Pair<User, Boolean?> ->
                    updateSettings(it.first, apiClientType)
                }
                .compose(Transformers.values())
                .distinctUntilChanged()
                .filter { ObjectUtils.isNotNull(it) }
                .map { requireNotNull(it) }
                .compose(bindToLifecycle())
                .subscribe {
                    refreshUserAndFinishActivity(it, currentUser)
                }

            uriFromIntent
                .filter { KSUri.isCheckoutUri(it, Secrets.WebEndpoint.PRODUCTION) }
                .map { appendRefTagIfNone(it) }
                .compose(bindToLifecycle())
                .subscribe {
                    startProjectActivityWithCheckout.onNext(it)
                }

            uriFromIntent
                .filter { it.isEmailSubdomain() }
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe {
                    executeRedirection(it, networkingClient)
                }

            val projectPreview = uriFromIntent
                .filter { !it.isEmailSubdomain() }
                .filter { isPreviewProjectUrl(it) }

            val unsupportedDeepLink = uriFromIntent
                .filter { !it.isEmailSubdomain() }
                .filter { !lastPathSegmentIsProjects(it) }
                .filter { !KSUri.isSettingsUrl(it) }
                .filter { !KSUri.isCheckoutUri(it, Secrets.WebEndpoint.PRODUCTION) }
                .filter { !isProjectUrl(it) }

            Observable.merge(projectPreview, unsupportedDeepLink)
                .map { it.toString() }
                .filter { !TextUtils.isEmpty(it) }
                .compose(bindToLifecycle())
                .subscribe {
                    startBrowser.onNext(it)
                }
        }

        private fun refreshUserAndFinishActivity(user: User, currentUser: CurrentUserType) {
            currentUser.refresh(user)
            finishDeeplinkActivity.onNext(null)
        }

        private fun appendRefTagIfNone(uri: Uri): Uri {
            val url = uri.toString()
            val ref = refTag(url)
            return if (ObjectUtils.isNull(ref)) {
                Uri.parse(appendRefTag(url, RefTag.deepLink().tag()))
            } else uri
        }

        private fun lastPathSegmentIsProjects(uri: Uri): Boolean {
            return uri.lastPathSegment == "projects"
        }

        private fun executeRedirection(fromEmailUri: Uri, networkingClient: OkHttpClient) {
            try {
                val request: Request = Request.Builder()
                    .url(fromEmailUri.toString())
                    .build()

                val response = networkingClient.newCall(request).execute()

                // - a redirection took place from the email url to the project url
                response.priorResponse?.isRedirect?.let {
                    val uri = Uri.parse(response.request.url.toString())
                    if (isProjectUrl(uri)) {
                        // -  once we have the project url launch the project activity
                        startProjectActivity.onNext(uri)
                    }
                }
            } catch (exception: IOException) {
                finishDeeplinkActivity.onNext(null)
            }
        }

        private fun isProjectUrl(uri: Uri) =
            KSUri.isProjectUri(uri, Secrets.WebEndpoint.PRODUCTION) ||
                KSUri.isProjectUri(uri, Secrets.WebEndpoint.STAGING)

        private fun isPreviewProjectUrl(uri: Uri) =
            !KSUri.isProjectPreviewUri(uri, Secrets.WebEndpoint.PRODUCTION) ||
                !KSUri.isProjectPreviewUri(uri, Secrets.WebEndpoint.STAGING)

        private fun isCheckoutUri(uri: Uri) =
            KSUri.isCheckoutUri(uri, Secrets.WebEndpoint.PRODUCTION) ||
                !KSUri.isCheckoutUri(uri, Secrets.WebEndpoint.STAGING)

        private fun updateSettings(
            user: User,
            apiClientType: ApiClientType
        ): Observable<Notification<User?>?> {
            val updatedUser = user.toBuilder().notifyMobileOfMarketingUpdate(true).build()
            return apiClientType.updateUserSettings(updatedUser)
                .materialize()
                .share()
        }

        override fun startBrowser(): Observable<String> = startBrowser

        override fun startDiscoveryActivity(): Observable<Void> = startDiscoveryActivity

        override fun startProjectActivity(): Observable<Uri> = startProjectActivity

        override fun startProjectActivityForCheckout(): Observable<Uri> = startProjectActivityWithCheckout

        override fun finishDeeplinkActivity(): Observable<Void> = finishDeeplinkActivity
    }
}
