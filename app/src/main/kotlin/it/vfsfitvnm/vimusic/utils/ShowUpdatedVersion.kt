package it.vfsfitvnm.vimusic.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.ui.styling.LocalAppearance
import java.io.File

@SuppressLint("SuspiciousIndentation")
@Composable
fun ShowUpdatedVersion(
    updatedVersion: String,
    onDismiss: () -> Unit,
    modifier: Modifier
    ) {
    val file = File(LocalContext.current.filesDir, stringResource(R.string.version_filename))
    val newVersion = file.readText()

    val (_, typography) = LocalAppearance.current

    BasicText(
        text = "New version $newVersion",
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = typography.xs.semiBold.secondary,
        )
}