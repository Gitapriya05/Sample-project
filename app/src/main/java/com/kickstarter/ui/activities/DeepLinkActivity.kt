package com.kickstarter.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.kickstarter.libs.BaseActivity
import com.kickstarter.libs.RefTag
import com.kickstarter.libs.qualifiers.RequiresActivityViewModel
import com.kickstarter.libs.rx.transformers.Transformers
import com.kickstarter.libs.utils.ApplicationUtils
import com.kickstarter.libs.utils.UrlUtils.refTag
import com.kickstarter.ui.IntentKey
import com.kickstarter.viewmodels.DeepLinkViewModel

@RequiresActivityViewModel(DeepLinkViewModel.ViewModel::class)
class DeepLinkActivity : BaseActivity<DeepLinkViewModel.ViewModel?>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // - initialized on super will never be null within OnCreate context
        val viewModel = requireNotNull(this.viewModel)

        viewModel.outputs.startBrowser()
            .compose(bindToLifecycle())
            .compose(Transformers.observeForUI())
            .subscribe { startBrowser(it) }

        viewModel.outputs.startDiscoveryActivity()
            .compose(bindToLifecycle())
            .compose(Transformers.observeForUI())
            .subscribe { startDiscoveryActivity() }

        viewModel.outputs.startProjectActivity()
            .compose(bindToLifecycle())
            .compose(Transformers.observeForUI())
            .subscribe { startProjectActivity(it) }

        viewModel.outputs.startProjectActivityForCheckout()
            .compose(bindToLifecycle())
            .compose(Transformers.observeForUI())
            .subscribe { startProjectActivityForCheckout(it) }

        viewModel.outputs.finishDeeplinkActivity()
            .compose(bindToLifecycle())
            .compose(Transformers.observeForUI())
            .subscribe { finish() }
    }

    private fun projectIntent(uri: Uri): Intent {
        val projectIntent = Intent(this, ProjectActivity::class.java)
            .setData(uri)
        val ref = refTag(uri.toString())
        if (ref != null) {
            projectIntent.putExtra(IntentKey.REF_TAG, RefTag.from(ref))
        }
        return projectIntent
    }

    private fun startDiscoveryActivity() {
        ApplicationUtils.startNewDiscoveryActivity(this)
        finish()
    }

    private fun startProjectActivity(uri: Uri) {
        startActivity(projectIntent(uri))
        finish()
    }

    private fun startProjectActivityForCheckout(uri: Uri) {
        val projectIntent = projectIntent(uri)
            .putExtra(IntentKey.EXPAND_PLEDGE_SHEET, true)
        startActivity(projectIntent)
        finish()
    }

    private fun startBrowser(url: String) {
        ApplicationUtils.openUrlExternally(this, url)
        finish()
    }
}
