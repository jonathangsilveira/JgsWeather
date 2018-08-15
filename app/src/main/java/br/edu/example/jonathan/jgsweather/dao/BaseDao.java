package br.edu.example.jonathan.jgsweather.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Update;

@Dao
public interface BaseDao<E> {

    @Insert
    long insert(E entity);

    @Update
    int update(E entity);

    @Delete
    int delete(E entity);

    /**
     * This method will insert if {@code entity} doesn't exist or replace it if already exists.
     *
     * @param entity Object
     * @return Id generate.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long persist(E entity);

}
