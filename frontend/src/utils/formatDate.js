import { format, formatDistanceToNow } from 'date-fns';

/** "Nov 12, 2020 at 2:38 PM" */
export const formatFull = (dateStr) =>
  format(new Date(dateStr), 'MMM d, yyyy \'at\' h:mm a');

/** "3 hours ago" */
export const formatRelative = (dateStr) =>
  formatDistanceToNow(new Date(dateStr), { addSuffix: true });
