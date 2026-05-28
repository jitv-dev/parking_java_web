import { useState } from 'react';
import { useAuth } from '../context/useAuth';

export default function LoginPage() {
    const { login } = useAuth();
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    async function handleSubmit(e) {
        e.preventDefault();
        try {
            setError('');
            await login(username, password);
        } catch (err) {
            setError(err.message);
        }
    }

    return (
        <div className="login-card">
            <div className="login-sidebar">
                <div className="sidebar-p">P</div>
                <div className="sidebar-brand">PARKING<br />SYSTEM</div>
            </div>
            <div className="login-panel">
                <h2 className="login-title">Iniciar Sesión</h2>
                <p className="login-subtitle">Ingresá tus credenciales</p>
                <form onSubmit={handleSubmit}>
                    {error && <div className="alert-error">{error}</div>}
                    <div className="field">
                        <label>Usuario</label>
                        <input
                            placeholder="Ej: admin"
                            value={username}
                            onChange={e => setUsername(e.target.value)}
                        />
                    </div>
                    <div className="field">
                        <label>Contraseña</label>
                        <input
                            type="password"
                            placeholder="••••••••"
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                        />
                    </div>
                    <button type="submit" className="btn-entrar">Entrar</button>
                </form>
            </div>
        </div>
    );
}