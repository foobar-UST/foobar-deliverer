package com.foobarust.deliverer.ui.sectiondetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.foobarust.deliverer.R
import com.foobarust.deliverer.data.models.Geolocation
import com.foobarust.deliverer.data.models.GeolocationPoint
import com.foobarust.deliverer.data.models.getNormalizedAddress
import com.foobarust.deliverer.databinding.*
import com.foobarust.deliverer.ui.sectiondetail.SectionDetailListModel.*
import com.foobarust.deliverer.ui.sectiondetail.SectionDetailViewHolder.*
import com.foobarust.deliverer.utils.format
import com.foobarust.deliverer.utils.getTimeBy12Hour
import com.foobarust.deliverer.utils.loadGlideUrl
import java.util.*

/**
 * Created by kevin on 3/19/21
 */

class SectionDetailAdapter(
    private val listener: SectionDetailAdapterListener
) : ListAdapter<SectionDetailListModel, SectionDetailViewHolder>(SectionDetailListModelDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionDetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.section_detail_time -> SectionDetailTimeViewHolder(
                SectionDetailTimeBinding.inflate(inflater, parent, false)
            )
            R.layout.section_detail_info -> SectionDetailInfoViewHolder(
                SectionDetailInfoBinding.inflate(inflater, parent, false)
            )
            R.layout.section_detail_orders -> SectionDetailOrdersViewHolder(
                SectionDetailOrdersBinding.inflate(inflater, parent, false)
            )
            R.layout.section_detail_seller -> SectionDetailSellerViewHolder(
                SectionDetailSellerBinding.inflate(inflater, parent, false)
            )
            R.layout.section_detail_location -> SectionDetailLocationViewHolder(
                SectionDetailLocationBinding.inflate(inflater, parent, false)
            )
            R.layout.section_detail_complete -> SectionDetailCompleteViewHolder(
                SectionDetailCompleteBinding.inflate(inflater, parent, false)
            )
            R.layout.section_detail_cancel -> SectionDetailCancelViewHolder(
                SectionDetailCancelBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: SectionDetailViewHolder, position: Int) {
        when (holder) {
            is SectionDetailTimeViewHolder -> bindSectionDetailTime(
                binding = holder.binding,
                timeModel = getItem(position) as SectionDetailTimeModel
            )
            is SectionDetailInfoViewHolder -> bindSectionDetailInfo(
                binding = holder.binding,
                infoModel = getItem(position) as SectionDetailInfoModel
            )
            is SectionDetailOrdersViewHolder -> bindSectionDetailOrdersModel(
                binding = holder.binding
            )
            is SectionDetailSellerViewHolder -> bindSectionDetailSellerModel(
                binding = holder.binding,
                sellerModel = getItem(position) as SectionDetailSellerModel
            )
            is SectionDetailLocationViewHolder -> bindSectionDetailLocationModel(
                binding = holder.binding,
                locationModel = getItem(position) as SectionDetailLocationModel
            )
            is SectionDetailCompleteViewHolder -> bindSectionDetailCompleteModel(
                binding = holder.binding
            )
            is SectionDetailCancelViewHolder -> bindSectionDetailCancelModel(
                binding = holder.binding
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SectionDetailTimeModel -> R.layout.section_detail_time
            is SectionDetailInfoModel -> R.layout.section_detail_info
            is SectionDetailOrdersModel -> R.layout.section_detail_orders
            is SectionDetailSellerModel -> R.layout.section_detail_seller
            is SectionDetailLocationModel -> R.layout.section_detail_location
            is SectionDetailCompleteModel -> R.layout.section_detail_complete
            is SectionDetailCancelModel -> R.layout.section_detail_cancel
        }
    }

    private fun bindSectionDetailTime(
        binding: SectionDetailTimeBinding,
        timeModel: SectionDetailTimeModel
    )  = binding.run {
        deliveryTimeTextView.text = timeModel.deliveryTime.getTimeBy12Hour()
    }

    private fun bindSectionDetailInfo(
        binding: SectionDetailInfoBinding,
        infoModel: SectionDetailInfoModel
    ) = binding.run {
        sectionIdTextView.text = infoModel.sectionId
        sectionTitleTextView.text = infoModel.sectionTitle
        descriptionTextView.text = infoModel.sectionDescription
        deliveryDateTextView.text = infoModel.sectionDeliveryTime.format("yyyy-MM-dd")
        deliveryTimeTextView.text = infoModel.sectionDeliveryTime.getTimeBy12Hour()

        usersCountTextView.text = root.context.getString(
            R.string.section_detail_users_count,
            infoModel.sectionJoinedUsersCount,
            infoModel.sectionMaxUsers
        )
    }

    private fun bindSectionDetailOrdersModel(
        binding: SectionDetailOrdersBinding
    ) = binding.run {
        showOrdersTextView.setOnClickListener {
            listener.onShowUserOrders()
        }
    }

    private fun bindSectionDetailSellerModel(
        binding: SectionDetailSellerBinding,
        sellerModel: SectionDetailSellerModel
    ) = binding.run {
        sellerNameTextView.text = sellerModel.sellerName
        sellerPhoneNumTextView.text = root.context.getString(
            R.string.section_detail_seller_phone_num,
            sellerModel.sellerPhoneNum
        )

        sellerImageView.loadGlideUrl(
            imageUrl = sellerModel.sellerImageUrl,
            circularCrop = true,
            placeholder = R.drawable.placeholder_card
        )

        sectionDetailSellerLayout.setOnClickListener {
            listener.onShowSellerDetail()
        }
    }

    private fun bindSectionDetailLocationModel(
        binding: SectionDetailLocationBinding,
        locationModel: SectionDetailLocationModel
    ) = binding.run {
        locationAddressTextView.text = locationModel.deliveryLocation.getNormalizedAddress()

        locationMapButton.setOnClickListener {
            listener.onShowDeliveryLocationMap(locationModel.deliveryLocation.locationPoint)
        }

        staticMapImageView.loadGlideUrl(
            imageUrl = locationModel.staticMapImageUrl,
            centerCrop = true,
            placeholder = R.drawable.placeholder_card
        )
    }

    private fun bindSectionDetailCompleteModel(
        binding: SectionDetailCompleteBinding
    ) = binding.run {
        actionTextView.setOnClickListener {
            listener.onCompleteDelivery()
        }
    }

    private fun bindSectionDetailCancelModel(
        binding: SectionDetailCancelBinding
    ) = binding.run {
        actionTextView.setOnClickListener {
            listener.onCancelDelivery()
        }
    }

    interface SectionDetailAdapterListener {
        fun onShowSellerDetail()
        fun onShowUserOrders()
        fun onShowDeliveryLocationMap(locationPoint: GeolocationPoint)
        fun onCompleteDelivery()
        fun onCancelDelivery()
    }
}

sealed class SectionDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class SectionDetailTimeViewHolder(
        val binding: SectionDetailTimeBinding
    ) : SectionDetailViewHolder(binding.root)

    class SectionDetailInfoViewHolder(
        val binding: SectionDetailInfoBinding
    ) : SectionDetailViewHolder(binding.root)

    class SectionDetailOrdersViewHolder(
        val binding: SectionDetailOrdersBinding
    ) : SectionDetailViewHolder(binding.root)

    class SectionDetailSellerViewHolder(
        val binding: SectionDetailSellerBinding
    ) : SectionDetailViewHolder(binding.root)

    class SectionDetailLocationViewHolder(
        val binding: SectionDetailLocationBinding
    ) : SectionDetailViewHolder(binding.root)

    class SectionDetailCompleteViewHolder(
        val binding: SectionDetailCompleteBinding
    ) : SectionDetailViewHolder(binding.root)

    class SectionDetailCancelViewHolder(
        val binding: SectionDetailCancelBinding
    ) : SectionDetailViewHolder(binding.root)
}

