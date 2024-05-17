import { DatePicker } from "antd";
import { AxiosError } from "axios";
import React, { useState } from "react";
import { useForm } from "react-hook-form";
// import { DatePicker } from 'antd';
import { useNavigate } from "react-router-dom";
import customFetch from "../utils/CustomFetch";

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
  } = useForm<Task>();
  const [startDate, setStartDate] = useState(new Date());
  const [error, setError] = useState("");

  const onChange = (date: Date | Date[]) => {
    setStartDate(date as Date);
  };

  const onSubmit = async (data: Task) => {
    if (!data.taskTitle || !data.periodInDays) {
      alert("Please enter a title and period for the task!");
      return;
    }
    try {
      data.startDate = startDate;
      console.log(data);

      await customFetch(localStorage.getItem("accessToken"))
        .post(`/task-mgmt/tasks`, data)
        .then((res) => {
          const responseData = res.data.responseData;

          console.log("Saving response: ", responseData);

          navigate("/home");
        });
    } catch (err) {
      if (err && err instanceof AxiosError)
        setError(err.response?.data.responseMessage);
      else if (err && err instanceof Error) setError(err.message);

      console.log("Error: ", err);
    }
  };

  return (
    <div className="container max-w-[80%] mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold mb-4">Add New Task</h1>
      <span className="text-red">{error}</span>
      <div className="flex flex-col mb-4">
        <label htmlFor="title" className="mb-2">
          Task Title:
        </label>
        <input
          type="text"
          id="title"
          className="h-12 border border-gray-300 rounded px-3 py-2 mb-4 focus:outline-none focus:ring-2 focus:ring-blue-500"
          placeholder="Enter Task Title"
          {...control.register("taskTitle", { required: true })}
        />
        {errors.taskTitle && (
          <p className="text-red-500 text-sm mb-2">Title is required</p>
        )}
      </div>
      <div className="flex flex-col mb-4">
        <label htmlFor="details" className="mb-2">
          Details (Optional):
        </label>
        <textarea
          id="details"
          className="h-12 border border-gray-300 rounded px-3 py-2 mb-4 focus:outline-none focus:ring-2 focus:ring-blue-500"
          placeholder="Add Details (Optional)"
          {...control.register("taskDetails")}
        />
      </div>
      <div className="flex flex-row items-center mb-4">
        <p className="mr-4">Completion Period (Days):</p>
        <input
          type="number"
          className="w-20 h-12 border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          {...control.register("periodInDays", { required: true })}
        />
        {errors.periodInDays && (
          <p className="text-red-500 text-sm ml-2">Period is required</p>
        )}
      </div>
      <div className="mb-4">
        <label htmlFor="startDate" className="mb-2 block">
          Start Date:
        </label>

        <DatePicker
          className="w-full md:w-9/8 px-3 py-2.5 bg-gray-200 border border-gray-200"
          onChange={onChange}
          id="startDate"
          showTime={false}
        />
      </div>
      <div className="w-full md:w-1/2 px-3 mb-6 md:mb-0 relative">
        <label className="mb-2" htmlFor="status">
          Task Status
        </label>
        <div className="relative">
          <select
            className="block appearance-none w-full bg-gray-200 border border-gray-200 text-gray-700 py-3 px-4 pr-8 rounded leading-tight focus:outline-none focus:bg-white focus:border-gray-500"
            id="status"
            {...control.register("status", { required: true })}
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
      </div>
      <button
        type="button"
        onClick={handleSubmit(onSubmit)}
        className="bg-green-500 mt-4 py-2 rounded text-white"
      >
        Add Task
      </button>
    </div>
  );
};

export default AddTaskScreen;
