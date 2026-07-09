package com.mhmtn.a6thsense.auth.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mhmtn.a6thsense.R

@Composable
fun FeatureHighlights() {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        FeatureItem(
            icon = Icons.Default.Star,
            text = stringResource(R.string.feature_item_1)
        )

        FeatureItem(
            icon = Icons.Default.Favorite,
            text = stringResource(R.string.feature_item_2)
        )

        FeatureItem(
            icon = Icons.Default.CheckCircle,
            text = stringResource(R.string.feature_item_3)
        )
    }
}