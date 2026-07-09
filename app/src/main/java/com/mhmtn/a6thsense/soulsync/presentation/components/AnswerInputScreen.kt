package com.mhmtn.a6thsense.soulsync.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.core.presentation.bounceClick
import com.mhmtn.a6thsense.ui.theme._6thSenseTheme
import kotlinx.coroutines.delay

@Composable
fun AnswerInputScreen(
    question: String,
    isDark: Boolean,
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var answer by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }

    val gradientColors = if (isDark) {
        listOf(Color(0xFF0F0C29), Color(0xFF1A1A2E), Color(0xFF24243E))
    } else {
        listOf(Color(0xFFF8F5FF), Color(0xFFF0EBFF), Color(0xFFE8DEFF))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = gradientColors
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Soru
            Text(
                text = question,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            // Input field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = Color(0xFF7B5EA7).copy(alpha = if (isDark) 0.5f else 0.2f)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(colorScheme.surfaceVariant)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF7B5EA7), Color(0xFF4568DC))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp)
            ) {
                BasicTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    textStyle = TextStyle(
                        color = colorScheme.onSurface,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(colorScheme.onSurface),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        capitalization = KeyboardCapitalization.Words
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (answer.isNotBlank()) {
                                onSubmit(answer)
                            }
                        }
                    ),
                    decorationBox = { innerTextField ->
                        if (answer.isEmpty()) {
                            Text(
                                text = stringResource( R.string.answer),
                                color = colorScheme.onSurface.copy(alpha = 0.4f),
                                fontSize = 28.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        innerTextField()
                    }
                )
            }

            // Submit button
            val buttonBackground = if (answer.isNotBlank()) {
                if (isDark) {
                    Brush.linearGradient(listOf(Color(0xFF7B5EA7), Color(0xFF4568DC)))
                } else {
                    // Light mode'da daha homojen bir görünüm için tek renk veya çok yakın tonlar
                    SolidColor(Color(0xFF7B5EA7))
                }
            } else {
                SolidColor(colorScheme.onSurface.copy(alpha = 0.12f))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = if (answer.isNotBlank()) 12.dp else 0.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = if (isDark) Color.Transparent else Color(0xFF7B5EA7).copy(alpha = 0.5f)
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(buttonBackground)
                    .bounceClick(
                        enabled = answer.isNotBlank(),
                        onClick = { onSubmit(answer) }
                    )
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${stringResource(R.string.submit)} ✨",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (answer.isNotBlank()) Color.White else colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnswerInputScreenPreview() {
    _6thSenseTheme(darkTheme = false) {
        AnswerInputScreen(
            question = "Favorite color?",
            isDark = false,
            onSubmit = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnswerInputScreenDarkPreview() {
    _6thSenseTheme(darkTheme = true) {
        AnswerInputScreen(
            question = "Favorite color?",
            isDark = true,
            onSubmit = {}
        )
    }
}
