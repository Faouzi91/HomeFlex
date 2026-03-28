package com.realestate.rental.dto.common;

/** Single value wrapper for primitives and small payloads (e.g. average rating, stats map). */
public record ApiValueResponse<T>(T data) {}
