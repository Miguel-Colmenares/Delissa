import React, { useState, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";
import SaleModal from "../pages/SaleModal";
import Stock from "../pages/Stock";
import {
  Plus,
  Minus,
  ShoppingCart,
  Trash2,
  Utensils,
  LayoutDashboard,
  Package,
  BarChart3,
  FileText,
  Search,
  LogOut,
  User,
} from "lucide-react";
import { useNavigate } from "react-router-dom";
import logo from "../assets/delissa_Logo.png";



export default function Dashboard() {
  const [cart, setCart] = useState([]);
  const [activeTab, setActiveTab] = useState("ventas");
  const [openMenu, setOpenMenu] = useState(false);
  const [products, setProducts] = useState([]);
  const [openSections, setOpenSections] = useState({
  entrada: true,
  hamburguesa: true,
  perro: true,
  carne: true,
  chorizo: true,
  bebida: true
  });
  const [openSaleModal, setOpenSaleModal] = useState(false);
  const [openCart, setOpenCart] = useState(false);
  

  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem("user")) || {};

  useEffect(() => {
    fetch("http://localhost:8080/products")
      .then(res => res.json())
      .then(data => setProducts(data))
      .catch(err => console.error(err));
  }, []);

  const toggleSection = (section) => {
    setOpenSections(prev => ({
      ...prev,
      [section]: !prev[section]
    }));
  };

  const addToCart = (product) => {
    setCart((prev) => {
      const exists = prev.find((i) => i.id === product.id);
      if (exists)
        return prev.map((i) =>
          i.id === product.id ? { ...i, qty: i.qty + 1 } : i
        );
      return [...prev, { ...product, qty: 1 }];
    });
  };

  const updateQty = (id, delta) => {
    setCart((prev) =>
      prev.map((i) =>
        i.id === id ? { ...i, qty: Math.max(1, i.qty + delta) } : i
      )
    );
  };

  const removeFromCart = (id) =>
    setCart((prev) => prev.filter((i) => i.id !== id));

  const subtotal = cart.reduce((acc, i) => acc + i.price * i.qty, 0);

  const handleLogout = () => {
    localStorage.removeItem("user");
    navigate("/");
  };

  const goProfile = () => {
    alert("Perfil próximamente 🔥");
  };

  const formatCOP = (value) => {
    return new Intl.NumberFormat("es-CO", {
      style: "currency",
      currency: "COP",
      minimumFractionDigits: 0,
    }).format(value);
  };

  const handleConfirmSale = async (modalData) => {
  try {
    const salePayload = {
      paymentMethod: modalData.paymentMethod,
      invoiceType: modalData.invoiceType,
        usuario: { id: user.id }, // 🔥 CLAVE

      details: cart.map(item => ({
        product: { id: item.id },
        quantity: item.qty
      })),

      clientInvoice: modalData.clientInvoice
    };

    console.log("ENVIANDO:", salePayload);

    const res = await fetch("http://localhost:8080/sales", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(salePayload)
    });

    const data = await res.json();

    console.log("RESPUESTA:", data);

    // 🔥 limpiar carrito
    setCart([]);

    // 🔥 cerrar modal
    setOpenSaleModal(false);

    alert("✅ Venta realizada con éxito");

  } catch (error) {
    console.error(error);
    alert("❌ Error al procesar la venta");
  }
};

  return (

  <div className="w-screen h-screen flex overflow-hidden bg-white">

    {/* SIDEBAR */}
    <nav className="group w-20 hover:w-64 transition-all duration-300 bg-[#FF9F1C] flex flex-col items-center py-8 text-white overflow-hidden">

      <div className="flex flex-col gap-4 w-full px-3">
        <SidebarItem icon={<LayoutDashboard />} label="Dash" active={activeTab==="dash"} onClick={()=>setActiveTab("dash")} />
        <SidebarItem icon={<ShoppingCart />} label="Ventas" active={activeTab==="ventas"} onClick={()=>setActiveTab("ventas")} />
        <SidebarItem icon={<Package />} label="Stock" active={activeTab==="stock"} onClick={()=>setActiveTab("stock")} />
        <SidebarItem icon={<BarChart3 />} label="Stats" active={activeTab==="stats"} onClick={()=>setActiveTab("stats")} />
        <SidebarItem icon={<FileText />} label="Facturas" active={activeTab==="facturas"} onClick={()=>setActiveTab("facturas")} />
        <div className="mt-auto w-full px-3 pt-6">

  <div className="mt-auto w-full px-3">

  <button
    className="flex items-center gap-3 p-3 rounded-xl w-full text-white hover:bg-white/20 transition-all duration-200"
  >

    {/* ICONO */}
    <User size={20} className="text-white" />

    {/* TEXTO */}
    <span className="hidden group-hover:block whitespace-nowrap">
      {user?.nombre || "Usuario"}
    </span>

  </button>

</div>

</div>

      </div>
    </nav>
    



    {/* PRODUCTOS */}
    <section
  className={`flex-1 bg-gradient-to-br from-gray-50 to-gray-100 p-6 overflow-y-auto transition-all duration-300
  ${openCart ? "mr-[350px]" : "mr-0"}`}
  >

  {/* 🔥 VENTAS */}
  {/* 🔥 VENTAS */}
  {activeTab === "ventas" && (
    <>
      <header className="flex justify-between items-center mb-8">
        <img src={logo} alt="logo" className="w-18 h-15 object-contain" />

        <h1 className="text-4xl font-extrabold tracking-tight">
          <span className="text-[#FF9F1C]">MENÚ</span>{" "}
          <span className="text-[#FF4040]">DEL LOCAL</span>
        </h1>

        <div className="relative w-72">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" size={18} />

          <input
            placeholder="Buscar productos..."
            className="w-full pl-11 pr-4 py-3 rounded-2xl bg-white border border-gray-200 shadow-md"
          />
        </div>
      </header>

      {/* 🥟 ENTRADAS */}
      <h2 className="text-xl font-bold mb-4 text-gray-700">Entradas</h2>

      <Dropdown
        title="Ver Entradas"
        open={openSections["entrada"]}
        onClick={() => toggleSection("entrada")}
        addToCart={addToCart}
        formatCOP={formatCOP}
      >
        {products.filter(p => p.category === "entrada")}
      </Dropdown>

      {/* 🍔 COMIDA */}
      <h2 className="text-xl font-bold mb-4 text-gray-700">Comida</h2>

      <div className="space-y-4 mb-8">
        <Dropdown
          title="Hamburguesas"
          open={openSections["hamburguesa"]}
          onClick={() => toggleSection("hamburguesa")}
          addToCart={addToCart}
          formatCOP={formatCOP}
        >
          {products.filter(p =>
            p.category === "comida" &&
            (p.sub_category || p.subCategory) === "hamburguesa"
          )}
        </Dropdown>

        <Dropdown
          title="Perros"
          open={openSections["perro"]}
          onClick={() => toggleSection("perro")}
          addToCart={addToCart}
          formatCOP={formatCOP}
        >
          {products.filter(p =>
            p.category === "comida" &&
            (p.sub_category || p.subCategory) === "perro"
          )}
        </Dropdown>

        <Dropdown
          title="Carnes"
          open={openSections["carne"]}
          onClick={() => toggleSection("carne")}
          addToCart={addToCart}
          formatCOP={formatCOP}
        >
          {products.filter(p =>
            p.category === "comida" &&
            (p.sub_category || p.subCategory) === "carne"
          )}
        </Dropdown>

        <Dropdown
          title="Chorizos"
          open={openSections["chorizo"]}
          onClick={() => toggleSection("chorizo")}
          addToCart={addToCart}
          formatCOP={formatCOP}
        >
          {products.filter(p =>
            p.category === "comida" &&
            (p.sub_category || p.subCategory) === "chorizo"
          )}
        </Dropdown>
      </div>

      {/* 🥤 BEBIDAS */}
      <h2 className="text-xl font-bold mb-4 text-gray-700">Bebidas</h2>

      <Dropdown
        title="Ver Bebidas"
        open={openSections["bebida"]}
        onClick={() => toggleSection("bebida")}
        addToCart={addToCart}
        formatCOP={formatCOP}
      >
        {products.filter(p => p.category === "bebida")}
      </Dropdown>
    </>
  )}

  {/* 🔥 STOCK */}
  {activeTab === "stock" && <Stock />}

</section>


    {/* 🛒 CARRITO DESLIZABLE */}
<aside
  className={`fixed top-0 right-0 h-full w-[350px] bg-white border-l flex flex-col z-[9998] transition-all duration-300
  ${openCart ? "translate-x-0" : "translate-x-full"}`}
>

  {/* 🔴 HEADER */}
  <div className="p-6 bg-[#FF4040] text-white relative">

    <h2 className="flex items-center gap-2 font-extrabold text-2xl">
      <ShoppingCart size={20} />
      Orden
    </h2>

    {/* ❌ BOTÓN CERRAR */}
    <button
      onClick={() => setOpenCart(false)}
      className="absolute top-4 right-4 text-white text-xl hover:scale-110 transition"
    >
      ✖
    </button>

    {/* 🔢 CONTADOR */}
    {cart.length > 0 && (
      <span className="absolute top-4 right-12 bg-white text-[#FF4040] text-xs font-bold px-2 py-0.5 rounded-full shadow">
        {cart.length}
      </span>
    )}
  </div>

  {/* 🧾 LISTA */}
  <div className="flex-1 overflow-y-auto p-4 space-y-3">
    <AnimatePresence>
      {cart.map((item) => (
        <motion.div
          key={item.id}
          layout
          initial={{ opacity: 0, x: 50 }}
          animate={{ opacity: 1, x: 0 }}
          exit={{ opacity: 0, x: 50 }}
          transition={{ type: "spring", stiffness: 300 }}
          className="flex justify-between items-center bg-gray-100 p-3 rounded-xl"
        >
          <div>
            <p className="font-bold text-sm">{item.name}</p>
            <p className="text-xs">
              {formatCOP(item.price * item.qty)}
            </p>
          </div>

          <div className="flex items-center gap-2">
            <button onClick={() => updateQty(item.id, -1)}>
              <Minus size={14} />
            </button>

            <span>{item.qty}</span>

            <button onClick={() => updateQty(item.id, 1)}>
              <Plus size={14} />
            </button>

            <button onClick={() => removeFromCart(item.id)}>
              <Trash2 size={14} />
            </button>
          </div>
        </motion.div>
      ))}
    </AnimatePresence>
  </div>

  {/* 💰 FOOTER */}
  <div className="p-4 border-t">

    <h3 className="font-bold mb-2">
      Total: {formatCOP(subtotal)}
    </h3>

    <button
      onClick={() => setOpenSaleModal(true)}
      className="w-full bg-gradient-to-r from-[#FF9F1C] to-[#FF4040] text-white py-3 rounded-xl shadow-md hover:opacity-90"
    >
      Finalizar Venta
    </button>

    <SaleModal
      open={openSaleModal}
      onClose={() => setOpenSaleModal(false)}
      onConfirm={handleConfirmSale}
    />

  </div>

</aside>

  {/* Carrito boton*/}
{!openCart && (
  <div className="fixed bottom-6 right-6 z-[9999]">

    <button
      onClick={() => setOpenCart(true)}
      className="relative w-14 h-14 bg-[#FF4040] text-white rounded-full shadow-xl flex items-center justify-center hover:scale-105 transition"
    >
      <ShoppingCart size={22} />

      {cart.length > 0 && (
        <span className="absolute -top-2 -right-2 bg-white text-[#FF4040] text-xs font-bold px-2 py-0.5 rounded-full shadow">
          {cart.length}
        </span>
      )}
    </button>

  </div>
)}
{/* 🔥 BOTÓN FLOTANTE */}
    <div className="fixed bottom-6 left-6 z-[9999] flex items-center gap-3">

  {/* ⚙️ BOTÓN */}
  <div>
    <AnimatePresence>
      {openMenu && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: 20 }}
          className="absolute bottom-16 left-0 flex flex-col gap-3 items-start"
          
        >
          <button
            onClick={goProfile}
            className="flex items-center gap-2 bg-white shadow-lg px-4 py-2 rounded-xl hover:bg-gray-100"
          >
            <User size={18} /> Perfil
          </button>

          <button
            onClick={handleLogout}
            className="flex items-center gap-2 bg-red-500 text-white shadow-lg px-4 py-2 rounded-xl hover:bg-red-600"
          >
            <LogOut size={18} /> Cerrar sesión
          </button>
        </motion.div>
      )}
    </AnimatePresence>

    <motion.button
      whileTap={{ scale: 0.9 }}
      onClick={() => setOpenMenu(!openMenu)}
      className="w-14 h-14 bg-[#f57c00] hover:bg-[#d35400] text-white rounded-full shadow-xl flex items-center justify-center text-xl transition"
    >
      ⚙️
    </motion.button>
  </div>




   </div> 
        
  </div> 
        
  
  
);




