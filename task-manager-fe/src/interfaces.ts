export interface Task {
  id: number;
  title: string;
  details?: string;
  email: string;
  firstName: string
  lastName: string;
  dateCreated: Date;
  taskPeriod: string;
  startDate: Date;
  dateModified: Date;
}