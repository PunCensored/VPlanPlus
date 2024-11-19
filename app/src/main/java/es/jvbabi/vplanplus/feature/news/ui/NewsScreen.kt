package es.jvbabi.vplanplus.feature.news.ui

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.preview.News
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.util.DateUtils
import java.time.ZonedDateTime

@Composable
fun NewsScreen(
    navHostController: NavHostController,
    viewModel: NewsViewModel = hiltViewModel()
) {

    NewsScreenContent(
        state = viewModel.state.value,
        goBack = { navHostController.navigateUp() },
        refresh = { viewModel.update() },
        onMessageOpened = { messageId ->
            navHostController.navigate("${Screen.NewsDetailScreen.route}/$messageId")
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreenContent(
    state: NewsState,
    goBack: () -> Unit = {},
    refresh: () -> Unit = {},
    onMessageOpened: (String) -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(id = R.string.news_homeTitle)) },
                navigationIcon = {
                    IconButton(onClick = { goBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(
                                id = R.string.back
                            )
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        val pullRefreshState = rememberPullToRefreshState()
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { refresh() },
            state = pullRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                var unreadDone = false
                if (state.news.isNotEmpty() || !state.initialized) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        items(state.news.sortedBy { (!it.isRead).toString() + (it.date.toInstant().epochSecond) }.reversed()) {
                            if (!unreadDone && it.isRead && state.news.any { n -> !n.isRead }) {
                                unreadDone = true
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                ) {
                                    HorizontalDivider(modifier = Modifier.offset(x = 0.dp, y = 8.dp))
                                    Row(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.surface)
                                            .align(Alignment.Center)
                                            .padding(horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Archive, contentDescription = null, modifier = Modifier
                                                .padding(end = 4.dp)
                                                .size(20.dp)
                                        )
                                        Text(text = "Archiv")
                                    }
                                }
                            }
                            NewsCard(
                                title = it.title,
                                content = it.content,
                                date = it.date,
                                isRead = it.isRead,
                                onClick = { onMessageOpened(it.id) }
                            )
                        }
                    }
                } else {
                    val colorScheme = MaterialTheme.colorScheme
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = CenterHorizontally,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Newspaper,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .strikethrough(colorScheme)
                            )
                            Text(text = stringResource(id = R.string.news_noNews))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsCard(
    title: String,
    content: String,
    date: ZonedDateTime,
    isRead: Boolean,
    onClick: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val spannableString = SpannableStringBuilder(content).toString()
    val spanned = HtmlCompat.fromHtml(spannableString, HtmlCompat.FROM_HTML_MODE_COMPACT).toAnnotatedString()
    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable { onClick() }
            .drawWithContent {
                drawContent()
                if (!isRead) drawRect(
                    colorScheme.primary,
                    topLeft = Offset(0f, 0f),
                    size = Size(8.dp.toPx(), size.height)
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val context = LocalContext.current
                val annotatedString = buildAnnotatedString {
                    withStyle(MaterialTheme.typography.headlineSmall.toSpanStyle()) {
                        append(title)
                    }
                    withStyle(MaterialTheme.typography.labelMedium.toSpanStyle().copy(baselineShift = BaselineShift(0.25f))) {
                        append(" $DOT ${DateUtils.localizedRelativeDate(context, date.toLocalDate())}")
                    }
                }
                Text(text = annotatedString)
            }
            Text(
                text = spanned,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
@Preview
fun NewsScreenPreview() {
    NewsScreenContent(
        state = NewsState(
            news = News.generateNews().toMutableList().apply { this.clear() }
        )
    )
}

@Preview
@Composable
private fun NewsCardPreview() {
    Column {
        NewsCard(
            title = "Example with a very, very long title",
            content = "Example <b>with</b> HTML " + es.jvbabi.vplanplus.ui.preview.Text.LOREM_IPSUM_100,
            date = ZonedDateTime.now(),
            isRead = false
        ) {}
    }
}

// https://stackoverflow.com/a/68935732/16682019
fun Spanned.toAnnotatedString(): AnnotatedString = buildAnnotatedString {
    val spanned = this@toAnnotatedString
    append(spanned.toString())
    getSpans(0, spanned.length, Any::class.java).forEach { span ->
        val start = getSpanStart(span)
        val end = getSpanEnd(span)
        when (span) {
            is StyleSpan -> when (span.style) {
                Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                Typeface.BOLD_ITALIC -> addStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic), start, end)
            }

            is UnderlineSpan -> addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, end)
            is ForegroundColorSpan -> addStyle(SpanStyle(color = Color(span.foregroundColor)), start, end)
        }
    }
}

fun Modifier.strikethrough(colorScheme: ColorScheme, thickness: Float = 9f) = this.drawWithContent {
    drawContent()
    scale(1.3f) {
        rotate(45f) {
            drawLine(colorScheme.onSurface, Offset(0f, (size.height / 2) + thickness / 2), Offset(size.width, (size.height / 2) + thickness / 2), thickness)
            drawLine(colorScheme.surface, Offset(0f, (size.height / 2) - thickness / 2), Offset(size.width, (size.height / 2) - thickness / 2), thickness)
        }
    }
}
