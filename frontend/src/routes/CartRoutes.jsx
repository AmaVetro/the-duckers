//frontend/src/routes/CartRoutes.jsx:

import { Navigate, Route, Routes } from "react-router-dom"
import { CartView } from "../components/CartView"
import { CatalogView } from "../components/CatalogView"
import { HomeView } from '../components/HomeView';
import { AccountView } from '../components/AccountView';
import { LoginView } from '../components/LoginView';
import { useAuth } from "../hooks/useAuth";
import { CheckoutView } from "../components/CheckoutView";
import { ProductDetailView } from "../components/ProductDetailView";
import { ProtectedRoute } from "../components/auth/ProtectedRoute";
import { RegisterView } from "../components/RegisterView";



export const CartRoutes = ({ handlerAddProductCart, handlerDeleteProductCart, handlerUpdateQuantity, cartItems }) => {
    
    const { isLogged } = useAuth();
    
    return (
        <div className="d-flex flex-column min-vh-100">
        <Routes>
            <Route path="home" element={<HomeView />} />
            <Route path="catalog" element={<CatalogView handler={handlerAddProductCart} />}/>
            <Route
                path="cart"
                element={
                    <ProtectedRoute>
                    {
                        cartItems?.length <= 0
                        ?
                        <div className="alert alert-warning">No hay productos en el carrito de compras!</div>
                        :
                        (
                        <div className="my-4">
                            <CartView
                                items={cartItems}
                                handlerDelete={handlerDeleteProductCart}
                                handlerUpdateQuantity={handlerUpdateQuantity}
                            />
                        </div>
                        )
                    }
                    </ProtectedRoute>
                }
            />
            
            <Route path="product/:id" element={ 
                <ProductDetailView handler={handlerAddProductCart} />
            } />

            <Route
                path="account"
                element={
                    <ProtectedRoute>
                        <AccountView />
                    </ProtectedRoute>
                }
            />
            <Route path="login" element={<LoginView />} />
            <Route path="register" element={<RegisterView />} />
            <Route path="/" element={<Navigate to={'/home'} />} />
            <Route
                path="checkout"
                element={
                    <ProtectedRoute>
                    <CheckoutView />
                    </ProtectedRoute>
                }
            />
        </Routes>
        </div>
    );
};

