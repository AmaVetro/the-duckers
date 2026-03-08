//FrontEndDuckers/src/utils/currency.js:

export const clp = (n) =>
    new Intl.NumberFormat("es-CL", {
        style: "currency",
        currency: "CLP",
        maximumFractionDigits: 0,
    }).format(n);