// date.utils.ts
export class DateUtils {
  static formatDate(date: Date | string): string {
    const d = new Date(date);
    return d.toLocaleDateString();
  }

  static formatDateTime(date: Date | string): string {
    const d = new Date(date);
    return d.toLocaleString();
  }

  static getRelativeTime(date: Date | string): string {
    const d = new Date(date);
    const now = new Date();
    const diffMs = now.getTime() - d.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return "Just now";
    if (diffMins < 60)
      return `${diffMins} minute${diffMins > 1 ? "s" : ""} ago`;
    if (diffHours < 24)
      return `${diffHours} hour${diffHours > 1 ? "s" : ""} ago`;
    if (diffDays < 7) return `${diffDays} day${diffDays > 1 ? "s" : ""} ago`;
    return this.formatDate(date);
  }
}
