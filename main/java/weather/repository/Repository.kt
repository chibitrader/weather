package ezike.tobenna.myweather.repository

import ezike.tobenna.myweather.data.Resource
import ezike.tobenna.myweather.data.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun fetchWeather(): Flow<Resource<WeatherResponse>>
}