package ru.quipy.projections

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.repository.TaskExecutorRepository
import ru.quipy.repository.TaskViewRepository
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
class TasksEventSubscriber(
    @Autowired var subscriptionsManager: AggregateSubscriptionsManager,
    @Autowired var taskViewRepository: TaskViewRepository,
    @Autowired var executorRepository: TaskExecutorRepository
) {
    val logger: Logger = LoggerFactory.getLogger(TasksEventSubscriber::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(TaskAggregate::class, "task-event-publish") {

            `when`(TaskCreatedEvent::class) { event ->
                logger.info("Task created: projectId:{}, taskId:{}", event.projectId, event.taskId)
                val task =
                    TasksViewDomain.Task(event.taskId, event.projectId, event.taskTitle, event.statusId)
                withContext(Dispatchers.IO) {
                    taskViewRepository.save(task)
                }
            }

            `when`(TaskEditedEvent::class) { event ->
                logger.info("Task edited: taskId:{}", event.taskId)
                withContext(Dispatchers.IO) {
                    val task = taskViewRepository.findById(event.taskId).orElseThrow()
                    task.name = event.title
                    taskViewRepository.save(task)
                }
            }

            `when`(TaskStatusAssignedEvent::class) { event ->
                logger.info("Task edited: taskId:{}", event.taskId)
                withContext(Dispatchers.IO) {
                    val task = taskViewRepository.findById(event.taskId).orElseThrow()
                    task.statusId = event.statusId
                    taskViewRepository.save(task)
                }
            }

            `when`(ExecutorsAddedEvent::class) { event ->
                logger.info("Executor added: taskId:{} executorId{}", event.taskId, event.executorsIds)
                val taskExecutors = TasksViewDomain.TaskExecutor(event.taskId, event.executorsIds)
                withContext(Dispatchers.IO) {
                    executorRepository.save(taskExecutors)
                }
            }
        }
    }
}