package com.javacodegeeks.examples.realtimeapp.part2.services;


import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.javacodegeeks.examples.realtimeapp.part2.domain.Task;

@RestController
@RequestMapping("/api/task")
public class TaskService {
	@Autowired
	private TaskExecutor taskExecutor;
	@Autowired
	private TaskCreator taskCreator;
	
	@PostConstruct
	public void initialize() {
		this.taskCreator.start();
	}
	
	@RequestMapping(path = "/all", method = RequestMethod.GET)
	public List<Task> getTasks() {
		return this.taskExecutor.getPool();
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public List<Task> getUpdatedTasks() {
		List<Task> updatedTasks = null;
		
		// Fetch updated task until there is one or more
		do {
			updatedTasks = this.taskExecutor.getUpdatedTasks();
		} while (updatedTasks.size() == 0);
		
		return updatedTasks;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public void addTask(@RequestBody Task taskToAdd) {
		this.taskExecutor.addTask(taskToAdd);
	}
	
	public void startIdleTasks() throws InterruptedException {
		this.taskExecutor.startAllTasks();
	}
	
}
