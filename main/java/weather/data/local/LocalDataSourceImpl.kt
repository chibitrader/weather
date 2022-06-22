package ezike.tobenna.myweather.data.local

import ezike.tobenna.myweather.data.local.db.WeatherDao
import ezike.tobenna.myweather.data.model.WeatherResponse
import ezike.tobenna.myweather.provider.LocationProvider
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(private val weatherDao: WeatherDao, private val locationProvider: LocationProvider)
    : LocalDataSource {

    override suspend fun save(weatherResponse: WeatherResponse?) {
        weatherDao.insert(weatherResponse)
    }

    override fun hasLocationChanged(weatherResponse: WeatherResponse): Boolean {
        return locationProvider.isLocationChanged(weatherResponse.weatherLocation);
    }

    override fun getWeather(): Flow<WeatherResponse> {
        return weatherDao.fetchWeather()
    }
}