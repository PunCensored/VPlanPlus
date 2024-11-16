package es.jvbabi.vplanplus.feature.main_home.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.jvbabi.vplanplus.util.blendColor
import es.jvbabi.vplanplus.util.toBlackAndWhite
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@SuppressLint("NewApi")
@Composable
fun DateCard(
    modifier: Modifier = Modifier,
    date: LocalDate,
    isSelected: Boolean,
    isHoliday: Boolean,
    onClick: (date: LocalDate) -> Unit
) {

    val selectedModifier by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        label = "selectedModifier",
        animationSpec = tween(250)
    )

    val background = blendColor(
        MaterialTheme.colorScheme.surface,
        MaterialTheme.colorScheme.tertiary,
        selectedModifier
    )

    val foreground = blendColor(
        MaterialTheme.colorScheme.onSurface,
        MaterialTheme.colorScheme.onTertiary,
        selectedModifier
    )

    val secondaryForeground = blendColor(
        MaterialTheme.colorScheme.onSurface,
        MaterialTheme.colorScheme.onTertiary.toBlackAndWhite(),
        selectedModifier
    )

    Column(modifier) {
        val cardShape = RoundedCornerShape(12.dp)
        Box(
            Modifier
                .shadow((8*selectedModifier).dp, cardShape)
                .then(
                    if (date.isEqual(LocalDate.now()) && !isSelected) Modifier.border(
                        2.dp,
                        MaterialTheme.colorScheme.primary,
                        cardShape
                    )
                    else Modifier
                )
                .clip(cardShape)
                .background(background)
                .size(60.dp)
                .clickable { onClick(date) }
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = date.format(DateTimeFormatter.ofPattern("EEE", Locale.getDefault())).uppercase().takeWhile { it.isLetter() },
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color =
                            if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else if (date.dayOfWeek.value > 5 || isHoliday) MaterialTheme.colorScheme.error
                            else secondaryForeground
                    ),
                )
                Text(
                    text = date.format(DateTimeFormatter.ofPattern("d", Locale.getDefault())),
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                    fontSize = 20.sp,
                    lineHeight = 20.sp,
                    color = foreground
                )
            }
        }

        val texts = mutableListOf<String>()
        if (date.dayOfMonth == 1) texts.add(date.format(DateTimeFormatter.ofPattern("MMMM")))
        if (date.dayOfWeek.value == 1) texts.add("KW ${date.format(DateTimeFormatter.ofPattern("w"))}")

        if (texts.isNotEmpty()) Text(
            text = texts.joinToString("\n"),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Light,
                color = Color.Gray
            ),
            modifier = Modifier.padding(top = 4.dp)
        )
        else Box(Modifier.height((MaterialTheme.typography.labelSmall.lineHeight.value * 2).dp + 4.dp))
    }
}

@Composable
@Preview
private fun DateCardPreview() {
    DateCard(
        date = LocalDate.now(),
        isSelected = false,
        isHoliday = false,
        onClick = {}
    )
}

@Composable
@Preview
private fun DateCardTomorrowPreview() {
    DateCard(
        date = LocalDate.now().plusDays(1),
        isSelected = false,
        isHoliday = true,
        onClick = {}
    )
}

@Composable
@Preview
private fun DateCardSelectedPreview() {
    DateCard(
        date = LocalDate.now(),
        isSelected = true,
        isHoliday = false,
        onClick = {}
    )
}

// inspired by https://www.pinterest.de/pin/870391065474232250/