function Dropdown({ title, open, onClick, children, addToCart, formatCOP }) {
  return (
    <div>
      <button
        onClick={onClick}
        className={`relative overflow-hidden w-full text-left font-semibold p-3 rounded-xl flex justify-between items-center transition-all duration-300
        ${
          open
            ? "bg-[#FF9F1C] text-gray-100 shadow-md"
            : "bg-white text-gray-500 shadow hover:bg-gray-100"
        }`}
      >

        {/* 🔥 EFECTO CÍRCULOS (SOLO SI ESTÁ ABIERTO) */}
        {open && (
  <>
    <motion.span
      className="absolute w-40 h-40 bg-white/25 rounded-full"
      animate={{ x: [-100, 120, -100], y: [0, 10, 0] }}
      transition={{ repeat: Infinity, duration: 6 }}
    />

    <motion.span
      className="absolute w-32 h-32 bg-white/20 rounded-full"
      animate={{ x: [120, -100, 120], y: [0, -10, 0] }}
      transition={{ repeat: Infinity, duration: 7 }}
    />

    <motion.span
      className="absolute w-52 h-52 bg-white/15 rounded-full"
      animate={{ x: [-150, 150, -150], y: [0, 5, 0] }}
      transition={{ repeat: Infinity, duration: 8 }}
    />
  </>
)}

        {/* CONTENIDO */}
        <span className="relative z-10">{title}</span>

      </button>

      {open && (
        <div className="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-5 mt-3">
          {children.map((p) => (
            <ProductCard
              key={p.id}
              p={p}
              addToCart={addToCart}
              formatCOP={formatCOP}
            />
          ))}
        </div>
      )}
    </div>
  );
}

}



