package es.jvbabi.vplanplus.feature.main_homework.add.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetClassByProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase.AddHomeworkUseCases
import es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase.CanShowVppIdBannerUseCase
import es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase.GetDefaultLessonsUseCase
import es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase.HideShowNewLayoutBalloonUseCase
import es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase.HideVppIdBannerUseCase
import es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase.IsShowNewLayoutBalloonUseCase
import es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase.SaveHomeworkUseCase
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AddHomeworkModule {

    @Provides
    @Singleton
    fun provideAddHomeworkUseCases(
        getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
        getClassByProfileUseCase: GetClassByProfileUseCase,
        defaultLessonRepository: DefaultLessonRepository,
        keyValueRepository: KeyValueRepository,
        homeworkRepository: HomeworkRepository
    ): AddHomeworkUseCases {
        return AddHomeworkUseCases(
            getDefaultLessonsUseCase = GetDefaultLessonsUseCase(
                defaultLessonRepository = defaultLessonRepository,
                getCurrentIdentityUseCase = getCurrentIdentityUseCase,
                getClassByProfileUseCase = getClassByProfileUseCase
            ),
            canShowVppIdBannerUseCase = CanShowVppIdBannerUseCase(
                keyValueRepository = keyValueRepository
            ),
            hideVppIdBannerUseCase = HideVppIdBannerUseCase(
                keyValueRepository = keyValueRepository
            ),
            saveHomeworkUseCase = SaveHomeworkUseCase(
                homeworkRepository = homeworkRepository,
                getCurrentIdentityUseCase = getCurrentIdentityUseCase,
                getClassByProfileUseCase = getClassByProfileUseCase
            ),

            isShowNewLayoutBalloonUseCase = IsShowNewLayoutBalloonUseCase(keyValueRepository),
            hideShowNewLayoutBalloonUseCase = HideShowNewLayoutBalloonUseCase(keyValueRepository)
        )
    }
}