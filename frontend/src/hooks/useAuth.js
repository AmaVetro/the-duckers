// src/hooks/useAuth.js

import { useEffect, useState } from "react";
import { login as loginRequest } from "../services/authService";


const TOKEN_KEY = "theduckers_token";



export const useAuth = () => {

    const [token, setToken] = useState(
        sessionStorage.getItem(TOKEN_KEY)
    );



    const isLogged = !!token;



    const login = async (email, password) => {

        const data = await loginRequest(email, password);

        const jwt = data.token;

        sessionStorage.setItem(TOKEN_KEY, jwt);

        setToken(jwt);

        window.dispatchEvent(new Event("auth-changed"));

        return true;
    };



    const logout = () => {

        sessionStorage.removeItem(TOKEN_KEY);

        setToken(null);

        window.dispatchEvent(new Event("auth-changed"));

    };



    useEffect(() => {

        const syncAuth = () => {
            setToken(sessionStorage.getItem(TOKEN_KEY));
        };

        window.addEventListener("auth-changed", syncAuth);

        window.addEventListener("storage", syncAuth);

        return () => {
            window.removeEventListener("auth-changed", syncAuth);
            window.removeEventListener("storage", syncAuth);
        };

    }, []);



    return {
        token,
        isLogged,
        login,
        logout
    };

};