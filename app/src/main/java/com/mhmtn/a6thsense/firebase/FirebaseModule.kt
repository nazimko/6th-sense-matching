package com.mhmtn.a6thsense.firebase

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mhmtn.a6thsense.activity.data.MatchingRepositoryImpl
import com.mhmtn.a6thsense.R
import com.mhmtn.a6thsense.activity.data.QuestionRepositoryImpl
import com.mhmtn.a6thsense.activity.domain.MatchingRepository
import com.mhmtn.a6thsense.activity.domain.QuestionRepository
import com.mhmtn.a6thsense.auth.data.AuthRepositoryImpl
import com.mhmtn.a6thsense.auth.domain.AuthRepository
import com.mhmtn.a6thsense.auth.presentation.AuthViewModel
import com.mhmtn.a6thsense.billing.data.BillingRepositoryImpl
import com.mhmtn.a6thsense.billing.domain.BillingRepository
import com.mhmtn.a6thsense.contact.data.ContactRepositoryImpl
import com.mhmtn.a6thsense.contact.domain.ContactRepository
import com.mhmtn.a6thsense.conversations.data.ConversationRepositoryImpl
import com.mhmtn.a6thsense.conversations.domain.ConversationRepository
import com.mhmtn.a6thsense.core.domain.analytics.AnalyticsHelper
import com.mhmtn.a6thsense.discover.data.DiscoverRepositoryImpl
import com.mhmtn.a6thsense.discover.domain.DiscoverRepository
import com.mhmtn.a6thsense.firebase.data.FirebaseSelectionDataSource
import com.mhmtn.a6thsense.friends.data.FriendsRepositoryImpl
import com.mhmtn.a6thsense.friends.domain.FriendsRepository
import com.mhmtn.a6thsense.home.data.HomeRepositoryImpl
import com.mhmtn.a6thsense.home.domain.HomeRepository
import com.mhmtn.a6thsense.invite.data.InviteRepositoryImpl
import com.mhmtn.a6thsense.invite.domain.InviteRepository
import com.mhmtn.a6thsense.matchhistory.data.MatchHistoryRepositoryImpl
import com.mhmtn.a6thsense.matchhistory.domain.MatchHistoryRepository
import com.mhmtn.a6thsense.messaging.data.MessagingRepositoryImpl
import com.mhmtn.a6thsense.messaging.domain.MessagingRepository
import com.mhmtn.a6thsense.onboarding.data.OnboardingRepositoryImpl
import com.mhmtn.a6thsense.onboarding.domain.OnboardingRepository
import com.mhmtn.a6thsense.premium.data.PremiumRepositoryImpl
import com.mhmtn.a6thsense.premium.domain.PremiumRepository
import com.mhmtn.a6thsense.profile.data.ProfileRepositoryImpl
import com.mhmtn.a6thsense.profile.domain.ProfileRepository
import com.mhmtn.a6thsense.settings.data.SettingsRepositoryImpl
import com.mhmtn.a6thsense.settings.domain.SettingsRepository
import com.mhmtn.a6thsense.similarity.data.SimilarityRepositoryImpl
import com.mhmtn.a6thsense.similarity.domain.SimilarityRepository
import com.mhmtn.a6thsense.similarity.presentation.SimilarityViewModel
import com.mhmtn.a6thsense.soulsync.data.SoulSyncRepositoryImpl
import com.mhmtn.a6thsense.soulsync.domain.SoulSyncRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideDatabase(): FirebaseDatabase =
        FirebaseDatabase.getInstance()

     @Provides
     @Singleton
     fun provideStorage(): FirebaseStorage =
         FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideGoogleSignInOptions(
        @ApplicationContext context: Context
    ): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    @Provides
    @Singleton
    fun provideGoogleSignInClient(
        @ApplicationContext context: Context,
        gso: GoogleSignInOptions
    ): GoogleSignInClient {
        return GoogleSignIn.getClient(context, gso)
    }

    @Provides
    @Singleton
    fun provideFirebaseSelectionDataSource(
        firestore: FirebaseFirestore
    ): FirebaseSelectionDataSource =
        FirebaseSelectionDataSource(firestore)

    @Provides
    @Singleton
    fun provideHomeRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): HomeRepository = HomeRepositoryImpl(auth, firestore)

    @Provides
    @Singleton
    fun provideMatchingRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        dataSource: FirebaseSelectionDataSource,
        @ApplicationContext context: Context
    ): MatchingRepository = MatchingRepositoryImpl(auth, firestore, dataSource, context)

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        googleSignInClient: GoogleSignInClient
    ): AuthRepository = AuthRepositoryImpl(auth, firestore, googleSignInClient)

    @Provides
    @Singleton
    fun provideSimilarityScore(): Int = 78

    @Provides
    @Singleton
    fun provideSimilarityRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): SimilarityRepository = SimilarityRepositoryImpl(auth, firestore)


    @Provides
    @Singleton
    fun provideMessagingRepository(
        firestore: FirebaseFirestore
    ): MessagingRepository = MessagingRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideConverstionRepository(
        firestore: FirebaseFirestore
    ): ConversationRepository = ConversationRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideProfileRepository(
        @ApplicationContext context: Context,
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        storage: FirebaseStorage
    ): ProfileRepository = ProfileRepositoryImpl(context, firestore, auth, storage)

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context,
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): SettingsRepository = SettingsRepositoryImpl(context, auth, firestore)

    @Provides
    @Singleton
    fun provideMatchHistoryRepository(
        firestore: FirebaseFirestore
    ): MatchHistoryRepository = MatchHistoryRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideDiscoverRepository(
        firestore: FirebaseFirestore
    ): DiscoverRepository = DiscoverRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun providePremiumRepository(
        @ApplicationContext context: Context,
        firestore: FirebaseFirestore
    ): PremiumRepository = PremiumRepositoryImpl(context, firestore)

    @Provides
    @Singleton
    fun provideOnboardingRepository(
        @ApplicationContext context: Context
    ): OnboardingRepository = OnboardingRepositoryImpl(context)

    @Provides
    @Singleton
    fun provideBillingRepository(
        @ApplicationContext context: Context,
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): BillingRepository = BillingRepositoryImpl(context, auth, firestore)

    @Provides
    @Singleton
    fun provideAnalyticsHelper(): AnalyticsHelper {
        return AnalyticsHelper()
    }

    @Provides
    @Singleton
    fun providesSoulSyncRepository(
        auth: FirebaseAuth
    ): SoulSyncRepository = SoulSyncRepositoryImpl(auth)

    @Provides
    @Singleton
    fun providesinviteRepository(
        firestore: FirebaseFirestore
    ): InviteRepository = InviteRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideQuestionRepository(
        firestore: FirebaseFirestore
    ): QuestionRepository {
        return QuestionRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideFriendsRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): FriendsRepository {
        return FriendsRepositoryImpl(firestore, auth)
    }

    @Provides
    @Singleton
    fun provideContactRepository(
        firestore: FirebaseFirestore
    ): ContactRepository {
        return ContactRepositoryImpl(firestore)
    }
}
