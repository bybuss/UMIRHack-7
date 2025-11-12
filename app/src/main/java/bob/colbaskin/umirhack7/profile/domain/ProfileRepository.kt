package bob.colbaskin.umirhack7.profile.domain

interface ProfileRepository {
    suspend fun logout()
}