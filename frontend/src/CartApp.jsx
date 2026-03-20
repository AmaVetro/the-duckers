//frontend/src/CartApp.jsx:

import { useState, useEffect } from "react";
import { Navbar } from "./components/Navbar";
import { useCart } from "./hooks/useCart";
import { CartRoutes } from "./routes/CartRoutes";
import { Footer } from "./components/Footer";
import { SessionExpiredModal } from "./components/SessionExpiredModal";


export const CartApp = () => {

    const {
        cartItems,
        loadingCart,
        handlerAddProductCart,
        handlerDeleteProductCart,
        updateQuantity,
        loadCart
    } = useCart();

    const [sessionExpired, setSessionExpired] = useState(false);

    useEffect(() => {

        const handleSessionExpired = () => {

            // ensure token is removed even if event was triggered manually
            sessionStorage.removeItem("theduckers_token");

            window.dispatchEvent(new Event("auth-changed"));

            setSessionExpired(true);

        };

        window.addEventListener("session-expired", handleSessionExpired);

        return () => {

            window.removeEventListener("session-expired", handleSessionExpired);

        };

    }, []);

    return (
        <>
            <div className="d-flex flex-column min-vh-100">

                <Navbar />

                <main className="flex-grow-1">

                    <div className="container mt-4 mb-0">

                        <CartRoutes
                            cartItems={cartItems}
                            loadingCart={loadingCart}
                            handlerAddProductCart={handlerAddProductCart}
                            handlerDeleteProductCart={handlerDeleteProductCart}
                            handlerUpdateQuantity={updateQuantity}
                            refreshCart={loadCart}
                        />

                    </div>

                </main>

                <Footer />

            </div>
            <SessionExpiredModal
                visible={sessionExpired}
                onClose={() => setSessionExpired(false)}
            />
        </>
    );

};