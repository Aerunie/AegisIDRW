<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List, aegisid.modelo.Venta, aegisid.modelo.DetalleVenta, aegisid.modelo.Producto, aegisid.modelo.Cliente"%>
<%
    String usuarioSesion = (String) session.getAttribute("usuario");
    String mensaje = request.getAttribute("mensaje") != null ? (String) request.getAttribute("mensaje") : "";
    String tipoMensaje = request.getAttribute("tipoMensaje") != null ? (String) request.getAttribute("tipoMensaje") : "";

    List<Venta> ventas = (List<Venta>) request.getAttribute("ventas");
    List<Producto> productos = (List<Producto>) request.getAttribute("productos");
    List<Cliente> clientes = (List<Cliente>) request.getAttribute("clientes");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aegis ID - Módulo de Ventas</title>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet"/>
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet"/>
    <style>html,body{margin:0;padding:0;}body{overscroll-behavior:none;}::-webkit-scrollbar{display:none;}</style>
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
    tailwind.config={theme:{extend:{colors:{"on-surface":"#131b2e","primary":"#070235","secondary":"#4e45d5","surface-container":"#eaedff","surface":"#faf8ff","surface-container-low":"#f2f3ff","error":"#ba1a1a"}}}}
    function actualizarPrecio() {
        const select = document.getElementById('selectProducto');
        const precio = select.options[select.selectedIndex].getAttribute('data-precio');
        document.getElementById('inputPrecio').value = precio || '';
    }
    </script>
