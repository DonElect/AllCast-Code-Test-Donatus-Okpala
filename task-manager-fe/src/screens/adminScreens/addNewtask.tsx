// AddTaskScreen.tsx
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
  const {
    control,
    handleSubmit,
    formState: { errors },
    register,
  } = useForm<Task>();
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
        .then((res) => {
          res.data.responseData;
          navigate("/tasks");
        });
    } catch (err) {
      if (err && err instanceof AxiosError) {
        if (err.response?.data.code === "401") {
          setError("Session expired");
        }
        setError(err.response?.data.description);
      } else if (err && err instanceof Error) setError(err.message);
    }
  };

  return (
    <div className="flex items-center justify-center">
      <div className="container max-w-md mx-auto px-4 py-8 bg-mantis-200 shadow-md rounded">
        <h1 className="text-3xl font-bold mb-6 text-center">Add New Task</h1>
        {error && (
          <span className="text-red-500 mb-4 block text-center">{error}</span>
        )}
        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="flex flex-col mb-4">
            <label htmlFor="title" className="mb-2 text-lg font-medium">
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
            <label htmlFor="details" className="mb-2 text-lg font-medium">
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
            <label htmlFor="periodInDays" className="mb-2 text-lg font-medium">
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
            <label htmlFor="startDate" className="mb-2 text-lg font-medium">
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
            <label htmlFor="status" className="mb-2 text-lg font-medium">
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
            className="w-full h-12 bg-green-500 hover:bg-green-700 text-white font-bold py-2 rounded mt-4 border-none"
          >
            Add Task
          </button>
          <button
            type="button"
            className="w-full h-12 bg-gray-400 hover:bg-gray-600 text-white font-bold py-2 rounded mt-4 border-none"
            onClick={() => navigate("/home")}
          >
            cancel
          </button>
        </form>
      </div>
    </div>
  );
};

export default AddTaskScreen;
