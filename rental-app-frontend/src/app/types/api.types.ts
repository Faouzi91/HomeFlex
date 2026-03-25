/** Mirrors backend ApiPageResponse / ApiListResponse / ApiValueResponse (AGENT.md). */
export interface ApiPageResponse<T> {
  data: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface ApiListResponse<T> {
  data: T[];
}

export interface ApiValueResponse<T> {
  data: T;
}
