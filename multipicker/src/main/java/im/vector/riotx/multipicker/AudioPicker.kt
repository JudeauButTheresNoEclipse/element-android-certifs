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

package im.vector.riotx.multipicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import im.vector.riotx.multipicker.entity.MultiPickerAudioType

class AudioPicker(override val requestCode: Int) : Picker<MultiPickerAudioType>(requestCode) {

    override fun startWith(activity: Activity) {
        activity.startActivityForResult(createIntent(), requestCode)
    }

    override fun startWith(fragment: Fragment) {
        fragment.startActivityForResult(createIntent(), requestCode)
    }

    override fun getSelectedFiles(context: Context, requestCode: Int, resultCode: Int, data: Intent?): List<MultiPickerAudioType> {
        if (requestCode != this.requestCode && resultCode != Activity.RESULT_OK) {
            return emptyList()
        }

        val audioList = mutableListOf<MultiPickerAudioType>()

        val selectedUriList = mutableListOf<Uri>()
        val dataUri = data?.data
        val clipData = data?.clipData

        if (clipData != null) {
            for (i in 0 until clipData.itemCount) {
                selectedUriList.add(clipData.getItemAt(i).uri)
            }
        } else if (dataUri != null) {
            selectedUriList.add(dataUri)
        } else {
            data?.extras?.get(Intent.EXTRA_STREAM)?.let {
                when (it) {
                    is List<*> -> selectedUriList.addAll(it as List<Uri>)
                    else     -> selectedUriList.add(it as Uri)
                }
            }
        }

        selectedUriList.forEach { selectedUri ->
            val projection = arrayOf(
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.SIZE
            )

            context.contentResolver.query(
                    selectedUri,
                    projection,
                    null,
                    null,
                    null
            )?.use { cursor ->
                val nameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
                val sizeColumn = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)

                if (cursor.moveToNext()) {
                    val name = cursor.getString(nameColumn)
                    val size = cursor.getLong(sizeColumn)
                    var duration = 0L

                    context.contentResolver.openFileDescriptor(selectedUri, "r")?.use { pfd ->
                        val mediaMetadataRetriever = MediaMetadataRetriever()
                        mediaMetadataRetriever.setDataSource(pfd.fileDescriptor)
                        duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
                    }

                    audioList.add(
                            MultiPickerAudioType(
                                    name,
                                    size,
                                    context.contentResolver.getType(selectedUri),
                                    selectedUri,
                                    duration
                            )
                    )
                }
            }
        }
        return audioList
    }

    private fun createIntent(): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, !single)
            type = "audio/*"
        }
    }
}
