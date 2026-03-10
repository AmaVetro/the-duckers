//frontend/src/services/authService.js:

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
    try {

        const data = await apiFetch("/auth/login", {
            method: "POST",
            body: JSON.stringify({
                email,
                password
            })
        });

        return data;

    } catch (error) {

        // Si backend envía mensaje, propagarlo
        const message =
            error?.message ||
            "Login failed. Please check your credentials.";

        throw new Error(message);
    }
};


/**
 * Register new user
 * Endpoint: POST /auth/register
 */
export const register = async ({
    email,
    password,
    firstName,
    lastNameFather,
    lastNameMother,
    referralCode
}) => {
    try {

        const data = await apiFetch("/auth/register", {
            method: "POST",
            body: JSON.stringify({
                email,
                password,
                firstName,
                lastNameFather,
                lastNameMother,
                referralCode
            })
        });

        return data;

    } catch (error) {

        // Extraer mensaje real del backend si existe
        const message =
            error?.message ||
            "Registration failed. Please verify your data.";

        throw new Error(message);
    }
};