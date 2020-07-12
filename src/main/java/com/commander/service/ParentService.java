package com.commander.service;


import java.util.ArrayList;
import java.util.List;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

/**
 * ParentService abstracts the logic for handling execution of
 * Services on the main application thread.
 */
public abstract class ParentService {
	
	private List<Service<?>> services;

	public ParentService() {
		this.services = new ArrayList<>();
	}

	/**
	 * @param task the process being handled by the Service
	 * @param onSuccess Service worker reports the successful completion of the Task
	 * @param beforeStart The Service worker reports its state before it has begun to consume thread resources
	 * @param <D> Task return type
	 * @return a Service
	 */
	protected <D> Service<D> createService(Task<D> task, EventHandler<WorkerStateEvent> onSuccess, EventHandler<WorkerStateEvent> beforeStart) {
		Service<D> service = new Service<D>() {
				protected Task<D> createTask() {
					return task;
				}
		};
		
		if (onSuccess != null)
			service.setOnSucceeded(onSuccess);
		
		if (beforeStart != null)
			service.setOnScheduled(beforeStart);
		
		service.setOnFailed(e -> {
			System.out.println("Failed: " + e.getSource().getException());
		});
		
		service.start();
		
		services.add(service);
		
		return service;
	}
	
	public void onClose() {
		
		for (Service<?> service : services) {
			service.cancel();
		}
		
	}
	
}
