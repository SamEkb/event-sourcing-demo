package ru.quipy.projections

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.EditProjectTitleEvent
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.repository.ProjectRepository
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
class ProjectsEventSubscriber(
    @Autowired var subscriptionsManager: AggregateSubscriptionsManager,
    @Autowired var repository: ProjectRepository
) {
    val logger: Logger = LoggerFactory.getLogger(ProjectsEventSubscriber::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "project-event-publish") {

            `when`(ProjectCreatedEvent::class) { event ->
                logger.info("Project created: {}", event.projectId)
                val project = ProjectsViewDomain.Project(id = event.projectId, title = event.title)
                withContext(Dispatchers.IO) {
                    repository.save(project)
                }
            }

            `when`(EditProjectTitleEvent::class) { event ->
                logger.info("Project edited: {}", event.projectId)
                withContext(Dispatchers.IO) {
                    val project = repository.findById(event.projectId).orElseThrow()
                    project.title = event.title
                    repository.save(project)
                }
            }
        }
    }
}