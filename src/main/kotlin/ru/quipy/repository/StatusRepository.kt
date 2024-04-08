package ru.quipy.repository

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.projections.StatusesViewDomain
import java.util.*

interface StatusRepository : MongoRepository<StatusesViewDomain.Status, UUID> {
}