package it.vfsfitvnm.vimusic.ui.screens.settings

import android.content.Intent
import android.os.Build
import android.text.format.Formatter
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.media3.common.util.UnstableApi
import coil.Coil
import coil.annotation.ExperimentalCoilApi
import it.vfsfitvnm.vimusic.LocalPlayerAwareWindowInsets
import it.vfsfitvnm.vimusic.LocalPlayerServiceBinder
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.enums.CoilDiskCacheMaxSize
import it.vfsfitvnm.vimusic.enums.ExoPlayerDiskCacheMaxSize
import it.vfsfitvnm.vimusic.enums.ExoPlayerDiskDownloadCacheMaxSize
import it.vfsfitvnm.vimusic.ui.components.themed.HeaderWithIcon
import it.vfsfitvnm.vimusic.ui.styling.LocalAppearance
import it.vfsfitvnm.vimusic.utils.coilDiskCacheMaxSizeKey
import it.vfsfitvnm.vimusic.utils.exoPlayerAlternateCacheLocationKey
import it.vfsfitvnm.vimusic.utils.exoPlayerDiskCacheMaxSizeKey
import it.vfsfitvnm.vimusic.utils.exoPlayerDiskDownloadCacheMaxSizeKey
import it.vfsfitvnm.vimusic.utils.rememberPreference

