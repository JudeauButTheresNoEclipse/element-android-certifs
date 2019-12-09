/*
 * Copyright 2019 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package im.vector.riotx.features.reactions

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import im.vector.riotx.R
import im.vector.riotx.core.extensions.cleanup
import im.vector.riotx.core.platform.VectorBaseFragment
import im.vector.riotx.core.utils.LiveEvent
import kotlinx.android.synthetic.main.fragment_generic_recycler.*
import javax.inject.Inject

class EmojiSearchResultFragment @Inject constructor(
        private val epoxyController: EmojiSearchResultController
) : VectorBaseFragment() {

    override fun getLayoutResId(): Int = R.layout.fragment_generic_recycler

    val viewModel: EmojiSearchResultViewModel by activityViewModel()

    var sharedViewModel: EmojiChooserViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = activityViewModelProvider.get(EmojiChooserViewModel::class.java)

        epoxyController.listener = object : ReactionClickListener {
            override fun onReactionSelected(reaction: String) {
                sharedViewModel?.selectedReaction = reaction
                sharedViewModel?.navigateEvent?.value = LiveEvent(EmojiChooserViewModel.NAVIGATE_FINISH)
            }
        }

        val lmgr = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = lmgr
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, lmgr.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
        recyclerView.adapter = epoxyController.adapter
    }

    override fun onDestroyView() {
        epoxyController.listener = null
        recyclerView.cleanup()
        super.onDestroyView()
    }

    override fun invalidate() = withState(viewModel) { state ->
        epoxyController.setData(state)
    }
}
