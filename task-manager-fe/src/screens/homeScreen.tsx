// TaskList.tsx
import React, { useEffect, useState } from "react";
import { Task } from "../interfaces";
import customFetch from "../utils/CustomFetch";
import { AxiosError } from "axios";
import { DatePicker } from "antd";

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
    <>
      <td className="px-4 py-2 border-r">{task.taskTitle}</td>
      <td className="px-4 py-2 border-r">
        {task.taskDetails || "No details provided."}
      </td>
      <td className="px-4 py-2 border-r">{task.firstName}</td>
      <td className="px-4 py-2 border-r">
        {new Date(task.dateCreated).toLocaleDateString()}
      </td>
      <td className="px-4 py-2 border-r">{task.periodInDays}</td>
      <td className="px-4 py-2 border-r">
        {new Date(task.startDate).toLocaleDateString()}
      </td>
      <td className="px-4 py-2 border-r">
        {task.dateModified
          ? new Date(task.dateModified).toLocaleDateString()
          : "N/A"}
      </td>
      <td className="px-4 py-2 border-r">{task.status.replace("_", " ")}</td>
      <td className="px-4 py-2 border-r flex space-x-2">
        <button
          onClick={() => handleEditClick(task)}
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
        >
          Edit
        </button>
        <button
          onClick={() => handleDeleteClick(task.id)}
          className="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded"
        >
          Delete
        </button>
      </td>
    </>
  );

  const renderTaskEdit = (task: Task) => (
    <td colSpan={7} className="px-4 py-2">
      <form onSubmit={(e) => e.preventDefault()} className="p-4">
        <label
          htmlFor="title"
          className="block text-sm font-medium text-gray-700"
        >
          Title:
        </label>
        <input
          type="text"
          id="title"
          value={task.taskTitle}
          onChange={(e) =>
            setCurrentTask({ ...task, taskTitle: e.target.value })
          }
          className="mt-1 h-8 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
        />
        <label
          htmlFor="details"
          className="block text-sm font-medium text-gray-700 mt-4"
        >
          Details:
        </label>
        <textarea
          id="details"
          value={task.taskDetails || ""}
          onChange={(e) =>
            setCurrentTask({ ...task, taskDetails: e.target.value })
          }
          className="mt-1 h-[80px] block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
        />
        <div className="flex flex-row items-center mb-4">
          <p className="mr-4">Completion Period (Days):</p>
          <input
            type="number"
            className="w-20 h-5 border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            onChange={(e) =>
              setCurrentTask({ ...task, periodInDays: e.target.value })
            }
          />
        </div>
        <div className="mb-4">
          <label htmlFor="startDate" className="mb-2 block">
            Start Date:
          </label>

          <DatePicker
            className="w-full md:w-9/8 px-3 py-2.5 border border-gray-200"
            onChange={(e) => setCurrentTask({ ...task, startDate: e.toDate() })}
            id="startDate"
            showTime={false}
          />
        </div>
        <label className="mb-2" htmlFor="status">
          Task Status
        </label>
        <div className="relative">
          <select
            className="block appearance-none w-full border border-gray-200 text-gray-700 py-3 px-4 pr-8 rounded leading-tight focus:outline-none focus:bg-white focus:border-gray-500"
            id="status"
            onChange={(e) =>
              setCurrentTask({ ...task, status: e.target.value })
            }
          >
            <option value="TODO">Todo</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="BLOCKED">Blocked</option>
            <option value="DONE">Done</option>
          </select>
          <div className="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
            <svg
              className="fill-current h-4 w-4"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 20 20"
            >
              <path d="M9.293 12.95l.707.707L15.657 8l-1.414-1.414L10 10.828 5.757 6.586 4.343 8z" />
            </svg>
          </div>
        </div>
        <div className="flex space-x-2 mt-4">
          <button
            type="button"
            onClick={() => handleSave(currentTask!)}
            className="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded"
          >
            Save
          </button>
          <button
            type="button"
            onClick={handleCancel}
            className="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded"
          >
            Cancel
          </button>
        </div>
      </form>
    </td>
  );

  return (
    <div className="task-list p-4">
      <h2 className="text-2xl font-bold mb-4">User Tasks</h2>
      <p className="self-start text-voilet-700">Add New Task</p>
      <div className="grid grid-cols-12 gap-4">
        <div className="col-span-12">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-gray-200 text-sm font-medium border-b">
                <th className="px-4 py-2 border-r">Title</th>
                <th className="px-4 py-2 border-r">Details</th>
                <th className="px-4 py-2 border-r">Assigned To</th>
                <th className="px-4 py-2 border-r">Date Created</th>
                <th className="px-4 py-2 border-r">Task Period</th>
                <th className="px-4 py-2 border-r">Start Date</th>
                <th className="px-4 py-2 border-r">Last Modified</th>
                <th className="px-4 py-2 border-r">Status</th>
                <th className="px-4 py-2 border-r">Actions</th>
              </tr>
            </thead>
            <tbody>
              {tasks &&
                tasks.map((task) => (
                  <tr key={task.id} className="border-b hover:bg-gray-100">
                    {isEditing && currentTask?.id === task.id
                      ? renderTaskEdit(task)
                      : renderTaskDetails(task)}
                  </tr>
                ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default HomeScreen;
