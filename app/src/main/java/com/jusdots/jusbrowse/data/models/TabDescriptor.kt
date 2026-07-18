package com.jusdots.jusbrowse.data.models

data class TabDescriptor(
    val id: String,
    val url: String,
    val title: String,
    val favicon: String?,
    val isPrivate: Boolean,
    val containerId: String,
    val isDesktopMode: Boolean,
    val parentGroupId: String?,
    val isGroupMaster: Boolean
) {
    constructor(tab: BrowserTab) : this(
        id = tab.id,
        url = tab.url,
        title = tab.title,
        favicon = tab.favicon,
        isPrivate = tab.isPrivate,
        containerId = tab.containerId,
        isDesktopMode = tab.isDesktopMode,
        parentGroupId = tab.parentGroupId,
        isGroupMaster = tab.isGroupMaster
    )
}

fun BrowserTab.toDescriptor() = TabDescriptor(
    id = id, url = url, title = title, favicon = favicon,
    isPrivate = isPrivate, containerId = containerId,
    isDesktopMode = isDesktopMode, parentGroupId = parentGroupId,
    isGroupMaster = isGroupMaster
)

fun TabDescriptor.toBrowserTab() = BrowserTab(
    id = id, url = url, title = title, favicon = favicon,
    isPrivate = isPrivate, containerId = containerId,
    isDesktopMode = isDesktopMode, parentGroupId = parentGroupId,
    isGroupMaster = isGroupMaster
)
