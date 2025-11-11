package bob.colbaskin.umirhack7.maplibre.data.fields

import android.util.Log
import androidx.room.withTransaction
import bob.colbaskin.umirhack7.common.ApiResult
import bob.colbaskin.umirhack7.common.utils.safeApiCall
import bob.colbaskin.umirhack7.maplibre.data.local.FieldsDatabase
import bob.colbaskin.umirhack7.maplibre.data.local.dao.FieldDao
import bob.colbaskin.umirhack7.maplibre.data.local.dao.ZoneDao
import bob.colbaskin.umirhack7.maplibre.data.models.FieldDTO
import bob.colbaskin.umirhack7.maplibre.data.models.GeometryDTO
import bob.colbaskin.umirhack7.maplibre.data.models.ZoneDTO
import bob.colbaskin.umirhack7.maplibre.data.models.toDomain
import bob.colbaskin.umirhack7.maplibre.data.models.toEntity
import bob.colbaskin.umirhack7.maplibre.domain.models.Field
import bob.colbaskin.umirhack7.maplibre.domain.fields.FieldsRepository
import bob.colbaskin.umirhack7.maplibre.domain.fields.FieldsService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "MapLibre"

class FieldsRepositoryImpl @Inject constructor(
    private val fieldsApi: FieldsService,
    private val database: FieldsDatabase,
): FieldsRepository {
    private val fieldDao: FieldDao = database.fieldDao()
    private val zoneDao: ZoneDao = database.zoneDao()

    override fun getFieldsStream(): Flow<List<Field>> {
        Log.d(TAG, "FieldsRepositoryImpl: Starting fields stream...")
        return fieldDao.getFieldsWithZonesStream().map { fieldsWithZones ->
            Log.d(TAG, "FieldsRepositoryImpl: Stream emitted ${fieldsWithZones.size} fields with zones")
            fieldsWithZones.map { fieldWithZones ->
                Log.v(TAG, "FieldsRepositoryImpl: Processed field: ${fieldWithZones.field.name} with ${fieldWithZones.zones.size} zones")
                fieldWithZones.field.toDomain(
                    zones = fieldWithZones.zones.map { it.toDomain() }
                )
            }
        }
    }

    override suspend fun syncFields(): ApiResult<Unit> {
        Log.d(TAG, "FieldsRepositoryImpl: Starting syncFields...")
        return safeApiCall(
            apiCall = {
                Log.d(TAG, "FieldsRepositoryImpl: Fetching fields from API...")
                /*fieldsApi.getFieldsList()*/ //FIXME: return back
                val mockData = mockFields
                Log.d(TAG, "FieldsRepositoryImpl: Get ${mockData.size} fields from API")
                mockData
            },
            successHandler = { fieldDTOs ->
                Log.d(TAG, "FieldsRepositoryImpl: Processing ${fieldDTOs.size} fields for DB storage")
                val fieldEntities = fieldDTOs.map { it.toEntity() }
                val zoneEntities = fieldDTOs.flatMap { fieldDTO ->
                    fieldDTO.zones.map { zoneDTO -> zoneDTO.toEntity(fieldDTO.id) }
                }

                Log.d(TAG, "FieldsRepositoryImpl: Prepared ${fieldEntities.size} field entities and ${zoneEntities.size} zone entities")
                Log.d(TAG, "FieldsRepositoryImpl: Starting database transaction...")

                database.withTransaction  {
                    fieldDao.clearFields()
                    zoneDao.clearZones()
                    fieldDao.insertFields(fieldEntities)
                    zoneDao.insertZones(zoneEntities)
                }

                Log.d(TAG, "FieldsRepositoryImpl: Sync completed successfully - stored ${fieldEntities.size} fields & ${zoneEntities.size} zones")

                ApiResult.Success(Unit)
            }
        )
    }

    override suspend fun getFieldsList(): ApiResult<List<Field>> {
        Log.d(TAG, "FieldsRepositoryImpl: Getting fields list from database...")
        val fields = fieldDao.getFieldsWithZonesStream().map { fieldsWithZones ->
            Log.d(TAG, "FieldsRepositoryImpl: Retrieved ${fieldsWithZones.size} fields with zones from DB")
            fieldsWithZones.map { fieldWithZones ->
                fieldWithZones.field.toDomain(
                    zones = fieldWithZones.zones.map { it.toDomain() }
                )
            }
        }

        return try {
            val firstFields = fields.first()
            Log.d(TAG, "FieldsRepositoryImpl: Successfully got ${firstFields.size} fields from database")
            ApiResult.Success(firstFields)
        } catch (e: Exception) {
            Log.e(TAG, "FieldsRepositoryImpl: Error getting fields from database: ${e.message}")
            ApiResult.Error(title = "${e.cause}", text = "Ошибка получения данных из базы: ${e.message}")
        }
    }

    companion object {
        //FIXME: delete
        private val mockFields = listOf(
            FieldDTO(
                id = 66,
                name = "MKADIK",
                area = 1114798512.1570663,
                color = "#da00ff",
                geometry = GeometryDTO(
                    type = "Polygon",
                    coordinates = listOf(
                        listOf(
                            listOf(37.34675, 55.90642),
                            listOf(37.463435, 55.552826),
                            listOf(37.917821, 55.577689),
                            listOf(37.820354, 55.905649),
                            listOf(37.34675, 55.90642)
                        )
                    )
                ),
                zones = emptyList()
            ),
            FieldDTO(
                id = 65,
                name = "121",
                area = 369340892.28461456,
                color = "#3388ff",
                geometry = GeometryDTO(
                    type = "Polygon",
                    coordinates = listOf(
                        listOf(
                            listOf(36.019408, 56.678627),
                            listOf(36.090786, 56.40591),
                            listOf(36.409244, 56.705791),
                            listOf(36.019408, 56.678627)
                        )
                    )
                ),
                zones = listOf(
                    ZoneDTO(
                        id = 21,
                        name = "tete",
                        area = 6390954.952297211,
                        color = "#fbff00",
                        geometry = GeometryDTO(
                            type = "Polygon",
                            coordinates = listOf(
                                listOf(
                                    listOf(36.059223, 56.663174),
                                    listOf(36.07226, 56.638247),
                                    listOf(36.114801, 56.660909),
                                    listOf(36.083238, 56.67148),
                                    listOf(36.059223, 56.663174)
                                )
                            )
                        )
                    )
                )
            ),
            FieldDTO(
                id = 49,
                name = "Поле 2",
                area = 4134362918.5013123,
                color = "#ff5555",
                geometry = GeometryDTO(
                    type = "Polygon",
                    coordinates = listOf(
                        listOf(
                            listOf(37.914511, 56.244501),
                            listOf(38.128719, 55.8269),
                            listOf(39.364534, 55.845419),
                            listOf(39.089909, 56.384728),
                            listOf(37.914511, 56.244501)
                        )
                    )
                ),
                zones = listOf(
                    ZoneDTO(
                        id = 19,
                        name = "зона внутри",
                        area = 199033787.87563372,
                        color = "#fbff00",
                        geometry = GeometryDTO(
                            type = "Polygon",
                            coordinates = listOf(
                                listOf(
                                    listOf(38.235697, 56.234455),
                                    listOf(38.285152, 56.156478),
                                    listOf(38.583257, 56.215357),
                                    listOf(38.576389, 56.27186),
                                    listOf(38.297516, 56.276437),
                                    listOf(38.235697, 56.234455)
                                )
                            )
                        )
                    ),
                    ZoneDTO(
                        id = 22,
                        name = "fef",
                        area = 11646297.05920887,
                        color = "#4caf50",
                        geometry = GeometryDTO(
                            type = "Polygon",
                            coordinates = listOf(
                                listOf(
                                    listOf(38.32523, 56.249568),
                                    listOf(38.339647, 56.226277),
                                    listOf(38.402809, 56.244987),
                                    listOf(38.378094, 56.267504),
                                    listOf(38.32523, 56.249568)
                                )
                            )
                        )
                    )
                )
            )
        )
    }
}
