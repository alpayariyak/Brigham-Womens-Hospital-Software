package edu.wpi.DapperDaemons.backend;

import edu.wpi.DapperDaemons.entities.Employee;
import edu.wpi.DapperDaemons.entities.requests.Request;
import java.util.ArrayList;
import java.util.List;

public class AutoAssigner {
  private static int findWeight(String priority) {
    switch (priority) {
      case "LOW":
        return 1;
      case "MEDIUM":
        return 2;
      case "HIGH":
        return 3;
      case "OVERDUE":
        return 5;
      default:
        return 4;
    }
  }

  public static String assignAdmin() {
    List<Employee> employeeList =
        new ArrayList<>(DAOPouch.getEmployeeDAO().filter(5, "ADMINISTRATOR").values());

    if (employeeList.isEmpty()) return "Unassigned";

    String bestPick = employeeList.get(0).getNodeID();

    int weightedWithFewest = 10000; // No one should have 10000 tasks ever
    for (Employee employee : employeeList) {
      List<Request> requests = DAOFacade.searchRequestsByAssignee(employee.getNodeID());
      int currentWeight = 0;
      for (Request request : requests) {
        currentWeight += findWeight(request.getPriority().name());
      }
      if (currentWeight < weightedWithFewest) {
        bestPick = employee.getNodeID();
        weightedWithFewest = currentWeight;
      }
    }
    return bestPick;
  }

  public static String assignKitchenStaff() {
    List<Employee> employeeList =
        new ArrayList<>(DAOPouch.getEmployeeDAO().filter(5, "KITCHEN").values());

    if (employeeList.isEmpty()) return "Unassigned";

    String bestPick = employeeList.get(0).getNodeID();

    int weightedWithFewest = 10000; // No one should have 10000 tasks ever
    for (Employee employee : employeeList) {
      List<Request> requests = DAOFacade.searchRequestsByAssignee(employee.getNodeID());
      int currentWeight = 0;
      for (Request request : requests) {
        currentWeight += findWeight(request.getPriority().name());
      }
      if (currentWeight < weightedWithFewest) {
        bestPick = employee.getNodeID();
        weightedWithFewest = currentWeight;
      }
    }
    return bestPick;
  }

  public static String assignDoctor() {
    List<Employee> employeeList =
        new ArrayList<>(DAOPouch.getEmployeeDAO().filter(5, "DOCTOR").values());

    if (employeeList.isEmpty()) return "Unassigned";

    String bestPick = employeeList.get(0).getNodeID();

    int weightedWithFewest = 10000; // No one should have 10000 tasks ever
    for (Employee employee : employeeList) {
      List<Request> requests = DAOFacade.searchRequestsByAssignee(employee.getNodeID());
      int currentWeight = 0;
      for (Request request : requests) {
        currentWeight += findWeight(request.getPriority().name());
      }
      if (currentWeight < weightedWithFewest) {
        bestPick = employee.getNodeID();
        weightedWithFewest = currentWeight;
      }
    }
    return bestPick;
  }

  public static String assignNurse() {
    List<Employee> employeeList =
        new ArrayList<>(DAOPouch.getEmployeeDAO().filter(5, "NURSE").values());

    if (employeeList.isEmpty()) return "Unassigned";

    String bestPick = employeeList.get(0).getNodeID();

    int weightedWithFewest = 10000; // No one should have 10000 tasks ever
    for (Employee employee : employeeList) {
      List<Request> requests = DAOFacade.searchRequestsByAssignee(employee.getNodeID());
      int currentWeight = 0;
      for (Request request : requests) {
        currentWeight += findWeight(request.getPriority().name());
      }
      if (currentWeight < weightedWithFewest) {
        bestPick = employee.getNodeID();
        weightedWithFewest = currentWeight;
      }
    }
    return bestPick;
  }

  public static String assignJanitor() {
    List<Employee> employeeList =
        new ArrayList<>(DAOPouch.getEmployeeDAO().filter(5, "JANITOR").values());

    if (employeeList.isEmpty()) return "Unassigned";

    String bestPick = employeeList.get(0).getNodeID();

    int weightedWithFewest = 10000; // No one should have 10000 tasks ever
    for (Employee employee : employeeList) {
      List<Request> requests = DAOFacade.searchRequestsByAssignee(employee.getNodeID());
      int currentWeight = 0;
      for (Request request : requests) {
        currentWeight += findWeight(request.getPriority().name());
      }
      if (currentWeight < weightedWithFewest) {
        bestPick = employee.getNodeID();
        weightedWithFewest = currentWeight;
      }
    }
    return bestPick;
  }

  public static String assignAny() {
    List<Employee> employeeList = new ArrayList<>(DAOPouch.getEmployeeDAO().getAll().values());

    if (employeeList.isEmpty()) return "Unassigned";

    String bestPick = employeeList.get(0).getNodeID();

    int weightedWithFewest = 10000; // No one should have 10000 tasks ever
    for (Employee employee : employeeList) {
      List<Request> requests = DAOFacade.searchRequestsByAssignee(employee.getNodeID());
      int currentWeight = 0;
      for (Request request : requests) {
        currentWeight += findWeight(request.getPriority().name());
      }
      if (currentWeight < weightedWithFewest) {
        bestPick = employee.getNodeID();
        weightedWithFewest = currentWeight;
      }
    }
    return bestPick;
  }
}
