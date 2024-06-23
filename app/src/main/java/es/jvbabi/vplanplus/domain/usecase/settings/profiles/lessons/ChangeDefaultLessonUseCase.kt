package es.jvbabi.vplanplus.domain.usecase.settings.profiles.lessons

import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.ProfileRepository

class ChangeDefaultLessonUseCase(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(profile: Profile, defaultLesson: DefaultLesson, enabled: Boolean) {
        profileRepository.setDefaultLessonActivationState(profile.id, defaultLesson.vpId, enabled)
    }
}