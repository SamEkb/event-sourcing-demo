package ru.quipy.projections

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.TaskAggregate
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskEditedEvent
import ru.quipy.api.TaskStatusAssignedEvent
import ru.quipy.repository.TaskViewRepository
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
class TasksViewEventSubscriber(
    @Autowired var subscriptionsManager: AggregateSubscriptionsManager,
    @Autowired var repository: TaskViewRepository
) {
    val logger: Logger = LoggerFactory.getLogger(TasksViewEventSubscriber::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(TaskAggregate::class, "task-event-publish") {

            `when`(TaskCreatedEvent::class) { event ->
                logger.info("Task created: projectId:{}, taskId:{}", event.projectId, event.taskId)
                val task =
                    TasksViewDomain.Task(event.taskId, event.projectId, event.taskTitle, event.statusId)
                withContext(Dispatchers.IO) {
                    repository.save(task)
                }
            }

            `when`(TaskEditedEvent::class) { event ->
                logger.info("Task edited: taskId:{}", event.taskId)
                withContext(Dispatchers.IO) {
                    val task = repository.findById(event.taskId).orElseThrow()
                    task.name = event.title
                    repository.save(task)
                }
            }

            `when`(TaskStatusAssignedEvent::class) { event ->
                logger.info("Task edited: taskId:{}", event.taskId)
                withContext(Dispatchers.IO) {
                    val task = repository.findById(event.taskId).orElseThrow()
                    task.statusId = event.statusId
                    repository.save(task)
                }
            }
        }
    }
}