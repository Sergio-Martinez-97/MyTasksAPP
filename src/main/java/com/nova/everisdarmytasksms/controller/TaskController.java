package com.nova.everisdarmytasksms.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.nova.everisdarmytasksms.model.Status;
import com.nova.everisdarmytasksms.model.Task;
import com.nova.everisdarmytasksms.repository.TaskRepository;
import com.nova.everisdarmytasksms.service.TaskService;

@RestController
public class TaskController {

	@Autowired
	TaskRepository taskRepository;
	
	@Autowired
	TaskService taskService;
	
	private static final Logger logger =  LoggerFactory.getLogger(TaskController.class);
	
	//Return all the tasks stored in DB
	@GetMapping("/tasks")
	public List<Task> getTasks() {
		return taskRepository.findAll();
	}
	
	//Create a new task if it's possible
	@PostMapping("/tasks")
	public ResponseEntity<String> createTasks(@RequestParam("status") Status status, 
			@RequestParam("description") String description) {
		Task task = taskService.createTask(status, description);
		if (task != null) {
			logger.info("Se puede crear la tarea correctamente");
			taskRepository.save(task);
			return new ResponseEntity<>("Tarea creada correctamente", HttpStatus.CREATED);
		} else {
			logger.info("La tarea no ha podido ser creada");
			return new ResponseEntity<>("La tarea debe tener una descripción "
					+ "con una longitud máxima de 255 caracteres", 
					HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
	
	//Get tasks by their current status
	@GetMapping("/tasks/status/{status}")
	public List<Task> getTasksByStatus(@PathVariable("status") Status status) {
		return taskRepository.findAllByStatus(status);
	}
	
	//Update the task's description and status
	@PutMapping("/tasks/{id}")
	public ResponseEntity<Task> updateTask(@PathVariable("id") Integer id, 
			@RequestBody Task task) {
		Task existingTask = taskService.getTaskById(id);
		if (existingTask != null) {
			logger.info("La tarea con el id " + id + " ha sido actualizada");
			existingTask.setDescription(task.getDescription());
			existingTask.setStatus(task.getStatus());
			taskRepository.save(existingTask);
			
			return new ResponseEntity<Task>(existingTask, 
					HttpStatus.OK);
		} else {
			logger.info("La tarea con el id " + id + " no existe");
			return new ResponseEntity<Task>(HttpStatus.BAD_REQUEST);
		}
	}
	
	//Update the task's description
	@PutMapping("/tasks/description/{id}")
	public ResponseEntity<Task> updateDescriptionTask(@PathVariable("id") Integer id, 
			@RequestParam("description") String description) {
		Task existingTask = taskService.getTaskById(id);
		if (existingTask != null) {
			logger.info("La descripcion de la tarea con el id " + id + " ha sido actualizada");
			existingTask.setDescription(description);
			taskRepository.save(existingTask);
			
			return new ResponseEntity<Task>(existingTask, 
					HttpStatus.OK);
		} else {
			logger.info("La tarea con el id " + id + " no existe");
			return new ResponseEntity<Task>(HttpStatus.BAD_REQUEST);
		}
	}
	
	//Update the task's status
	@PutMapping("/tasks/status/{id}")
	public ResponseEntity<Task> updateStatusTask(@PathVariable("id") Integer id, 
			@RequestParam("status") Status status) {
		Task existingTask = taskService.getTaskById(id);
		if (existingTask != null) {
			logger.info("El status de la tarea con el id " + id + " ha sido actualizada");
			existingTask.setStatus(status);
			taskRepository.save(existingTask);
			
			return new ResponseEntity<Task>(existingTask, 
					HttpStatus.OK);
		} else {
			logger.info("La tarea con el id " + id + " no existe");
			return new ResponseEntity<Task>(HttpStatus.BAD_REQUEST);
		}
	}
	
	//Delete the task with the given id
	@DeleteMapping("/tasks/{id}")
	public ResponseEntity<String> deleteTask(@PathVariable("id") Integer id) {
		if (taskRepository.existsById(id)) {
			taskRepository.deleteById(id);
			logger.info("La tarea con el id " + id + " ha sido eliminada correctamente");
			return new ResponseEntity<String>("Tarea eliminada correctamente", 
					HttpStatus.OK);
		} else {
			logger.info("La tarea con el id " + id + " no existe");
			return new ResponseEntity<String>("La tarea con el id indicado no existe", HttpStatus.BAD_REQUEST);
		}
		
		
	}
}
