import { useState, useEffect, useRef } from 'react';
import { api } from '../api';
import Layout from '../components/Layout';

async function fetchParkingData() {
    const [v, s] = await Promise.all([
        api('GET', '/api/parking'),
        api('GET', '/api/parking/settings')
    ]);
    return { vehicles: v || [], settings: s || { costPerMinute: 0 } };
}

export default function DashboardPage() {
    const [vehicles, setVehicles] = useState([]);
    const [settings, setSettings] = useState({ costPerMinute: 0 });
    const [plate, setPlate] = useState('');
    const [search, setSearch] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');

    const [modal, setModal] = useState(null);
    const [paymentMethod, setPaymentMethod] = useState('EFECTIVO');
    const timerRef = useRef(null);
    const [timer, setTimer] = useState(300);

    useEffect(() => {
        let ignore = false;
        fetchParkingData()
            .then(data => { if (!ignore) { setVehicles(data.vehicles); setSettings(data.settings); } })
            .catch(err => { console.error(err); if (!ignore) setVehicles([]); });
        return () => { ignore = true; };
    }, []);

    function showMsg(msg) { setMessage(msg); setTimeout(() => setMessage(''), 3000); }
    function showErr(msg) { setError(msg); setTimeout(() => setError(''), 5000); }

    async function handleEntry(e) {
        e.preventDefault();
        try {
            await api('POST', '/api/parking/entry', { plate });
            setPlate('');
            showMsg('Vehículo registrado correctamente');
            const data = await fetchParkingData();
            setVehicles(data.vehicles);
            setSettings(data.settings);
        } catch (err) { showErr(err.message); }
    }

    async function handleCalculate(plate) {
        try {
            const data = await api('POST', `/api/parking/calculate/${plate}`);
            setModal({ plate, cost: data.cost });
            setPaymentMethod('EFECTIVO');
            setTimer(300);
        } catch (err) { showErr(err.message); }
    }

    useEffect(() => {
        if (!modal) { clearInterval(timerRef.current); return; }
        timerRef.current = setInterval(() => {
            setTimer(t => { if (t <= 1) { setModal(null); return 0; } return t - 1; });
        }, 1000);
        return () => clearInterval(timerRef.current);
    }, [modal]);

    async function handleExit() {
        try {
            await api('POST', '/api/parking/exit', { plate: modal.plate, paymentMethod });
            setModal(null);
            showMsg('Salida registrada correctamente');
            const data = await fetchParkingData();
            setVehicles(data.vehicles);
            setSettings(data.settings);
        } catch (err) { showErr(err.message); }
    }

    async function handleCancelCheckout() {
        try {
            await api('DELETE', `/api/parking/calculate/${modal.plate}`);
        } catch (err) {
            console.warn('Error al cancelar:', err);
        }
        setModal(null);
    }

    async function handleDelete(plate) {
        if (!confirm('¿Seguro que deseas Eliminar esta entrada? No se registrará ningún cobro.')) return;
        try {
            await api('DELETE', `/api/parking/${plate}`);
            showMsg('Registro eliminado');
            const data = await fetchParkingData();
            setVehicles(data.vehicles);
            setSettings(data.settings);
        } catch (err) { showErr(err.message); }
    }

    const filtered = vehicles.filter(v =>
        v.plate.toLowerCase().includes(search.toLowerCase())
    );

    const mins = String(Math.floor(timer / 60)).padStart(2, '0');
    const secs = String(timer % 60).padStart(2, '0');

    return (
        <Layout>
            <div className="stats-grid">
                <div className="stat-card">
                    <p className="stat-label">Vehículos Activos</p>
                    <p className="stat-value">{vehicles.length}</p>
                </div>
                <div className="stat-card">
                    <p className="stat-label">Costo por Minuto</p>
                    <p className="stat-value">${settings.costPerMinute}</p>
                </div>
            </div>

            {message && <div className="alert-success mt-dashboard-alert">{message}</div>}
            {error && <div className="alert-error mt-dashboard-alert">{error}</div>}

            <div className="login-card dashboard-card">
                <div className="login-sidebar dashboard-sidebar">
                    <span className="sidebar-p sidebar-p-small">P</span>
                    <span className="sidebar-brand">Panel<br />Control</span>
                </div>
                <div className="login-panel">
                    <p className="panel-title">Registrar Entrada</p>
                    <form onSubmit={handleEntry} className="form-inline mt-1-5">
                        <div className="field field-inline">
                            <label htmlFor="plate">Patente / Placa</label>
                            <input
                                id="plate"
                                placeholder="Ej: ABCD12"
                                value={plate}
                                onChange={e => setPlate(e.target.value.toUpperCase())}
                                autoComplete="off"
                            />
                        </div>
                        <button type="submit" className="btn-entrar btn-inline">Ingresar</button>
                    </form>
                </div>
            </div>

            <div className="login-panel panel-rounded">
                <div className="header-flex">
                    <p className="login-title title-small">Vehículos en Estacionamiento</p>
                </div>

                <div className="search-container">
                    <input
                        type="text"
                        className="search-input"
                        placeholder="Buscar patente"
                        value={search}
                        onChange={e => setSearch(e.target.value)}
                        autoComplete="off"
                    />
                </div>

                <table className="data-table">
                    <thead>
                        <tr>
                            <th>Patente</th>
                            <th>Hora Ingreso</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        {filtered.length === 0 ? (
                            <tr>
                                <td colSpan={3} className="td-empty">
                                    {search ? 'No se encontraron vehículos con esa patente.' : 'No hay vehículos registrados en este momento.'}
                                </td>
                            </tr>
                        ) : (
                            filtered.map(v => (
                                <tr key={v.id}>
                                    <td className="td-bold">{v.plate}</td>
                                    <td>{new Date(v.entryTime).toLocaleTimeString()}</td>
                                    <td className="td-actions">
                                        <button className="btn-action" onClick={() => handleCalculate(v.plate)}>Cobrar</button>
                                        <button className="btn-eliminar" onClick={() => handleDelete(v.plate)}>Eliminar</button>
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>

            {modal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <p className="modal-title">Confirmar Salida</p>
                        <div className="checkout-details">
                            <p>Patente: <strong>{modal.plate}</strong></p>
                            <p>Total a Pagar: <strong>${modal.cost.toFixed(2)}</strong></p>
                            <p className="timer-text">El precio se mantendrá por: <span>{mins}:{secs}</span></p>
                        </div>
                        <div className="field">
                            <label>Método de Pago</label>
                            <select
                                className="search-input"
                                value={paymentMethod}
                                onChange={e => setPaymentMethod(e.target.value)}
                            >
                                <option value="EFECTIVO">Efectivo</option>
                                <option value="TARJETA">Tarjeta</option>
                            </select>
                        </div>
                        <button className="btn-pagar" onClick={handleExit}>Sí, Pagar y Dar Salida</button>
                        <button className="btn-cancelar" onClick={handleCancelCheckout}>No, Cancelar (Continuar tiempo)</button>
                    </div>
                </div>
            )}
        </Layout>
    );
}