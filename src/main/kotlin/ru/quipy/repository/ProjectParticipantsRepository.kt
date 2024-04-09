package ru.quipy.repository

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.projections.ProjectParticipantsViewDomain
import java.util.*

interface ProjectParticipantsRepository : MongoRepository<ProjectParticipantsViewDomain.ProjectParticipants, UUID> {
}