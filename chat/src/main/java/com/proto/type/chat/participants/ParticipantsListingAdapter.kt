import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.inflate
import com.proto.type.base.extension.loadAvatar
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.list_item_participant.view.*

class ParticipantsListingAdapter(
    val context: Context,
    private val participantsList: MutableList<UserModel> = mutableListOf(),
    val onItemSelected: (action: Int, user: UserModel, position: Int) -> Unit
): RecyclerView.Adapter<ParticipantsListingAdapter.ViewHolder>() {

    private var admins = listOf<String>()

    fun setData(list: MutableList<UserModel>, adminIds: List<String>) {
        admins = adminIds
        participantsList.clear()
        participantsList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = parent.inflate(R.layout.list_item_participant, false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = participantsList[position]
        val itemView = holder.itemView
        itemView.civAvatar.loadAvatar(user)
        itemView.tvUserName.text = user.displayingName()
        itemView.tvUserRole.visibility = if (admins.contains(user.id)) View.VISIBLE else View.GONE
        itemView.layoutItemUser.setOnClickListener {
            onItemSelected(Actions.ACTION_SELECT, user, position)
        }
    }

    override fun getItemCount() = participantsList.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    object Actions {
        const val ACTION_SELECT = 0
    }
}