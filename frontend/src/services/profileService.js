// src/services/profileService.js

import { apiFetch } from "./apiClient";


/**
 * Fetch authenticated user profile
 * Endpoint: GET /me
 *
 * Requires JWT token in Authorization header
 */
export const getProfile = async () => {

    const profile = await apiFetch("/me", {
        method: "GET"
    });

    return profile;

};