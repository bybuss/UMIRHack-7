package bob.colbaskin.umirhack7.point_picker.domain

import bob.colbaskin.umirhack7.maplibre.domain.models.Zone
import jakarta.inject.Inject

class GetZoneByIdUseCase @Inject constructor(
    private val zoneRepository: ZoneRepository
) {
    suspend operator fun invoke(zoneId: Int): Zone {
        return zoneRepository.getZoneById(zoneId)
            ?: throw IllegalArgumentException("Зона с id $zoneId не найдена")
    }
}
