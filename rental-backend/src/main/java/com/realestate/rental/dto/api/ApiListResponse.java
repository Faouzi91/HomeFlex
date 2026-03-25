package com.realestate.rental.dto.api;

import java.util.List;

/** Non-paged list wrapper (AGENT.md — never return a raw JSON array). */
public record ApiListResponse<T>(List<T> data) {}
