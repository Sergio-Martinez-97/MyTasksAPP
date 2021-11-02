package com.nova.everisdarmytasksms;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.everisdarmytasksms.controller.TaskController;
import com.nova.everisdarmytasksms.model.Task;
import com.nova.everisdarmytasksms.repository.TaskRepository;
import com.nova.everisdarmytasksms.service.TaskService;

@SpringBootTest
@AutoConfigureMockMvc
class EverisDarMytasksMsApplicationTests {

	@MockBean
	TaskRepository taskRepository;
	
	@Autowired
	TaskService taskService;
	
	@Autowired
	TaskController taskController;
	
	@Autowired
	MockMvc mockMvc;
	
	//Delete all tasks
	@After
	void emptyDB() {
		taskRepository.deleteAll();
	}
	
	@Test
	void createTaskFromServiceTest() {
		//Description with 256 characters
		Task task256Characters = build256CharactersTask();
		//Description with 255 characters
		Task task255Characters = build255CharactersTask();
		
		assertEquals(task256Characters, null);
		assertNotEquals(task255Characters, null);
	}
	
	@Test
	void getTasksTest() throws Exception {
		
		List<Task> tasks = new ArrayList<Task>();
		tasks.add(buildPendingTask());
		tasks.add(buildInProgressTask());
		tasks.add(buildFinishedTask());
		
		when(taskRepository.findAll()).thenReturn(tasks);
		this.mockMvc.perform(get("/getTasks")).
		andDo(print()).andExpect(status().isOk()).
		andExpect(jsonPath("$.length()",is(3))).
		andExpect(jsonPath("$.[0].status").value(Task.PENDING)).
		andExpect(jsonPath("$.[1].status").value(Task.IN_PROGRESS)).
		andExpect(jsonPath("$.[2].status").value(Task.FINISHED));
	} 
	
	@Test
	void createTaskWithLessThan256Characters() throws Exception {
		Task task = buildPendingTask();
		
		this.mockMvc.perform(post("/createTask").
		param("status", task.getStatus()).param("description", task.getDescription())).
		andDo(print()).andExpect(status().isCreated()).
		andExpect(content().string("Tarea creada correctamente"));
	}
	
	@Test
	void createTaskWith256Characters() throws Exception {		
		this.mockMvc.perform(post("/createTask").
		param("status", Task.PENDING).param("description", descriptionOf256Characters())).
		andDo(print()).andExpect(status().isUnprocessableEntity()).
		andExpect(content().string("La tarea debe tener una descripción "
				+ "con una longitud máxima de 255 caracteres"));
	}
	
	@Test
	void getTasksByStatusPendingTest() throws Exception {
		List<Task> tasks = new ArrayList<Task>();
		tasks.add(buildPendingTask());
		
		when(taskRepository.findAllByStatus(Task.PENDING)).thenReturn(tasks);
		this.mockMvc.perform(get("/getTasks/" + Task.PENDING)).
		andDo(print()).andExpect(status().isOk()).
		andExpect(jsonPath("$.length()",is(1))).
		andExpect(jsonPath("$.[0].status").value(Task.PENDING));
	}
	
	@Test
	void getTasksByStatusInProgressTest() throws Exception {
		List<Task> tasks = new ArrayList<Task>();
		tasks.add(buildInProgressTask());
		
		when(taskRepository.findAllByStatus(Task.IN_PROGRESS)).thenReturn(tasks);
		this.mockMvc.perform(get("/getTasks/" + Task.IN_PROGRESS)).
		andDo(print()).andExpect(status().isOk()).
		andExpect(jsonPath("$.length()",is(1))).
		andExpect(jsonPath("$.[0].status").value(Task.IN_PROGRESS));
	}
	
	@Test
	void getTasksByStatusFinishedTest() throws Exception {
		List<Task> tasks = new ArrayList<Task>();
		tasks.add(buildFinishedTask());
		
		when(taskRepository.findAllByStatus(Task.FINISHED)).thenReturn(tasks);
		this.mockMvc.perform(get("/getTasks/" + Task.FINISHED)).
		andDo(print()).andExpect(status().isOk()).
		andExpect(jsonPath("$.length()",is(1))).
		andExpect(jsonPath("$.[0].status").value(Task.FINISHED));
	}
	
	@Test
	void updateTaskTest() throws Exception {
		Task task = buildPendingTask();
		task.setId(1);
		Optional<Task> optional = Optional.of(task);
		
		Task updatedTask = buildUpdatedTask();
		ObjectMapper map = new ObjectMapper();
		String jsonString = map.writeValueAsString(updatedTask);
		
		Mockito.when(taskRepository.findById(1)).thenReturn(optional);
		this.mockMvc.perform(put("/updateTask/1").contentType(MediaType.APPLICATION_JSON)
				.content(jsonString)).
		andDo(print()).andExpect(status().isOk()).
		andExpect(content().json("{\"id\": 1,\"status\":\"" + updatedTask.getStatus() + "\"," + 
				"\"description\":\"" + updatedTask.getDescription() + "\"}"));
	}
	
	
	private Task buildUpdatedTask() {
		return taskService.createTask(Task.FINISHED, "Descripcion actualizada");
	}

	private Task buildPendingTask() {
		return taskService.createTask(Task.PENDING, "Descripcion PENDING");
	}
	
	private Task buildInProgressTask() {
		return taskService.createTask(Task.IN_PROGRESS, "Descripcion IN PROGRESS");
	}
	
	private Task buildFinishedTask() {
		return taskService.createTask(Task.FINISHED, "Descripcion FINISHED");
	}
	
	private Task build255CharactersTask() {
		return taskService.createTask(Task.PENDING, "sssssssssssssssssssss"
				+ "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss"
				+ "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss"
				+ "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss"
				+ "ssssssssssssssssssssssssssssss");
	}
	
	private Task build256CharactersTask() {
		return taskService.createTask(Task.PENDING, descriptionOf256Characters());
	}
	
	private String descriptionOf256Characters() {
		return "sssssssssssssssssssss"
				+ "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss"
				+ "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss"
				+ "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss"
				+ "ssssssssssssssssssssssssssssssss";
	}

}
