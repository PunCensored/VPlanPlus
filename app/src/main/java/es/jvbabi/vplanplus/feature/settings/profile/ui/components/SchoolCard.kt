package es.jvbabi.vplanplus.feature.settings.profile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.feature.settings.profile.ui.components.dialogs.SchoolDetailsDialog
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.preview.GroupPreview
import es.jvbabi.vplanplus.ui.preview.RoomPreview
import es.jvbabi.vplanplus.ui.preview.ProfilePreview as PreviewProfile
import es.jvbabi.vplanplus.ui.preview.SchoolPreview as PreviewSchool

@Composable
fun SchoolCard(
    school: School,
    profiles: List<Profile>,
    onAddProfileClicked: () -> Unit,
    onProfileClicked: (Profile) -> Unit,
    onDeleteRequest: () -> Unit,
    onShareRequest: () -> Unit,
    onUpdateCredentialsRequest: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showInfoDialog by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f, true)
                        .padding(top = 16.dp, bottom = 8.dp, end = 16.dp),
                ) {
                    Text(
                        text = school.name,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(text = school.username, style = MaterialTheme.typography.labelSmall)
                    if (school.credentialsValid == false) RowVerticalCenter(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = stringResource(id = R.string.home_invalidCredentialsTitle),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(text = stringResource(id = R.string.home_invalidCredentialsTitle), style = MaterialTheme.typography.labelSmall)
                    }
                }
                Box(
                    modifier = Modifier
                ) {
                    IconButton(onClick = { menuExpanded = !menuExpanded }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(id = R.string.menu)
                        )
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.settings_profileDeleteSchool)) },
                            onClick = { menuExpanded = false; onDeleteRequest() },
                            leadingIcon = { Icon(imageVector = Icons.Default.Delete, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.settings_profileShareSchool)) },
                            onClick = { menuExpanded = false; onShareRequest() },
                            leadingIcon = { Icon(imageVector = Icons.Default.Share, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.settings_profileUpdateSchoolCredentials)) },
                            onClick = { menuExpanded = false; onUpdateCredentialsRequest() },
                            leadingIcon = { Icon(imageVector = Icons.Default.Key, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.profileManagement_detailsLabel)) },
                            onClick = { menuExpanded = false; showInfoDialog = true },
                            leadingIcon = { Icon(imageVector = Icons.Default.Info, contentDescription = null) }
                        )
                    }
                }
            }
            LazyRow(
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp, bottom = 16.dp)
                    .fillMaxWidth()
            ) {
                items(
                    profiles.sortedBy { it.getType().ordinal.toString() + it.displayName }
                ) { profile ->
                    ProfileCard(
                        type = profile.getType(),
                        name = profile.displayName,
                        onClick = { onProfileClicked(profile) }
                    )
                }
                item {
                    ProfileCard(
                        type = null,
                        name = "+",
                        onClick = onAddProfileClicked
                    )
                }
            }
        }
    }

    if (showInfoDialog) SchoolDetailsDialog(school = school) {
        showInfoDialog = false
    }
}

@Preview
@Composable
private fun SchoolCardPreview() {
    val school = PreviewSchool.generateRandomSchools(1).first().copy(credentialsValid = false)
    val room = RoomPreview.generateRoom(school)
    val group = GroupPreview.generateGroup(school)
    SchoolCard(
        school = school,
        profiles = listOf(
            PreviewProfile.generateRoomProfile(room),
            PreviewProfile.generateClassProfile(group),
        ),
        onAddProfileClicked = {},
        onProfileClicked = {},
        onShareRequest = {},
        onDeleteRequest = {},
        onUpdateCredentialsRequest = {},
    )
}