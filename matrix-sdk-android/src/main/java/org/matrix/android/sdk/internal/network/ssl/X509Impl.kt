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

import android.content.Context
import android.security.KeyChain
import android.security.KeyChainException
import java.net.Socket
import java.security.Principal
import java.security.PrivateKey
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.X509KeyManager

class X509Impl(private val alias: String, private val certChain: Array<X509Certificate>, private val privateKey: PrivateKey) : X509KeyManager {
    override fun chooseClientAlias(arg0: Array<String>, arg1: Array<Principal>, arg2: Socket): String {
        return alias
    }

    override fun getCertificateChain(alias: String): Array<X509Certificate> {
        return certChain
    }

    override fun getPrivateKey(alias: String): PrivateKey {
        return privateKey
    }

    // Methods unused (for client SSLSocket callbacks)
    override fun chooseServerAlias(keyType: String, issuers: Array<Principal>, socket: Socket): String {
        throw UnsupportedOperationException()
    }

    override fun getClientAliases(keyType: String, issuers: Array<Principal>): Array<String> {
        throw UnsupportedOperationException()
    }

    override fun getServerAliases(keyType: String, issuers: Array<Principal>): Array<String> {
        throw UnsupportedOperationException()
    }

    companion object {
        @JvmStatic
        @Throws(CertificateException::class)
        fun fromAlias(context: Context?, alias: String): X509Impl {
            val certChain: Array<X509Certificate>?
            val privateKey: PrivateKey?
            try {
                certChain = KeyChain.getCertificateChain(context!!, alias)
                privateKey = KeyChain.getPrivateKey(context, alias)
            } catch (e: KeyChainException) {
                throw CertificateException(e)
            } catch (e: InterruptedException) {
                throw CertificateException(e)
            }
            if (certChain == null || privateKey == null) {
                throw CertificateException("Can't access certificate from keystore")
            }
            return X509Impl(alias, certChain, privateKey)
        }
    }
}
