// src/services/apiClient.js

const API_BASE = import.meta.env.VITE_API_URL;


/**
 * Centralized API fetch wrapper
 *
 * Responsibilities:
 * - Attach Authorization header (JWT)
 * - Apply base API URL
 * - Handle JSON parsing
 * - Standardize error handling
 */
export const apiFetch = async (path, options = {}) => {

    const token = sessionStorage.getItem("theduckers_token");

    const headers = {
        "Content-Type": "application/json",
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...(options.headers || {})
    };

    const response = await fetch(`${API_BASE}${path}`, {
        ...options,
        headers
    });

    if (!response.ok) {

        const errorText = await response.text();

        throw new Error(
            `API Error ${response.status}: ${errorText}`
        );
    }

    return response.json();
};