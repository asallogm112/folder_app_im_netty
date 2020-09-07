package com.chen.logic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class AsyncTask {

	private ExecutorService service;

	@PostConstruct
	public void init() {
		service = Executors.newFixedThreadPool(5);
	}
	
	public void addTask(Runnable task) {
		service.submit(task);
	}
}
