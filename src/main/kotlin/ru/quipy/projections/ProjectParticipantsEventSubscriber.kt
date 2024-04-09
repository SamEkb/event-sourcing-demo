package ru.quipy.projections

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.ParticipantAddedEvent
import ru.quipy.api.ProjectAggregate
import ru.quipy.repository.ProjectParticipantsRepository
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
class ProjectParticipantsEventSubscriber(
    @Autowired var subscriptionsManager: AggregateSubscriptionsManager,
    @Autowired var repository: ProjectParticipantsRepository
) {
    val logger: Logger = LoggerFactory.getLogger(ProjectParticipantsEventSubscriber::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "projectParticipants-event-publish") {

            `when`(ParticipantAddedEvent::class) { event ->
                logger.info(
                    "Project participants added: projectId:{}, participantId:{}",
                    event.projectId,
                    event.participantId
                )
                val projectParticipants =
                    ProjectParticipantsViewDomain.ProjectParticipants(event.projectId, event.participantId)
                withContext(Dispatchers.IO) {
                    repository.save(projectParticipants)
                }
            }
        }
    }
}