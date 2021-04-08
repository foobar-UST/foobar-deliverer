package com.foobarust.deliverer.ui.sectionuser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.deliverer.R
import com.foobarust.deliverer.data.models.OrderState
import com.foobarust.deliverer.databinding.SectionUserListHeaderBinding
import com.foobarust.deliverer.databinding.SectionUserListItemBinding
import com.foobarust.deliverer.ui.sectionuser.SectionUserListModel.SectionUserListHeaderModel
import com.foobarust.deliverer.ui.sectionuser.SectionUserListModel.SectionUserListItemModel
import com.foobarust.deliverer.ui.sectionuser.SectionUserListViewHolder.SectionUserListHeaderViewHolder
import com.foobarust.deliverer.ui.sectionuser.SectionUserListViewHolder.SectionUserListItemViewHolder
import com.foobarust.deliverer.utils.loadGlideUrl

/**
 * Created by kevin on 4/3/21
 */

class SectionUserListAdapter(
    private val listener: SectionUserListAdapterListener
) : ListAdapter<SectionUserListModel, SectionUserListViewHolder>(SectionUserListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionUserListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.section_user_list_item -> SectionUserListItemViewHolder(
                SectionUserListItemBinding.inflate(inflater, parent, false)
            )
            R.layout.section_user_list_header -> SectionUserListHeaderViewHolder(
                SectionUserListHeaderBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SectionUserListViewHolder, position: Int) {
        when (holder) {
            is SectionUserListItemViewHolder -> bindUserListItem(
                binding = holder.binding,
                itemModel = getItem(position) as SectionUserListItemModel
            )
            is SectionUserListHeaderViewHolder -> Unit
        }
    }

    private fun bindUserListItem(
        binding: SectionUserListItemBinding,
        itemModel: SectionUserListItemModel
    ) = binding.run {
        userPhotoImageView.loadGlideUrl(
            imageUrl = itemModel.userPhotoUrl,
            circularCrop = true,
            placeholder = R.drawable.ic_user
        )

        usernameTextView.text = itemModel.username

        orderItemsCountTextView.text = root.context.getString(
            R.string.section_user_list_item_items_count_text_view,
            itemModel.orderIdentifier,
            itemModel.orderItemsCount
        )

        orderStateTextView.text = when (itemModel.orderState) {
            OrderState.IN_TRANSIT, OrderState.READY_FOR_PICK_UP -> root.context.getString(
                R.string.section_user_list_item_pick_up
            )
            OrderState.DELIVERED, OrderState.ARCHIVED -> root.context.getString(
                R.string.section_user_list_item_delivered
            )
            else -> throw IllegalStateException("Invalid order state: ${itemModel.orderState}")
        }

        userListItemLayout.setOnClickListener {
            listener.onUserOrderClicked(
                itemModel.orderId,
                itemModel.username,
                itemModel.userPhotoUrl
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SectionUserListItemModel -> R.layout.section_user_list_item
            is SectionUserListHeaderModel -> R.layout.section_user_list_header
            else -> throw IllegalStateException("Unknown view type at: $position")
        }
    }

    interface SectionUserListAdapterListener {
        fun onUserOrderClicked(orderId: String, username: String, userPhotoUrl: String?)
    }
}

sealed class SectionUserListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class SectionUserListItemViewHolder(
        val binding: SectionUserListItemBinding
    ) : SectionUserListViewHolder(binding.root)

    class SectionUserListHeaderViewHolder(
        val binding: SectionUserListHeaderBinding
    ) : SectionUserListViewHolder(binding.root)
}

sealed class SectionUserListModel {
    data class SectionUserListItemModel(
        val orderId: String,
        val orderIdentifier: String,
        val userId: String,
        val username: String,
        val userPhotoUrl: String?,
        val orderItemsCount: Int,
        val orderState: OrderState
    ) : SectionUserListModel()

    object SectionUserListHeaderModel : SectionUserListModel()
}

object SectionUserListModelDiff: DiffUtil.ItemCallback<SectionUserListModel>() {
    override fun areItemsTheSame(
        oldItem: SectionUserListModel,
        newItem: SectionUserListModel
    ): Boolean {
        return when {
            oldItem is SectionUserListItemModel && newItem is SectionUserListItemModel ->
                oldItem.userId == newItem.userId
            oldItem is SectionUserListHeaderModel && newItem is SectionUserListHeaderModel -> true
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: SectionUserListModel,
        newItem: SectionUserListModel
    ): Boolean {
        return when {
            oldItem is SectionUserListItemModel && newItem is SectionUserListItemModel ->
                oldItem == newItem
            oldItem is SectionUserListHeaderModel && newItem is SectionUserListHeaderModel -> true
            else -> false
        }
    }
}