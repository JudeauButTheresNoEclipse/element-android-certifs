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

package im.vector.riotx.multipicker.entity

data class MultiPickerContactType(
        val displayName: String,
        val photoUri: String?,
        val phoneNumberList: List<String>,
        val emailList: List<String>
) {
    private val CONTACT_FORMAT = "Name: %s, Photo: %s, Phones: %s, Emails: %s"

    override fun toString(): String {
        val phoneNumberString = phoneNumberList.joinToString(separator = ", ", prefix = "[", postfix = "]")
        val emailString = emailList.joinToString(separator = ", ", prefix = "[", postfix = "]")
        return String.format(CONTACT_FORMAT, displayName, photoUri, phoneNumberString, emailString)
    }
}
