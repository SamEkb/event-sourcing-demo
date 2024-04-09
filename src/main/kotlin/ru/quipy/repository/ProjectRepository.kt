package ru.quipy.repository

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import ru.quipy.projections.ProjectsViewDomain
import java.util.*

interface ProjectRepository: MongoRepository<ProjectsViewDomain.Project, UUID> {
}