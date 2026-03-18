//frontend/src/components/ProductDetailView.jsx:


import { useParams, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { getProductById } from "../services/productService";
import { clp } from "../utils/currency";
import { useAuth } from "../hooks/useAuth";


export const ProductDetailView = ({ handler }) => {

    const { id } = useParams();
    const [product, setProduct] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const { isLogged } = useAuth();

    const navigate = useNavigate();


    useEffect(() => {

        const loadProduct = async () => {

            try {

                const prod = await getProductById(id);
                setProduct(prod);

            } catch (error) {

                console.error("Error cargando producto", error);

            } finally {

                setIsLoading(false);

            }

        };

        loadProduct();

    }, [id]);


    if (isLoading) {
        return (
            <div className="alert alert-info mt-3">
                Cargando producto...
            </div>
        );
    }

    if (!product) {
        return (
            <div className="alert alert-danger mt-3">
                Producto no encontrado.
            </div>
        );
    }


    const image = product.images?.[0] || "https://placehold.co/600x400?text=No+Image";


    const onAddProduct = () => {

        if (!isLogged) {
            navigate("/login");
            return;
        }

        handler({
            id: product.id,
            name: product.name,
            description: product.description,
            price: product.price,
        });

        alert("Producto agregado exitosamente al carrito.");

    };


    const specsEntries = product.specs ? Object.entries(product.specs) : [];

    // ===============================
    // Stock display logic
    // ===============================
    let stockLabel;

    if (product.stock === 0) {
        stockLabel = "No disponible";
    } else if (product.stock > 30) {
        stockLabel = "+30";
    } else {
        stockLabel = product.stock;
    }

    return (

        <div className="container my-4">

            <div className="row g-4">


                <div className="col-12 col-md-5">

                    <div className="card">

                        <img
                            src={image}
                            alt={product.name}
                            className="card-img-top"
                            style={{ objectFit: "cover", width: "100%" }}
                        />

                    </div>

                </div>


                <div className="col-12 col-md-7">

                    <div className="card h-100">

                        <div className="card-body">

                            <h3 className="card-title mb-2">
                                {product.name}
                            </h3>

                            <p className="card-text mb-3">
                                {product.description}
                            </p>

                            <p className="h4 mb-3">
                                {clp(product.price)}
                            </p>

                            <p className="mb-3">
                                Stock disponible: {stockLabel}
                            </p>


                            {specsEntries.length > 0 && (
                                <>
                                    <h5 className="mt-3">
                                        Especificaciones técnicas
                                    </h5>

                                    <ul>

                                        {specsEntries.map(([key, value]) => (

                                            <li key={key}>
                                                <strong>{key}:</strong> {value}
                                            </li>

                                        ))}

                                    </ul>
                                </>
                            )}


                            <div className="mt-4 d-flex gap-2">

                                <button
                                    className={`btn ${product.stock === 0 ? "btn-secondary" : "btn-primary"}`}
                                    disabled={product.stock === 0}
                                    onClick={onAddProduct}
                                >
                                    Añadir al carrito
                                </button>

                                <button
                                    className="btn btn-outline-secondary"
                                    onClick={() => navigate(-1)}
                                >
                                    Volver atrás
                                </button>

                            </div>

                        </div>

                    </div>

                </div>

            </div>

        </div>

    );

};