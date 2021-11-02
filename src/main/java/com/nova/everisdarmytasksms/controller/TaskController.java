package com.nova.everisdarmytasksms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nova.everisdarmytasksms.model.Task;
import com.nova.everisdarmytasksms.repository.TaskRepository;
import com.nova.everisdarmytasksms.service.TaskService;

@RestController
public class TaskController {

	@Autowired
	TaskRepository taskRepository;
	
	@Autowired
	TaskService taskService;
	
	//Return all the tasks stored in DB
	@GetMapping("/tasks")
	public List<Task> getTasks() {
		return taskRepository.findAll();
	}
	
	//Create a new task if it's possible
	@PostMapping("/tasks")
	public ResponseEntity<String> createTasks(@RequestParam("status") String status, 
			@RequestParam("description") String description) {
		Task task = taskService.createTask(status, description);
		if (task != null) {
			taskRepository.save(task);
			return new ResponseEntity<>("Tarea creada correctamente", HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>("La tarea debe tener una descripción "
					+ "con una longitud máxima de 255 caracteres", 
					HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
	
	//Get tasks by their current status
	@GetMapping("/tasks/status/{status}")
	public List<Task> getTasksByStatus(@PathVariable("status") String status) {
		return taskRepository.findAllByStatus(status);
	}
	
	//Update the task's description and status
	@PutMapping("/tasks/{id}")
	public ResponseEntity<Task> updateTask(@PathVariable("id") Integer id, 
			@RequestBody Task task) {
		Task existingTask = taskService.getTaskById(id);
		if (existingTask != null) {
			existingTask.setDescription(task.getDescription());
			existingTask.setStatus(task.getStatus());
			taskRepository.save(existingTask);
			
			return new ResponseEntity<Task>(existingTask, 
					HttpStatus.OK);
		} else {
			return new ResponseEntity<Task>(HttpStatus.BAD_REQUEST);
		}
	}
	
	//Update the task's description
	@PutMapping("/tasks/description/{id}")
	public ResponseEntity<Task> updateDescriptionTasks(@PathVariable("id") Integer id, 
			@RequestParam("description") String description) {
		Task existingTask = taskService.getTaskById(id);
		if (existingTask != null) {
			existingTask.setDescription(description);
			taskRepository.save(existingTask);
			
			return new ResponseEntity<Task>(existingTask, 
					HttpStatus.OK);
		} else {
			return new ResponseEntity<Task>(HttpStatus.BAD_REQUEST);
		}
	}
	
	//Update the task's status
	@PutMapping("/tasks/status/{id}")
	public ResponseEntity<Task> updateStatusTasks(@PathVariable("id") Integer id, 
			@RequestParam("status") String status) {
		Task existingTask = taskService.getTaskById(id);
		if (existingTask != null) {
			existingTask.setStatus(status);
			taskRepository.save(existingTask);
			
			return new ResponseEntity<Task>(existingTask, 
					HttpStatus.OK);
		} else {
			return new ResponseEntity<Task>(HttpStatus.BAD_REQUEST);
		}
	}
	
	//Delete the task with the given id
	@DeleteMapping("/tasks/{id}")
	public ResponseEntity<String> deleteTask(@PathVariable("id") Integer id) {
		taskRepository.deleteById(id);
		
		return new ResponseEntity<String>("Tarea eliminada correctamente", 
				HttpStatus.OK);
	}
}
