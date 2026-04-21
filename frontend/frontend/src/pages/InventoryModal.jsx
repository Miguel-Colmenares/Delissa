import React, { useEffect, useState } from "react";

export default function InventoryModal({ date, onClose }) {

  const [products, setProducts] = useState([]);
  const [movements, setMovements] = useState([]);
  const [summary, setSummary] = useState([]);
  

  useEffect(() => {
    loadAll();
  }, [date]);

  const loadAll = async () => {

    const resProducts = await fetch("http://localhost:8080/products");
    const productsData = await resProducts.json();

    const start = new Date(date);
    start.setHours(0,0,0,0);

    const end = new Date(date);
    end.setHours(23,59,59,999);

    const resMov = await fetch(
      `http://localhost:8080/products/inventory?start=${start.toISOString()}&end=${end.toISOString()}`
    );

    const movData = await resMov.json();
    const list = Array.isArray(movData) ? movData : movData.content || [];

    setProducts(productsData);
    setMovements(list);

    const grouped = {};

    list.forEach(item => {
      const id = item.product?.id;
      if (!grouped[id]) grouped[id] = 0;
      grouped[id] += item.quantity;
    });

    const result = productsData.map(p => ({
      name: p.name,
      change: grouped[p.id] || 0,
      finalStock: p.stock
    }));

    setSummary(result);
  };

  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-sm z-[9999] flex justify-center items-start overflow-y-auto p-6">

      {/* 🔥 MODAL */}
      <div className="bg-white w-full max-w-[1400px] max-h-[95vh] p-6 rounded-2xl shadow-2xl flex flex-col">

        {/* HEADER */}
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-2xl font-bold text-[#FF9F1C]">
            📊 Daily Inventory Report
          </h2>

          <button
            onClick={onClose}
            className="text-gray-500 hover:text-red-500 text-2xl"
          >
            ✖
          </button>
        </div>

        {/* FECHA */}
        <p className="text-sm text-gray-400 mb-4">
          {new Date(date).toLocaleDateString()}
        </p>

        {/* 🔥 CONTENIDO SCROLLEABLE */}
        <div className="flex-1 flex flex-col gap-4 overflow-hidden min-h-0">

          {/* 🔥 RESUMEN */}
          <div className="flex flex-col">

            <h3 className="font-bold mb-2 text-gray-700">
              Resumen del día
            </h3>

            <div className="max-h-[250px] overflow-y-auto pr-2">

              <table className="w-full text-sm border rounded-xl">

                <thead className="bg-[#FF9F1C]/10 sticky top-0 z-10">
                  <tr>
                    <th className="p-3 border text-left">Producto</th>
                    <th className="p-3 border text-center">Cambio</th>
                    <th className="p-3 border text-center">Stock</th>
                  </tr>
                </thead>

                <tbody>
                  {summary.map((item, i) => (
                    <tr key={i} className="border-t hover:bg-gray-50">

                      <td className="p-3 border font-semibold">{item.name}</td>

                      <td className="p-3 border text-center font-bold">
                        <span className={`px-2 py-1 rounded-lg ${
                          item.change > 0
                            ? "bg-green-100 text-green-600"
                            : item.change < 0
                            ? "bg-red-100 text-red-500"
                            : "bg-gray-100 text-gray-400"
                        }`}>
                          {item.change > 0 ? "+" : ""}
                          {item.change}
                        </span>
                      </td>

                      <td className="p-3 border text-center font-bold">
                        {item.finalStock}
                      </td>

                    </tr>
                  ))}
                </tbody>

              </table>
            </div>
          </div>

          {/* 🔥 HISTORIAL */}
          <div className="flex flex-col flex-1 min-h-0">

            <h3 className="font-bold mb-2 text-gray-700">
              Movimientos del día
            </h3>

            <div className="flex-1 overflow-y-auto pr-2 min-h-0">

              <table className="w-full text-sm border rounded-xl">

                <thead className="bg-[#FF9F1C]/10 sticky top-0 z-10">
                  <tr>
                    <th className="p-3 border">Producto</th>
                    <th className="p-3 border">Cambio</th>
                    <th className="p-3 border">Hora</th>
                    <th className="p-3 border">Usuario</th>
                  </tr>
                </thead>

                <tbody>
                  {movements.map(item => (
                    <tr key={item.id} className="border-t hover:bg-gray-50">

                      <td className="p-3 border font-semibold">
                        {item.product?.name}
                      </td>

                      <td className="p-3 border text-center font-bold">
                        <span className={`px-2 py-1 ${
                          item.quantity > 0
                            ? "text-green-600"
                            : "text-red-500"
                        }`}>
                          {item.quantity > 0 ? "+" : ""}
                          {item.quantity}
                        </span>
                      </td>

                      <td className="p-3 border text-center text-gray-500">
                        {new Date(item.lastUpdate).toLocaleTimeString()}
                      </td>

                      <td className="p-3 border text-center">
                        <span className="bg-[#FF9F1C]/10 text-[#FF9F1C] px-3 py-1 rounded-full text-xs font-semibold">
                          👤 {item.user?.nombre || "Admin"}
                        </span>
                      </td>

                    </tr>
                  ))}
                </tbody>

              </table>
            </div>

          </div>

        </div>

      </div>
    </div>
  );
}