package com.javacodegeeks.examples.realtimeapp.part2.services;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.javacodegeeks.examples.realtimeapp.part2.domain.Task;
import com.javacodegeeks.examples.realtimeapp.part2.domain.TaskStatus;

@Component
@Scope("singleton")
public class TaskExecutor {
	private List<Task> pool = new LinkedList<>();
	private Set<Task> updatedTaskPool = new HashSet<>();
	
	@PostConstruct
	public void initialize() {
		Runnable taskPoolConsumer = () -> {
			synchronized(this) {
				while (true) {
					try {
						this.pool.stream()
								.filter(task -> task.isRunning() && task.getDuration() > 0)
								.forEach(task -> {
									task.decrementDuration();
								});
						
						this.pool.stream()
							.filter(task -> task.isRunning() && task.getDuration() == 0)
							.forEach(task -> {
								task.setStatus(TaskStatus.SUCCESS);
								this.updatedTaskPool.add(task);
							});
	
						this.wait(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		new Thread(taskPoolConsumer).start();
		
	}
	
	public synchronized List<Task> getUpdatedTasks() {
		List<Task> updatedTasks = new LinkedList<>();
		
		updatedTasks.addAll(this.pool.stream()
				.filter(task -> task.getStatus().equals(TaskStatus.CREATED))
				.collect(Collectors.toList()));
		updatedTasks.addAll(this.updatedTaskPool);
		
		this.changeCreatedStatusToIdle();
		this.updatedTaskPool.clear();
		
		return updatedTasks;
	}

	private void changeCreatedStatusToIdle() {
		this.pool.stream()
				.filter(task -> task.getStatus().equals(TaskStatus.CREATED))
				.forEach(task -> task.setStatus(TaskStatus.IDLE));
	}

	
	public synchronized void startAllTasks() throws InterruptedException {
		this.pool.stream()
			.filter(task -> task.getStatus().equals(TaskStatus.IDLE))
			.forEach(task -> {
				task.start();
				this.updatedTaskPool.add(task);
			});	
	}

	public List<Task> getPool() {
		this.changeCreatedStatusToIdle();
		return this.pool;
	}

	public void addTask(Task taskToAdd) {
		this.pool.add(taskToAdd);
	}

}
