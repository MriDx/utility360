package tech.sumato.utility360.presentation.fragments.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.R
import tech.sumato.utility360.databinding.NotificationItemViewBinding
import tech.sumato.utility360.presentation.fragments.base.listing.DemoListingFragment
import tech.sumato.utility360.presentation.fragments.base.listing.ListingFragment


@AndroidEntryPoint
class NotificationsFragment : ListingFragment() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchIcon(false)

        setTitle("Notifications")

    }

/*

    override fun getTitle(): String {
        return "Notifications"
    }
*/

}

/*
@AndroidEntryPoint
class NotificationsFragment : DemoListingFragment() {

    // TODO: replace with live

    data class PersonData(
        val name: String = "",
        val avatar: String = ""
    )

    data class NotificationData(
        val id: Int = 0,
        val from: PersonData? = null,
        val description: String = "",
        val postedAt: String = ""
    )

    val notifications = listOf(
        NotificationData(
            id = 6,
            from = PersonData(
                name = "Steve Rogers",
                avatar = "https://avatarfiles.alphacoders.com/272/272010.png"
            ),
            description = "<strong>We did it, we've won !</strong> - said <strong>Steve Rogers</strong> after <strong>Iron Man</strong> destroyed the nuclear bomb",
            postedAt = "1 sec ago"
        ),
        NotificationData(
            id = 1,
            from = PersonData(
                name = "Nick Fury",
                avatar = "https://avatarfiles.alphacoders.com/114/11447.jpg"
            ),
            description = "<strong>Nick Fury</strong> assigned <strong>Tony Stark</strong> to destroy the launched nuclear bomb",
            postedAt = "2 min ago"
        ),
        NotificationData(
            id = 2,
            from = PersonData(
                name = "Nick Fury",
                avatar = "https://avatarfiles.alphacoders.com/114/11447.jpg"
            ),
            description = "<strong>Nick Fury</strong> assigned <strong>The Avengers</strong> to evacuate the city",
            postedAt = "22 min ago"
        ),
        NotificationData(
            id = 3,
            from = PersonData(
                name = "Nick Fury",
                avatar = "https://avatarfiles.alphacoders.com/114/11447.jpg"
            ),
            description = "<strong>Nick Fury</strong> assigned <strong>The Avengers</strong> to eliminate the threat and capture <strong>Loki</strong>",
            postedAt = "2 hour ago"
        ),
        NotificationData(
            id = 4,
            from = PersonData(
                name = "Loki Odinson",
                avatar = "https://avatarfiles.alphacoders.com/295/295374.jpg"
            ),
            description = "<strong>Loki Odinson</strong> announced on national radio that he wants the <strong>Space Stone</strong> to handed over",
            postedAt = "5 hour ago"
        ),
        NotificationData(
            id = 5,
            from = PersonData(
                name = "Tony Stark",
                avatar = "https://avatarfiles.alphacoders.com/839/83919.jpg"
            ),
            description = "<strong>' I'm Iron Man '</strong> - <strong>Tony Stark</strong> revealed on press conference that he is the Iron Man",
            postedAt = "2 mons ago"
        )
    )

    override fun showSearch() = false
    override fun showFilter() = false

    override fun getItemsCount(): Int {
        return notifications.size
    }

    override fun buildItem(parent: ViewGroup, position: Int): View {
        return DataBindingUtil.inflate<NotificationItemViewBinding>(
            LayoutInflater.from(parent.context),
            R.layout.notification_item_view,
            parent,
            false
        ).root
    }

    override fun bindItem(parent: View, position: Int) {
        super.bindItem(parent, position)
        val data = notifications[position]
        DataBindingUtil.bind<NotificationItemViewBinding>(parent)!!.apply {
            Glide.with(parent.context)
                .asBitmap()
                .load(data.from!!.avatar)
                .into(avatarView)

            postedView.text = data.postedAt
            descriptionView.text =
                HtmlCompat.fromHtml(data.description, FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)


        }
    }

    override fun getTitle(): String {
        return "Notifications"
    }


}*/
