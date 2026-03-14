//frontend/src/components/OrderConfirmationView.jsx:

import { useParams, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { getOrderById } from "../services/orderService";
import { clp } from "../utils/currency";

export const OrderConfirmationView = () => {

    const { orderId } = useParams();
    const navigate = useNavigate();

    const [order, setOrder] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {

        const loadOrder = async () => {

            try {

                const data = await getOrderById(orderId);

                setOrder(data);

            } catch (error) {

                console.error("Error loading order", error);

            } finally {

                setLoading(false);

            }

        };

        loadOrder();

    }, [orderId]);


    if (loading) {
        return (
            <div className="container my-4">
                <div className="alert alert-info">
                    Cargando información de la orden...
                </div>
            </div>
        );
    }


    if (!order) {
        return (
            <div className="container my-4">
                <div className="alert alert-danger">
                    No se pudo cargar la orden.
                </div>
            </div>
        );
    }


    const date = new Date(order.createdAt).toLocaleString("es-CL");

    const subtotal = order.subtotal ?? 0;
    const duocDiscount = order.duocDiscount ?? 0;
    const pointsDiscount = order.pointsDiscount ?? 0;
    const iva = order.iva ?? 0;
    const total = order.total ?? 0;


    return (

        <div className="container my-4">

            <h3 className="mb-3">
                Compra realizada con éxito
            </h3>

            <div className="card">

                <div className="card-body">

                    <p>
                        <strong>Número de orden:</strong> {order.orderId}
                    </p>

                    <p>
                        <strong>Fecha:</strong> {date}
                    </p>

                    <p>
                        <strong>Estado:</strong> {order.status}
                    </p>

                    <hr />

                    <h5 className="mb-3">
                        Productos
                    </h5>

                    <table className="table table-sm">

                        <thead>
                            <tr>
                                <th>Producto</th>
                                <th className="text-center">Cantidad</th>
                                <th className="text-end">Precio</th>
                                <th className="text-end">Subtotal</th>
                            </tr>
                        </thead>

                        <tbody>

                            {order.items?.map((item, index) => (

                                <tr key={index}>

                                    <td>{item.productName}</td>

                                    <td className="text-center">
                                        {item.quantity}
                                    </td>

                                    <td className="text-end">
                                        {clp(item.unitPrice)}
                                    </td>

                                    <td className="text-end">
                                        {clp(item.subtotal)}
                                    </td>

                                </tr>

                            ))}

                        </tbody>

                        <tfoot>

                            <tr>

                                <td colSpan={3} className="text-end fw-bold">
                                    Total
                                </td>

                                <td className="text-end fw-bold">
                                    {clp(total)}
                                </td>

                            </tr>

                        </tfoot>

                    </table>


                    <div className="mt-4">

                        <h6 className="fw-bold mb-2">
                            Resumen del pago
                        </h6>

                        <div className="d-flex justify-content-between">
                            <span>Subtotal</span>
                            <span>{clp(subtotal)}</span>
                        </div>

                        <div className="d-flex justify-content-between">
                            <span>Descuento DUOC UC</span>
                            <span className="text-success">- {clp(duocDiscount)}</span>
                        </div>

                        <div className="d-flex justify-content-between">
                            <span>Descuento por puntos</span>
                            <span className="text-success">- {clp(pointsDiscount)}</span>
                        </div>

                        <div className="d-flex justify-content-between">
                            <span>IVA (19%)</span>
                            <span>{clp(iva)}</span>
                        </div>

                        <hr />

                        <div className="d-flex justify-content-between fw-bold">
                            <span>Total final</span>
                            <span>{clp(total)}</span>
                        </div>

                    </div>

                    <div className="mt-3">

                        <button
                            className="btn btn-primary"
                            onClick={() => navigate("/account")}
                        >
                            Ir a mis órdenes
                        </button>

                    </div>

                </div>

            </div>

        </div>

    );

};