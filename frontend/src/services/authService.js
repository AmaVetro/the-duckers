// src/services/authService.js

import { apiFetch } from "./apiClient";


/**
 * Login user
 * Endpoint: POST /auth/login
 *
 * Body:
 * {
 *   email,
 *   password
 * }
 */
export const login = async (email, password) => {

    const data = await apiFetch("/auth/login", {
        method: "POST",
        body: JSON.stringify({
            email,
            password
        })
    });

    return data;
};



/**
 * Register new user
 * Endpoint: POST /auth/register
 *
 * Body:
 * {
 *   email,
 *   password
 * }
 */
export const register = async (email, password) => {

    const data = await apiFetch("/auth/register", {
        method: "POST",
        body: JSON.stringify({
            email,
            password
        })
    });

    return data;
};