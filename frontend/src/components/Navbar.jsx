//FrontEndDuckers/src/components/Navbar.jsx:

import { NavLink } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";


export const Navbar = () => {

    const { isLogged, logout } = useAuth();

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
            <div className="container-fluid">
                <a className="navbar-brand fs-3" href="/">The Duckers</a>
                <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse" id="navbarNav">
                    <ul className="navbar-nav">
                        <li className="nav-item">
                            <NavLink className={'nav-link'} to="/">Inicio</NavLink>
                        </li>
                        <li className="nav-item">
                            <NavLink className={'nav-link'} to="/catalog">Catálogo</NavLink>
                        </li>
                        <li className="nav-item">
                            <NavLink className={'nav-link'} to="/cart">Carrito</NavLink>
                        </li>
                    </ul>
                    <div className="ms-auto d-flex gap-2">
                        {!isLogged ? (
                            <NavLink className="btn btn-outline-primary" to="/login">Iniciar sesión</NavLink>
                        ) : (
                            <>
                                <NavLink className="btn btn-outline-primary" to="/account">Mi Cuenta</NavLink>
                                <button className="btn btn-outline-danger" onClick={() => logout()}>Cerrar sesión</button>
                            </>
                        )}
                    </div>
                </div>
            </div>
        </nav>
    );
}