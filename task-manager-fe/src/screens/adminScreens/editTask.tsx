import { DatePicker } from "antd";
import { AxiosError } from "axios";
import { Task } from "../../interfaces";
import customFetch from "../../utils/CustomFetch";
import { useState } from "react";

interface Props {
  task: Task;
  onCancle: () => void;
}

function EditTask({ task, onCancle }: Props) {
  const [error, setError] = useState("");
  const [newTitle, setNewTitle] = useState(task.taskTitle);
  const [newDetails, setNewDetails] = useState(task.taskDetails);
  const [newStatus, setNewStatus] = useState(task.status);
  const [newPeriod, setNewPeriod] = useState(task.periodInDays);
  const [newStartdate, setNewstartdate] = useState(task.startDate);

  const handleSave = () => {
    const updatedTask: Task = {
      id: task.id,
      taskTitle: newTitle,
      taskDetails: newDetails,
      status: newStatus,
      email: "",
      firstName: "",
      lastName: "",
      periodInDays: newPeriod,
      startDate: newStartdate,
      dateCreated: task.dateCreated,
      dateModified: task.dateModified
    }
    onEdit(updatedTask);
    onCancle();
  };

  async function onEdit(updatedTask: Task) {
    if (!updatedTask.taskTitle || !updatedTask.periodInDays) {
      alert("Please enter a title and period for the task!");
      return;
    }
    console.log("Updated task: ", updatedTask);
    try {
      await customFetch(localStorage.getItem("accessToken"))
        .put(`/task-mgmt/tasks?taskId=${updatedTask.id}`, updatedTask)
        .then((res) => {
          const responseData = res.data.responseData;

          console.log("Saving response: ", responseData);

          alert("Task updated successfully!");
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

  return (
    <td colSpan={7} className="px-4 py-2">
      <span className="text-right text-red-700">{error}</span>
      <form onSubmit={(e) => e.preventDefault()} className="p-4">
        <label
          htmlFor="taskTitle"
          className="block text-sm font-medium text-gray-700"
        >
          Title:
        </label>
        <input
          type="text"
          id="taskTitle"
          name="taskTitle"
          value={newTitle}
          onChange={
            (e) => setNewTitle(e.target.value)}
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
          value={newDetails}
          onChange={(e) =>setNewDetails(e.target.value)
          }
          className="mt-1 h-[80px] block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
        />
        <div className="flex flex-row items-center mb-4">
          <p className="mr-4">Completion Period (Days):</p>
          <input
            type="number"
            inputMode={"numeric"}
            className="w-20 h-5 border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            onChange={(e) =>setNewPeriod(parseInt(e.target.value))}
            value={newPeriod}
          />
        </div>
        <div className="mb-4">
          <label htmlFor="startDate" className="mb-2 block">
            Start Date:
          </label>

          <DatePicker
            className="w-full md:w-9/8 px-3 py-2.5 border border-gray-200"
            onChange={(e) => setNewstartdate(e.toDate())}
            id="startDate"
            showTime={false}
            // value={task.startDate}
          />
        </div>
        <label className="mb-2" htmlFor="status">
          Task Status
        </label>
        <div className="relative">
          <select
            className="block appearance-none w-full border border-gray-200 text-gray-700 py-3 px-4 pr-8 rounded leading-tight focus:outline-none focus:bg-white focus:border-gray-500"
            id="status"
            onChange={(e) =>setNewStatus(e.target.value)
            }
            value={newStatus}
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
            onClick={() => handleSave()}
            className="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded border-none"
          >
            Save
          </button>
          <button
            type="button"
            onClick={onCancle}
            className="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded border-none"
          >
            Cancel
          </button>
        </div>
      </form>
    </td>
  );
}

export default EditTask;
