//FrontEndDuckers/src/hooks/useAuth.js:

import { useEffect, useState } from "react";


const STORAGE_KEY = "theduckers_auth";


const readUserFromStorage = () => {
  try {
    const raw = sessionStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
};




export const useAuth = () => {

  const [user, setUser] = useState(readUserFromStorage()); 

  useEffect(() => {
    const sync = () => setUser(readUserFromStorage());
    window.addEventListener("auth-changed", sync);

    window.addEventListener("storage", sync);
    return () => {
      window.removeEventListener("auth-changed", sync);
      window.removeEventListener("storage", sync);
    };
  }, []);




  const login = ({ username }) => {

    const cleanUsername = (username || "demo_user").trim();

    const mockUser = {
      username: cleanUsername,                 
      name: cleanUsername,                    
      email: `${cleanUsername}@example.com`,   
    };

    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(mockUser));
    window.dispatchEvent(new Event("auth-changed"));
    return true;
  };

  const logout = () => {
    sessionStorage.removeItem(STORAGE_KEY);
    window.dispatchEvent(new Event("auth-changed"));
  };

  const isLogged = !!user;

  return { user, isLogged, login, logout };
};
