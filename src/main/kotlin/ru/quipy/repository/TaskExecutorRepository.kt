package ru.quipy.repository

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.projections.TasksViewDomain
import java.util.*

interface TaskExecutorRepository : MongoRepository<TasksViewDomain.TaskExecutor, UUID> {
}