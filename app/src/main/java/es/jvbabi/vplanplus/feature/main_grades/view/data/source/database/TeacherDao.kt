package es.jvbabi.vplanplus.feature.main_grades.view.data.source.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import es.jvbabi.vplanplus.feature.main_grades.view.data.model.DbTeacher
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TeacherDao {

    @Upsert
    abstract fun insert(teacher: DbTeacher)

    @Query("SELECT * FROM grade_teacher")
    abstract fun getAllTeachers(): Flow<List<DbTeacher>>
}