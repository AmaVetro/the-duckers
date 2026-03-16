//frontend/src/components/ProductCardView.jsx:


import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { clp } from "../utils/currency";

export const ProductCardView = ({ handler, id, image, name, description, price, stock }) => {

    const navigate = useNavigate(); 

    const maxLen = 90;
    const [showFull, setShowFull] = useState(false);
    const previewDescription = (() => {
        if (!showFull) { 
            return description.length > maxLen 
                ? description.slice(0, maxLen) + "..." 
                : description;                           
        }

        return description;
    })();

    const hasMore = description.length > maxLen;


    // ===============================
    // Stock display logic
    // ===============================
    let stockLabel;

    if (stock === 0) {
        stockLabel = "No disponible";
    } else if (stock > 30) {
        stockLabel = "+30";
    } else {
        stockLabel = stock;
    }


    const onAddProduct = (product) => {
        console.log(product);
        handler(product);
        alert("Producto agregado exitosamente al carrito.");
    };

    const onViewDetails = () => {
        navigate(`/product/${id}`); 
    };



    return (
        <div className="card h-100">
            <div className="card-body d-flex flex-column">
                <div className="product-image-container">
                    <img src={image} alt={name} />
                </div>
                <h5 className="card-title">{name}</h5>

                <div className={`description-collapse ${showFull ? "expanded" : ""}`}>
                    <p className="card-text mb-1">
                        {previewDescription} 
                        {hasMore && (
                            <button 
                                type="button"
                                className="btn btn-link btn-sm p-0 ms-1 text-info ver-mas-btn"
                                onClick={() => setShowFull(prev => !prev)}
                            >
                                {showFull ? "Ver menos" : "Ver más"} 
                            </button>
                        )}
                    </p>
                </div>


                <p className="card-text fw-bold mb-3">{clp(price)}</p>

                <p className="card-text mb-2">
                    Stock disponible: {stockLabel}
                </p>

                <div className="mt-auto d-flex gap-2">
                    <button
                        className={`btn ${stock === 0 ? "btn-secondary" : "btn-primary"}`}
                        disabled={stock === 0}
                        onClick={() => onAddProduct({ id, name, description, price })}
                    >
                        Añadir
                    </button>

                    <button
                        className="btn btn-outline-secondary"
                        onClick={onViewDetails} 
                    >   
                        Ver detalles
                    </button>
                </div>
            </div>
        </div>
    );
};
