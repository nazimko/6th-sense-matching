package com.mhmtn.a6thsense.activity.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.mhmtn.a6thsense.activity.domain.Question
import com.mhmtn.a6thsense.core.presentation.ErrorImagePlaceholder
import com.mhmtn.a6thsense.core.presentation.LoadingImagePlaceholder

@Composable
fun ImageQuestionTextOptionsScreen(
    question: Question,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(vertical = 24.dp) // 👈 Padding burada
    ) {
        // Spacer
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Ana soru görseli
        item {
            question.imageUrl?.let { imageUrl ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    SubcomposeAsyncImage(
                        model = imageUrl,
                        contentDescription = question.question.asString(),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    ) {
                        when (painter.state) {
                            is AsyncImagePainter.State.Loading -> {
                                LoadingImagePlaceholder()
                            }
                            is AsyncImagePainter.State.Error -> {
                                ErrorImagePlaceholder()
                            }
                            is AsyncImagePainter.State.Success -> {
                                SubcomposeAsyncImageContent()
                            }
                            else -> {}
                        }
                    }
                }
            }
        }

        // Soru metni
        item {
            Text(
                text = question.question.asString(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )
        }

        // Spacer
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 👇 Text seçenekleri
        items(question.options) { option ->
            TextOptionCard(
                option = option,
                isSelected = selectedOption == option.id,
                onClick = { onOptionSelected(option.id) }
            )
        }

        // Alt boşluk
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}