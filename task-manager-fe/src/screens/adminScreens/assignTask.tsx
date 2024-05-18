import { useEffect, useState } from "react";
import { useForm, Controller } from "react-hook-form";
import customFetch from "../../utils/CustomFetch";
import { AxiosError } from "axios";
import { Task } from "../../interfaces";

interface User {
  firstName: string;
  lastName: string;
  email: string;
}

interface AssignTaskForm {
  taskId: number;
  email: string;
}

interface Props {
  task: Task;
  onCancle: () => void;
}

const AssignTaskScreen = ({ task, onCancle }: Props) => {
  const {
    control,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<AssignTaskForm>();
  const [users, setUsers] = useState<User[]>([]);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const res = await customFetch(localStorage.getItem("accessToken")).get(
        `/user-mgmt/users?pageNum=0&pageSize=20`
      );
      setUsers(res.data.responseData.content);
    } catch (err) {
      handleError(err);
    }
  };

  const handleError = (err: any) => {
    if (err && err instanceof AxiosError) {
      if (err.response?.data.code === "401") {
        setError("Session expired");
      }
      setError(err.response?.data.description);
    } else if (err && err instanceof Error) {
      setError(err.message);
    }
  };

  const onSubmit = async (data: AssignTaskForm) => {
    try {
      data.taskId = task.id;
      await customFetch(localStorage.getItem("accessToken"))
      .put(
        "/task-mgmt/assign",
        data
      );
      alert("Task assigned successfully!");
      reset();
      onCancle();
    } catch (err) {
      handleError(err);
    }
  };

  return (
    <td colSpan={7} className="px-4 py-2">
      <span className="text-right text-red-700">{error}</span>
      <div className="container max-w-md mx-auto bg-white shadow-md rounded p-6">
        <h2 className="text-2xl font-bold mb-4 text-center">
          Assign Task to User
        </h2>
        {error && <span className="text-red-500 mb-4 block">{error}</span>}
        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="flex flex-col mb-4">
            <label htmlFor="userId" className="mb-2 text-lg font-medium">
              Select User:
            </label>
            <Controller
              name="email"
              control={control}
              defaultValue={""}
              rules={{ required: true }}
              render={({ field }) => (
                <select
                  {...field}
                  className="h-12 border border-gray-300 rounded px-3 py-2 mb-1 focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">Select a user</option>
                  {users && users.map((user) => (
                    <option key={user.email} value={user.email}>
                      {user.firstName} {user.lastName}
                    </option>
                  ))}
                </select>
              )}
            />
            {errors.email && (
              <p className="text-red-500 text-sm">User is required</p>
            )}
          </div>
          <button
            type="submit"
            className="w-full h-12 mb-5 bg-green-500 hover:bg-green-700 text-white font-bold py-2 rounded mt-4 border-none"
          >
            Assign Task
          </button>
          <button
            type="button"
            onClick={onCancle}
            className="w-full h-12 bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded border-none"
          >
            Cancel
          </button>
        </form>
      </div>
    </td>
  );
};

export default AssignTaskScreen;