@OptIn(ExperimentalCoilApi::class)
@ExperimentalAnimationApi
@UnstableApi
@Composable
fun CacheSettings() {
    val context = LocalContext.current
    val (colorPalette) = LocalAppearance.current
    val binder = LocalPlayerServiceBinder.current

    var coilDiskCacheMaxSize by rememberPreference(
        coilDiskCacheMaxSizeKey,
        CoilDiskCacheMaxSize.`128MB`
    )
    var exoPlayerDiskCacheMaxSize by rememberPreference(
        exoPlayerDiskCacheMaxSizeKey,
        ExoPlayerDiskCacheMaxSize.`2GB`
    )

    var exoPlayerDiskDownloadCacheMaxSize by rememberPreference(
        exoPlayerDiskDownloadCacheMaxSizeKey,
        ExoPlayerDiskDownloadCacheMaxSize.`2GB`
    )

    var exoPlayerAlternateCacheLocation by rememberPreference(
        exoPlayerAlternateCacheLocationKey,""
    )

    val dirRequest = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        uri?.let {
            context.applicationContext.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.applicationContext.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            Log.d("exoAltLocationCache",uri.path.toString())
            exoPlayerAlternateCacheLocation = uri.path.toString()
        }
    }

    //val release = Build.VERSION.RELEASE;
    val sdkVersion = Build.VERSION.SDK_INT;
    //if (sdkVersion.toShort() < 29) exoPlayerAlternateCacheLocation=""
    //Log.d("SystemInfo","Android SDK: " + sdkVersion + " (" + release +")")

    Column(
        modifier = Modifier
            .background(colorPalette.background0)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                LocalPlayerAwareWindowInsets.current
                    .only(WindowInsetsSides.Vertical + WindowInsetsSides.End)
                    .asPaddingValues()
            )
    ) {
        HeaderWithIcon(
            title = stringResource(R.string.cache),
            iconId = R.drawable.sync,
            enabled = false,
            showIcon = true,
            modifier = Modifier,
            onClick = {}
        )

        SettingsDescription(text = stringResource(R.string.cache_cleared))

        Coil.imageLoader(context).diskCache?.let { diskCache ->
            val diskCacheSize = remember(diskCache) {
                diskCache.size
            }

            SettingsGroupSpacer()

            SettingsEntryGroupText(title = stringResource(R.string.image_cache))

            SettingsDescription(
                text = "${
                    Formatter.formatShortFileSize(
                        context,
                        diskCacheSize
                    )
                } ${stringResource(R.string.used)} (${diskCacheSize * 100 / coilDiskCacheMaxSize.bytes.coerceAtLeast(1)}%)"
            )

            EnumValueSelectorSettingsEntry(
                title = stringResource(R.string.max_size),
                selectedValue = coilDiskCacheMaxSize,
                onValueSelected = { coilDiskCacheMaxSize = it },
                valueText = {
                    when (it) {
                        CoilDiskCacheMaxSize.`128MB` -> "128MB"
                        CoilDiskCacheMaxSize.`256MB`-> "256MB"
                        CoilDiskCacheMaxSize.`512MB`-> "512MB"
                        CoilDiskCacheMaxSize.`1GB`-> "1GB"
                        CoilDiskCacheMaxSize.`2GB` -> "2GB"
                    }
                }
            )
        }

        binder?.cache?.let { cache ->
            val diskCacheSize by remember {
                derivedStateOf {
                    cache.cacheSpace
                }
            }

            SettingsGroupSpacer()

            SettingsEntryGroupText(title = stringResource(R.string.song_cache_by_player))

            if(exoPlayerDiskCacheMaxSize != ExoPlayerDiskCacheMaxSize.Disabled)
            SettingsDescription(
                text = buildString {
                    append(Formatter.formatShortFileSize(context, diskCacheSize))
                    append(" ${stringResource(R.string.used)}")
                    when (val size = exoPlayerDiskCacheMaxSize) {
                        ExoPlayerDiskCacheMaxSize.Unlimited -> {}
                        else -> append(" (${diskCacheSize * 100 / size.bytes}%)")
                    }
                }
            )

            EnumValueSelectorSettingsEntry(
                title = stringResource(R.string.max_size),
                selectedValue = exoPlayerDiskCacheMaxSize,
                onValueSelected = { exoPlayerDiskCacheMaxSize = it },
                valueText = {
                    when (it) {
                        ExoPlayerDiskCacheMaxSize.Disabled -> stringResource(R.string.turn_off)
                        ExoPlayerDiskCacheMaxSize.Unlimited -> stringResource(R.string.unlimited)
                        ExoPlayerDiskCacheMaxSize.`32MB` -> "32MB"
                        ExoPlayerDiskCacheMaxSize.`512MB` -> "512MB"
                        ExoPlayerDiskCacheMaxSize.`1GB` -> "1GB"
                        ExoPlayerDiskCacheMaxSize.`2GB` -> "2GB"
                        ExoPlayerDiskCacheMaxSize.`4GB` -> "4GB"
                        ExoPlayerDiskCacheMaxSize.`8GB` -> "8GB"

                    }
                }
            )
        }

        binder?.downloadCache?.let { downloadCache ->
            val diskDownloadCacheSize by remember {
                derivedStateOf {
                    downloadCache.cacheSpace
                }
            }

            SettingsGroupSpacer()

            SettingsEntryGroupText(title = stringResource(R.string.song_cache_by_download))

            if(exoPlayerDiskDownloadCacheMaxSize != ExoPlayerDiskDownloadCacheMaxSize.Disabled)
            SettingsDescription(
                text = buildString {
                    append(Formatter.formatShortFileSize(context, diskDownloadCacheSize))
                    append(" ${stringResource(R.string.used)}")
                    when (val size = exoPlayerDiskDownloadCacheMaxSize) {
                        ExoPlayerDiskDownloadCacheMaxSize.Unlimited -> {}
                        else -> append(" (${diskDownloadCacheSize * 100 / size.bytes}%)")
                    }
                }
            )

            EnumValueSelectorSettingsEntry(
                title = stringResource(R.string.max_size),
                selectedValue = exoPlayerDiskDownloadCacheMaxSize,
                onValueSelected = { exoPlayerDiskDownloadCacheMaxSize = it },
                valueText = {
                    when (it) {
                        ExoPlayerDiskDownloadCacheMaxSize.Disabled -> stringResource(R.string.turn_off)
                        ExoPlayerDiskDownloadCacheMaxSize.Unlimited -> stringResource(R.string.unlimited)
                        ExoPlayerDiskDownloadCacheMaxSize.`32MB` -> "32MB"
                        ExoPlayerDiskDownloadCacheMaxSize.`512MB` -> "512MB"
                        ExoPlayerDiskDownloadCacheMaxSize.`1GB` -> "1GB"
                        ExoPlayerDiskDownloadCacheMaxSize.`2GB` -> "2GB"
                        ExoPlayerDiskDownloadCacheMaxSize.`4GB` -> "4GB"
                        ExoPlayerDiskDownloadCacheMaxSize.`8GB` -> "8GB"

                    }
                }
            )
        }


        SettingsGroupSpacer()

        SettingsEntryGroupText(title = stringResource(R.string.folder_cache))

        SettingsDescription(
            text = stringResource(R.string.custom_cache_from_android_10_may_not_be_available)
        )

        SettingsEntry(
            title = stringResource(R.string.cache_location_folder),
            text = if (exoPlayerAlternateCacheLocation == "") "Default" else exoPlayerAlternateCacheLocation,
            isEnabled = if (sdkVersion.toShort() < 29) true else false,
            onClick = {
                dirRequest.launch(null)
            }
        )

        ImportantSettingsDescription(text = stringResource(R.string.cache_reset_by_clicking_button))

        SettingsEntry(
            title = stringResource(R.string.reset_cache_location_folder),
            text = "",
            isEnabled = if (sdkVersion.toShort() < 29) true else false,
            onClick = {
                exoPlayerAlternateCacheLocation = ""
            }
        )


    }
}
