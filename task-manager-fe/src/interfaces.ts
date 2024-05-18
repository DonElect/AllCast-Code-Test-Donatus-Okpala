export interface Task {
  id: number;
  taskTitle: string;
  taskDetails?: string;
  status: string;
  email: string;
  firstName: string
  lastName: string;
  dateCreated: Date;
  periodInDays: number;
  startDate: Date;
  dateModified: Date;
}