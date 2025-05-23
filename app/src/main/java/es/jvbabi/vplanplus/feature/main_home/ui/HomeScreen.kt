package es.jvbabi.vplanplus.feature.main_home.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.DayType
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.Menu
import es.jvbabi.vplanplus.feature.main_home.ui.components.DayPager
import es.jvbabi.vplanplus.feature.main_home.ui.components.DayView
import es.jvbabi.vplanplus.feature.main_home.ui.components.Head
import es.jvbabi.vplanplus.feature.main_home.ui.components.ImportantHeader
import es.jvbabi.vplanplus.feature.main_home.ui.components.LastSyncText
import es.jvbabi.vplanplus.feature.main_home.ui.components.PlanHeader
import es.jvbabi.vplanplus.feature.main_home.ui.components.QuickActions
import es.jvbabi.vplanplus.feature.main_home.ui.components.VersionHintsInformation
import es.jvbabi.vplanplus.feature.main_home.ui.components.banners.BadCredentialsBanner
import es.jvbabi.vplanplus.feature.main_home.ui.components.cards.MissingVppIdLinkToProfileCard
import es.jvbabi.vplanplus.feature.main_home.ui.components.views.NoData
import es.jvbabi.vplanplus.feature.main_home.ui.preview.navBar
import es.jvbabi.vplanplus.feature.main_homework.add.ui.AddHomeworkSheet
import es.jvbabi.vplanplus.feature.main_homework.add.ui.AddHomeworkSheetInitialValues
import es.jvbabi.vplanplus.feature.migration.ui.components.BetaTestAdvert
import es.jvbabi.vplanplus.feature.migration.ui.components.NewAppCard
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.onLogin
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.ui.common.keyboardAsState
import es.jvbabi.vplanplus.ui.common.openLink
import es.jvbabi.vplanplus.ui.preview.GroupPreview
import es.jvbabi.vplanplus.ui.preview.PreviewFunction
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.ui.preview.ProfilePreview.toActiveVppId
import es.jvbabi.vplanplus.ui.preview.SchoolPreview
import es.jvbabi.vplanplus.ui.preview.VppIdPreview
import es.jvbabi.vplanplus.ui.screens.Screen
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

