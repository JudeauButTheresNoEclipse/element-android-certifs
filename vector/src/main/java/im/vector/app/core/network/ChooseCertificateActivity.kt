/*
 * Copyright (c) 2020 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.app.core.network

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.security.KeyChain
import android.security.KeyChainAliasCallback
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.databinding.ActivityChooseCertificateBinding
import io.realm.Realm
import org.matrix.android.sdk.internal.network.ssl.X509Impl.Companion.fromAlias
import javax.inject.Inject

/**
 * Activity to select a certificate from system store.
 */
@AndroidEntryPoint
class ChooseCertificateActivity : VectorBaseActivity<ActivityChooseCertificateBinding>(), KeyChainAliasCallback {
    @Inject lateinit var sessionHolder: ActiveSessionHolder

    override fun getBinding() = ActivityChooseCertificateBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KeyChain.choosePrivateKeyAlias(this, this, arrayOf("RSA"), null, null, -1, null)
        finish()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ChooseCertificateActivity::class.java).apply {
            }
        }
    }

    override fun alias(alias: String?) {
        if (alias == null)
            throw RuntimeException("No certificate selected")
        else {
            var context = Realm.getApplicationContext() ?: throw RuntimeException("Could not query context");
            org.matrix.android.sdk.internal.network.ssl.globalAlias.cert = arrayOf(fromAlias(context, alias))
        }
        val resultIntent = Intent()
        resultIntent.putExtra("alias", alias)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}
