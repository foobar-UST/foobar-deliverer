package com.foobarust.deliverer.ui.sectionuser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.deliverer.R
import com.foobarust.deliverer.data.models.OrderState
import com.foobarust.deliverer.databinding.*
import com.foobarust.deliverer.ui.sectionuser.SectionUserDetailListModel.*
import com.foobarust.deliverer.ui.sectionuser.SectionUserDetailViewHolder.*
import com.foobarust.deliverer.utils.drawableFitVertical
import com.foobarust.deliverer.utils.loadGlideUrl
import com.foobarust.deliverer.utils.setDrawables

/**
 * Created by kevin on 4/4/21
 */

class SectionUserDetailAdapter(
    private val listener: SectionUserDetailAdapterListener
) : ListAdapter<SectionUserDetailListModel, SectionUserDetailViewHolder>(SectionUserDetailListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionUserDetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.section_user_detail_order_info -> SectionUserDetailOrderInfoViewHolder(
                SectionUserDetailOrderInfoBinding.inflate(inflater, parent, false)
            )
            R.layout.section_user_detail_order_item -> SectionUserDetailOrderItemViewHolder(
                SectionUserDetailOrderItemBinding.inflate(inflater, parent, false)
            )
            R.layout.section_user_detail_cost -> SectionUserDetailCostViewHolder(
                SectionUserDetailCostBinding.inflate(inflater, parent, false)
            )
            R.layout.section_user_detail_payment -> SectionUserDetailPaymentViewHolder(
                SectionUserDetailPaymentBinding.inflate(inflater, parent, false)
            )
            R.layout.section_user_detail_payment_notice -> SectionUserDetailPaymentNoticeViewHolder(
                SectionUserDetailPaymentNoticeBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SectionUserDetailViewHolder, position: Int) {
        when (holder) {
            is SectionUserDetailOrderInfoViewHolder -> bindOrderInfo(
                binding = holder.binding,
                orderInfoModel = getItem(position) as SectionUserDetailOrderInfoModel
            )
            is SectionUserDetailOrderItemViewHolder -> bindOrderItem(
                binding = holder.binding,
                orderItemModel = getItem(position) as SectionUserDetailOrderItemModel
            )
            is SectionUserDetailCostViewHolder -> bindCost(
                binding = holder.binding,
                costModel = getItem(position) as SectionUserDetailCostModel
            )
            is SectionUserDetailPaymentViewHolder -> bindPayment(
                binding = holder.binding,
                paymentModel = getItem(position) as SectionUserDetailPaymentModel
            )
            is SectionUserDetailPaymentNoticeViewHolder -> bindPaymentNotice(
                binding = holder.binding
            )
        }
    }

    private fun bindOrderInfo(
        binding: SectionUserDetailOrderInfoBinding,
        orderInfoModel: SectionUserDetailOrderInfoModel
    ) = binding.run {
        orderImageView.loadGlideUrl(
            imageUrl = orderInfoModel.orderImageUrl,
            centerCrop = true,
            placeholder = R.drawable.placeholder_card
        )

        identifierTitleTextView.text = root.context.getString(
            R.string.section_user_detail_order_info_identifier,
            orderInfoModel.orderIdentifier
        )

        orderTitleTextView.text = orderInfoModel.orderTitle

        with(messageTextView) {
            text = root.context.getString(
                R.string.section_user_detail_order_info_message,
                orderInfoModel.orderMessage
            )
            isVisible = orderInfoModel.orderMessage != null
        }

        totalCostTextView.text = root.context.getString(
            R.string.section_user_detail_order_info_total_cost,
            orderInfoModel.totalCost
        )

        deliveredManualButton.setOnClickListener { listener.onDeliverOrderManual() }
        deliveredScanButton.setOnClickListener { listener.onDeliverOrderScan() }
        contactUserButton.setOnClickListener { listener.onContactUser(orderInfoModel.userId) }

        val deliveredStates = listOf(OrderState.DELIVERED, OrderState.ARCHIVED)

        actionButtonGroup.isVisible = orderInfoModel.orderState !in deliveredStates
        orderDeliveredTextView.isVisible = orderInfoModel.orderState in deliveredStates
    }

    private fun bindOrderItem(
        binding: SectionUserDetailOrderItemBinding,
        orderItemModel: SectionUserDetailOrderItemModel
    ) = binding.run {
        itemImageView.isVisible = orderItemModel.itemImageUrl != null

        if (orderItemModel.itemImageUrl != null) {
            itemImageView.loadGlideUrl(
                imageUrl = orderItemModel.itemImageUrl,
                centerCrop = true,
                placeholder = R.drawable.placeholder_card
            )
        }

        itemTitleTextView.text = orderItemModel.itemTitle

        itemPriceTextView.text = root.context.getString(
            R.string.section_user_detail_cost_format,
            orderItemModel.itemPrice
        )
    }

    private fun bindCost(
        binding: SectionUserDetailCostBinding,
        costModel: SectionUserDetailCostModel
    ) = binding.run {
        subtotalValueTextView.text = root.context.getString(
            R.string.section_user_detail_cost_format,
            costModel.subtotal
        )
        deliveryCostValueTextView.text = root.context.getString(
            R.string.section_user_detail_cost_format,
            costModel.deliveryCost
        )
    }

    private fun bindPayment(
        binding: SectionUserDetailPaymentBinding,
        paymentModel: SectionUserDetailPaymentModel
    ) = binding.run {
        usernameTextView.text = paymentModel.username

        userPhotoImageView.loadGlideUrl(
            imageUrl = paymentModel.userPhotoUrl,
            circularCrop = true,
            placeholder = R.drawable.ic_user
        )

        with(paymentMethodTextView) {
            text = paymentModel.paymentMethodItem.title
            setDrawables(drawableLeft = paymentModel.paymentMethodItem.drawable)
        }

        isPaidTextView.text = if (paymentModel.isPaid) {
            root.context.getString(R.string.section_user_detail_payment_paid)
        } else {
            root.context.getString(R.string.section_user_detail_payment_not_paid)
        }
    }

    private fun bindPaymentNotice(
        binding: SectionUserDetailPaymentNoticeBinding
    ) = binding.run {
        paymentNoticeTextView.drawableFitVertical()
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SectionUserDetailOrderInfoModel -> R.layout.section_user_detail_order_info
            is SectionUserDetailOrderItemModel -> R.layout.section_user_detail_order_item
            is SectionUserDetailCostModel -> R.layout.section_user_detail_cost
            is SectionUserDetailPaymentModel -> R.layout.section_user_detail_payment
            is SectionUserDetailPaymentNoticeModel -> R.layout.section_user_detail_payment_notice
        }
    }

    interface SectionUserDetailAdapterListener {
        fun onDeliverOrderManual()
        fun onDeliverOrderScan()
        fun onContactUser(userId: String)
    }
}

