package com.nova.everisdarmytasksms;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.nova.everisdarmytasksms.controller.Task;
import com.nova.everisdarmytasksms.controller.TaskController;
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
		tasks.add(buildTask());
		tasks.add(buildTask());
		when(taskRepository.findAll()).thenReturn(tasks);
		this.mockMvc.perform(get("/getTasks")).
		andDo(print()).andExpect(status().isOk()).
		andExpect(jsonPath("$.length()",is(2))).
		andExpect(jsonPath("$.[0].status").value(Task.PENDING)).
		andExpect(jsonPath("$.[1].status").value(Task.PENDING));
	} 
	
	@Test
	void createTaskWithLessThan256Characters() throws Exception {
		Task task = buildTask();
		
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
	
	
	private Task buildTask() {
		return taskService.createTask(Task.PENDING, "Descripcion PENDING");
//		Task taskPending = taskService.createTask(Task.PENDING, "Descripcion PENDING");
//		Task taskInProgress = taskService.createTask(Task.IN_PROGRESS, "Descripcion IN PROGRESS");
//		Task taskFinished = taskService.createTask(Task.FINISHED, "Descripcion FINISHED");
//		
//		taskRepository.save(taskPending);
//		taskRepository.save(taskInProgress);
//		taskRepository.save(taskFinished);
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
