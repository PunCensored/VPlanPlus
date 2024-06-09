package es.jvbabi.vplanplus.data.model.homework

import androidx.room.Entity
import androidx.room.ForeignKey
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask

@Entity(
    tableName = "homework_task",
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(
            entity = DbHomework::class,
            parentColumns = ["id"],
            childColumns = ["homeworkId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DbHomeworkTask(
    val id: Long,
    val homeworkId: Long,
    val content: String,
    val done: Boolean,
) {
    fun toModel(): HomeworkTask {
        return HomeworkTask(
            id = id,
            content = content,
            done = done
        )
    }
}