const val PAGER_SIZE = 365*2

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    navBar: @Composable (expanded: Boolean) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    startDate: LocalDate = LocalDate.now(),
    onNewAppClicked: () -> Unit
) {
    val state = homeViewModel.state
    val context = LocalContext.current

    LaunchedEffect(key1 = startDate) { homeViewModel.setSelectedDate(startDate) }

    HomeScreenContent(
        navBar = navBar,
        state = state,
        onBookRoomClicked = { navHostController.navigate(Screen.SearchAvailableRoomScreen.route) },
        onOpenMenu = homeViewModel::onMenuOpenedChange,
        onSetSelectedDate = homeViewModel::setSelectedDate,
        onInfoExpandChange = homeViewModel::onInfoExpandChange,
        onVersionHintsClosed = homeViewModel::hideVersionHintsDialog,

        onSwitchProfile = homeViewModel::switchProfile,
        onManageProfiles = {
            homeViewModel.onMenuOpenedChange(false)
            navHostController.navigate(Screen.SettingsProfileScreen.route)
        },
        onManageProfile = {
            homeViewModel.onMenuOpenedChange(false)
            navHostController.navigate("${Screen.SettingsProfileScreen.route}/${it.id}")
        },
        onNewAppClicked = onNewAppClicked,
        onOpenNews = { homeViewModel.onMenuOpenedChange(false); navHostController.navigate(Screen.NewsScreen.route) },
        onOpenSettings = { homeViewModel.onMenuOpenedChange(false); navHostController.navigate(Screen.SettingsScreen.route) },
        onPrivacyPolicyClicked = {
            openLink(
                context,
                "${state.server.uiHost}/privacy"
            )
        },
        onRepositoryClicked = {
            openLink(
                context,
                "https://github.com/VPlanPlus-Project/VPlanPlus"
            )
        },
        onOpenSearch = { navHostController.navigate(Screen.SearchScreen.route) },
        onRefreshClicked = { homeViewModel.onMenuOpenedChange(false); homeViewModel.onRefreshClicked(context) },
        onFixVppIdSessionClicked = { onLogin(context, state.server) },
        onFixVppIdLinksClicked = { navHostController.navigate(Screen.SettingsVppIdScreen.route) },
        onIgnoreInvalidVppIdSessions = homeViewModel::ignoreInvalidVppIdSessions,
        onFixCredentialsClicked = { navHostController.navigate("${Screen.SettingsProfileScreen.route}?task=update_credentials&schoolId=${state.currentProfile?.getSchool()?.id}") },
        onSendFeedback = { navHostController.navigate(Screen.SettingsHelpFeedbackScreen.route) },
        onNewAppBannerClicked = { homeViewModel.onNewAppBannerClicked() },
        onNewAppBannerClosed = { homeViewModel.onNewAppBannerClosed() },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenContent(
    navBar: @Composable (expanded: Boolean) -> Unit,
    state: HomeState,
    onOpenMenu: (state: Boolean) -> Unit = {},
    onSetSelectedDate: (date: LocalDate) -> Unit = {},
    onInfoExpandChange: (to: Boolean) -> Unit = {},
    onBookRoomClicked: () -> Unit,
    onOpenSearch: () -> Unit = {},

    onSwitchProfile: (to: Profile) -> Unit,
    onManageProfiles: () -> Unit = {},
    onManageProfile: (profile: Profile) -> Unit = {},
    onOpenNews: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onPrivacyPolicyClicked: () -> Unit = {},
    onRepositoryClicked: () -> Unit = {},
    onRefreshClicked: () -> Unit = {},

    onFixVppIdSessionClicked: () -> Unit = {},
    onIgnoreInvalidVppIdSessions: () -> Unit = {},
    onFixVppIdLinksClicked: () -> Unit = {},

    onFixCredentialsClicked: () -> Unit = {},

    onSendFeedback: () -> Unit = {},
    onNewAppClicked: () -> Unit = {},

    onNewAppBannerClicked: () -> Unit = {},
    onNewAppBannerClosed: () -> Unit = {},

    onVersionHintsClosed: (untilNextVersion: Boolean) -> Unit = {}
) {
    if (state.currentProfile == null) return

    if (state.isVersionHintsDialogOpen && state.versionHint != null) VersionHintsInformation(
        currentVersion = state.currentVersion,
        hint = state.versionHint,
        onCloseUntilNextTime = { onVersionHintsClosed(false) },
        onCloseUntilNextVersion = { onVersionHintsClosed(true) }
    )

    var addHomeworkSheetInitialValues by rememberSaveable<MutableState<AddHomeworkSheetInitialValues?>> { mutableStateOf(null) }
    if (addHomeworkSheetInitialValues != null) {
        AddHomeworkSheet(
            onClose = { addHomeworkSheetInitialValues = null },
            initialValues = addHomeworkSheetInitialValues ?: AddHomeworkSheetInitialValues()
        )
    }


    val contentPagerState = rememberPagerState(pageCount = { PAGER_SIZE }, initialPage = PAGER_SIZE / 2)
    val lazyListState = rememberLazyListState()

    LaunchedEffect(key1 = state.selectedDate) {
        contentPagerState.animateScrollToPage(page = LocalDate.now().until(state.selectedDate, ChronoUnit.DAYS).toInt() + PAGER_SIZE / 2)
    }

    LaunchedEffect(key1 = state.autoNextDay) {
        if (state.autoNextDay) onSetSelectedDate(state.nextSchoolDayWithData ?: return@LaunchedEffect) }

    LaunchedEffect(key1 = contentPagerState.settledPage) {
        val date = LocalDate.now().plusDays(contentPagerState.targetPage.toLong() - PAGER_SIZE / 2)
        onSetSelectedDate(date)

        delay(150)
        if (!contentPagerState.isScrollInProgress && lazyListState.firstVisibleItemIndex > 1) {
            lazyListState.animateScrollToItem(1, 0)
        }
    }

    Scaffold(
        bottomBar = { navBar(!keyboardAsState().value) },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            Head(
                profile = state.currentProfile,
                currentTime = state.currentTime,
                isSyncing = state.isSyncRunning,
                showNotificationDot = state.hasUnreadNews,
                onProfileClicked = { onOpenMenu(true) },
                onSearchClicked = onOpenSearch,
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                state = lazyListState
            ) {
                item appHead@{
                    Collapsable(
                        expand = state.hasMissingVppIdToProfileLinks || state.hasInvalidVppIdSession
                    ) { ImportantHeader(Modifier.padding(horizontal = 16.dp)) }
                    Collapsable(expand = state.hasInvalidVppIdSession) {
                        InfoCard(
                            imageVector = Icons.Default.NoAccounts,
                            title = stringResource(id = R.string.home_invalidVppIdSessionTitle),
                            text = stringResource(id = R.string.home_invalidVppIdSessionText),
                            buttonText1 = stringResource(id = R.string.ignore),
                            buttonAction1 = onIgnoreInvalidVppIdSessions,
                            buttonText2 = stringResource(id = R.string.fix),
                            buttonAction2 = onFixVppIdSessionClicked,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    Collapsable(expand = state.hasMissingVppIdToProfileLinks) {
                        MissingVppIdLinkToProfileCard(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            onFixClicked = onFixVppIdLinksClicked
                        )
                    }
                    BadCredentialsBanner(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        expand = state.currentProfile.getSchool().credentialsValid == false,
                        onFixCredentialsClicked = onFixCredentialsClicked
                    )

                    val context = LocalContext.current
                    if (isPackageInstalled(context, "plus.vplan.app")) NewAppCard(onNewAppClicked)
                    Spacer8Dp()
                    if (state.newAppBanner != NewAppBannerType.HIDDEN) BetaTestAdvert(
                        onClicked = {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = "https://beta.vplan.plus?ref=old_app".toUri()
                            }
                            context.startActivity(intent)
                            onNewAppBannerClicked()
                        },
                        canClose = state.newAppBanner == NewAppBannerType.CAN_HIDE,
                        onCloseClicked = onNewAppBannerClosed
                    )

                    QuickActions(
                        modifier = Modifier.padding(vertical = 16.dp),
                        nextSchoolDayWithData = state.nextSchoolDayWithData,
                        selectedDate = state.selectedDate,
                        onNewHomeworkClicked = { addHomeworkSheetInitialValues = AddHomeworkSheetInitialValues() },
                        onFindAvailableRoomClicked = onBookRoomClicked,
                        onPrepareNextDayClicked = { onSetSelectedDate(state.nextSchoolDayWithData ?: state.currentTime.toLocalDate().plusDays(1L)) },
                        onSendFeedback = onSendFeedback,
                        allowHomeworkQuickAction = (state.currentProfile as? ClassProfile)?.isHomeworkEnabled ?: false
                    )
                }
                stickyHeader dateSelector@{
                    Column(Modifier.background(MaterialTheme.colorScheme.background)) {
                        PlanHeader(
                            modifier = Modifier.padding(bottom = 4.dp),
                            currentDate = state.currentTime.toLocalDate(),
                            selectedDate = state.selectedDate,
                            onSetSelectedDate = onSetSelectedDate,
                        )
                        DayPager(
                            selectedDate = state.selectedDate,
                            today = state.currentTime.toLocalDate(),
                            onDateSelected = onSetSelectedDate,
                            holidays = state.holidays
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .drawWithContent {
                                drawContent()
                                drawRect(brush = Brush.verticalGradient(listOf(Color.DarkGray.copy(alpha = .3f), Color.DarkGray.copy(alpha = 0f))), topLeft = Offset(0f, 0f), size = size)
                            }
                    ) {}
                }
                item {
                    HorizontalPager(
                        state = contentPagerState,
                        modifier = Modifier.fillMaxSize(),
                        pageSize = PageSize.Fill,
                        verticalAlignment = Alignment.Top,
                        flingBehavior = PagerDefaults.flingBehavior(
                            state = contentPagerState,
                            snapAnimationSpec = tween(100)
                        ),
                        beyondViewportPageCount = 7
                    ) contentHost@{
                        val date = LocalDate.now().plusDays(it.toLong() - PAGER_SIZE / 2)
                        val day = state.days[date]

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) content@{
                            val start by rememberSaveable { mutableLongStateOf(System.currentTimeMillis() / 1000) }
                            val timeOffset = 1
                            AnimatedVisibility(visible = day == null && start + timeOffset < System.currentTimeMillis() / 1000) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    LinearProgressIndicator(Modifier.fillMaxWidth(.5f))
                                    Text(
                                        text = stringResource(id = R.string.home_longerThanExpected),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 16.dp, start = 8.dp, end = 8.dp)
                                    )
                                }
                            }

                            val animationDuration = 300
                            AnimatedVisibility(
                                modifier = Modifier.fillMaxSize(),
                                visible = day != null,
                                enter = fadeIn(animationSpec = tween(animationDuration)) + slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(animationDuration)),
                                exit = fadeOut(animationSpec = tween(animationDuration))
                            ) dayViewRoot@{
                                Column {
                                    if (day?.lessons?.size == 0 && day.type == DayType.NORMAL) NoData(date)
                                    else DayView(
                                        day = day,
                                        currentTime = state.currentTime,
                                        showCountdown = state.currentTime.toLocalDate().isEqual(date),
                                        isInfoExpanded = if (state.currentTime.toLocalDate().isEqual(date)) state.infoExpanded else null,
                                        currentProfile = state.currentProfile,
                                        bookings = state.bookings,
                                        homework = state.homework,
                                        onChangeInfoExpandState = onInfoExpandChange,
                                        onAddHomework = { defaultLesson -> addHomeworkSheetInitialValues = AddHomeworkSheetInitialValues(defaultLessonId = defaultLesson?.defaultLessonId) },
                                        onBookRoomClicked = onBookRoomClicked,
                                        hideFinishedLessons = state.hideFinishedLessons,
                                    )
                                }
                            }
                            LastSyncText(lastSync = state.lastSync, modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
    }

    Menu(
        isVisible = state.menuOpened,
        isSyncing = state.isSyncRunning,
        profiles = state.profiles,
        hasUnreadNews = state.hasUnreadNews,
        selectedProfile = state.currentProfile,
        onCloseMenu = { onOpenMenu(false) },
        onProfileClicked = onSwitchProfile,
        onManageProfilesClicked = onManageProfiles,
        onProfileLongClicked = onManageProfile,
        onNewsClicked = onOpenNews,
        onSettingsClicked = onOpenSettings,
        onPrivacyPolicyClicked = onPrivacyPolicyClicked,
        onRepositoryClicked = onRepositoryClicked,
        onRefreshClicked = onRefreshClicked
    )
}

@OptIn(PreviewFunction::class)
@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    val school = SchoolPreview.generateRandomSchools(1).first()
    val group = GroupPreview.generateGroup(school)
    val profile = ProfilePreview.generateClassProfile(group, VppIdPreview.generateVppId(group).toActiveVppId())
    HomeScreenContent(
        navBar = navBar,
        state = HomeState(
            currentProfile = profile,
            menuOpened = false,
            hasUnreadNews = true,
            profiles = listOf(profile),
            hasMissingVppIdToProfileLinks = true,
            lastSync = ZonedDateTime.now().minusDays(1L)
        ),
        onBookRoomClicked = {},
        onOpenMenu = {},
        onSetSelectedDate = {},
        onInfoExpandChange = {},
        onSwitchProfile = {},
    )
}

@Composable
fun Collapsable(modifier: Modifier = Modifier, expand: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        modifier = modifier,
        visible = expand,
        enter = expandVertically(tween(250)),
        exit = shrinkVertically(tween(250))
    ) {
        content()
    }
}

fun isPackageInstalled(context: Context, packageName: String?): Boolean {
    var result = false
    try {
        // is the application installed?
        context.packageManager.getPackageInfo(packageName!!, PackageManager.GET_ACTIVITIES)
        result = true
    } catch (e: PackageManager.NameNotFoundException) {
        //Not installed
    }
    return result
}