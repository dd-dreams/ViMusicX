package it.vfsfitvnm.vimusic.enums

enum class NavigationTab {
    `Default`,
    `QuickPics`,
    `Songs`,
    `Playlists`,
    `Artists`,
    `Albums`,
    `Settings`;

    val index: Int
        get() = when (this) {
            Default -> 100
            QuickPics -> 0
            Songs -> 1
            Playlists -> 2
            Artists -> 3
            Albums -> 4
            Settings -> 5
        }

}