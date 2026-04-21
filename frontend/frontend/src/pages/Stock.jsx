import React, { useEffect, useState } from "react";
import { Minus, Trash2, Search, Plus } from "lucide-react";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import InventoryModal from "./InventoryModal";

export default function Stock() {

  const [products, setProducts] = useState([]);
  const [originalProducts, setOriginalProducts] = useState([]);
  const [dirty, setDirty] = useState(false);

  const [search, setSearch] = useState("");
  const [category, setCategory] = useState("all");
  const [date, setDate] = useState(new Date());
  const [history, setHistory] = useState([]);
  const [historySearch, setHistorySearch] = useState("");
  const [openModal, setOpenModal] = useState(false);
  const [openInventoryModal, setOpenInventoryModal] = useState(false);
  const [newProduct, setNewProduct] = useState({
    name: "",
    price: "",
    category: "",
    subCategory: "",
    stock: 0
  });

  useEffect(() => {
    loadProducts();
  }, []);

  useEffect(() => {
    loadHistory();
  }, [date]);

  const loadProducts = async () => {
    const res = await fetch("http://localhost:8080/products");
    const data = await res.json();
    setProducts(data);
    setOriginalProducts(data);
    setDirty(false);
  };

  const loadHistory = async () => {
    const start = new Date(date);
    start.setHours(0,0,0,0);

    const end = new Date(date);
    end.setHours(23,59,59,999);

    const res = await fetch(
      `http://localhost:8080/products/inventory?start=${start.toISOString()}&end=${end.toISOString()}`
    );

    const data = await res.json();
    setHistory(Array.isArray(data) ? data : []);
  };

  const deleteProduct = async (id) => {
    await fetch(`http://localhost:8080/products/${id}`, {
      method: "DELETE"
    });
    loadProducts();
  };

  const saveInventory = async () => {
    await fetch("http://localhost:8080/products/bulk-update", {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(products)
    });

    alert("Inventario guardado 🔥");
    setDirty(false);
    loadHistory();
  };

  const createProduct = async () => {
  try {
    const res = await fetch("http://localhost:8080/products", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        ...newProduct,
        price: Number(newProduct.price),
        stock: Number(newProduct.stock)
      })
    });

    const created = await res.json();

    // 🔥 actualizar lista local
    setProducts(prev => [...prev, created]);

    // 🔥 actualizar dashboard automáticamente
    localStorage.setItem("reloadProducts", Date.now());

    // 🔥 limpiar formulario
    setNewProduct({
      name: "",
      price: "",
      category: "",
      subCategory: "",
      stock: 0
    });

    setOpenModal(false);

  } catch (err) {
    console.error(err);
    alert("Error creando producto");
  }
};
  const categories = ["all", ...new Set(products.map(p => p.category))];

  const filtered = products.filter(p =>
    (category === "all" || p.category === category) &&
    p.name.toLowerCase().includes(search.toLowerCase())
  );

  const filteredHistory = history.filter(h =>
    h.product?.name.toLowerCase().includes(historySearch.toLowerCase())
  );

  return (
    <div className="flex gap-6">

      {/* IZQUIERDA */}
      <div className="flex-1">

        <div className="flex justify-between items-center mb-4">

          <h1 className="text-2xl font-bold text-[#FF9F1C]">
            Inventario
          </h1>

          <div className="flex gap-2">
            <button
              disabled={!dirty}
              onClick={saveInventory}
              className={`px-4 py-2 rounded-xl shadow text-white ${
                dirty ? "bg-green-600 hover:bg-green-700" : "bg-gray-400 cursor-not-allowed"
              }`}
            >
              💾 Guardar
            </button>

            <button
              onClick={() => setOpenModal(true)}
              className="bg-[#FF9F1C] text-white px-4 py-2 rounded-xl flex items-center gap-2"
            >
              <Plus size={16}/> Crear
            </button>
          </div>
        </div>

        {/* BUSCADOR */}
        <div className="flex gap-4 mb-4">

          <div className="relative flex-1">
            <Search className="absolute left-3 top-3 text-gray-400" size={18}/>
            <input
              placeholder="Buscar producto..."
              value={search}
              onChange={(e)=>setSearch(e.target.value)}
              className="w-full pl-10 pr-4 py-2 rounded-xl bg-white border shadow-sm"
            />
          </div>

          <select
            value={category}
            onChange={(e)=>setCategory(e.target.value)}
            className="px-4 py-2 rounded-xl bg-white border"
          >
            {categories.map((c,i)=>(
              <option key={i} value={c}>
                {c==="all"?"Todas":c}
              </option>
            ))}
          </select>

        </div>

        {/* TABLA */}
        <div className="bg-white rounded-2xl shadow overflow-hidden">

          <table className="w-full text-sm border">

            <thead className="bg-[#FF9F1C]/10">
              <tr>
                <th className="p-3 border">Producto</th>
                <th className="p-3 border">Categoría</th>
                <th className="p-3 border">Sub</th>
                <th className="p-3 border">Precio</th>
                <th className="p-3 border">Stock</th>
                <th className="p-3 border">Acciones</th>
              </tr>
            </thead>

            <tbody>
              {filtered.map(p=>(
                <tr key={p.id} className="hover:bg-gray-50">

                  <td className="p-3 border font-semibold">{p.name}</td>
                  <td className="p-3 border">{p.category}</td>
                  <td className="p-3 border">{p.subCategory}</td>
                  <td className="p-3 border text-[#FF4040] font-bold">${p.price}</td>

                  {/* STOCK EDITABLE */}
                  <td className="p-3 border text-center">
                    <input
                      type="number"
                      value={p.stock}
                      onChange={(e)=>{
                        setDirty(true);
                        setProducts(prev =>
                          prev.map(prod =>
                            prod.id===p.id
                              ? {...prod, stock:Number(e.target.value)}
                              : prod
                          )
                        );
                      }}
                      className={`w-16 text-center border rounded
                        ${p.stock<=5?"border-red-500 text-red-500":""}`}
                    />
                  </td>

                  {/* ACCIONES */}
                  <td className="p-3 border text-center">

                    {/* ➖ */}
                    <button
                      onClick={()=>{
                        setDirty(true);
                        setProducts(prev =>
                          prev.map(prod =>
                            prod.id===p.id
                              ? {...prod, stock: prod.stock - 1}
                              : prod
                          )
                        );
                      }}
                      className="bg-red-500 text-white p-2 rounded"
                    >
                      <Minus size={14}/>
                    </button>

                    {/* ➕ */}
                    <button
                      onClick={()=>{
                        setDirty(true);
                        setProducts(prev =>
                          prev.map(prod =>
                            prod.id===p.id
                              ? {...prod, stock: prod.stock + 1}
                              : prod
                          )
                        );
                      }}
                      className="bg-green-500 text-white p-2 rounded ml-2"
                    >
                      <Plus size={14}/>
                    </button>

                    {/* 🗑 */}
                    <button
                      onClick={()=>deleteProduct(p.id)}
                      className="bg-gray-800 text-white p-2 rounded ml-2"
                    >
                      <Trash2 size={14}/>
                    </button>

                  </td>

                </tr>
              ))}
            </tbody>

          </table>
        </div>
      </div>

      {/* DERECHA */}
      <div className="w-[300px] flex flex-col gap-4">

        <div className="bg-white p-4 rounded-2xl shadow">
          <h2 className="text-center text-[#FF9F1C] font-bold mb-2">
            📅 Calendario
          </h2>
          <Calendar value={date} onChange={setDate}/>
        </div>

        <div className="bg-white p-4 rounded-2xl shadow">

<button
  onClick={() => setOpenInventoryModal(true)}
  className="w-full mt-3 bg-[#FF9F1C] hover:bg-[#e68900] text-white py-2 rounded-xl shadow"
>
  📊 Ver inventario del día
</button>
          <h2 className="font-bold text-[#FF9F1C] mb-2">
            Historial
          </h2>

          <input
            placeholder="Buscar movimiento..."
            value={historySearch}
            onChange={(e)=>setHistorySearch(e.target.value)}
            className="w-full p-2 border rounded-lg mb-2"
          />

          <div className="max-h-[200px] overflow-y-auto text-sm">

            {filteredHistory.map(h=>(
              <div key={h.id} className="border-b py-1">

                <p className="font-semibold">
                  {h.product?.name}
                </p>

                <p className={`${h.quantity>0?"text-green-600":"text-red-500"}`}>
                  {h.quantity>0?"+":""}{h.quantity}
                </p>

                <p className="text-gray-400 text-xs">
                  {new Date(h.lastUpdate).toLocaleTimeString()}
                </p>

              </div>
            ))}

          </div>

        </div>

      </div>

      {/* MODAL */}
      {openModal && (
        <div className="fixed inset-0 bg-black/30 flex justify-center items-center">

          <div className="bg-white p-6 rounded-2xl w-[350px]">

            <h2 className="text-[#FF9F1C] font-bold mb-4">
              Crear Producto
            </h2>

            <div className="flex flex-col gap-2">

              <input placeholder="Nombre"
                onChange={(e)=>setNewProduct({...newProduct,name:e.target.value})}
                className="border p-2 rounded"
              />

              <input placeholder="Precio" type="number"
                onChange={(e)=>setNewProduct({...newProduct,price:e.target.value})}
                className="border p-2 rounded"
              />

              <input placeholder="Categoría"
                onChange={(e)=>setNewProduct({...newProduct,category:e.target.value})}
                className="border p-2 rounded"
              />

              <input placeholder="Subcategoría"
                onChange={(e)=>setNewProduct({...newProduct,subCategory:e.target.value})}
                className="border p-2 rounded"
              />

              <input placeholder="Stock" type="number"
                onChange={(e)=>setNewProduct({...newProduct,stock:e.target.value})}
                className="border p-2 rounded"
              />

              <input
  type="text"
  placeholder="URL de la imagen"
  value={newProduct.img || ""}
  onChange={(e) =>
    setNewProduct({
      ...newProduct,
      img: e.target.value
    })
  }
  className="p-3 border rounded-xl"
/>

            </div>

            <div className="flex justify-end gap-2 mt-4">
              <button onClick={()=>setOpenModal(false)} className="bg-gray-300 px-3 py-1 rounded">
                Cancelar
              </button>

              <button onClick={createProduct} className="bg-[#FF9F1C] text-white px-3 py-1 rounded">
                Crear
              </button>
            </div>

          </div>

        </div>
      )}

    {openInventoryModal && (
  <InventoryModal
    date={date}
    onClose={() => setOpenInventoryModal(false)}
  />
)}
    </div>
  );
}