package it.vfsfitvnm.vimusic.ui.screens.playlist

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.stringResource
import androidx.media3.common.util.UnstableApi
import it.vfsfitvnm.compose.persist.PersistMapCleanup
import it.vfsfitvnm.compose.routing.RouteHandler
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.ui.components.themed.Scaffold
import it.vfsfitvnm.vimusic.ui.screens.globalRoutes

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@UnstableApi
@Composable
fun PlaylistScreen(
    browseId: String,
    params: String?,
    maxDepth: Int? = null
) {
    val saveableStateHolder = rememberSaveableStateHolder()
    PersistMapCleanup(tagPrefix = "playlist/$browseId")

    RouteHandler(listenToGlobalEmitter = true) {
        globalRoutes()

        host {
            Scaffold(
                topIconButtonId = R.drawable.chevron_back,
                onTopIconButtonClick = pop,
                tabIndex = 0,
                onTabChanged = { },
                tabColumnContent = { Item ->
                    Item(0, stringResource(R.string.songs), R.drawable.musical_notes)
                }
            ) { currentTabIndex ->
                saveableStateHolder.SaveableStateProvider(key = currentTabIndex) {
                    when (currentTabIndex) {
                        0 -> PlaylistSongList(
                            browseId = browseId,
                            params = params,
                            maxDepth = maxDepth
                        )
                    }
                }
            }
        }
    }
}
