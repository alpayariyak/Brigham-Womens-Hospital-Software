package edu.wpi.DapperDaemons.entities.requests;

import edu.wpi.DapperDaemons.tables.TableHandler;

public interface Request {

  enum RequestStatus {
    REQUESTED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
  }

  enum Priority {
    LOW,
    MEDIUM,
    HIGH,
    OVERDUE
  }

  /** @return name of the Request class */
  @TableHandler(table = 1, col = 0)
  @TableHandler(table = 2, col = 0)
  String requestType();

  /** @return the priority of a given request */
  @TableHandler(table = 1, col = 2)
  @TableHandler(table = 2, col = 5)
  Priority getPriority();

  /** @return whether or not this is a request that requires things to be moved */
  boolean requiresTransport();

  /** @return roomID of a given request */
  @TableHandler(table = 2, col = 1)
  String getRoomID();

  String transportFromRoomID();

  String getNodeID();

  @TableHandler(table = 2, col = 2)
  String getRequesterID();

  @TableHandler(table = 1, col = 1)
  @TableHandler(table = 2, col = 3)
  String getAssigneeID();

  @TableHandler(table = 2, col = 4)
  RequestStatus getStatus();

  String getDateNeeded();
}
