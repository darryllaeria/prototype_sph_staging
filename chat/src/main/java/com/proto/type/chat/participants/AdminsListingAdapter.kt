import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.inflate
import com.proto.type.base.extension.loadAvatar
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.list_item_participant.view.*

class AdminsListingAdapter(
    val context: Context,
    private val adminsList: MutableList<UserModel> = mutableListOf(),
    val onItemSelected: (action: Int, user: UserModel, position: Int) -> Unit
) : RecyclerView.Adapter<AdminsListingAdapter.ViewHolder>() {

    fun setData(list: MutableList<UserModel>) {
        adminsList.clear()
        adminsList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = parent.inflate(R.layout.list_item_participant, false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = adminsList[position]
        val itemView = holder.itemView
        itemView.civAvatar.loadAvatar(user)
        itemView.tvUserName.text = user.displayingName()
        itemView.layoutItemUser.setOnClickListener {
            onItemSelected(Actions.ACTION_SELECT, user, position)
        }
    }

    override fun getItemCount() = adminsList.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    object Actions {
        const val ACTION_SELECT = 0
    }
}