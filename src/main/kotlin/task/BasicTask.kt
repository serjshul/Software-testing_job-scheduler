package task

import statemachine.State
import java.util.*

open class BasicTask(
    override val priority: Priority = Priority.LOW,
    override val name: String = UUID.randomUUID().toString(),
    override val executionTime: Long = 1000,
    override var suspendingTime: Long = 1000
) : Task {

    override var state: State = State.SUSPENDED

    // Новая задача переводится в состояние готовности
    override fun activateSM() {
        if (state == State.SUSPENDED) {
            state = State.READY
        } else {
            throw Exception("Illegal state of the task $name: $state but must be SUSPENDED")
        }
    }

    // Выполняется задача, выбранная планировщиком
    override fun startSM() {
        if (state == State.READY) {
            state = State.RUNNING
            run()
        } else {
            throw Exception("Illegal state of the task $name: $state but must be READY")
        }
    }

    // Планировщик запускает другую задачу. Запущенная задача переводится в состояние готовности
    override fun preemptSM() {
        if (state == State.RUNNING) {
            state = State.READY
        } else {
            throw Exception("Illegal state of the task $name: $state but must be RUNNING")
        }
    }

    // Запушенная задача переходит в состояние suspended
    override fun terminateSM() {
        if (state == State.RUNNING) {
            state = State.SUSPENDED
        } else {
            throw Exception("Illegal state of the task $name: $state but must be RUNNING")
        }
    }

    override fun run() {
        Thread.sleep(executionTime)
        terminateSM()
    }

    override fun toString(): String {
        return "BasicTask(name=$name, type=Basic, state=$state, priority=$priority, executionTime=$executionTime, " +
                "suspendingTime=$suspendingTime)"
    }

    override fun decreaseSuspendingTime() {
        suspendingTime -= 1
        if (suspendingTime <= 0) {
            activateSM()
        }
    }
}