package org.cardna.presentation.ui.mypage.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.cardna.databinding.ItemAlarmFriendRequestBinding
import org.cardna.presentation.ui.mypage.view.setting.AlarmActivity
import org.cardna.presentation.ui.mypage.viewmodel.AlarmViewModel

class FriendRequestAdapter(
    private val clickListener: (FriendRequestData) -> Unit,
    private val friendRequestAcceptListener: (FriendRequestData) -> Unit,
    private val friendRequestRefuseListener: (FriendRequestData) -> Unit,
) : androidx.recyclerview.widget.ListAdapter<FriendRequestData, FriendRequestAdapter.FriendRequestViewHolder>(diffUtil) {

    var defaultStatus = true

    inner class FriendRequestViewHolder(private val binding: ItemAlarmFriendRequestBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(data: FriendRequestData) {
            binding.apply {

                //TODO  서버연결 후 data 연결
                tvItemAlarmFriendRequestFriendName.text = data.friendName
                tvItemAlarmFriendRequestDate.text = data.requestDate
                root.setOnClickListener {
                    clickListener(data)
                }
                //TODO  서버연결 후 수락 api
                mbItemAlarmFriendRequestAccept.setOnClickListener {
                    ctlItemAlarmFriendRequestBtn.visibility = View.INVISIBLE
                    tvItemFriendRequestAcceptText.visibility = View.VISIBLE
                    tvItemFriendRequestAcceptText.text=data.friendName+"님과 친구가 되었어요"
                    friendRequestAcceptListener(data)
                }
                //TODO  서버연결 후 거절 api
                mbItemAlarmFriendRequestRefuse.setOnClickListener {
                    friendRequestRefuseListener(data)
                }

                if (defaultStatus) {
                    viewItemAlarmFriendRequestDiv.visibility = View.INVISIBLE
                } else {
                    viewItemAlarmFriendRequestDiv.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestAdapter.FriendRequestViewHolder {
        val binding = ItemAlarmFriendRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendRequestViewHolder(binding)
    }

    override fun getItemCount() =
        if (defaultStatus) {
            AlarmActivity.DEFAULT_COUNT
        } else {
            currentList.size
        }

    override fun onBindViewHolder(holder: FriendRequestAdapter.FriendRequestViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<FriendRequestData>() {
            override fun areContentsTheSame(oldItem: FriendRequestData, newItem: FriendRequestData) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: FriendRequestData, newItem: FriendRequestData) =
                oldItem.friendName == newItem.friendName  //TODO 친구 id로 비교
        }
    }
}