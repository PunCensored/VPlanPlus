package es.jvbabi.vplanplus.feature.main_grades.view.data.model

import androidx.room.Entity
import androidx.room.Index
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Teacher

@Entity(
    tableName = "grade_teacher",
    indices = [
        Index(value = ["id"], unique = true)
    ],
    primaryKeys = ["id"]
)
data class DbTeacher(
    val id: Long,
    val short: String,
    val firstname: String,
    val lastname: String
) {
    fun toModel(): Teacher {
        return Teacher(
            id = id,
            short = short,
            firstname = firstname,
            lastname = lastname
        )
    }
}
