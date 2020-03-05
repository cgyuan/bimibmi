/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cyuan.bimibimi.ui.base

import androidx.recyclerview.widget.ListUpdateCallback

/**
 * ListUpdateCallback that dispatches update events to the given adapter.
 *
 * @see DiffUtil.DiffResult.dispatchUpdatesTo
 */
class MyAdapterListUpdateCallback
/**
 * Creates an AdapterListUpdateCallback that will dispatch update events to the given adapter.
 *
 * @param adapter The Adapter to send updates to.
 */(private val mAdapter: BasePagedListAdapter<*, *>) :
    ListUpdateCallback {
    /** {@inheritDoc}  */
    override fun onInserted(position: Int, count: Int) {
        val pos = getOffsetPosition(position)
        mAdapter.notifyItemRangeInserted(pos, count)
    }

    /** {@inheritDoc}  */
    override fun onRemoved(position: Int, count: Int) {
        val pos = getOffsetPosition(position)
        mAdapter.notifyItemRangeRemoved(pos, count)
    }

    /** {@inheritDoc}  */
    override fun onMoved(fromPosition: Int, toPosition: Int) {
        val fromPos = getOffsetPosition(fromPosition)
        val toPos = getOffsetPosition(toPosition)
        mAdapter.notifyItemMoved(fromPos, toPos)
    }

    /** {@inheritDoc}  */
    override fun onChanged(position: Int, count: Int, payload: Any?) {
        val pos = getOffsetPosition(position)
        mAdapter.notifyItemRangeChanged(pos, count, payload)
    }

    private fun getOffsetPosition(position: Int) =
        if (mAdapter.headerLayoutId != null) position + 1 else position

}