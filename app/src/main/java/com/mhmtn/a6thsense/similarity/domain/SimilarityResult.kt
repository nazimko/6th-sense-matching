package com.mhmtn.a6thsense.similarity.domain

import com.mhmtn.a6thsense.auth.domain.AuthUser

data class SimilarityResult(
    val currentUser: AuthUser,
    val matchedUser: AuthUser?,
    val similarity: Int
)

