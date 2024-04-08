package ru.quipy.projections

import org.springframework.data.annotation.Id
import ru.quipy.domain.Unique
import java.util.*

class StatusesViewDomain {
    data class Status(
        @Id
        override val id: UUID,
        val projectId: UUID,
        val statusName: String,
        val color: String,
        var isDeleted: Boolean
    ) : Unique<UUID>
}