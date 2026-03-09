//frontend/src/components/ProductCardView.jsx:


import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { clp } from "../utils/currency";

export const ProductCardView = ({ handler, id, image, name, description, price }) => {

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
            <img className="card-img-top mb-2" src={image} alt={name} />
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

            <div className="mt-auto d-flex gap-2">
            <button
                className="btn btn-primary"
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
