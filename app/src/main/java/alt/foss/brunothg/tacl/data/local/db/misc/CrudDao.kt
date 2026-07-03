/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.data.local.db.misc

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Upsert

/**
 * Base DAO for CRUD operations.
 *
 * @param T The entity type.
 */
interface CrudDao<T> {

    /**
     * Insert an entity.
     *
     * @param entity The entity to insert.
     * @return The row ID of the newly inserted item.
     */
    @Insert
    suspend fun insert(entity: T): Long

    /**
     * Insert all entities.
     *
     * @param entities The entities to insert.
     * @return The list of row IDs of the newly inserted items.
     */
    @Insert
    suspend fun insertAll(entities: List<T>): List<Long>

    /**
     * Upsert an entity.
     *
     * @param entity The entity to upsert.
     * @return The row ID of the upserted item.
     */
    @Upsert
    suspend fun upsert(entity: T): Long

    /**
     * Upsert all entities.
     *
     * @param entities The entities to upsert.
     * @return The list of row IDs of the upserted items.
     */
    @Upsert
    suspend fun upsertAll(entities: List<T>): List<Long>

    /**
     * Update an entity.
     *
     * @param entity The entity to update.
     */
    @Update
    suspend fun update(entity: T)

    /**
     * Update all entities.
     *
     * @param entities The entities to update.
     * @return The number of rows updated.
     */
    @Update
    suspend fun updateAll(entities: List<T>): Int

    /**
     * Delete an entity.
     *
     * @param entity The entity to delete.
     */
    @Delete
    suspend fun delete(entity: T)

}