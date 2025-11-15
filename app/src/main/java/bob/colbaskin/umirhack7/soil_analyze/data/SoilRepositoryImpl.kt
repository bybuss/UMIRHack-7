package bob.colbaskin.umirhack7.soil_analyze.data

import bob.colbaskin.umirhack7.common.ApiResult
import bob.colbaskin.umirhack7.common.utils.safeApiCall
import bob.colbaskin.umirhack7.soil_analyze.data.local.entity.SoilAnalysisQueueEntity
import bob.colbaskin.umirhack7.soil_analyze.data.models.AnalysisQueueStatus
import bob.colbaskin.umirhack7.soil_analyze.data.models.CreateReportBody
import bob.colbaskin.umirhack7.soil_analyze.data.models.Location
import bob.colbaskin.umirhack7.soil_analyze.domain.SoilAnalysisQueueRepository
import bob.colbaskin.umirhack7.soil_analyze.domain.SoilRepository
import bob.colbaskin.umirhack7.soil_analyze.domain.SoilService
import bob.colbaskin.umirhack7.soil_analyze.domain.models.SoilAnalysisData
import jakarta.inject.Inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class SoilRepositoryImpl @Inject constructor(
    private val soilApi: SoilService,
    private val queueRepository: SoilAnalysisQueueRepository
) : SoilRepository {

    private val dateFormatter = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        Locale.getDefault()
    ).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override suspend fun createReport(soilData: SoilAnalysisData): ApiResult<Unit> {
        return if (soilData.location == null) {
            ApiResult.Error("Location is required", "")
        } else {
            val queueEntity = SoilAnalysisQueueEntity(
                N = soilData.N,
                P = soilData.P,
                K = soilData.K,
                Temperature = soilData.Temperature,
                Humidity = soilData.Humidity,
                pH = soilData.pH,
                RainFall = soilData.RainFall,
                latitude = soilData.location.coordinates[1].toDouble(),
                longitude = soilData.location.coordinates[0].toDouble(),
                createdAt = soilData.createdAt,
                status = AnalysisQueueStatus.PENDING
            )

            queueRepository.queueAnalysis(queueEntity)
        }
    }

    override suspend fun syncPendingReports(): ApiResult<Unit> {
        val pendingAnalyses = queueRepository.getPendingAnalyses()

        if (pendingAnalyses.isEmpty()) {
            return ApiResult.Success(Unit)
        }

        var successCount = 0
        var errorCount = 0

        for (analysis in pendingAnalyses) {
            if (analysis.attemptCount > 5) {
                queueRepository.updateAnalysisStatus(
                    analysis.id,
                    AnalysisQueueStatus.FAILED,
                    "Too many attempts (${analysis.attemptCount})"
                )
                errorCount++
                continue
            }

            queueRepository.updateAnalysisStatus(analysis.id, AnalysisQueueStatus.SENDING)

            val result = safeApiCall<Unit, Unit>(
                apiCall = {
                    soilApi.createReport(
                        userId = "8b41db6e-af65-5e01-4ac0-4d706875b964",
                        body = CreateReportBody(
                            N = analysis.N,
                            P = analysis.P,
                            K = analysis.K,
                            Temperature = analysis.Temperature,
                            Humidity = analysis.Humidity,
                            pH = analysis.pH,
                            RainFall = analysis.RainFall,
                            createdAt = dateFormatter.format(analysis.createdAt),
                            location = analysis.toLocationData()
                        )
                    )
                },
                successHandler = { response ->
                    response
                }
            )

            when (result) {
                is ApiResult.Success -> {
                    queueRepository.updateAnalysisStatus(analysis.id, AnalysisQueueStatus.SUCCESS)
                    successCount++
                }
                is ApiResult.Error -> {
                    val updatedEntity = analysis.copy(
                        attemptCount = analysis.attemptCount + 1,
                        lastAttemptDate = Date(),
                        errorMessage = result.text
                    )
                    queueRepository.updateAnalysis(updatedEntity)
                    errorCount++
                }
            }
        }

        return when {
            successCount > 0 -> ApiResult.Success(Unit)
            else -> ApiResult.Error("Failed to send $errorCount analyses", "")
        }
    }
}

private fun SoilAnalysisQueueEntity.toLocationData() = Location(
    type = "Point",
    coordinates = listOf(longitude, latitude)
)
