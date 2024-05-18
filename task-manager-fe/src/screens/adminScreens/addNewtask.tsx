import { DatePicker } from "antd";
import { AxiosError } from "axios";
import { useState } from "react";
import { useForm, Controller } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import customFetch from "../../utils/CustomFetch";

interface Task {
  taskTitle: string;
  taskDetails?: string;
  periodInDays: number;
  startDate: Date;
  status: string;
}

const AddTaskScreen = () => {
  const navigate = useNavigate();
  const { control, handleSubmit, formState: { errors }, register } = useForm<Task>();
  const [startDate, setStartDate] = useState(new Date());
  const [error, setError] = useState("");

  const onChange = (date: any) => {
    setStartDate(date ? date.toDate() : new Date());
  };

  const onSubmit = async (data: Task) => {
    if (!data.taskTitle || !data.periodInDays) {
      alert("Please enter a title and period for the task!");
      return;
    }
    try {
      data.startDate = startDate;
      await customFetch(localStorage.getItem("accessToken"))
        .post(`/task-mgmt/tasks`, data)
        .then(() => {
          navigate("/tasks");
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

  return (
    <div className="flex items-center justify-center min-h-screen bg-gradient-to-r">
      <div className="container max-w-lg mx-auto p-6 bg-gray-300 shadow-2xl rounded-lg">
        <h1 className="text-4xl font-bold mb-6 text-center text-blue-700">Add New Task</h1>
        {error && (
          <span className="text-red-500 mb-4 block text-center">{error}</span>
        )}
        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="flex flex-col mb-4">
            <label htmlFor="title" className="mb-2 text-lg font-medium text-gray-700">
              Task Title:
            </label>
            <input
              type="text"
              id="title"
              className="h-10 border border-gray-300 rounded px-3 py-2 mb-1 focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Enter Task Title"
              {...register("taskTitle", { required: true })}
            />
            {errors.taskTitle && (
              <p className="text-red-500 text-sm">Title is required</p>
            )}
          </div>
          <div className="flex flex-col mb-4">
            <label htmlFor="details" className="mb-2 text-lg font-medium text-gray-700">
              Details (Optional):
            </label>
            <textarea
              id="details"
              className="h-24 border border-gray-300 rounded px-3 py-2 mb-1 focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Add Details (Optional)"
              {...register("taskDetails")}
            />
          </div>
          <div className="flex flex-col mb-4">
            <label htmlFor="periodInDays" className="mb-2 text-lg font-medium text-gray-700">
              Completion Period (Days):
            </label>
            <input
              type="number"
              id="periodInDays"
              className="h-10 border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
              {...register("periodInDays", { required: true })}
            />
            {errors.periodInDays && (
              <p className="text-red-500 text-sm">Period is required</p>
            )}
          </div>
          <div className="flex flex-col mb-4">
            <label htmlFor="startDate" className="mb-2 text-lg font-medium text-gray-700">
              Start Date:
            </label>
            <Controller
              name="startDate"
              control={control}
              render={({ field }) => (
                <DatePicker
                  className="w-full h-10 border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  onChange={(date) => {
                    field.onChange(date);
                    onChange(date);
                  }}
                />
              )}
            />
          </div>
          <div className="flex flex-col mb-4">
            <label htmlFor="status" className="mb-2 text-lg font-medium text-gray-700">
              Task Status:
            </label>
            <select
              id="status"
              className="block w-full h-10 border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
              {...register("status", { required: true })}
            >
              <option value="TODO">Todo</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="BLOCKED">Blocked</option>
              <option value="DONE">Done</option>
            </select>
            {errors.status && (
              <p className="text-red-500 text-sm">Status is required</p>
            )}
          </div>
          <button
            type="submit"
            className="w-full h-12 bg-green-500 hover:bg-green-700 text-white font-bold py-2 rounded mt-4 border-none transition duration-200"
          >
            Add Task
          </button>
          <button
            type="button"
            className="w-full h-12 bg-gray-400 hover:bg-gray-600 text-white font-bold py-2 rounded mt-4 border-none transition duration-200"
            onClick={() => navigate("/tasks")}
          >
            Cancel
          </button>
        </form>
      </div>
    </div>
  );
};

export default AddTaskScreen;
