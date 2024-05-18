import { useEffect, useState } from "react";
import { Task } from "../../interfaces";
import customFetch from "../../utils/CustomFetch";
import { AxiosError } from "axios";
import { useNavigate } from "react-router-dom";
import EditTask from "./editTask";
import AssignTaskScreen from "./assignTask";

const HomeScreen = () => {
  const [isEditing, setIsEditing] = useState(false);
  const [currentTask, setCurrentTask] = useState<Task | null>(null);
  const [pageNum, setPageNum] = useState(0);
  const [pageSize, setPageSize] = useState(5);
  const [error, setError] = useState("");
  const [isLast, setLast] = useState(true);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [hasRefresh, setRefresh] = useState(false);
  const [isAssigning, setIsAssigning] = useState(false);
  const [role, setRole] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");

  const navigate = useNavigate();

  async function onDelete(taskId: number) {
    try {
      await customFetch(localStorage.getItem("accessToken"))
        .delete(`/task-mgmt/tasks?taskId=${taskId}`)
        .then(() => {
          setRefresh(!hasRefresh);
          alert("Task deleted successfully!");
        });
    } catch (err) {
      if (err && err instanceof AxiosError) {
        if (err.response?.data.code === "401") {
          setError("Session expired");
        }
        setError(err.response?.data.description);
      } else if (err && err instanceof Error) {
        setError(err.message);
      }
    }
  }

  useEffect(() => {
    const userRole = localStorage.getItem("role") || "";
    getOrderHistory(userRole);
    setRole(userRole);
    setFirstName(localStorage.getItem("firstName") || "");
    setLastName(localStorage.getItem("lastName") || "");
  }, [pageNum, hasRefresh]);

  const getOrderHistory = async (role: string) => {
    try {
      await customFetch(localStorage.getItem("accessToken"))
        .get(
          `/task-mgmt${
            role === "ADMIN" ? "/tasks" : "/tasks/users"
          }?pageNum=${pageNum}&pageSize=${pageSize}`
        )
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
      } else if (err && err instanceof Error) {
        setError(err.message);
      }
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
  };

  const cancelAssigning = () => {
    setRefresh(!hasRefresh);
    setIsAssigning(false);
    setCurrentTask(null);
  };

  const handleActionChange = (
    event: React.ChangeEvent<HTMLSelectElement>,
    task: Task
  ) => {
    const action = event.target.value;
    switch (action) {
      case "edit":
        handleEditClick(task);
        break;
      case "delete":
        handleDeleteClick(task.id);
        break;
      case "assign":
        handleAssignClick(task);
        break;
      default:
        break;
    }
  };

  const renderTaskDetails = (task: Task) => (
    <>
      <td className="px-4 py-2 border-r">{task.taskTitle}</td>
      <td className="px-4 py-2 border-r">
        {task.taskDetails || "No details provided."}
      </td>
      <td className="px-4 py-2 border-r">
        {task.firstName} {task.lastName}
      </td>
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
      <td className="px-4 py-2 border-r">
        {role === "ADMIN" ? (
          <select
            className="bg-white border rounded px-4 py-2"
            onChange={(event) => handleActionChange(event, task)}
            defaultValue=""
          >
            <option value="" disabled>
              Select Action
            </option>
            <option value="edit">Edit</option>
            <option value="delete">Delete</option>
            <option value="assign">Assign</option>
          </select>
        ) : (
          <button
            onClick={() => handleEditClick(task)}
            className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded transition duration-200 border-none"
          >
            Edit
          </button>
        )}
      </td>
    </>
  );

  function handlePreviousPage() {
    setPageNum((prev) => (prev > 0 ? prev - 1 : 0));
  }

  function handleNextpage() {
    if (!isLast) setPageNum((prev) => prev + 1);
  }

  return (
    <div className="flex min-h-screen bg-gradient-to-r from-gray-300 via-gray-100 to-gray-300 p-4">
      <div className="container max-w-6xl mx-auto bg-white shadow-2xl rounded-lg p-8">
        <h2 className="text-3xl font-bold mb-6 text-center text-indigo-700">
          {role === "ADMIN" ? "User Tasks" : `${firstName} ${lastName}`}
        </h2>
        {role === "ADMIN" && (
          <p
            className="text-right text-indigo-600 cursor-pointer mb-6"
            onClick={() => navigate("/tasks/new")}
          >
            Add New Task
          </p>
        )}
        {error && <span className="text-red-700 block mb-4">{error}</span>}
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-indigo-100 text-sm font-semibold text-indigo-800">
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
              {tasks.map((task, i) => (
                <>
                  <tr key={i} className="hover:bg-indigo-50">
                    {isEditing && currentTask?.id === task.id ? (
                      <EditTask task={task} onCancle={handleCancel} />
                    ) : (
                      renderTaskDetails(task)
                    )}
                  </tr>
                  <tr key={task.id} className="hover:bg-indigo-50">
                    {isAssigning && currentTask?.id === task.id && (
                      <AssignTaskScreen
                        onCancle={cancelAssigning}
                        task={task}
                      />
                    )}
                  </tr>
                </>
              ))}
            </tbody>
          </table>
        </div>
        <div className="flex justify-between mt-6">
          <button
            type="button"
            onClick={handlePreviousPage}
            className={`${
              pageNum < 1
                ? "bg-gray-200 text-gray-500 cursor-not-allowed"
                : "bg-indigo-500 hover:bg-indigo-700 text-white"
            } font-bold py-2 px-4 rounded transition duration-200`}
          >
            Previous
          </button>
          <button
            type="button"
            onClick={handleNextpage}
            className={`${
              isLast
                ? "bg-gray-200 text-gray-500 cursor-not-allowed"
                : "bg-indigo-500 hover:bg-indigo-700 text-white"
            } font-bold py-2 px-4 rounded transition duration-200`}
          >
            Next
          </button>
        </div>
      </div>
    </div>
  );
};

export default HomeScreen;
