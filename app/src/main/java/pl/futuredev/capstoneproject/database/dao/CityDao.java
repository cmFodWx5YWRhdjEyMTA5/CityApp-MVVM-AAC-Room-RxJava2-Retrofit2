package pl.futuredev.capstoneproject.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import javax.inject.Singleton;

import dagger.Provides;
import io.reactivex.Flowable;
import pl.futuredev.capstoneproject.database.entity.CityPOJO;


@Dao
public interface CityDao {

    @Query("SELECT * FROM city")
    Flowable<List<CityPOJO>> getAllCitiesRx();

    @Query("SELECT * FROM city WHERE name = :name")
    LiveData<CityPOJO> getCityById(String name);

    @Query("SELECT * FROM city")
    List<CityPOJO> getAllCities();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCity(CityPOJO cityPOJO);

    @Delete
    void deleteCity(CityPOJO cityPOJO);

    @Query("SELECT * FROM city WHERE name = :name")
    Flowable<CityPOJO> getSelectedCityFromDatabase(String name);
}
