import { useState, useEffect } from 'react';
import { api } from '../api';
import Layout from '../components/Layout';

async function fetchHistory(filterDate) {
    const path = filterDate ? `/api/history?date=${filterDate}` : '/api/history';
    const data = await api('GET', path);
    return { history: data.history || [], totalEarnings: data.totalEarnings || 0, selectedDate: data.selectedDate || '' };
}

export default function HistoryPage() {
    const [history, setHistory] = useState([]);
    const [totalEarnings, setTotalEarnings] = useState(0);
    const [selectedDate, setSelectedDate] = useState('');
    const [date, setDate] = useState('');

    useEffect(() => {
        let ignore = false;
        fetchHistory()
            .then(data => { if (!ignore) { setHistory(data.history); setTotalEarnings(data.totalEarnings); setSelectedDate(data.selectedDate); } })
            .catch(() => { if (!ignore) setHistory([]); });
        return () => { ignore = true; };
    }, []);

    function handleFilter(e) {
        e.preventDefault();
        fetchHistory(date || undefined)
            .then(data => { setHistory(data.history); setTotalEarnings(data.totalEarnings); setSelectedDate(data.selectedDate); })
            .catch(() => setHistory([]));
    }

    function handleClear() {
        setDate('');
        fetchHistory()
            .then(data => { setHistory(data.history); setTotalEarnings(data.totalEarnings); setSelectedDate(data.selectedDate); })
            .catch(() => setHistory([]));
    }

    return (
        <Layout>
            <div className="stats-grid">
                <div className="stat-card">
                    <p className="stat-label">Registros encontrados</p>
                    <p className="stat-value">{history.length}</p>
                </div>
                <div className="stat-card">
                    <p className="stat-label">Total recaudado</p>
                    <p className="stat-value">${totalEarnings.toFixed(0)}</p>
                </div>
            </div>

            <div className="login-panel panel-rounded">
                <div className="header-flex">
                    <p className="login-title title-small">Historial de Salidas</p>
                </div>

                <form onSubmit={handleFilter} className="form-inline" style={{ marginBottom: '1.5rem' }}>
                    <div className="field field-inline" style={{ marginBottom: 0 }}>
                        <label htmlFor="date">Filtrar por día</label>
                        <input
                            type="date" id="date" className="search-input"
                            value={date}
                            onChange={e => setDate(e.target.value)}
                            style={{ colorScheme: 'dark' }}
                        />
                    </div>
                    <button type="submit" className="btn-entrar btn-inline">Filtrar</button>
                    <button type="button" className="btn-entrar btn-inline"
                        style={{ background: '#1c1e22', color: '#888', border: '1px solid #2e3035' }}
                        onClick={handleClear}>Ver Todo</button>
                </form>

                {selectedDate && (
                    <div className="alert-success" style={{ marginBottom: '1.5rem' }}>
                        Mostrando registros del día: <strong>{new Date(selectedDate).toLocaleDateString()}</strong>
                    </div>
                )}

                <table className="data-table">
                    <thead>
                        <tr>
                            <th>Patente</th>
                            <th>Entrada</th>
                            <th>Salida</th>
                            <th>Minutos</th>
                            <th>Método</th>
                            <th>Operador</th>
                            <th>Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        {history.length === 0 ? (
                            <tr><td colSpan={7} className="td-empty">No hay registros para mostrar.</td></tr>
                        ) : (
                            history.map(r => (
                                <tr key={r.id}>
                                    <td className="td-bold">{r.plate}</td>
                                    <td>{new Date(r.entryTime).toLocaleString()}</td>
                                    <td>{new Date(r.exitTime).toLocaleString()}</td>
                                    <td>{r.minutes.toFixed(1)}</td>
                                    <td>{r.paymentMethod}</td>
                                    <td className="td-operator">{r.operator}</td>
                                    <td className="td-total-positive">${r.cost.toFixed(0)}</td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
        </Layout>
    );
}