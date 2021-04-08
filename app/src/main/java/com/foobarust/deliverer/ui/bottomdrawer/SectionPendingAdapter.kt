package com.foobarust.deliverer.ui.bottomdrawer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.deliverer.R
import com.foobarust.deliverer.databinding.EmptyListItemBinding
import com.foobarust.deliverer.databinding.SectionPendingHeaderBinding
import com.foobarust.deliverer.databinding.SectionPendingItemBinding
import com.foobarust.deliverer.ui.bottomdrawer.SectionPendingListModel.*
import com.foobarust.deliverer.ui.bottomdrawer.SectionPendingViewHolder.*
import com.foobarust.deliverer.utils.getTimeBy12Hour
import com.foobarust.deliverer.utils.loadGlideUrl
import com.foobarust.deliverer.utils.setSrc
import java.util.*

/**
 * Created by kevin on 3/9/21
 */

class SectionPendingAdapter(
    private val listener: SectionPendingAdapterListener
) : PagingDataAdapter<SectionPendingListModel, SectionPendingViewHolder>(SectionPendingListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionPendingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.section_pending_item -> SectionPendingItemViewHolder(
                SectionPendingItemBinding.inflate(inflater, parent, false)
            )
            R.layout.empty_list_item -> SectionPendingEmptyViewHolder(
                EmptyListItemBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SectionPendingViewHolder, position: Int) {
        when (holder) {
            is SectionPendingItemViewHolder -> bindSectionPendingItem(
                binding = holder.binding,
                sectionPendingItemModel = getItem(position) as? SectionPendingItemModel
            )
            is SectionPendingEmptyViewHolder -> bindSectionPendingEmpty(
                binding = holder.binding,
                sectionPendingEmptyModel = getItem(position) as? SectionPendingEmptyModel
            )
        }
    }

    private fun bindSectionPendingItem(
        binding: SectionPendingItemBinding,
        sectionPendingItemModel: SectionPendingItemModel?
    ) = binding.run {
        if (sectionPendingItemModel == null) return@run
        val context = root.context

        sectionTitleTextView.text = sectionPendingItemModel.sectionTitle

        sectionDeliveryTimeTextView.text = context.getString(
            R.string.section_pending_item_delivery_time,
            sectionPendingItemModel.sectionDeliveryTime.getTimeBy12Hour()
        )

        sectionUsersCountTextView.text = context.getString(
            R.string.section_pending_item_users_count,
            sectionPendingItemModel.sectionJoinedUsersCount,
            sectionPendingItemModel.sectionMaxUsers
        )

        sectionImageView.loadGlideUrl(
            imageUrl = sectionPendingItemModel.sectionImageUrl,
            centerCrop = true,
            placeholder = R.drawable.placeholder_card
        )

        sectionPendingItemContainer.setOnClickListener {
            listener.onSectionClicked(sectionPendingItemModel.sectionId)
        }
    }

    private fun bindSectionPendingEmpty(
        binding: EmptyListItemBinding,
        sectionPendingEmptyModel: SectionPendingEmptyModel?
    ) = binding.run {
        if (sectionPendingEmptyModel == null) return@run

        emptyListItemMessageTextView.text = sectionPendingEmptyModel.message
        emptyListItemImageView.setSrc(sectionPendingEmptyModel.drawableRes)
        emptyListItemImageView.contentDescription = sectionPendingEmptyModel.message
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SectionPendingItemModel -> R.layout.section_pending_item
            is SectionPendingEmptyModel -> R.layout.empty_list_item
            else -> throw IllegalStateException("Unknown view type at: $position")
        }
    }

    interface SectionPendingAdapterListener {
        fun onSectionClicked(sectionId: String)
    }
}

sealed class SectionPendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class SectionPendingItemViewHolder(
        val binding: SectionPendingItemBinding
    ) : SectionPendingViewHolder(binding.root)

    class SectionPendingEmptyViewHolder(
        val binding: EmptyListItemBinding
    ) : SectionPendingViewHolder(binding.root)

    class SectionPendingHeaderViewHolder(
        val binding: SectionPendingHeaderBinding
    ) : SectionPendingViewHolder(binding.root)
}

sealed class SectionPendingListModel {
    data class SectionPendingItemModel(
        val sectionId: String,
        val sectionTitle: String,
        val sectionDeliveryTime: Date,
        val sectionMaxUsers: Int,
        val sectionJoinedUsersCount: Int,
        val sectionImageUrl: String?
    ) : SectionPendingListModel()

    data class SectionPendingEmptyModel(
        val message: String,
        @DrawableRes val drawableRes: Int
    ) : SectionPendingListModel()
}

object SectionPendingListModelDiff : DiffUtil.ItemCallback<SectionPendingListModel>() {
    override fun areItemsTheSame(oldItem: SectionPendingListModel, newItem: SectionPendingListModel): Boolean {
        return when {
            oldItem is SectionPendingItemModel && newItem is SectionPendingItemModel ->
                oldItem.sectionId == newItem.sectionId
            oldItem is SectionPendingEmptyModel && newItem is SectionPendingEmptyModel ->
                true
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: SectionPendingListModel, newItem: SectionPendingListModel): Boolean {
        return when {
            oldItem is SectionPendingItemModel && newItem is SectionPendingItemModel ->
                oldItem == newItem
            oldItem is SectionPendingEmptyModel && newItem is SectionPendingEmptyModel ->
                oldItem == newItem
            else -> false
        }
    }
}