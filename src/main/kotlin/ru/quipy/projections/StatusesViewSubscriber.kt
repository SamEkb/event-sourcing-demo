package ru.quipy.projections

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.StatusCreatedEvent
import ru.quipy.api.StatusDeletedEvent
import ru.quipy.repository.StatusRepository
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
class StatusesViewSubscriber(
    @Autowired var subscriptionsManager: AggregateSubscriptionsManager,
    @Autowired var repository: StatusRepository
) {
    val logger: Logger = LoggerFactory.getLogger(StatusesViewSubscriber::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "status-event-publish") {

            `when`(StatusCreatedEvent::class) { event ->
                logger.info("Status created: projectId:{}, statusId:{}", event.projectId, event.statusId)
                val status = StatusesViewDomain.Status(
                    id = event.statusId,
                    projectId = event.projectId,
                    statusName = event.statusName,
                    color = event.color,
                    isDeleted = false
                )
                withContext(Dispatchers.IO) {
                    repository.save(status)
                }
            }

            `when`(StatusDeletedEvent::class) { event ->
                logger.info("Status deleted: projectId:{}, statusId:{}", event.projectId, event.statusId)
                withContext(Dispatchers.IO) {
                    val status = repository.findById(event.statusId).orElseThrow()
                    status.isDeleted = true
                    repository.save(status)
                }
            }
        }
    }
}