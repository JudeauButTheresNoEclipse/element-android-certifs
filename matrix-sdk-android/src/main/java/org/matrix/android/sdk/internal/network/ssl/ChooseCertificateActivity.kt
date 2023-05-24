/*
 * Copyright (c) 2023 New Vector Ltd
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
package org.matrix.android.sdk.internal.network.ssl

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.security.KeyChain
import android.security.KeyChainAliasCallback
import io.realm.Realm
import org.matrix.android.sdk.internal.network.ssl.X509Impl.Companion.fromAlias

class ChooseCertificateActivity : Activity(), KeyChainAliasCallback {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KeyChain.choosePrivateKeyAlias(this, this, arrayOf("RSA"), null, null, -1, null)
    }

    override fun alias(alias: String?) {
        val resultIntent = Intent()
        resultIntent.putExtra("alias", alias)
        globalAlias.alias = alias
        var context = Realm.getApplicationContext() ?: throw RuntimeException("Could not query context");
        globalAlias.cert = arrayOf(fromAlias(context, globalAlias.getAlias()))
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}