</head>
<body class="bg-surface text-on-surface min-h-screen font-sans" style="font-family:'Plus Jakarta Sans',sans-serif;">
    <header class="bg-primary text-white shadow-md">
        <div class="max-w-7xl mx-auto px-6 h-20 flex items-center justify-between">
            <div class="flex items-center gap-3">
                <a href="bienvenido" class="w-10 h-10 rounded-xl bg-white/10 flex items-center justify-center hover:bg-white/20 transition-all"><span class="material-symbols-outlined text-white text-[24px]">arrow_back</span></a>
                <div><h1 class="text-lg font-bold tracking-tight">Módulo 7: Ventas y Facturación</h1><p class="text-xs text-indigo-200">Integración con Clientes (idCliente) y Detalle</p></div>
            </div>
            <a href="logout" class="px-4 py-2 rounded-xl bg-red-600/20 hover:bg-red-600 text-red-200 hover:text-white text-xs font-semibold transition-all border border-red-500/30 flex items-center gap-2">
                <span class="material-symbols-outlined text-[18px]">logout</span><span>Salir</span>
            </a>
        </div>
    </header>
    <main class="max-w-7xl mx-auto px-6 py-10">
        <% if (!mensaje.isEmpty()) { %>
        <div class="mb-6 p-4 rounded-xl text-sm font-semibold text-center <%= "error".equals(tipoMensaje) ? "bg-red-100 text-red-800 border border-red-300" : "bg-emerald-50 text-emerald-800 border border-emerald-200" %>"><%= mensaje %></div>
        <% } %>
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <div class="bg-white p-6 rounded-2xl shadow-sm border border-slate-100 h-fit">
                <h2 class="text-xl font-bold text-slate-900 mb-1">Registrar Venta (idCliente)</h2>
                <p class="text-xs text-slate-500 mb-6">Selecciona cliente, producto y emite comprobante.</p>
                <form method="post" action="ventas" class="space-y-4">
                    <input type="hidden" name="accion" value="registrar">
                    <div>
                        <label class="text-xs text-slate-700 font-semibold block mb-1.5">Cliente (idCliente)</label>
                        <select name="id_cliente" required class="w-full h-[46px] px-4 rounded-xl border border-slate-200 text-sm outline-none focus:ring-2 focus:ring-secondary/40 bg-white">
                            <option value="">Seleccione un cliente</option>
                            <% if (clientes != null) { for (Cliente c : clientes) { %>
                            <option value="<%= c.getId() %>"><%= c.getNombre() %> <%= c.getApellido() %> (DNI/RUC: <%= c.getDniRuc() %>)</option>
                            <% }} %>
                        </select>
                    </div>
                    <div>
                        <label class="text-xs text-slate-700 font-semibold block mb-1.5">Tipo de Comprobante</label>
                        <select name="tipo_comprobante" class="w-full h-[46px] px-4 rounded-xl border border-slate-200 text-sm outline-none focus:ring-2 focus:ring-secondary/40 bg-white">
                            <option value="Boleta de Venta">Boleta de Venta</option>
                            <option value="Factura Electrónica">Factura Electrónica</option>
                        </select>
                    </div>
                    <div>
                        <label class="text-xs text-slate-700 font-semibold block mb-1.5">Producto del Inventario</label>
                        <select name="id_producto" id="selectProducto" required onchange="actualizarPrecio()" class="w-full h-[46px] px-4 rounded-xl border border-slate-200 text-sm outline-none focus:ring-2 focus:ring-secondary/40 bg-white">
                            <option value="">Seleccione un producto</option>
                            <% if (productos != null) { for (Producto p : productos) { %>
                            <option value="<%= p.getId() %>" data-precio="<%= p.getPrecio() %>"><%= p.getNombre() %> (Stock: <%= p.getStock() %>)</option>
                            <% }} %>
                        </select>
                    </div>
                    <div class="grid grid-cols-2 gap-3">
                        <div><label class="text-xs text-slate-700 font-semibold block mb-1.5">Cantidad</label><input type="number" name="cantidad" required min="1" value="1" class="w-full h-[46px] px-4 rounded-xl border border-slate-200 text-sm outline-none focus:ring-2 focus:ring-secondary/40"/></div>
                        <div><label class="text-xs text-slate-700 font-semibold block mb-1.5">Precio Unitario ($)</label><input type="number" step="0.01" name="precio_unitario" id="inputPrecio" required placeholder="0.00" class="w-full h-[46px] px-4 rounded-xl border border-slate-200 text-sm outline-none focus:ring-2 focus:ring-secondary/40"/></div>
                    </div>
                    <button type="submit" class="w-full h-[48px] rounded-xl bg-secondary hover:bg-indigo-700 text-white font-semibold text-sm shadow-md transition-all flex items-center justify-center gap-2">
                        <span class="material-symbols-outlined text-[20px]">shopping_cart_checkout</span><span>Procesar Venta y Detalle</span>
                    </button>
                </form>
            </div>
            <div class="lg:col-span-2 bg-white p-6 rounded-2xl shadow-sm border border-slate-100">
                <h2 class="text-xl font-bold text-slate-900 mb-1">Historial de Ventas y Desglose</h2>
                <p class="text-xs text-slate-500 mb-6">Comprobantes emitidos con vinculación a cliente y detalle.</p>
                <div class="space-y-4">
                    <%
                        if (ventas != null && !ventas.isEmpty()) {
                            for (Venta v : ventas) {
                    %>
                    <div class="border border-slate-100 rounded-xl p-4 bg-slate-50/50">
                        <div class="flex justify-between items-center mb-2">
                            <div>
                                <span class="text-xs font-bold text-slate-500">#<%= v.getId() %></span>
                                <span class="font-bold text-slate-900 ml-2"><%= v.getNombreCliente() != null ? v.getNombreCliente() : "Cliente ID: " + v.getIdCliente() %></span>
                                <span class="ml-3 px-2.5 py-0.5 rounded bg-indigo-50 text-indigo-700 text-xs font-semibold"><%= v.getTipoComprobante() %></span>
                            </div>
                            <div class="flex items-center gap-3">
                                <span class="font-bold text-slate-800">$<%= String.format("%.2f", v.getTotal()) %></span>
                                <span class="px-2.5 py-1 rounded-full <%= "Anulada".equals(v.getEstado()) ? "bg-red-50 text-red-600" : "bg-emerald-50 text-emerald-700" %> text-xs font-semibold"><%= v.getEstado() %></span>
                                <% if (!"Anulada".equals(v.getEstado())) { %>
                                <a href="ventas?accion=anular&id=<%= v.getId() %>" onclick="return confirm('¿Anular esta venta?');" class="text-red-500 hover:text-red-700 text-xs font-semibold">Anular</a>
                                <% } %>
                            </div>
                        </div>
                        <div class="bg-white rounded-lg border border-slate-100 p-2 overflow-x-auto">
                            <table class="w-full text-left text-xs">
                                <thead>
                                    <tr class="text-slate-400 border-b border-slate-100">
                                        <th class="py-1 px-2">Producto</th>
                                        <th class="py-1 px-2">Cant</th>
                                        <th class="py-1 px-2">Precio U.</th>
                                        <th class="py-1 px-2">Subtotal</th>
                                    </tr>
                                </thead>
                                <tbody class="divide-y divide-slate-50">
                                    <% if (v.getDetalles() != null) { for (DetalleVenta d : v.getDetalles()) { %>
                                    <tr>
                                        <td class="py-1 px-2 font-medium text-slate-800"><%= d.getNombreProducto() %></td>
                                        <td class="py-1 px-2 text-slate-600"><%= d.getCantidad() %></td>
                                        <td class="py-1 px-2 text-slate-600">$<%= String.format("%.2f", d.getPrecioUnitario()) %></td>
                                        <td class="py-1 px-2 font-semibold text-slate-900">$<%= String.format("%.2f", d.getSubtotal()) %></td>
                                    </tr>
                                    <% }} %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <% 
                            }
                        } else { 
                    %>
                    <p class="text-xs text-slate-400 text-center py-6">No hay ventas registradas.</p>
                    <% } %>
                </div>
            </div>
        </div>
    </main>
</body>
</html>