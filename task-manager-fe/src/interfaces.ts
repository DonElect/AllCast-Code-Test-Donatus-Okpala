export interface Task {
  id: number;
  taskTitle: string;
  taskDetails?: string;
  status: string;
  email: string;
  firstName: string
  lastName: string;
  dateCreated: Date;
  periodInDays: string;
  startDate: Date;
  dateModified: Date;
}