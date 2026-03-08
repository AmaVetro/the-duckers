import { Navigate } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";


export const ProtectedRoute = ({ children }) => {

    const { isLogged } = useAuth();

    if (!isLogged) {
        return <Navigate to="/login" replace />;
    }

    return children;

};