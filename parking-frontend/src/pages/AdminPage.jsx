import { useState, useEffect } from 'react';
import { api } from '../api';
import Layout from '../components/Layout';

async function fetchAdminData() {
    const data = await api('GET', '/api/admin');
    return { users: data.users || [], settings: data.settings || { costPerMinute: 0 } };
}

export default function AdminPage() {
    const [users, setUsers] = useState([]);
    const [settings, setSettings] = useState({ costPerMinute: 0 });
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');

    const [newCost, setNewCost] = useState('');
    const [newUser, setNewUser] = useState({ username: '', password: '', role: '' });

    useEffect(() => {
        let ignore = false;
        fetchAdminData()
            .then(data => { if (!ignore) { setUsers(data.users); setSettings(data.settings); setNewCost(data.settings.costPerMinute || ''); } })
            .catch(() => { if (!ignore) setUsers([]); });
        return () => { ignore = true; };
    }, []);

    function showMsg(m) { setMessage(m); setTimeout(() => setMessage(''), 3000); }
    function showErr(m) { setError(m); setTimeout(() => setError(''), 5000); }

    async function handleUpdateCost(e) {
        e.preventDefault();
        try {
            await api('PUT', '/api/admin/cost', { costPerMinute: parseFloat(newCost) });
            showMsg(`Costo actualizado a $${newCost} por minuto.`);
            const data = await fetchAdminData();
            setUsers(data.users);
            setSettings(data.settings);
        } catch (err) { showErr(err.message); }
    }

    async function handleCreateUser(e) {
        e.preventDefault();
        try {
            await api('POST', '/api/admin', newUser);
            setNewUser({ username: '', password: '', role: '' });
            showMsg('Usuario creado correctamente');
            const data = await fetchAdminData();
            setUsers(data.users);
            setSettings(data.settings);
        } catch (err) { showErr(err.message); }
    }

    async function handleToggle(id) {
        try {
            await api('PUT', `/api/admin/${id}`);
            const data = await fetchAdminData();
            setUsers(data.users);
        } catch (err) { showErr(err.message); }
    }

    async function handleDelete(id) {
        if (!confirm('¿Seguro que deseas eliminar este usuario? Esta acción no se puede deshacer.')) return;
        try {
            await api('DELETE', `/api/admin/${id}`);
            showMsg('Usuario eliminado correctamente.');
            const data = await fetchAdminData();
            setUsers(data.users);
        } catch (err) { showErr(err.message); }
    }

    return (
        <Layout>
            <div className="stats-grid">
                <div className="stat-card">
                    <p className="stat-label">Usuarios Registrados</p>
                    <p className="stat-value">{users.length}</p>
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
                    <span className="sidebar-p sidebar-p-small">$</span>
                    <span className="sidebar-brand">Costo<br />Minuto</span>
                </div>
                <div className="login-panel">
                    <p className="panel-title">Actualizar Tarifa</p>
                    <form onSubmit={handleUpdateCost} className="form-inline mt-1-5">
                        <div className="field field-inline">
                            <label htmlFor="costPerMinute">Nuevo costo por minuto ($)</label>
                            <input
                                type="number" id="costPerMinute" step="0.01" min="0.01" required
                                value={newCost} onChange={e => setNewCost(e.target.value)}
                                placeholder="Ej: 150.00"
                            />
                        </div>
                        <button type="submit" className="btn-entrar btn-inline">Guardar</button>
                    </form>
                </div>
            </div>

            <div className="login-card dashboard-card">
                <div className="login-sidebar dashboard-sidebar">
                    <span className="sidebar-p sidebar-p-small">+</span>
                    <span className="sidebar-brand">Nuevo<br />Usuario</span>
                </div>
                <div className="login-panel">
                    <p className="panel-title">Crear Usuario</p>
                    <form onSubmit={handleCreateUser} className="form-inline-wrap">
                        <div className="field field-inline min-w-140">
                            <label htmlFor="username">Usuario</label>
                            <input
                                type="text" id="username" placeholder="nombre"
                                value={newUser.username}
                                onChange={e => setNewUser(u => ({ ...u, username: e.target.value }))}
                                autoComplete="off"
                            />
                        </div>
                        <div className="field field-inline min-w-140">
                            <label htmlFor="password">Contraseña</label>
                            <input
                                type="password" id="password" placeholder="mín. 8 caracteres"
                                value={newUser.password}
                                onChange={e => setNewUser(u => ({ ...u, password: e.target.value }))}
                            />
                        </div>
                        <div className="field field-inline min-w-120">
                            <label htmlFor="role">Rol</label>
                            <select
                                id="role" className="search-input select-input-md"
                                value={newUser.role}
                                onChange={e => setNewUser(u => ({ ...u, role: e.target.value }))}
                            >
                                <option value="" disabled>Seleccionar...</option>
                                <option value="ROLE_ADMIN">Admin</option>
                                <option value="ROLE_WORKER">Worker</option>
                            </select>
                        </div>
                        <button type="submit" className="btn-entrar btn-inline">Crear</button>
                    </form>
                </div>
            </div>

            <div className="login-panel panel-rounded">
                <div className="header-flex">
                    <p className="login-title title-small">Usuarios del Sistema</p>
                </div>
                <table className="data-table">
                    <thead>
                        <tr>
                            <th>Usuario</th>
                            <th>Rol</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        {users.length === 0 ? (
                            <tr><td colSpan={4} className="td-empty">No hay usuarios registrados.</td></tr>
                        ) : (
                            users.map(u => (
                                <tr key={u.id}>
                                    <td className="td-bold">{u.username}</td>
                                    <td>{u.role === 'ROLE_ADMIN' ? 'Administrador' : 'Trabajador'}</td>
                                    <td>
                                        <span className={u.enabled ? 'status-active' : 'status-inactive'}>
                                            ● {u.enabled ? 'Activo' : 'Inactivo'}
                                        </span>
                                    </td>
                                    <td className="td-actions">
                                        <button className="btn-action" onClick={() => handleToggle(u.id)}>
                                            {u.enabled ? 'Deshabilitar' : 'Habilitar'}
                                        </button>
                                        <button className="btn-eliminar" onClick={() => handleDelete(u.id)}>Eliminar</button>
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
        </Layout>
    );
}