sealed class SectionUserDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class SectionUserDetailOrderInfoViewHolder(
        val binding: SectionUserDetailOrderInfoBinding
    ) : SectionUserDetailViewHolder(binding.root)

    class SectionUserDetailOrderItemViewHolder(
        val binding: SectionUserDetailOrderItemBinding
    ) : SectionUserDetailViewHolder(binding.root)

    class SectionUserDetailCostViewHolder(
        val binding: SectionUserDetailCostBinding
    ) : SectionUserDetailViewHolder(binding.root)

    class SectionUserDetailPaymentViewHolder(
        val binding: SectionUserDetailPaymentBinding
    ) : SectionUserDetailViewHolder(binding.root)

    class SectionUserDetailPaymentNoticeViewHolder(
        val binding: SectionUserDetailPaymentNoticeBinding
    ) : SectionUserDetailViewHolder(binding.root)
}

sealed class SectionUserDetailListModel {
    data class SectionUserDetailOrderInfoModel(
        val orderIdentifier: String,
        val orderTitle: String,
        val orderMessage: String?,
        val orderImageUrl: String?,
        val orderState: OrderState,
        val totalCost: Double,
        val userId: String,
        val isPaid: Boolean
    ) : SectionUserDetailListModel()

    data class SectionUserDetailOrderItemModel(
        val itemId: String,
        val itemTitle: String,
        val itemAmounts: Int,
        val itemPrice: Double,
        val itemImageUrl: String?
    ) : SectionUserDetailListModel()

    data class SectionUserDetailCostModel(
        val subtotal: Double,
        val deliveryCost: Double
    ) : SectionUserDetailListModel()

    data class SectionUserDetailPaymentModel(
        val username: String,
        val userPhotoUrl: String?,
        val paymentMethodItem: PaymentMethodItem,
        val isPaid: Boolean
    ) : SectionUserDetailListModel()

    object SectionUserDetailPaymentNoticeModel : SectionUserDetailListModel()
}

object SectionUserDetailListModelDiff : DiffUtil.ItemCallback<SectionUserDetailListModel>() {
    override fun areItemsTheSame(
        oldItem: SectionUserDetailListModel,
        newItem: SectionUserDetailListModel
    ): Boolean {
        return when {
            oldItem is SectionUserDetailOrderInfoModel && newItem is SectionUserDetailOrderInfoModel ->
                true
            oldItem is SectionUserDetailOrderItemModel && newItem is SectionUserDetailOrderItemModel ->
                oldItem.itemId == newItem.itemId
            oldItem is SectionUserDetailCostModel && newItem is SectionUserDetailCostModel ->
                true
            oldItem is SectionUserDetailPaymentModel && newItem is SectionUserDetailPaymentModel ->
                true
            oldItem is SectionUserDetailPaymentNoticeModel && newItem is SectionUserDetailPaymentNoticeModel ->
                true
            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: SectionUserDetailListModel,
        newItem: SectionUserDetailListModel
    ): Boolean {
        return when {
            oldItem is SectionUserDetailOrderInfoModel && newItem is SectionUserDetailOrderInfoModel ->
                oldItem == newItem
            oldItem is SectionUserDetailOrderItemModel && newItem is SectionUserDetailOrderItemModel ->
                oldItem == newItem
            oldItem is SectionUserDetailCostModel && newItem is SectionUserDetailCostModel ->
                oldItem == newItem
            oldItem is SectionUserDetailPaymentModel && newItem is SectionUserDetailPaymentModel ->
                oldItem == newItem
            oldItem is SectionUserDetailPaymentNoticeModel && newItem is SectionUserDetailPaymentNoticeModel ->
                oldItem == newItem
            else -> false
        }
    }
}

