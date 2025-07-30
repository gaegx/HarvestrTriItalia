package com.gaegxh.harvester.component;


import com.gaegxh.harvester.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskInverser {

    public  Task getInversedTask(Task task) {
        Task reversedTask = new Task();
        reversedTask.setTaskUuid(task.getTaskUuid());
        reversedTask.setCrawlerType(task.getCrawlerType());
        reversedTask.setDepartureStation(task.getArrivalStation());
        reversedTask.setArrivalStation(task.getDepartureStation());
        reversedTask.setCoachClasses(task.getCoachClasses());
        reversedTask.setTrainBrands(task.getTrainBrands());
        reversedTask.setFareCodes(task.getFareCodes());
        reversedTask.setDepartureDate(task.getDepartureDate());
        reversedTask.setReverse(task.getReverse());
        reversedTask.setChanges(task.getChanges());
        return reversedTask;
    }
}