sealed class SectionDetailListModel {
    data class SectionDetailTimeModel(
        val deliveryTime: Date
    ) : SectionDetailListModel()

    data class SectionDetailInfoModel(
        val sectionId: String,
        val sectionTitle: String,
        val sectionDescription: String,
        val sectionDeliveryTime: Date,
        val sectionMaxUsers: Int,
        val sectionJoinedUsersCount: Int
    ) : SectionDetailListModel()

    object SectionDetailOrdersModel : SectionDetailListModel()

    data class SectionDetailSellerModel(
        val sellerId: String,
        val sellerName: String,
        val sellerImageUrl: String?,
        val sellerPhoneNum: String
    ) : SectionDetailListModel()

    data class SectionDetailLocationModel(
        val deliveryLocation: Geolocation,
        val staticMapImageUrl: String?
    ) : SectionDetailListModel()

    object SectionDetailCompleteModel : SectionDetailListModel()

    object SectionDetailCancelModel : SectionDetailListModel()
}

object SectionDetailListModelDiff : DiffUtil.ItemCallback<SectionDetailListModel>() {
    override fun areItemsTheSame(
        oldItem: SectionDetailListModel,
        newItem: SectionDetailListModel
    ): Boolean = when {
        oldItem is SectionDetailTimeModel && newItem is SectionDetailTimeModel -> true
        oldItem is SectionDetailInfoModel && newItem is SectionDetailInfoModel -> true
        oldItem is SectionDetailOrdersModel && newItem is SectionDetailOrdersModel -> true
        oldItem is SectionDetailSellerModel && newItem is SectionDetailSellerModel -> true
        oldItem is SectionDetailLocationModel && newItem is SectionDetailLocationModel -> true
        oldItem is SectionDetailCompleteModel && newItem is SectionDetailCompleteModel -> true
        oldItem is SectionDetailCancelModel && newItem is SectionDetailCancelModel -> true
        else -> false
    }

    override fun areContentsTheSame(
        oldItem: SectionDetailListModel,
        newItem: SectionDetailListModel
    ): Boolean = when {
        oldItem is SectionDetailTimeModel && newItem is SectionDetailTimeModel -> oldItem == newItem
        oldItem is SectionDetailInfoModel && newItem is SectionDetailInfoModel -> oldItem == newItem
        oldItem is SectionDetailOrdersModel && newItem is SectionDetailOrdersModel -> true
        oldItem is SectionDetailSellerModel && newItem is SectionDetailSellerModel -> oldItem == newItem
        oldItem is SectionDetailLocationModel && newItem is SectionDetailLocationModel -> oldItem == newItem
        oldItem is SectionDetailCompleteModel && newItem is SectionDetailCompleteModel -> true
        oldItem is SectionDetailCancelModel && newItem is SectionDetailCancelModel -> true
        else -> false
    }
}