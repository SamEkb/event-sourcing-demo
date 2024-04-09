package ru.quipy.projections

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import ru.quipy.domain.Unique
import java.util.*

class ProjectsViewDomain {
    @Document
    data class Project(
        @Id
        override val id: UUID,
        var title: String
    ) : Unique<UUID>
}