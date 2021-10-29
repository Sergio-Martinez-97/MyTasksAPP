package com.nova.everisdarmytasksms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nova.everisdarmytasksms.controller.Task;
import com.nova.everisdarmytasksms.repository.TaskRepository;

@Service
public class TaskService {
	
	@Autowired
	TaskRepository taskRepository;
	
	public Task createTask(String status, String description) {
		if(description.length() < 256) {
			Task task = new Task(status, description);
			return task;
		} else {
			return null;
		}
	}

	public Task getTaskById(Integer id) {
		return taskRepository.findById(id).orElse(null);
	}
}
