package ru.quipy.projections

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import ru.quipy.domain.Unique
import java.util.*

class ProjectParticipantsViewDomain {
    @Document
    data class ProjectParticipants(
        @Id
        override val id: UUID,
        val participantId: UUID
    ) : Unique<UUID>
}