package ru.quipy.projections

import org.springframework.data.annotation.Id
import ru.quipy.domain.Unique
import java.util.*

class TasksViewDomain {
    data class Task(
        @Id
        override val id: UUID,
        val projectId: UUID,
        var name: String,
        var statusId: UUID,
    ) : Unique<UUID>

    data class TaskExecutor(
        @Id
        override val id: UUID,
        val executorIds: Set<UUID>
    ) : Unique<UUID>
}