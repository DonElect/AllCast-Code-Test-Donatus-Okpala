// interfaces.ts

// TaskList.tsx
import React, { useEffect, useState } from "react";
import { Task } from "../interfaces";
import customFetch from "../utils/CustomFetch";
import { AxiosError } from "axios";

const HomeScreen = () => {
  const [isEditing, setIsEditing] = useState(false);
  const [currentTask, setCurrentTask] = useState<Task | null>(null);
  const [pageNum, setPageNum] = useState(0);
  const [pageSize, setPageSize] = useState(20);
  const [error, setError] = useState("");
  const [isLast, setLast] = useState(true);
  const [tasks, setTasks] = useState<Task[]>([]);

  function onEdit(updatedTask: Task) {}

  function onDelete(taskId: number) {}

  useEffect(() => {
    getOrderHistory();
  }, [pageNum]);

  const getOrderHistory = async () => {
    try {
      await customFetch(localStorage.getItem("accessToken"))
        .get(`/task-mgmt/tasks?pageNum=${pageNum}&pageSize=${pageSize}`)
        .then((res) => {
          const data = res.data.responseData;

          setPageNum(data.pageNum);
          setPageSize(data.pageSize);
          setLast(data.last);
          setTasks(data.content);
        });
    } catch (err) {
      if (err && err instanceof AxiosError)
        setError(err.response?.data.responseMessage);
      else if (err && err instanceof Error) setError(err.message);

      console.log("Error: ", err);
    }
  };

  const handleEditClick = (task: Task) => {
    setIsEditing(true);
    setCurrentTask(task);
  };

  const handleDeleteClick = (taskId: number) => {
    onDelete(taskId);
  };

  const handleSave = (updatedTask: Task) => {
    onEdit(updatedTask);
    setIsEditing(false);
    setCurrentTask(null);
  };

  const handleCancel = () => {
    setIsEditing(false);
    setCurrentTask(null);
  };

  const renderTaskDetails = (task: Task) => (
    <div>
      <p>Details: {task.details || "No details provided."}</p>
      <p>Assigned to: {task.firstName}</p>
      <p>Date Created: {task.dateCreated.toLocaleDateString()}</p>
      <p>Task Period: {task.taskPeriod}</p>
      <p>Start Date: {task.startDate.toLocaleDateString()}</p>
      {task.dateModified && (
        <p>Last Modified: {task.dateModified.toLocaleDateString()}</p>
      )}
    </div>
  );

  const renderTaskEdit = (task: Task) => (
    <form onSubmit={(e) => e.preventDefault()}>
      <label htmlFor="title">Title:</label>
      <input
        type="text"
        id="title"
        value={task.title}
        onChange={(e) => setCurrentTask({ ...task, title: e.target.value })}
      />
      <label htmlFor="details">Details:</label>
      <textarea
        id="details"
        value={task.details || ""}
        onChange={(e) => setCurrentTask({ ...task, details: e.target.value })}
      />
      <div className="actions">
        <button type="button" onClick={() => handleSave(currentTask!)}>
          Save
        </button>
        <button type="button" onClick={handleCancel}>
          Cancel
        </button>
      </div>
    </form>
  );

  return (
    <div className="task-list">
      <h2>Your Tasks</h2>
      <ul>
        {tasks.map((task) => (
          <li key={task.id}>
            {isEditing && currentTask?.id === task.id ? (
              renderTaskEdit(task)
            ) : (
              <>
                <h3>{task.title}</h3>
                {renderTaskDetails(task)}
                <div className="actions">
                  <button onClick={() => handleEditClick(task)}>Edit</button>
                  <button onClick={() => handleDeleteClick(task.id)}>
                    Delete
                  </button>
                </div>
              </>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default HomeScreen;
