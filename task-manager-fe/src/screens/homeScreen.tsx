// TaskList.tsx
import { useEffect, useState } from "react";
import { Task } from "../interfaces";
import customFetch from "../utils/CustomFetch";
import { AxiosError } from "axios";
import { useNavigate } from "react-router-dom";
import EditTask from "./editTask";
import AssignTaskScreen from "./assignTask";

const HomeScreen = () => {
  const [isEditing, setIsEditing] = useState(false);
  const [currentTask, setCurrentTask] = useState<Task | null>(null);
  const [pageNum, setPageNum] = useState(0);
  const [pageSize, setPageSize] = useState(20);
  const [error, setError] = useState("");
  const [isLast, setLast] = useState(true);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [hasRefresh, setRefresh] = useState(false);
  const [isAssigning, setIsAssigning] = useState(false);
  
  const navigate = useNavigate();

  async function onDelete(taskId: number) {
    try {
      await customFetch(localStorage.getItem("accessToken"))
        .delete(`/task-mgmt/tasks?taskId=${taskId}`)
        .then((res) => {
          res.data.responseData;

          setRefresh(!hasRefresh);

          alert("Task deleted successfully!");
        });
    } catch (err) {
      if (err && err instanceof AxiosError) {
        if (err.response?.data.code === "401") {
          setError("Session expired");
        }
        setError(err.response?.data.description);
      } else if (err && err instanceof Error) setError(err.message);

      console.log("Error: ", err);
    }
  }

  useEffect(() => {
    getOrderHistory();
  }, [pageNum, hasRefresh]);

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
      if (err && err instanceof AxiosError) {
        if (err.response?.data.code === "401") {
          setError("Session expired");
        }
        setError(err.response?.data.description);
      } else if (err && err instanceof Error) setError(err.message);

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

  const handleCancel = () => {
    setRefresh(!hasRefresh);
    setIsEditing(false);
    setCurrentTask(null);
  };

  const handleAssignClick = (task: Task) => {
    setIsAssigning(true);
    setCurrentTask(task);
  }

  const cancelAssigning = ()=> {
    setRefresh(!hasRefresh);
    setIsAssigning(false);
  }

  const renderTaskDetails = (task: Task) => (
    <>
      <td className="px-4 py-2 border-r">{task.taskTitle}</td>
      <td className="px-4 py-2 border-r">
        {task.taskDetails || "No details provided."}
      </td>
      <td className="px-4 py-2 border-r">{task.firstName}{" "}{task.lastName}</td>
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
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded border-none"
        >
          Edit
        </button>
        <button
          onClick={() => handleDeleteClick(task.id)}
          className="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded border-none"
        >
          Delete
        </button>
        <button
          onClick={() => handleAssignClick(task)}
          className="bg-mantis-500 hover:bg-mantis-700 text-white font-bold py-2 px-4 rounded border-none"
        >
          Assign
        </button>
      </td>
    </>
  );

  function handlePreviousPage() {
    setPageNum((prev) => {
      let n = prev - 1;

      if (n < 0) n = 0;

      return n;
    });
  }

  function handleNextpage() {
    if (isLast) return;

    setPageNum((prev) => prev + 1);
  }

  return (
    <div className="flex min-h-screen bg-gray-100 p-4">
      <div className="container max-w-6xl mx-auto bg-white shadow-md rounded p-6">
        <h2 className="text-2xl font-bold mb-4 text-center">User Tasks</h2>
        <p
          className="text-right text-violet-700 cursor-pointer mb-4"
          onClick={() => navigate("/tasks/new")}
        >
          Add New Task
        </p>
        <span className="text-red-700 block mb-4">{error}</span>
        <div className="overflow-x-auto">
          <table className="w-full text-left table-auto border-collapse">
            <thead>
              <tr className="bg-gray-200 text-sm font-medium">
                <th className="px-4 py-2 border-b">Title</th>
                <th className="px-4 py-2 border-b">Details</th>
                <th className="px-4 py-2 border-b">Assigned To</th>
                <th className="px-4 py-2 border-b">Date Created</th>
                <th className="px-4 py-2 border-b">Task Period</th>
                <th className="px-4 py-2 border-b">Start Date</th>
                <th className="px-4 py-2 border-b">Last Modified</th>
                <th className="px-4 py-2 border-b">Status</th>
                <th className="px-4 py-2 border-b">Actions</th>
              </tr>
            </thead>
            <tbody>
              {tasks &&
                tasks.map((task) => (
                  <>
                  <tr key={task.id} className="hover:bg-gray-100">
                    {isEditing && currentTask?.id === task.id
                      ? <EditTask task={task} onCancle={handleCancel} />
                      : renderTaskDetails(task)}
                  </tr>
                  <tr key={task.id} className="hover:bg-gray-100">
                  {(isAssigning && currentTask?.id === task.id)
                    && <AssignTaskScreen  onCancle={cancelAssigning} task={task} />}
                </tr>
                </>
                ))}
            </tbody>
          </table>
        </div>
        <div className="flex justify-between mt-4">
          <button
            type="button"
            onClick={handlePreviousPage}
            className={` ${
              pageNum < 1
                ? "bg-gray-200 text-gray-700 cursor-not-allowed"
                : "bg-green-400 hover:bg-green-700 text-white"
            } font-bold py-2 px-4 rounded border-none`}
          >
            Previous
          </button>
          <button
            type="button"
            onClick={handleNextpage}
            className={`${
              isLast
                ? "bg-gray-200 text-gray-700 cursor-not-allowed"
                : "bg-green-400 hover:bg-green-700 text-white"
            } font-bold py-2 px-4 rounded border-none`}
          >
            Next
          </button>
        </div>
      </div>
    </div>
  );
};

export default HomeScreen;
