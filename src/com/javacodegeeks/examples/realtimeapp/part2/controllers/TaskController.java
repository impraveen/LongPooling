package com.javacodegeeks.examples.realtimeapp.part2.controllers;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.javacodegeeks.examples.realtimeapp.part2.services.TaskService;

@ManagedBean(name = "taskController", eager=true)
@Component
@RequestScoped
public class TaskController {
	@Autowired
	private TaskService taskService;
	
	public void startTasks(ActionEvent event) throws InterruptedException {
		this.taskService.startIdleTasks();
	}
	
}
