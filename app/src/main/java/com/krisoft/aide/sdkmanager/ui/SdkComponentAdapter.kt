package com.krisoft.aide.sdkmanager.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.krisoft.aide.R
import com.krisoft.aide.sdkmanager.InstallProgress
import com.krisoft.aide.sdkmanager.SdkComponentType

private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_ITEM = 1

private sealed class SdkListItem {
    abstract val stableId: String

    data class Header(val title: String) : SdkListItem() {
        override val stableId = "header:$title"
    }

    data class Item(val state: SdkComponentUiState) : SdkListItem() {
        override val stableId = "item:${state.component.id}"
    }
}

class SdkComponentAdapter(
    private val onInstall: (com.krisoft.aide.sdkmanager.SdkComponent) -> Unit,
    private val onUninstall: (com.krisoft.aide.sdkmanager.SdkComponent) -> Unit,
    private val onRetry: (com.krisoft.aide.sdkmanager.SdkComponent) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<SdkListItem> = emptyList()

    fun submitList(states: List<SdkComponentUiState>) {
        val grouped = states
            .sortedBy { it.component.displayName }
            .groupBy { it.component.type }

        val newItems = mutableListOf<SdkListItem>()
        for (type in SdkComponentType.entries) {
            val group = grouped[type] ?: continue
            newItems += SdkListItem.Header(sectionTitleFor(type))
            newItems += group.map { SdkListItem.Item(it) }
        }

        val diffResult = DiffUtil.calculateDiff(SdkListItemDiffCallback(items, newItems))
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    private fun sectionTitleFor(type: SdkComponentType): String = when (type) {
        SdkComponentType.JDK -> "JDK"
        SdkComponentType.CMDLINE_TOOLS -> "Command-line Tools"
        SdkComponentType.PLATFORM -> "Android Platform"
        SdkComponentType.BUILD_TOOLS -> "Build Tools"
        SdkComponentType.PLATFORM_TOOLS -> "Platform Tools"
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is SdkListItem.Header -> VIEW_TYPE_HEADER
        is SdkListItem.Item -> VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_HEADER) {
            HeaderViewHolder(inflater.inflate(R.layout.item_sdk_section_header, parent, false))
        } else {
            ComponentViewHolder(inflater.inflate(R.layout.item_sdk_component, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is SdkListItem.Header -> (holder as HeaderViewHolder).bind(item.title)
            is SdkListItem.Item -> (holder as ComponentViewHolder).bind(item.state, onInstall, onUninstall, onRetry)
        }
    }

    override fun getItemCount(): Int = items.size

    private class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(title: String) {
            (itemView as TextView).text = title
        }
    }

    private class ComponentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameView: TextView = itemView.findViewById(R.id.textComponentName)
        private val statusView: TextView = itemView.findViewById(R.id.textComponentStatus)
        private val actionButton: Button = itemView.findViewById(R.id.buttonAction)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

        fun bind(
            state: SdkComponentUiState,
            onInstall: (com.krisoft.aide.sdkmanager.SdkComponent) -> Unit,
            onUninstall: (com.krisoft.aide.sdkmanager.SdkComponent) -> Unit,
            onRetry: (com.krisoft.aide.sdkmanager.SdkComponent) -> Unit,
        ) {
            val component = state.component
            nameView.text = "${component.displayName} (${component.version})"

            if (component.installMethod == com.krisoft.aide.sdkmanager.InstallMethod.VIA_SDKMANAGER) {
                progressBar.visibility = View.GONE
                statusView.text = "Dipasang lewat sdkmanager via Terminal (menunggu Phase 3)"
                actionButton.text = "Belum tersedia"
                actionButton.isEnabled = false
                actionButton.setOnClickListener(null)
                return
            }

            when (val progress = state.progress) {
                is InstallProgress.Downloading -> {
                    progressBar.visibility = View.VISIBLE
                    val percent = if (progress.totalBytes > 0) {
                        ((progress.bytesDownloaded * 100) / progress.totalBytes).toInt()
                    } else 0
                    progressBar.progress = percent
                    statusView.text = "Mengunduh... $percent%"
                    actionButton.text = "Batal"
                    actionButton.isEnabled = false
                    actionButton.setOnClickListener(null)
                }
                InstallProgress.Verifying -> {
                    progressBar.visibility = View.VISIBLE
                    statusView.text = "Memverifikasi..."
                    actionButton.isEnabled = false
                }
                InstallProgress.Extracting -> {
                    progressBar.visibility = View.VISIBLE
                    statusView.text = "Mengekstrak..."
                    actionButton.isEnabled = false
                }
                is InstallProgress.Failed -> {
                    progressBar.visibility = View.GONE
                    statusView.text = "Gagal: ${progress.message}"
                    actionButton.text = "Coba lagi"
                    actionButton.isEnabled = true
                    actionButton.setOnClickListener { onRetry(component) }
                }
                InstallProgress.Installed, InstallProgress.Idle -> {
                    progressBar.visibility = View.GONE
                    if (state.installed) {
                        statusView.text = "Terpasang"
                        actionButton.text = "Hapus"
                        actionButton.isEnabled = true
                        actionButton.setOnClickListener { onUninstall(component) }
                    } else {
                        statusView.text = "Belum terpasang"
                        actionButton.text = "Pasang"
                        actionButton.isEnabled = true
                        actionButton.setOnClickListener { onInstall(component) }
                    }
                }
            }
        }
    }
}

private class SdkListItemDiffCallback(
    private val old: List<SdkListItem>,
    private val new: List<SdkListItem>,
) : DiffUtil.Callback() {
    override fun getOldListSize() = old.size
    override fun getNewListSize() = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        old[oldItemPosition].stableId == new[newItemPosition].stableId

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        old[oldItemPosition] == new[newItemPosition]
}