function SidebarItem({ icon, label, active, onClick }) {
  return (
    <button
      onClick={onClick}
      className={`flex items-center gap-3 p-3 rounded-xl w-full transition-all duration-300
      ${active 
        ? "bg-white text-[#FF9F1C]" 
        : "text-white/80 hover:bg-white/20"}
      `}
    >
      {/* ICONO (SIEMPRE VISIBLE) */}
      <div className="flex justify-center w-full group-hover:w-auto transition-all duration-300">
        {icon}
      </div>

      {/* TEXTO (SOLO EN HOVER) */}
      <span className="opacity-0 translate-x-[-10px] group-hover:opacity-100 group-hover:translate-x-0 transition-all duration-300 whitespace-nowrap">
        {label}
      </span>
    </button>
  );
}
function ProductCard({ p, addToCart, formatCOP }) {

  const getImage = () => {
    if (!p.img) return "https://placehold.co/300x200?text=Sin+imagen";

    if (p.img.startsWith("http")) {
      return p.img; // externa
    }

    return `http://localhost:8080${p.img}`; // backend
  };

  return (
    <motion.div
      whileHover={{ y: -5 }}
      className="bg-white p-3 rounded-2xl shadow-md hover:shadow-2xl transition-all"
    >
      <img
        src={getImage()}
        onError={(e) => {
          e.target.src = "https://placehold.co/300x200?text=Error";
        }}
        className="h-28 w-full object-cover rounded-xl mb-2"
      />

      <h3 className="text-sm font-bold text-center">{p.name}</h3>

      <p className="text-center text-[#FF4040] font-bold mb-2">
        {formatCOP(p.price)}
      </p>

      <div className="flex justify-center">
        <button
          onClick={() => addToCart(p)}
          className="bg-gradient-to-r from-[#FF9F1C] to-[#FF4040] text-white px-3 py-1 rounded-lg"
        >
          +
        </button>
      </div>
    </motion.div>
  );
}