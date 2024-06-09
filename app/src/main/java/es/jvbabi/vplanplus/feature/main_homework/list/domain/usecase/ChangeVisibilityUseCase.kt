package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository

class ChangeVisibilityUseCase(
    private val homeworkRepository: HomeworkRepository
) {

    suspend operator fun invoke(homework: Homework): HomeworkModificationResult {
        return homeworkRepository.changeShareStatus(homework)
    }
}