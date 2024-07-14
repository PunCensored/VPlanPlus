package es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetVppIdServerUseCase

data class AdvancedSettingsUseCases(
    val deleteCacheUseCase: DeleteCacheUseCase,
    val getVppIdServerUseCase: GetVppIdServerUseCase,
    val setVppIdServerUseCase: SetVppIdServerUseCase,
    val updateFcmTokenUseCase: UpdateFcmTokenUseCase,
    val resetBalloonsUseCase: ResetBalloonsUseCase
)