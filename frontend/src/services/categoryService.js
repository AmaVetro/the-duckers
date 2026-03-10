//frontend/src/services/categoryService.js:

import { apiFetch } from "./apiClient";

/**
 * Fetch all categories from backend
 *
 * Endpoint:
 * GET /categories
 *
 * Response example:
 * [
 *   {
 *     "id": "vr_sets",
 *     "name": "VR Sets",
 *     "description": "Virtual reality devices"
 *   }
 * ]
 */
export const getCategories = async () => {

    const categories = await apiFetch("/categories", {
        method: "GET"
    });

    return categories;
};